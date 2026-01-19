package io.github.aktomik.redclocktower.game;

import com.mojang.brigadier.context.CommandContext;
import io.github.aktomik.redclocktower.DataKey;
import io.github.aktomik.redclocktower.RedClocktower;
import io.github.aktomik.redclocktower.utils.UUIDDataType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.function.Consumer;

public class BloodGame {

	// class
	private static MiniMessage mini = MiniMessage.miniMessage();

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
	public static BloodGame get(CommandSourceStack ctx) {
		return BloodGame.get(ctx.getLocation().getWorld());
	}
	public static BloodGame get(CommandContext<CommandSourceStack> ctx) {
		return BloodGame.get(ctx.getSource());
	}

	// sett
	static final NamedTextColor NOMINATE_TEAM_COLOR = NamedTextColor.GOLD;
	static final NamedTextColor PYLORI_TEAM_COLOR = NamedTextColor.RED;
	static final float DEFAULT_VOLUME = .25f;
	static final float VOTE_VOLUME = .25f;
	static final float EVENT_VOLUME = .5f;

	// get & set
	public void setState(GameState gameState)
	{
		pdc.set(DataKey.GAME_STATE.key(), PersistentDataType.INTEGER, gameState.ordinal());
	}
	public GameState getState()
	{
		int ordinal = pdc.getOrDefault(DataKey.GAME_STATE.key(), PersistentDataType.INTEGER, GameState.NOTHING.ordinal());
		return GameState.values()[ordinal];
	}

	private void setTime(GamePeriod gameState)
	{
		pdc.set(DataKey.GAME_PERIOD.key(), PersistentDataType.INTEGER, gameState.ordinal());
	}
	public GamePeriod getTime()
	{
		int ordinal = pdc.getOrDefault(DataKey.GAME_PERIOD.key(), PersistentDataType.INTEGER, GamePeriod.FREE.ordinal());
		return GamePeriod.values()[ordinal];
	}

	private void setRoundCount(int count)
	{
		pdc.set(DataKey.GAME_ROUND_COUNT.key(), PersistentDataType.INTEGER, count);
	}
	public int getRoundCount()
	{
		return pdc.getOrDefault(DataKey.GAME_ROUND_COUNT.key(), PersistentDataType.INTEGER, 0);
	}

	private void setRoundId(String strid)
	{
		pdc.set(DataKey.GAME_ROUND_ID.key(), PersistentDataType.STRING, strid);
	}
	public String getRoundId()
	{
		return pdc.get(DataKey.GAME_ROUND_ID.key(), PersistentDataType.STRING);
	}

	private void setStorytellerUuid(UUID uuid)
	{
		pdc.set(DataKey.GAME_STORYTELLER_UUID.key(), UUIDDataType.INSTANCE, uuid);
	}
	void clearStorytellerUuid()
	{
		pdc.remove(DataKey.GAME_STORYTELLER_UUID.key());
	}
	public UUID getStorytellerUuid()
	{
		return pdc.get(DataKey.GAME_STORYTELLER_UUID.key(), UUIDDataType.INSTANCE);
	}

	public void setVoteStep(GameVoteStep voteStep)
	{
		pdc.set(DataKey.GAME_VOTE_STEP.key(), PersistentDataType.INTEGER, voteStep.ordinal());
	}
	public void clearVoteStep()
	{
		pdc.remove(DataKey.GAME_VOTE_STEP.key());
	}
	public GameVoteStep getVoteStep()
	{
		int ordinal = pdc.getOrDefault(DataKey.GAME_VOTE_STEP.key(), PersistentDataType.INTEGER, GameVoteStep.NOTHING.ordinal());
		return GameVoteStep.values()[ordinal];
	}

