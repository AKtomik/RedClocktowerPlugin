package io.github.AKtomik.redclocktower.game;

import io.github.AKtomik.redclocktower.DataKey;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

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
	public static BloodGame get(World world)
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
	public boolean isEnded()
	{
		return getState() == BloodGameState.ENDED;
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

	public List<Player> getAllPlayers()
	{
		return getPlayersUuids().stream().map(Bukkit::getPlayer).toList();
	}

	public List<OfflinePlayer> getAllPlayersAsOffline()
	{
		return getPlayersUuids().stream().map(Bukkit::getOfflinePlayer).toList();
	}

	public List<BloodPlayer> getAllBloodPlayers()
	{
		return getAllPlayers().stream().map(BloodPlayer::get).toList();
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

	public void removeOfflinePlayer(OfflinePlayer offlinePlayer)
	{
		// only if really offline
		if (offlinePlayer.isOnline())
		{
			removePlayer(Objects.requireNonNull(offlinePlayer.getPlayer()));
		}
		// values
		String uuid = offlinePlayer.getUniqueId().toString();
		// removing from the uuid list
		ArrayList<String> playersUuid = new ArrayList<>(getPlayersUuids());
		boolean removed = playersUuid.removeIf(loopUuid -> loopUuid != null && Objects.equals(loopUuid, uuid));
		// check
		if (!removed) throw new RuntimeException("trying to remove an offline player in a game where he is not");
		// really removing from the uuid list
		setPlayersUuid(playersUuid);
		// ! no blood player object quit
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
	public void doAction(BloodGameAction action, CommandSender sender)
	{
		gameAction.get(action).accept(this, sender);
	}
	public void switchTime(BloodGamePeriod period, CommandSender sender)
	{
		gamePeriodEnter.get(period).accept(this, sender);
		setTime(period);
	}

	public void broadcast(String richString)
	{
		Bukkit.getServer().broadcast(mini.deserialize(richString));
	}

	// code/action
	static Map<BloodGameAction, BiConsumer<BloodGame, CommandSender>> gameAction = Map.ofEntries(

	Map.entry(BloodGameAction.SETUP, (game, sender) -> {
		if (game.isReady()) {
			sender.sendRichMessage("<red>game is already setup!");
			return;
		}
		game.world.setTime(12000);
		game.world.setGameRule(GameRules.ADVANCE_TIME, false);
		game.generateNewId();
		game.createTeam();
		sender.sendRichMessage("<light_purple>setup game <dark_gray><round_id>", Placeholder.parsed("round_id", game.getRoundId()));
		game.setState(BloodGameState.WAITING);
	}),

	Map.entry(BloodGameAction.START, (game, sender) -> {
		if (!game.isReady()) {
			sender.sendRichMessage("<red>game is not setup!");
			return;
		}
		game.setState(BloodGameState.INGAME);
		sender.sendRichMessage("<light_purple>starting game <dark_gray><round_id>", Placeholder.parsed("round_id", game.getRoundId()));
		game.broadcast("<red><b>are you ready to bleed?");
	}),

	Map.entry(BloodGameAction.FINISH, (game, sender) -> {
		if (!game.isStarted()) {
			sender.sendRichMessage("<red>game is not started!");
			return;
		}
		game.setState(BloodGameState.ENDED);
		sender.sendRichMessage("<light_purple>ending game <dark_gray><round_id>", Placeholder.parsed("round_id", game.getRoundId()));
		game.broadcast("<red><b>the game is over!");
	}),

	Map.entry(BloodGameAction.RESET, (game, sender) -> {
		for (OfflinePlayer offlinePlayer : game.getAllPlayersAsOffline())
			game.removeOfflinePlayer(offlinePlayer);
		game.clearPlayersUuid();
		game.deleteTeam();
		game.setState(BloodGameState.NOTHING);
		sender.sendRichMessage("<light_purple>reseting game");
	}),

	Map.entry(BloodGameAction.REPLAY, (game, sender) -> {
		if (!game.isStarted() && !game.isEnded()) {
			sender.sendRichMessage("<red>game is not started nor ended!");
			return;
		}
		game.setState(BloodGameState.WAITING);
		sender.sendRichMessage("<light_purple>waiting for a new game with same players and settings <dark_gray><round_id>", Placeholder.parsed("round_id", game.getRoundId()));
	}),

	Map.entry(BloodGameAction.CLEAR, (game, sender) -> {
		if (game.isReady()) {
			sender.sendRichMessage("<red>game is running!");
			return;
		}
		game.world.setGameRule(GameRules.ADVANCE_TIME, true);
		game.setState(BloodGameState.NOTHING);
		sender.sendRichMessage("<light_purple>clearing game");
	})
	);

	// code/period
	static Map<BloodGamePeriod, BiConsumer<BloodGame, CommandSender>> gamePeriodEnter = Map.ofEntries(
	Map.entry(BloodGamePeriod.MORNING, (game, sender) -> {
		game.broadcast("<white><b>it's the morning!");
		game.broadcast("<gray><i>it's the morning!");
		game.world.setTime(0);
	}),
	Map.entry(BloodGamePeriod.FREE, (game, sender) -> {
		game.broadcast("<white><b>wonder time");
		game.broadcast("<gray><i>you are free to go and talk");
		game.world.setTime(6000);
	}),
	Map.entry(BloodGamePeriod.MEET, (game, sender) -> {
		game.broadcast("<white><b>debate time");
		game.broadcast("<gray><i>everyone is attended to the townhall");
		game.world.setTime(12000);
	}),
	Map.entry(BloodGamePeriod.NIGHT, (game, sender) -> {
		game.broadcast("<white><b>the moon is rising...");
		game.broadcast("<gray><i>go to your house and sleep well");
		game.world.setTime(18000);
	})
	);
}