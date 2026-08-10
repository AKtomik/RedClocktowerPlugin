package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.RedClocktower;
import io.github.aktomik.redclocktower.game.town.TownChair;
import io.github.aktomik.redclocktower.game.town.TownHallPlace;
import io.github.aktomik.redclocktower.oldgame.OldBloodPlayer;
import io.github.aktomik.redclocktower.oldgame.OldBloodSlot;
import io.github.aktomik.redclocktower.oldgame.OldGamePlace;
import io.github.aktomik.redclocktower.oldgame.OldGameVoteStep;
import io.github.aktomik.redclocktower.utils.TickSequence;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.aktomik.redclocktower.game.BloodGame.VOTE_BROADCAST_VOTERS;
import static io.github.aktomik.redclocktower.game.BloodGame.VOTE_VOLUME;

public class SlotCircle {

	private final BloodGame game;
	private final BloodSlot[] slots;

	VoteStep voteStep = VoteStep.NOTHING;
	Integer precedentMajority = null;
	@Nullable Seated nominated = null;
	@Nullable Seated sentenced = null;

	SlotCircle(BloodGame game) {
		this.game = game;
		List<TownChair> chairs = game.getTownHall().getAllChairs().toList();
		this.slots = IntStream.range(0, chairs.size())
			.mapToObj(i -> new BloodSlot(game, chairs.get(i), i))
			.toArray(BloodSlot[]::new);
	}

	// global simple interfaces
	private final Stream<BloodSlot> getSlotsStream()
	{
		return Arrays.stream(slots);
	}

	public final List<BloodSlot> getSlotsList()
	{
		return Arrays.stream(slots).toList();
	}

	public final Integer getSlotCount()
	{
		return slots.length;
	}

	public boolean isFull()
	{
		for (BloodSlot slot : slots)
			if (!slot.isOccupied())
				return false;
		return true;
	}

	public void forEachSlots(Consumer<BloodSlot> consumer)
	{
		getSlotsStream().forEach(consumer);
	}

	// global specific interfaces
	public void lockAll()
	{
		forEachSlots(slot -> slot.setVoteLocked(true));
	}

	public void unlockAll()
	{
		forEachSlots(slot -> slot.setVoteLocked(false));
	}

	public void showLabels()
	{
		forEachSlots(slot -> slot.setLabelVisibility(true));
	}

	public void hiddeLabels()
	{
		forEachSlots(slot -> slot.setLabelVisibility(false));
	}

	// individual interfaces
	public BloodSlot getSlot(int index)
	{
		return slots[index];
	}

	public boolean isValidSlot(int index)
	{
		return index >= 0 && index < slots.length;
	}

	public int getFirstEmptySlotIndex() throws RuntimeException {
		for (int i = 0; i < slots.length; i++)
			if (!slots[i].isOccupied())
				return i;
		throw new RuntimeException("getFirstEmptySlotIndex() but the game is full: there is no empty slot");
	}

	// players participants
	public Stream<Seated> getAllSeated() {
		return getSlotsStream().filter(BloodSlot::isOccupied).map(BloodSlot::getSeated);
	}

	public Stream<SeatedPlayer> getAllSeatedPlayers() {
		return getAllSeated().filter(SeatedPlayer.class::isInstance).map(SeatedPlayer.class::cast);
	}

	public Stream<OfflinePlayer> getOfflinePlayers() {
		return getAllSeatedPlayers().map(SeatedPlayer::getBloodPlayer).map(BloodPlayer::getOfflinePlayer);
	}

	public Stream<Player> getOnlinePlayers() {
		return getOfflinePlayers().map(OfflinePlayer::getPlayer).filter(Objects::nonNull);
	}

	// vote session
	private void setVoteStep(VoteStep step) {
		voteStep = step;
	}

	public boolean isVoteSystemBusy() {
		return voteStep != VoteStep.NOTHING;
	}

	public boolean isExclusionVote() {
		return false;
	}

	public void cleanVoteSession() {
		setVoteStep(VoteStep.NOTHING);
		removeNominated();
		removeSentenced();
		precedentMajority = null;
	}

	public int getVoteAlive() {
		return (int)getAllSeated().filter(Seated::getAlive).count();
	}

	public int getVoteMajority() {
		return (precedentMajority != null) ? precedentMajority + 1 : Math.ceilDiv(getVoteAlive(), 2);
	}

	public int getVoteEquality() {
		return (sentenced != null) ? precedentMajority : -1;
	}

	public void setNominated(Seated seated) {
		nominated = seated;
		nominated.setNominated(true);
	}

	public void removeNominated() {
		if (nominated == null) return;
		nominated.setNominated(false);
		nominated = null;
	}