	private void setVoteNominatedUuid(UUID uuid)
	{
		pdc.set(DataKey.GAME_VOTE_NOMINATED_UUID.key(), UUIDDataType.INSTANCE, uuid);
	}
	private void clearVoteNominatedUuid()
	{
		pdc.remove(DataKey.GAME_VOTE_NOMINATED_UUID.key());
	}
	public UUID getVoteNominatedUuid()
	{
		return pdc.get(DataKey.GAME_VOTE_NOMINATED_UUID.key(), UUIDDataType.INSTANCE);
	}

	private void setVotePyloriUuid(UUID uuid)
	{
		pdc.set(DataKey.GAME_VOTE_PYLORI_UUID.key(), UUIDDataType.INSTANCE, uuid);
	}
	private void clearVotePyloriUuid()
	{
		pdc.remove(DataKey.GAME_VOTE_PYLORI_UUID.key());
	}
	private UUID getVotePyloriUuid()
	{
		return pdc.get(DataKey.GAME_VOTE_PYLORI_UUID.key(), UUIDDataType.INSTANCE);
	}

	private void setVotePyloriAgainst(int count)
	{
		pdc.set(DataKey.GAME_VOTE_PYLORI_AGAINST.key(), PersistentDataType.INTEGER, count);
	}
	private void clearVotePyloriAgainst()
	{
		pdc.remove(DataKey.GAME_VOTE_PYLORI_AGAINST.key());
	}
	public int getVotePyloriAgainst()
	{
		return pdc.getOrDefault(DataKey.GAME_VOTE_PYLORI_AGAINST.key(), PersistentDataType.INTEGER, 0);
	}

	private void setSlotsUuid(List<UUID> uuids)
	{
		pdc.set(DataKey.GAME_SLOTS_UUID.key(), PersistentDataType.LIST.listTypeFrom(UUIDDataType.INSTANCE), uuids);
	}
	void clearSlotsUuid()
	{
		pdc.remove(DataKey.GAME_SLOTS_UUID.key());
	}
	public List<UUID> getSlotsUuid()
	{
		return pdc.getOrDefault(DataKey.GAME_SLOTS_UUID.key(), PersistentDataType.LIST.listTypeFrom(UUIDDataType.INSTANCE), List.of());
	}

	private void setSlotsPdc(List<PersistentDataContainer> uuids)
	{
		pdc.set(DataKey.GAME_SLOTS_PDC.key(), PersistentDataType.LIST.dataContainers(), uuids);
	}
	void clearSlotsPdc()
	{
		pdc.remove(DataKey.GAME_SLOTS_PDC.key());
	}
	private List<PersistentDataContainer> getSlotsPdc()
	{
		return pdc.getOrDefault(DataKey.GAME_SLOTS_PDC.key(), PersistentDataType.LIST.dataContainers(), List.of());
	}

	public void setPosition(GamePlace place, Location pos)
	{
		pdc.set(DataKey.GAME_LOC.get(place).key(), PersistentDataType.INTEGER_ARRAY, new int[] {pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()});
	}
	public Location getPosition(GamePlace place)
	{
		int[] posArray = pdc.get(DataKey.GAME_LOC.get(place).key(), PersistentDataType.INTEGER_ARRAY);
		if (posArray == null || posArray.length != 3) return null;
		return new Location(world, posArray[0], posArray[1], posArray[2]);
	}

	private void setSettingsSlotLimit(int count)
	{
		pdc.set(DataKey.GAME_SETTINGS_SLOT_LIMIT.key(), PersistentDataType.INTEGER, count);
	}
	public int getSettingsSlotLimit()
	{
		return pdc.getOrDefault(DataKey.GAME_SETTINGS_SLOT_LIMIT.key(), PersistentDataType.INTEGER, 15);
	}

	// states & time
	public boolean isStarted()
	{
		return getState() == GameState.INGAME;
	}
	public boolean isEnded()
	{
		return getState() == GameState.ENDED;
	}
	public boolean isReady()
	{
		GameState state = getState();
		return (state == GameState.WAITING || state == GameState.INGAME);
	}
	public boolean isVoteMoment() { return (getTime() == GamePeriod.MEET); }

