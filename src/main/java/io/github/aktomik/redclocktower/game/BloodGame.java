package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.DataKey;
import io.github.aktomik.redclocktower.RedClocktower;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.function.BiConsumer;

import static org.bukkit.Bukkit.getLogger;

public class BloodGame {

	// class
	static MiniMessage mini = MiniMessage.miniMessage();

	public final World world;
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

	private void setStorytellerUuid(String uuid)
	{
		pdc.set(DataKey.GAME_STORYTELLER_UUID.key, PersistentDataType.STRING, uuid);
	}
	private void clearStorytellerUuid()
	{
		pdc.remove(DataKey.GAME_STORYTELLER_UUID.key);
	}
	public String getStorytellerUuid()
	{
		return pdc.get(DataKey.GAME_STORYTELLER_UUID.key, PersistentDataType.STRING);
	}

	private void setVoteNominatedUuid(String uuid)
	{
		pdc.set(DataKey.GAME_VOTE_NOMINATED_UUID.key, PersistentDataType.STRING, uuid);
	}
	private void clearVoteNominatedUuid()
	{
		pdc.remove(DataKey.GAME_VOTE_NOMINATED_UUID.key);
	}
	public String getVoteNominatedUuid()
	{
		return pdc.get(DataKey.GAME_VOTE_NOMINATED_UUID.key, PersistentDataType.STRING);
	}

	private void setVotePyloriUuid(String uuid)
	{
		pdc.set(DataKey.GAME_VOTE_PYLORI_UUID.key, PersistentDataType.STRING, uuid);
	}
	private void clearVotePyloriUuid()
	{
		pdc.remove(DataKey.GAME_VOTE_PYLORI_UUID.key);
	}
	public String getVotePyloriUuid()
	{
		return pdc.get(DataKey.GAME_VOTE_PYLORI_UUID.key, PersistentDataType.STRING);
	}

	private void setSlotsUuid(List<String> uuids)
	{
		pdc.set(DataKey.GAME_SLOTS_UUID.key, PersistentDataType.LIST.strings(), uuids);
	}
	private void clearSlotsUuid()
	{
		pdc.remove(DataKey.GAME_SLOTS_UUID.key);
	}
	public List<String> getSlotsUuid()
	{
		return pdc.getOrDefault(DataKey.GAME_SLOTS_UUID.key, PersistentDataType.LIST.strings(), List.of());
	}

	private void setSlotsPdc(List<PersistentDataContainer> uuids)
	{
		pdc.set(DataKey.GAME_SLOTS_PDC.key, PersistentDataType.LIST.dataContainers(), uuids);
	}
	private void clearSlotsPdc()
	{
		pdc.remove(DataKey.GAME_SLOTS_PDC.key);
	}
	private List<PersistentDataContainer> getSlotsPdc()
	{
		return pdc.getOrDefault(DataKey.GAME_SLOTS_PDC.key, PersistentDataType.LIST.dataContainers(), List.of());
	}

	public void setPosition(BloodGamePlace place, Location pos)
	{
		pdc.set(DataKey.GAME_LOC.get(place).key, PersistentDataType.INTEGER_ARRAY, new int[] {pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()});
	}
	public Location getPosition(BloodGamePlace place)
	{
		int[] posArray = pdc.get(DataKey.GAME_LOC.get(place).key, PersistentDataType.INTEGER_ARRAY);
		if (posArray == null || posArray.length != 3) return null;
		return new Location(world, posArray[0], posArray[1], posArray[2]);
	}

	private void setSettingsSlotLimit(int count)
	{
		pdc.set(DataKey.GAME_SETTINGS_SLOT_LIMIT.key, PersistentDataType.INTEGER, count);
	}
	public int getSettingsSlotLimit()
	{
		return pdc.getOrDefault(DataKey.GAME_SETTINGS_SLOT_LIMIT.key, PersistentDataType.INTEGER, 15);
	}

	// states & time
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
	public boolean isVoteMoment() { return (getTime() == BloodGamePeriod.MEET); }

	// players
	private int findPlayerIndex(String playerUuid)
	{
		List<String> uuids = getSlotsUuid();
		for (int i = 0; i < uuids.size(); i++)
		{
			String loopUuid = uuids.get(i);
			if (!loopUuid.isEmpty() && Objects.equals(loopUuid, playerUuid))
			{
				return i;
			}
		}
		return -1;
	}

	public List<String> getPlayersUuid()
	{
		return getSlotsUuid().stream().filter(uuid -> !uuid.isEmpty()).toList();
	}

	public List<Integer> getEmptySlotsIndex(List<String> slots)
	{
		List<Integer> indexes = new ArrayList<>();
		for (int i = 0; i < slots.size(); i++)
		{
			if (slots.get(i).isEmpty()) indexes.add(i);
		}
		return indexes;
	}

	public List<Player> getAllPlayers()
	{
		return getPlayersUuid().stream().map(UUID::fromString).map(Bukkit::getPlayer).toList();
	}

