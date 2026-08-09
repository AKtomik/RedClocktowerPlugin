package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.RedClocktower;
import io.github.aktomik.redclocktower.game.town.TownChair;
import io.github.aktomik.redclocktower.utils.TickSequence;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

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
	Seated nominated = null;
	Seated sentenced = null;

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

	public void setNominated(Seated seated) {
		nominated = seated;
	}

	public void removeNominated() {
		nominated = null;
	}

	public Seated getNominated() {
		return nominated;
	}

	public void setSentenced(Seated seated, int votes) {
		sentenced = seated;
		precedentMajority = votes;
	}

	public void removeSentenced() {
		sentenced = null;
	}

	public Seated getSentenced() {
		return sentenced;
	}

	// vote process
	private boolean checkVoteProcess() {
		if (voteStep == VoteStep.CANCEL) {
			voteStep = VoteStep.NOTHING;
			return true;
		}
		return voteStep == VoteStep.VOTE_PROCESS;
	}

	private record VoteSnapshot(boolean haveEquality, int voteAlive, int voteEquality, int voteMajority) {}

	private VoteSnapshot snapshotVoteState() {
		boolean haveEquality = sentenced != null;
		int voteAlive = (int) getAllSeated().filter(Seated::getAlive).count();
		int voteEquality = haveEquality ? precedentMajority : -1;
		int voteMajority = (precedentMajority != null) ? precedentMajority + 1 : Math.ceilDiv(voteAlive, 2);
		return new VoteSnapshot(haveEquality, voteAlive, voteEquality, voteMajority);
	}

	public void startVoteProcess()
	{
		VoteSnapshot snap = snapshotVoteState();

		int pyloriSlotIndex = nominated.getSlot().getIndex();

		TagResolver resolvers = TagResolver.resolver(
			Placeholder.parsed("target", nominated.getName()),
			Placeholder.parsed("vote_alive", Integer.toString(snap.voteAlive)),
			Placeholder.parsed("vote_alive_s", (snap.voteAlive > 1) ? "s" : ""),
			Placeholder.parsed("vote_majority", Integer.toString(snap.voteMajority)),
			Placeholder.parsed("vote_majority_s", (snap.voteMajority > 1) ? "s" : "")
		);

		setVoteStep(VoteStep.VOTE_PROCESS);
		unlockAll();
		game.broadcast("<gold>there is <vote_alive> player<vote_alive_s> alive", resolvers);
		
		new TickSequence(RedClocktower.plugin(), this::checkVoteProcess)
			.then(40L, () -> {
				game.broadcast("<gold>a majority of <vote_majority> vote<vote_majority_s> is required to place <b><target></b> on the pylori", resolvers);
			})
			.then(40L, () -> game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.3f))
			.then(20L, () -> game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.2f))
			.then(20L, () -> {
				game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.1f);
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
				Placeholder.parsed("target", nominated.getName()),
				Placeholder.parsed("vote_alive", Integer.toString(snap.voteAlive)),
				Placeholder.parsed("vote_alive_s", (snap.voteAlive > 1) ? "s" : ""),
				Placeholder.parsed("vote_majority", Integer.toString(snap.voteMajority)),
				Placeholder.parsed("vote_majority_s", (snap.voteMajority > 1) ? "s" : ""),
				Placeholder.parsed("vote_count", Integer.toString(votes)),
				Placeholder.parsed("vote_count_s", ((votes) > 1) ? "s" : "")
			);

			//step 0
			game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.4f);
			String votesRichString = "<b><gold><vote_count> vote<vote_count_s></b>";
			if (VOTE_BROADCAST_VOTERS)
			{
				if (votes == 0)
					votesRichString += "<gold>. no one voted.";
				else
					votesRichString += "<gold>. player<vote_count_s> who voted:<br><gold>"+String.join(" ", voters.stream().map(Seated::getName).toList());
			}
			game.broadcast(votesRichString, resolvers);

			Runnable runnableExe;

			if (votes >= snap.voteMajority)
				// place/replace
				runnableExe = () -> {
					removeNominated();
					setSentenced(nominated, votes);
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
}