	// players
	private int findPlayerIndex(UUID playerUuid)
	{
		List<UUID> uuids = getSlotsUuid();
		for (int i = 0; i < uuids.size(); i++)
		{
			UUID loopUuid = uuids.get(i);
			if (loopUuid != null && Objects.equals(loopUuid, playerUuid))
			{
				return i;
			}
		}
		return -1;
	}

	public Player getPlayerAtIndex(int index)
	{
		UUID uuid = getSlotsUuid().get(index);
		if (uuid == null) return null;
		return Bukkit.getPlayer(uuid);
	}

	public List<UUID> getPlayersUuid()
	{
		return getSlotsUuid().stream().filter(uuid -> uuid != null).toList();
	}

	public List<Integer> getEmptySlotsIndex(List<UUID> slots)
	{
		List<Integer> indexes = new ArrayList<>();
		for (int i = 0; i < slots.size(); i++)
		{
			if (slots.get(i) == null) indexes.add(i);
		}
		return indexes;
	}

	public List<Player> getAllPlayers()
	{
		return getPlayersUuid().stream().map(Bukkit::getPlayer).toList();
	}

	public List<OfflinePlayer> getAllPlayersAsOffline()
	{
		return getPlayersUuid().stream().map(Bukkit::getOfflinePlayer).toList();
	}

	public List<BloodPlayer> getAllBloodPlayers()
	{
		return getAllPlayers().stream().map(BloodPlayer::get).toList();
	}

	public int getAliveCitizenCount()
	{
		return getAllBloodPlayers().stream().filter(bp -> bp.isAlive() && !bp.isTraveller()).toList().size();
	}

	public int getPyloriMajority(int aliveCitizenCount)
	{
		if (getVotePyloriUuid() != null) return getVotePyloriAgainst();
		return Math.ceilDiv(aliveCitizenCount, 2);
	}

	public void addPlayer(Player player)
	{
		UUID uuid = player.getUniqueId();
		// check if player already exist
		if (isUuidIn(uuid)) throw new RuntimeException("trying to add a player in a game where he already is");
		// fill it to the uuid list
		ArrayList<UUID> slotsUuid = new ArrayList<>(getSlotsUuid());
		List<Integer> emptySlotsIndex = getEmptySlotsIndex(slotsUuid);
		if (emptySlotsIndex.isEmpty()) throw new RuntimeException("trying to add a player in a game with no available slot (check if the limit is well set and refresh)");
		int slotIndex = emptySlotsIndex.getFirst();
		slotsUuid.set(slotIndex, uuid);
		setSlotsUuid(slotsUuid);
		// team
		getTeam().addPlayer(player);
		// blood player object join
		// must be done after game add player
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.joinGame(this, slotIndex);
	}

	public void removePlayer(OfflinePlayer offlinePlayer)
	{
		UUID uuid = offlinePlayer.getUniqueId();
		// clear special uuid if in it
		if (Objects.equals(getVoteNominatedUuid(), uuid)) removeNominatedPlayer();
		if (Objects.equals(getVotePyloriUuid(), uuid)) removePyloriPlayer();
		if (Objects.equals(getStorytellerUuid(), uuid)) clearStorytellerUuid();
		// blood player object quit if online
		Player player = offlinePlayer.getPlayer();
		if (player != null) {// must be done before game remove player
			BloodPlayer bloodPlayer = BloodPlayer.get(player);
			boolean wasSpectator = bloodPlayer.isSpectator();
			bloodPlayer.quitGameFinalStep();
			// spectators are not in game uuid
			if (wasSpectator) return;
		}
		// player team
		getTeam().removePlayer(offlinePlayer);
		// set null from the uuid list
		ArrayList<UUID> slotsUuid = new ArrayList<>(getSlotsUuid());
		boolean removed = false;
		for (int i = 0; i < slotsUuid.size(); i++)
		{
			if (Objects.equals(slotsUuid.get(i), uuid))
			{
				slotsUuid.set(i, null);
				removed = true;
				break;
			}
		}
		// check
		if (!removed) throw new RuntimeException("trying to remove an offline player in a game where he is not");
		// really removing from the uuid list
		setSlotsUuid(slotsUuid);
	}