	public List<OfflinePlayer> getAllPlayersAsOffline()
	{
		return getPlayersUuid().stream().map(UUID::fromString).map(Bukkit::getOfflinePlayer).toList();
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
		// fill it to the uuid list
		ArrayList<String> slotsUuid = new ArrayList<>(getSlotsUuid());
		List<Integer> emptySlotsIndex = getEmptySlotsIndex(slotsUuid);
		if (emptySlotsIndex.isEmpty()) throw new RuntimeException("trying to add a player in a game with no available slot (check if the limit is well set and refresh)");
		int slotIndex = emptySlotsIndex.getFirst();
		slotsUuid.set(slotIndex, uuid);
		setSlotsUuid(slotsUuid);
		// blood player object join
		// must be done after game add player
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.joinGame(this, slotIndex);
		return bloodPlayer;
	}

	public void removePlayer(OfflinePlayer offlinePlayer)
	{
		String uuid = offlinePlayer.getUniqueId().toString();
		// set null from the uuid list
		ArrayList<String> slotsUuid = new ArrayList<>(getSlotsUuid());
		boolean removed = false;
		for (int i = 0; i < slotsUuid.size(); i++)
		{
			if (Objects.equals(slotsUuid.get(i), uuid))
			{
				slotsUuid.set(i, "");
				removed = true;
				break;
			}
		}
		// check
		if (!removed) throw new RuntimeException("trying to remove an offline player in a game where he is not");
		// blood player object quit if online
		Player player = offlinePlayer.getPlayer();
		if (player != null) {// must be done before game remove player
			BloodPlayer bloodPlayer = BloodPlayer.get(player);
			bloodPlayer.quitGame(this);
		};
		// really removing from the uuid list
		setSlotsUuid(slotsUuid);
	}

	public boolean isUuidIn(String uuid)
	{
		return (findPlayerIndex(uuid) != -1);
	}

	public boolean isPlayerIn(Player player)
	{
		return isUuidIn(player.getUniqueId().toString());
	}

	public void changeStoryteller(Player player)
	{
		setStorytellerUuid(player.getUniqueId().toString());

		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.joinGame(this, true);
	}

	public Player getStoryteller()
	{
		return Bukkit.getPlayer(UUID.fromString(getStorytellerUuid()));
	}

	public void changePyloriPlayer(Player player)
	{
		String uuid = player.getUniqueId().toString();
		if (!isUuidIn(uuid)) throw new RuntimeException("trying to put a player on pylori that is not in game");
		setVotePyloriUuid(uuid);
	}

	public Player getPyloriPlayer()
	{
		return Bukkit.getPlayer(UUID.fromString(getStorytellerUuid()));
	}

	// slots

	public List<BloodSlot> getSlots()
	{
		return getSlotsPdc().stream().map(slot -> BloodSlot.get(world, slot)).toList();
	}
	public BloodSlot getSlot(int index)
	{
		return BloodSlot.get(world, getSlotsPdc().get(index));
	}
	public int getSlotCount()
	{
		return getSlotsPdc().size();
	}
	public void setSlot(int index, BloodSlot slot)
	{
		List<PersistentDataContainer> slotsPdc = new ArrayList<>(getSlotsPdc());
		slotsPdc.set(index, slot.pdc);
		setSlotsPdc(slotsPdc);
	}
	public void setSlots(int index, List<BloodSlot> slots)
	{
		setSlotsPdc(slots.stream().map(slot -> slot.pdc).toList());
	}

	public void addLastSlot()
	{
		List<PersistentDataContainer> slotsPdc = new ArrayList<>(getSlotsPdc());
		slotsPdc.add(pdc.getAdapterContext().newPersistentDataContainer());
		setSlotsPdc(slotsPdc);
	}
	public void removeLastSlot()
	{
		List<PersistentDataContainer> slotsPdc = new ArrayList<>(getSlotsPdc());
		slotsPdc.remove(slotsPdc.size() - 1);
		setSlotsPdc(slotsPdc);
	}

	public void applySlotLimit()
	{
		int slotLimit = getSettingsSlotLimit();
		int slotSize = getSlotsUuid().size();

		getLogger().info("SLOT LIMIT APPLY");
		if (slotSize < slotLimit)
		{
			getLogger().info("SLOT LIMIT ADDER");
			ArrayList<String> slotsUuid = new ArrayList<>(getSlotsUuid());
			for (int repeat = 0; repeat < slotLimit - slotSize; repeat++)
			{
				slotsUuid.add("");
			}
			List<String> list = slotsUuid.stream().toList();
			setSlotsUuid(list);
		}
		else if (slotSize > slotLimit)
		{
			ArrayList<String> slotsUuid = new ArrayList<>(getSlotsUuid());
			for (int repeat = 0; repeat < slotSize - slotLimit; repeat++)
			{
				String lastUuid = slotsUuid.getLast();
				slotsUuid.removeLast();
				if (lastUuid.isEmpty()) continue;

				Player player = Bukkit.getPlayer(UUID.fromString(lastUuid));
				getLogger().info(player.toString());
				if (player == null || !player.isOnline()) continue;
				BloodPlayer bloodPlayer = BloodPlayer.get(player);
				bloodPlayer.quitGame(this);
			}
			setSlotsUuid(slotsUuid);
		}
	}

