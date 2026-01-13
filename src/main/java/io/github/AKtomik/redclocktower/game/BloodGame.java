package io.github.AKtomik.redclocktower.game;

import io.github.AKtomik.redclocktower.DataKey;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class BloodGame {

	// class
	public static MiniMessage mini = MiniMessage.miniMessage();

	final public World world;
	private final PersistentDataContainer pdc;

	private BloodGame(World world)
	{
		this.world = world;
		this.pdc = world.getPersistentDataContainer();
	}
	public static BloodGame WorldGame(World world)
	{
		return new BloodGame(world);
	}

	// get & set
	public void setState(BloodGameState gameState)
	{
		pdc.set(DataKey.GAME_STATE.key, PersistentDataType.INTEGER, gameState.ordinal());
	}
	public BloodGameState getState()
	{
		int ordinal = pdc.getOrDefault(DataKey.GAME_STATE.key, PersistentDataType.INTEGER, BloodGameState.NOTHING.ordinal());
		return BloodGameState.values()[ordinal];
	}

	private void setTime(BloodGamePeriod gameState)
	{
		pdc.set(DataKey.GAME_PERIOD.key, PersistentDataType.INTEGER, gameState.ordinal());
	}
	public BloodGamePeriod getTime()
	{
		int ordinal = pdc.getOrDefault(DataKey.GAME_PERIOD.key, PersistentDataType.INTEGER, BloodGamePeriod.FREE.ordinal());
		return BloodGamePeriod.values()[ordinal];
	}

	private void setRoundCount(int count)
	{
		pdc.set(DataKey.GAME_ROUND_COUNT.key, PersistentDataType.INTEGER, count);
	}
	public int getRoundCount()
	{
		return pdc.getOrDefault(DataKey.GAME_ROUND_COUNT.key, PersistentDataType.INTEGER, 0);
	}

	private void setRoundId(String strid)
	{
		pdc.set(DataKey.GAME_ROUND_ID.key, PersistentDataType.STRING, strid);
	}
	public String getRoundId()
	{
		return pdc.get(DataKey.GAME_ROUND_ID.key, PersistentDataType.STRING);
	}

	private void setPlayersUuid(List<String> uuids)
	{
		pdc.set(DataKey.GAME_PLAYERS_UUID.key, PersistentDataType.LIST.strings(), uuids);
	}
	public List<String> getPlayersUuids()
	{
		return pdc.getOrDefault(DataKey.GAME_PLAYERS_UUID.key, PersistentDataType.LIST.strings(), List.of());
	}
	private void clearPlayersUuid()
	{
		pdc.remove(DataKey.GAME_PLAYERS_UUID.key);
	}

	// if
	public boolean isStarted()
	{
		return getState() == BloodGameState.INGAME;
	}
	public boolean isReady()
	{
		BloodGameState state = getState();
		return (state == BloodGameState.WAITING || state == BloodGameState.INGAME);

	}

	public boolean isUuidIn(String uuid)
	{
		return (findPlayerIndex(uuid) != -1);
	}

	public boolean isPlayerIn(Player player)
	{
		return isUuidIn(player.getUniqueId().toString());
	}

	// players
	private int findPlayerIndex(String playerUuid)
	{
		List<String> uuids = getPlayersUuids();
		for (int i = 0; i < uuids.size(); i++)
		{
			String loopUuid = uuids.get(i);
			if (loopUuid != null && Objects.equals(loopUuid, playerUuid))
			{
				return i;
			}
		}
		return -1;
	}

	public List<BloodPlayer> allBloodPlayers()
	{
		return getPlayersUuids().stream().map(uuid -> BloodPlayer.get(Bukkit.getPlayer(uuid))).toList();
	}

	public BloodPlayer addPlayer(Player player)
	{
		String uuid = player.getUniqueId().toString();
		// check if player already exist
		if (isUuidIn(uuid)) throw new RuntimeException("trying to add a player in a game where he already is");
		// adding to the uuid list
		ArrayList<String> playersUuid = new ArrayList<>(getPlayersUuids());
		playersUuid.add(uuid);
		setPlayersUuid(playersUuid);
		// blood player object join
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.joinGame(this);
		return bloodPlayer;
	}

	public void removePlayer(Player player)
	{
		String uuid = player.getUniqueId().toString();
		// removing from the uuid list
		ArrayList<String> playersUuid = new ArrayList<>(getPlayersUuids());
		boolean removed = playersUuid.removeIf(loopUuid -> loopUuid != null && Objects.equals(loopUuid, uuid));
		// check
		if (!removed) throw new RuntimeException("trying to remove a player in a game where he is not");
		// really removing from the uuid list
		setPlayersUuid(playersUuid);
		// blood player object quit
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.quitGame(this);
	}

	// teams
	private void generateNewId()
	{
		int incrementedRoundCount = getRoundCount() + 1;
		setRoundCount(incrementedRoundCount);
		setRoundId("blood-%s-%s".formatted(world.getName(), Integer.toString(incrementedRoundCount)));
	}

	private void createTeam()
	{
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		String teamId = getRoundId();
		if (teamId == null) throw new RuntimeException("trying to create team but there is no round id");
		Team team = board.registerNewTeam(teamId);
		team.setAllowFriendlyFire(false);
		team.setCanSeeFriendlyInvisibles(true);
		team.prefix(mini.deserialize("<red>✴ "));
	}

	Team getTeam()
	{
		String teamId = getRoundId();
		if (teamId == null) throw new RuntimeException("trying to get team but there is no round id");
		return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamId);
	}

	private void deleteTeam()
	{
		if (getRoundId() == null) return;
		Team team = getTeam();
		if (team == null) return;
		team.unregister();
	}

	// runs
	public void doAction(BloodGameAction action)
	{
		gameAction.get(action).accept(this);
	}
	public void switchTime(BloodGamePeriod period)
	{
		gamePeriodEnter.get(period).accept(this);
		setTime(period);
	}

	public void broadcast(String richString)
	{
		Bukkit.getServer().broadcast(mini.deserialize(richString));
	}

	// code/action
	static Map<BloodGameAction, Consumer<BloodGame>> gameAction = Map.ofEntries(
	Map.entry(BloodGameAction.RESET, (game) -> {
//		for (PersistentDataContainer pdc : game.getPlayersPdc())
//			game.removePlayer(pdc);
	}),
	Map.entry(BloodGameAction.SETUP, (game) -> {
//		if (game.isReady()) return;
		game.world.setTime(12000);
		game.world.setGameRule(GameRules.ADVANCE_TIME, false);
		game.generateNewId();
		game.createTeam();
		game.setState(BloodGameState.WAITING);
	}),
	Map.entry(BloodGameAction.SETOUT, (game) -> {
		game.world.setGameRule(GameRules.ADVANCE_TIME, true);
		game.clearPlayersUuid();
		game.deleteTeam();
		game.setState(BloodGameState.NOTHING);
	}),
	Map.entry(BloodGameAction.START, (game) -> {
//		if (!game.isReady()) return;
		game.setState(BloodGameState.INGAME);
		game.broadcast("<red><b>are you ready to bleed?");
	}),
	Map.entry(BloodGameAction.FINISH, (game) -> {
		game.setState(BloodGameState.ENDED);
		game.broadcast("<red><b>the game is over!");
	}),
	Map.entry(BloodGameAction.CLEAN, (game) -> {
		game.doAction(BloodGameAction.RESET);
		game.doAction(BloodGameAction.SETOUT);
		game.setState(BloodGameState.NOTHING);
	}));

	// code/period
	static Map<BloodGamePeriod, Consumer<BloodGame>> gamePeriodEnter = Map.ofEntries(
	Map.entry(BloodGamePeriod.MORNING, (game) -> {
		game.broadcast("<white><b>it's the morning!");
		game.broadcast("<gray><i>it's the morning!");
		game.world.setTime(0);
	}),
	Map.entry(BloodGamePeriod.FREE, (game) -> {
		game.broadcast("<white><b>wonder time");
		game.broadcast("<gray><i>you are free to go and talk");
		game.world.setTime(6000);
	}),
	Map.entry(BloodGamePeriod.MEET, (game) -> {
		game.broadcast("<white><b>debate time");
		game.broadcast("<gray><i>everyone is attended to the townhall");
		game.world.setTime(12000);
	}),
	Map.entry(BloodGamePeriod.NIGHT, (game) -> {
		game.broadcast("<white><b>the moon is rising...");
		game.broadcast("<gray><i>go to your house and sleep well");
		game.world.setTime(18000);
	})
	);
}