	public boolean isUuidIn(UUID uuid)
	{
		return (findPlayerIndex(uuid) != -1);
	}

	public boolean isPlayerIn(Player player)
	{
		return isUuidIn(player.getUniqueId());
	}

	public void addSpectator(Player player)
	{
		// blood player object spectator join
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.joinGame(this, false);
	}

	public void changeStoryteller(Player player)
	{
		Player lastStoryteller = getStoryteller();
		if (lastStoryteller != null)  removePlayer(lastStoryteller);

		setStorytellerUuid(player.getUniqueId());

		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.joinGame(this, true);
	}

	public Player getStoryteller()
	{
		UUID uuid = getStorytellerUuid();
		if (uuid == null) return null;
		return Bukkit.getPlayer(uuid);
	}

	void applyColorGlowingToOne(Player player, NamedTextColor color)
	{
		getAllPlayers().forEach(loopPlayer -> loopPlayer.removePotionEffect(PotionEffectType.GLOWING));
		getTeam().color(color);
		player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, -1, 9, true, false, true));
	}

	public void changePyloriPlayer(Player player, int votesAgainst)
	{
		UUID uuid = player.getUniqueId();
		if (!isUuidIn(uuid)) throw new RuntimeException("trying to put a player on pylori that is not in game");

		removePyloriPlayer();
		if (Objects.equals(getVoteNominatedUuid(), uuid)) removeNominatedPlayer();

		applyColorGlowingToOne(player, PYLORI_TEAM_COLOR);
		setVotePyloriAgainst(votesAgainst);
		setVotePyloriUuid(uuid);
	}
	public void removePyloriPlayer()
	{
		Player lastPlayer = getPyloriPlayer();
		if (lastPlayer == null) return;
		lastPlayer.removePotionEffect(PotionEffectType.GLOWING);

		Player nominatePlayer = getNominatedPlayer();
		if (nominatePlayer != null) applyColorGlowingToOne(nominatePlayer, NOMINATE_TEAM_COLOR);

		clearVotePyloriAgainst();
		clearVotePyloriUuid();
	}
	public Player getPyloriPlayer()
	{
		UUID uuid = getVotePyloriUuid();
		if (uuid == null) return null;
		return Bukkit.getPlayer(uuid);
	}

	public void changeNominatedPlayer(Player player)
	{
		UUID uuid = player.getUniqueId();
		if (!isUuidIn(uuid)) throw new RuntimeException("trying to nominated that is not in game");

		removeNominatedPlayer();
		if (Objects.equals(getVotePyloriUuid(), uuid)) removePyloriPlayer();

		applyColorGlowingToOne(player, NOMINATE_TEAM_COLOR);
		mutateSlots(BloodSlot::unlock);
		setVoteNominatedUuid(uuid);
	}
	public void removeNominatedPlayer()
	{
		Player lastPlayer = getNominatedPlayer();
		if (lastPlayer == null) return;
		lastPlayer.removePotionEffect(PotionEffectType.GLOWING);

		Player pyloriPlayer = getPyloriPlayer();
		if (pyloriPlayer != null) applyColorGlowingToOne(pyloriPlayer, PYLORI_TEAM_COLOR);

		clearVoteNominatedUuid();
	}
	public Player getNominatedPlayer()
	{
		UUID uuid = getVoteNominatedUuid();
		if (uuid == null) return null;
		return Bukkit.getPlayer(uuid);
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
	public void setSlots(List<BloodSlot> slots)
	{
		setSlotsPdc(slots.stream().map(slot -> slot.pdc).toList());
	}

	public void mutateSlots(Consumer<BloodSlot> action)
	{
		List<BloodSlot> slots = getSlots();
		slots.forEach(action);
		setSlots(slots);
	}
	public void mutateSlot(int index, Consumer<BloodSlot> action)
	{
		BloodSlot slot = getSlot(index);
		action.accept(slot);
		setSlot(index, slot);
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

		if (slotSize < slotLimit)
		{
			ArrayList<UUID> slotsUuid = new ArrayList<>(getSlotsUuid());
			for (int repeat = 0; repeat < slotLimit - slotSize; repeat++)
			{
				slotsUuid.add(null);
			}
			List<UUID> list = slotsUuid.stream().toList();
			setSlotsUuid(list);
		}
		else if (slotSize > slotLimit)
		{
			ArrayList<UUID> slotsUuid = new ArrayList<>(getSlotsUuid());
			for (int repeat = 0; repeat < slotSize - slotLimit; repeat++)
			{
				UUID lastUuid = slotsUuid.getLast();
				slotsUuid.removeLast();
				if (lastUuid == null) continue;

				Player player = Bukkit.getPlayer(lastUuid);
				if (player == null || !player.isOnline()) continue;
				BloodPlayer bloodPlayer = BloodPlayer.get(player);
				bloodPlayer.quitGameFinalStep();
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
			if (Objects.equals(slot.getPosition(SlotPlace.LEVER), loc)) return true;
		}
		return false;
	}

	// teams
	void generateNewId()
	{
		int incrementedRoundCount = getRoundCount() + 1;
		setRoundCount(incrementedRoundCount);
		setRoundId("blood-%s-%s".formatted(world.getName(), Integer.toString(incrementedRoundCount)));
	}

	void setupTeam()
	{
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		String teamId = getRoundId();
		if (teamId == null) throw new RuntimeException("trying to create team but there is no round id");
		Team team = board.registerNewTeam(teamId);
		team.setAllowFriendlyFire(false);
		team.color(NamedTextColor.AQUA);
		team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
		team.setCanSeeFriendlyInvisibles(true);
	}

	Team getTeam()
	{
		String teamId = getRoundId();
		if (teamId == null) throw new RuntimeException("trying to get team but there is no round id");
		return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamId);
	}

	void deleteTeam()
	{
		if (getRoundId() == null) return;
		Team team = getTeam();
		if (team == null) return;
		team.unregister();
	}

	// vote system

	public int countVotes()
	{
		return getAllBloodPlayers().stream().mapToInt(BloodPlayer::getVote).sum();
	}

	public boolean isVoteSystemBusy()
	{
		return (getVoteStep() != GameVoteStep.NOTHING);
	}

	public boolean isVoteProcessCanceled()
	{
		GameVoteStep step = getVoteStep();
		boolean canceled = step != GameVoteStep.VOTE_PROCESS;
		if (!canceled) return false;
		if (step != GameVoteStep.CANCEL_VOTE_PROCESS) return true;
		setVoteStep(GameVoteStep.NOTHING);
		return true;
	}

	public boolean isExecutionProcessCanceled()
	{
		GameVoteStep step = getVoteStep();
		boolean canceled = step != GameVoteStep.EXECUTION_PROCESS;
		if (!canceled) return false;
		if (step != GameVoteStep.CANCEL_EXECUTION_PROCESS) return true;
		setVoteStep(GameVoteStep.NOTHING);
		return true;
	}

	public void startVoteProcess()
	{
		int count = getAliveCitizenCount();
		int majority = getPyloriMajority(count);
		Player nominatedPlayer = getNominatedPlayer();
		BloodPlayer nominatedBloodPlayer = BloodPlayer.get(nominatedPlayer);
		int pyloriSlotIndex = nominatedBloodPlayer.getSlotIndex();

		TagResolver[] resolvers = new TagResolver[]{
			Placeholder.parsed("target", nominatedBloodPlayer.getName()),
			Placeholder.parsed("count", Integer.toString(count)),
			Placeholder.parsed("majority", Integer.toString(majority))
		};

		Runnable startVoteProcessStep4 = () -> {
			if (isVoteProcessCanceled()) return;
			pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.1f);
			Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), slotVoteProcessRunnable(pyloriSlotIndex, pyloriSlotIndex), 20L);
		};
		Runnable startVoteProcessStep3 = () -> {
			if (isVoteProcessCanceled()) return;
			pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.2f);
			Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), startVoteProcessStep4, 20L);
		};
		Runnable startVoteProcessStep2 = () -> {
			if (isVoteProcessCanceled()) return;
			// broadcast("<gold>the vote will start in 3 seconds", resolvers);
			pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.3f);
			Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), startVoteProcessStep3, 20L);
		};
		Runnable startVoteProcessStep1 = () -> {
			if (isVoteProcessCanceled()) return;
			broadcast("<gold>a majority of <majority> votes is required to place <b><target></b> on the pylori", resolvers);
			Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), startVoteProcessStep2, 40L);
		};

		//step 0
		setVoteStep(GameVoteStep.VOTE_PROCESS);
		mutateSlots(BloodSlot::unlock);
		broadcast("<gold>there is <count> players alive", resolvers);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), startVoteProcessStep1, 40L);
	}


	Runnable slotVoteProcessRunnable(int lastIndex, int startIndex)
	{
		return () -> {
			if (isVoteProcessCanceled()) return;
			List<BloodSlot> slots = getSlots();

			int actualIndex = lastIndex + 1;
			if (actualIndex >= slots.size()) actualIndex = 0;

			BloodSlot slot = slots.get(actualIndex);
			slot.lock();
			setSlot(actualIndex, slot);

			if (actualIndex == startIndex)
			{
				Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), finishVoteProcess, 60L);
				return;
			}
			Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), slotVoteProcessRunnable(actualIndex, startIndex), 20L);
		};
	}

	Runnable finishVoteProcess = () ->
	{
		int count = getAliveCitizenCount();
		int majority = getPyloriMajority(count);
		int votes = countVotes();
		Player nominatedPlayer = getNominatedPlayer();
		BloodPlayer nominatedBloodPlayer = BloodPlayer.get(nominatedPlayer);
		Player lastPyloriPlayer = getPyloriPlayer();
		boolean hasLastPlayer = (lastPyloriPlayer != null);
		BloodPlayer lastPyloriBloodPlayer = (hasLastPlayer) ?  BloodPlayer.get(lastPyloriPlayer) : null;

		TagResolver[] resolvers = new TagResolver[]{
			Placeholder.parsed("last", (hasLastPlayer) ? lastPyloriBloodPlayer.getName() : ""),
			Placeholder.parsed("target", nominatedBloodPlayer.getName()),
			Placeholder.parsed("count", Integer.toString(count)),
			Placeholder.parsed("majority", Integer.toString(majority)),
			Placeholder.parsed("votes", Integer.toString(votes))
		};

		//step 0
		if (isVoteProcessCanceled()) return;
		pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.4f);
		broadcast("<gold><votes> votes", resolvers);

		//step 2
		Runnable finishRunnableStep2 = () -> {
			if (isVoteProcessCanceled()) return;
			setVoteStep(GameVoteStep.NOTHING);
			mutateSlots(BloodSlot::unlock);
		};

		//step 1
		Runnable finishRunnableStep1;

		if (votes < majority)
			// no/less
			finishRunnableStep1 = () -> {
				if (isVoteProcessCanceled()) return;
				removeNominatedPlayer();
				pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, .9f);
				broadcast((hasLastPlayer)
				? "<gold>this is not enough to replace <red><last></red> on the pylori"
				: "<gold>this is not enough to mount <yellow><target></yellow> on the pylori"
				, resolvers);
				Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), finishRunnableStep2, 60L);
			};

		else if (votes == majority && hasLastPlayer)
			// equality
			finishRunnableStep1 = () -> {
				if (isVoteProcessCanceled()) return;
				removeNominatedPlayer();
				removePyloriPlayer();
				pingSound(Sound.ENTITY_PLAYER_LEVELUP, VOTE_VOLUME, .9f);
				broadcast("<gold><b>EQUALITY!</b> <yellow><last></yellow> steps down from the pylori", resolvers);
				Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), finishRunnableStep2, 60L);
			};

		else
			// place/replace
			finishRunnableStep1 = () -> {
				if (isVoteProcessCanceled()) return;
				removeNominatedPlayer();
				changePyloriPlayer(nominatedPlayer, votes);
				pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 2f);
				broadcast((hasLastPlayer)
				? "<gold>this is enough for <b><red><target></red></b> to replace <yellow><last></yellow> on the pylori"
				: "<gold>this is enough to place <b><red><target></red></b> on the pylori"
				, resolvers);
				Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), finishRunnableStep2, 60L);
			};

		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), finishRunnableStep1, 40L);
	};

	public void cancelVoteProcess(CommandSender sender)
	{
		switch (getVoteStep())
		{
			case GameVoteStep.NOTHING:
			{
				if (getVoteNominatedUuid() != null)
				{
					removeNominatedPlayer();
					sender.sendRichMessage("<aqua>the nomination was <red>canceled</red>.");
				} else if (getVotePyloriUuid() != null) {
					removePyloriPlayer();
					sender.sendRichMessage("<aqua>the pylori was <red>cleared</red>.");
				} else {
					mutateSlots(BloodSlot::unlock);
					sender.sendRichMessage("<aqua>reseting votes pistons.<white> there is nothing else to cancel.");
				}
			} break;

			case GameVoteStep.VOTE_PROCESS:
			{
				removeNominatedPlayer();
				mutateSlots(BloodSlot::unlock);
				setVoteStep(GameVoteStep.CANCEL_VOTE_PROCESS);
				sender.sendRichMessage("<aqua><red>canceling</red> the vote...");
			} break;
			case GameVoteStep.CANCEL_VOTE_PROCESS:
			{
				setVoteStep(GameVoteStep.VOTE_PROCESS);
				sender.sendRichMessage("<aqua>cancel the vote cancel");
			} break;

			case GameVoteStep.EXECUTION_PROCESS:
			{
				setVoteStep(GameVoteStep.CANCEL_EXECUTION_PROCESS);
				sender.sendRichMessage("<aqua><red>canceling</red> the execution...");
			} break;
			case GameVoteStep.CANCEL_EXECUTION_PROCESS:
			{
				setVoteStep(GameVoteStep.EXECUTION_PROCESS);
				sender.sendRichMessage("<aqua>cancel the execution cancel");
			} break;

			case GameVoteStep.PYLORI_MOUNT:
			{
				setVoteStep(GameVoteStep.NOTHING);
				sender.sendRichMessage("<aqua>the mount was <red>canceled</red>.");
			} break;
		}
	}

	public void startExecuteProcess()
	{
		return;
	}

	// runs

	public void doStep(GameStepAction action, CommandSender sender)
	{
		GameAction.step.get(action).accept(this, sender);
	}
	public void doDebug(GameDebugAction action, CommandSender sender)
	{
		GameAction.debug.get(action).accept(this, sender);
	}
	public void switchTime(GamePeriod period, CommandSender sender)
	{
		GameAction.periodEnter.get(period).accept(this, sender);
		setTime(period);
	}

	// utils

	public void broadcast(String richString)
	{
		Bukkit.getServer().broadcast(mini.deserialize(richString));
	}

	public void pingSound(Sound sound, float volume, float pitch)
	{
		for (Player player : world.getPlayers()) {
			Location loc = Objects.requireNonNull(player.getLocation());
			player.playSound(loc, sound, SoundCategory.MASTER, volume, pitch);
		}
	}
	public void pingSound(Sound sound, float pitch)
	{
		pingSound(sound, DEFAULT_VOLUME, pitch);
	}
	public void pingSound(Sound sound)
	{
		pingSound(sound,1f);
	}

	public void broadcast(String richString, final TagResolver... tagResolvers)
	{
		Bukkit.getServer().broadcast(mini.deserialize(richString, tagResolvers));
	}
}