	public void changeSlotLimit(int newLimit)
	{
		setSettingsSlotLimit(newLimit);
		applySlotLimit();
	}

	public int remainingSlots()
	{
		return getSettingsSlotLimit() - getPlayersUuid().size();
	}

	public boolean isFull()
	{
		return remainingSlots() <= 0;
	}

	public boolean isLevelInSlots(Location loc)
	{
		for (BloodSlot slot : getSlots())
		{
			if (Objects.equals(slot.getPosition(BloodSlotPlace.LEVER), loc)) return true;
		}
		return false;
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
		team.setNameTagVisibility(NameTagVisibility.NEVER);
		team.setCanSeeFriendlyInvisibles(true);
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
		game.world.setDifficulty(Difficulty.PEACEFUL);
		game.generateNewId();
		game.createTeam();
		game.applySlotLimit();
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
			game.removePlayer(offlinePlayer);
		game.clearSlotsUuid();
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
	}),

	Map.entry(BloodGameAction.BRUTAL_CLEAN, (game, sender) -> {
		// game.clearSlotsPdc();//!should
		game.clearSlotsUuid();
		game.clearStorytellerUuid();
		//game.applySlotLimit();//!shouldn't
		sender.sendRichMessage("<light_purple>game was brutally cleaned");
		sender.sendRichMessage("<orange>this may result as unintended behavior and must be used in last resort.");
		sender.sendRichMessage("<orange>it is advised to restart server or at least deco/reco all players.");
		sender.sendRichMessage("<orange>after that, resetup your game.");
	})
	);

	// code/period
	static Map<BloodGamePeriod, BiConsumer<BloodGame, CommandSender>> gamePeriodEnter = Map.ofEntries(
	Map.entry(BloodGamePeriod.MORNING, (game, sender) -> {
		game.world.setTime(0);
		game.world.playSound(game.getPosition(BloodGamePlace.CENTER), Sound.BLOCK_BELL_USE, 123456789f, 0.3f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin, () -> {
			game.world.playSound(game.getPosition(BloodGamePlace.CENTER), Sound.BLOCK_BELL_USE, 123456789f, 0.4f);
			game.broadcast("<white><b>it's the morning!");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin, () -> {
			game.world.playSound(game.getPosition(BloodGamePlace.CENTER), Sound.BLOCK_BELL_USE, 123456789f, 0.5f);
			game.broadcast("<gray><i>everyone is attended to the townhall");
		}, 40L);
	}),
	Map.entry(BloodGamePeriod.FREE, (game, sender) -> {
		game.world.setTime(6000);
		game.world.playSound(game.getPosition(BloodGamePlace.CENTER), Sound.BLOCK_ANVIL_LAND, 123456789f, 1.7f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin, () -> {
			game.world.playSound(game.getPosition(BloodGamePlace.CENTER), Sound.BLOCK_ANVIL_LAND, 123456789f, 1.7f);
			game.broadcast("<white><b>wonder time");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin, () -> {
			game.world.playSound(game.getPosition(BloodGamePlace.CENTER), Sound.BLOCK_ANVIL_LAND, 123456789f, 1.7f);
			game.broadcast("<gray><i>you are free to go and talk");
		}, 40L);
	}),
	Map.entry(BloodGamePeriod.MEET, (game, sender) -> {
		game.world.setTime(12000);
		game.world.playSound(game.getPosition(BloodGamePlace.CENTER), Sound.BLOCK_BELL_USE, 123456789f, 0.3f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin, () -> {
			game.world.playSound(game.getPosition(BloodGamePlace.CENTER), Sound.BLOCK_BELL_USE, 123456789f, 0.4f);
			game.broadcast("<white><b>debate time");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin, () -> {
			game.world.playSound(game.getPosition(BloodGamePlace.CENTER), Sound.BLOCK_BELL_USE, 123456789f, 0.5f);
			game.broadcast("<gray><i>everyone is attended to the townhall");
		}, 40L);
	}),
	Map.entry(BloodGamePeriod.NIGHT, (game, sender) -> {
		game.world.setTime(18000);
		game.world.playSound(game.getPosition(BloodGamePlace.CENTER), Sound.ENTITY_ALLAY_HURT, 123456789f, 0f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin, () -> {
			game.world.playSound(game.getPosition(BloodGamePlace.CENTER), Sound.BLOCK_WOODEN_DOOR_OPEN, 123456789f, .5f);
			game.broadcast("<white><b>the moon is rising...");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin, () -> {
			game.world.playSound(game.getPosition(BloodGamePlace.CENTER), Sound.BLOCK_WOODEN_DOOR_CLOSE, 123456789f, .5f);
			game.broadcast("<gray><i>go to your house and sleep well");
		}, 40L);
	})
	);
}