	@Nullable
	public Seated getNominated() {
		return nominated;
	}

	public void setSentenced(Seated seated, int votes) {
		sentenced = seated;
		sentenced.setSentenced(true);
		precedentMajority = votes;
	}

	public void removeSentenced() {
		if (sentenced == null) return;
		sentenced.setSentenced(false);
		sentenced = null;
	}

	@Nullable
	public Seated getSentenced() {
		return sentenced;
	}

	@Nullable
	public static Player extractOnlinePlayer(Seated seated) {
		if (!(seated instanceof SeatedPlayer seatedPlayer)) return null;
		return seatedPlayer.getBloodPlayer().getOnlinePlayer();
	}

	// vote process
	private boolean checkVoteProcess() {
		if (voteStep == VoteStep.CANCEL) {
			voteStep = VoteStep.NOTHING;
			return true;
		}
		return voteStep == VoteStep.VOTE_PROCESS;
	}

	private record VoteSnapshot(int voteAlive, int voteMajority, boolean haveEquality, int voteEquality) {}

	private VoteSnapshot snapshotVoteState() {
		int voteAlive = getVoteAlive();
		int voteEquality = getVoteEquality();
		int voteMajority = getVoteMajority();
		return new VoteSnapshot(voteAlive, voteMajority, voteEquality != -1, voteEquality);
	}

	private TagResolver snapshotResolver(VoteSnapshot snap) {
		return TagResolver.resolver(
			Placeholder.parsed("target", (nominated != null) ? nominated.getName() : "<none>"),
			Placeholder.parsed("last", (sentenced != null) ? sentenced.getName() : "<none>"),
			Placeholder.parsed("vote_alive", Integer.toString(snap.voteAlive)),
			Placeholder.parsed("vote_alive_s", (snap.voteAlive > 1) ? "s" : ""),
			Placeholder.parsed("vote_majority", Integer.toString(snap.voteMajority)),
			Placeholder.parsed("vote_majority_s", (snap.voteMajority > 1) ? "s" : ""),
			Placeholder.parsed("vote_equality", Integer.toString(snap.voteEquality)),
			Placeholder.parsed("vote_equality_s", (snap.voteEquality > 1) ? "s" : "")
		);
	}

	public void startVoteProcess()
	{
		VoteSnapshot snap = snapshotVoteState();
		TagResolver resolvers = snapshotResolver(snap);

		setVoteStep(VoteStep.VOTE_PROCESS);
		unlockAll();
		game.broadcast("<gold>there is <vote_alive> player<vote_alive_s> alive", resolvers);
		
		new TickSequence(RedClocktower.plugin(), this::checkVoteProcess)
			.then(40L, () -> {
				String richString = (snap.haveEquality)
				?   "<gold><vote_equality> vote<vote_equality_s> are needed to remove <red><last></red> from the pylori<br>" +
					"<gold>and <vote_majority> vote<vote_majority_s> are required to place <b><target></b> instead"
				: "<gold>a majority of <vote_majority> vote<vote_majority_s> is required to place <b><target></b> on the pylori";
				game.broadcast(richString, resolvers);
			})
			.then(40L, () -> game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.3f))
			.then(20L, () -> game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.2f))
			.then(20L, () -> {
				game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.1f);
				int pyloriSlotIndex = nominated.getSlot().getIndex();
				new TickSequence(RedClocktower.plugin(), this::checkVoteProcess)
					.then(20L, slotVoteProcessRunnable(pyloriSlotIndex, pyloriSlotIndex)).run();
			})
			.run();
	}

	private Runnable slotVoteProcessRunnable(int lastIndex, int startIndex)
	{
		return () -> {
			int currentIndex = lastIndex + 1;
			if (currentIndex >= slots.length) currentIndex = 0;

			BloodSlot slot = slots[currentIndex];
			slot.lock();

			if (currentIndex == startIndex)
				new TickSequence(RedClocktower.plugin(), this::checkVoteProcess)
					.then(20L, finishVoteProcess()).run();
			else
				new TickSequence(RedClocktower.plugin(), this::checkVoteProcess)
					.then(20L, slotVoteProcessRunnable(currentIndex, startIndex)).run();
		};
	}

	private Runnable finishVoteProcess() {
		return () -> {
			VoteSnapshot snap = snapshotVoteState();

			// count & power & use token
			List<Seated> voters = getAllSeated().filter(Seated::getVotePull).toList();
			int votes = getAllSeated().mapToInt(Seated::useVote).sum();

			TagResolver resolvers = TagResolver.resolver(
			snapshotResolver(snap),
			Placeholder.parsed("vote_count", Integer.toString(votes)),
			Placeholder.parsed("vote_count_s", (votes > 1) ? "s" : "")
			);

			//step 0
			game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.4f);
			String votesRichString = "<b><gold><vote_count> vote<vote_count_s></b>";
			if (VOTE_BROADCAST_VOTERS) {
				if (votes == 0)
					votesRichString += "<gold>. no one voted.";
				else
					votesRichString += "<gold>. player<vote_count_s> who voted:<br><gold>" + String.join(" ", voters.stream().map(Seated::getName).toList());
			}
			game.broadcast(votesRichString, resolvers);

			Runnable runnableExe;

			if (votes >= snap.voteMajority)
				// place/replace
				runnableExe = () -> {
					Seated seated = nominated;
					removeNominated();
					setSentenced(seated, votes);
					game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 2f);
					game.broadcast((snap.haveEquality)
					? "<gold>this is enough for <b><red><target></red></b> to replace <yellow><last></yellow> on the pylori"
					: "<gold>this is enough to place <b><red><target></red></b> on the pylori"
					, resolvers);
				};

			else if (votes == snap.voteEquality)
				// equality
				runnableExe = () -> {
					removeNominated();
					removeSentenced();
					game.pingSound(Sound.ENTITY_PLAYER_LEVELUP, VOTE_VOLUME, .9f);
					game.broadcast("<gold><b>EQUALITY!</b> <b><yellow><last></yellow></b> steps down from the pylori", resolvers);
				};

			else
				// no/less
				runnableExe = () -> {
					removeNominated();
					game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, .9f);
					game.broadcast((snap.haveEquality)
					? "<gold>this is not enough to replace <red><last></red> on the pylori"
					: "<gold>this is not enough to mount <yellow><target></yellow> on the pylori"
					, resolvers);
				};

			new TickSequence(RedClocktower.plugin(), this::checkVoteProcess)
			.then(40L, runnableExe)
			.then(60L, () -> {
				setVoteStep(VoteStep.NOTHING);
				//changeExclusionMode(false);
				unlockAll();
			})
			.run();
		};
	}

	// execution process
	private boolean checkExecutionProcess() {
		if (voteStep == VoteStep.CANCEL) {
			voteStep = VoteStep.NOTHING;
			return true;
		}
		return voteStep == VoteStep.EXECUTION_PROCESS;
	}

	public void mountBeforeExecution()
	{
		if (sentenced == null) return;
		Player player = extractOnlinePlayer(sentenced);
		if (player == null) return;
		Location location = game.getTownHall().getPosition(TownHallPlace.PYLORI).toCenterLocation();
		Location lastLocation = Objects.requireNonNull(player.getLocation());
		if (location.distance(lastLocation) < .2) return;
		player.teleport(location);
	}

	public void startExecuteProcess(boolean reallyDies)
	{
		Seated executedSeated = sentenced;
		Player executedPlayer = extractOnlinePlayer(executedSeated);

		TagResolver resolvers = Placeholder.parsed("target", executedSeated.getName());

		setVoteStep(VoteStep.EXECUTION_PROCESS);
		mountBeforeExecution();
		removeSentenced();
		game.broadcast("<red><b><target></b> is executed", resolvers);

		Location location = game.getTownHall().getPosition(TownHallPlace.PYLORI).toCenterLocation();

		World world = game.getTownHall().getWorld();
		Location honeyLocation = game.getTownHall().getPosition(TownHallPlace.PYLORI).add(new Vector(0, -1, 0));
		honeyLocation.setY(honeyLocation.getY() - 1);
		BlockData beforeHoney = world.getBlockData(honeyLocation);
		world.setBlockData(honeyLocation, BlockType.HONEY_BLOCK.createBlockData());

		location.setY(location.getY() + 60);
		world.spawn(location, FallingBlock.class, falling -> {
			falling.setBlockData(BlockType.ANVIL.createBlockData());
			falling.setDropItem(false);
			falling.setCancelDrop(true);
			falling.setHurtEntities(true);
			falling.setDamagePerBlock(999);
			falling.setFallDistance(999);
		});

		if (!reallyDies && executedPlayer != null)
			executedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 9, false, false, false));

		new TickSequence(RedClocktower.plugin(), this::checkExecutionProcess)
		.then(66L, () -> {
			executedSeated.setAlive(false);
			if (executedPlayer != null)
				executedPlayer.setHealth(0);
		})
		.then(10L, () -> {
			world.setBlockData(honeyLocation, beforeHoney);
			setVoteStep(VoteStep.NOTHING);
		})
		.run();
	}
}
