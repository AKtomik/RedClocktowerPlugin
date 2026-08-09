package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.RedClocktower;
import io.github.aktomik.redclocktower.game.town.TownChair;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.aktomik.redclocktower.game.BloodGame.VOTE_VOLUME;

public class SlotCircle {

	private final BloodGame game;
	private final BloodSlot[] slots;
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

	// vote process
	public boolean isExclusionVote() {
		return false;
	}

	private boolean isVoteProcessCanceled() {
		return false;
	}

	// vote session
	Integer precedentMajority = null;
	Seated nominated = null;
	Seated sentenced = null;

	public void cleanVoteSession() {
		precedentMajority = null;
		nominated = null;
		sentenced = null;
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

	public void startVoteProcess()
	{
		boolean haveEquality = sentenced != null;
		int voteAlive = (int)getAllSeated().filter(Seated::getAlive).count();
		int voteEquality = (haveEquality) ? precedentMajority : -1;
		int voteMajority = (precedentMajority != null)
			? precedentMajority + 1 : Math.ceilDiv(voteAlive, 2);

		int pyloriSlotIndex = nominated.getSlot().getIndex();

		TagResolver[] resolvers = new TagResolver[] {
			Placeholder.parsed("target", nominated.getName()),
			Placeholder.parsed("vote_alive", Integer.toString(voteAlive)),
			Placeholder.parsed("vote_majority", Integer.toString(voteMajority))
		};

		Runnable startVoteProcessStep4 = () -> {
			if (isVoteProcessCanceled()) return;
			game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.1f);
			Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), slotVoteProcessRunnable(pyloriSlotIndex, pyloriSlotIndex), 20L);
		};
		Runnable startVoteProcessStep3 = () -> {
			if (isVoteProcessCanceled()) return;
			game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.2f);
			Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), startVoteProcessStep4, 20L);
		};
		Runnable startVoteProcessStep2 = () -> {
			if (isVoteProcessCanceled()) return;
			// game.broadcast("<gold>the vote will start in 3 seconds", resolvers);
			game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.3f);
			Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), startVoteProcessStep3, 20L);
		};
		Runnable startVoteProcessStep1 = () -> {
			if (isVoteProcessCanceled()) return;
			game.broadcast("<gold>a majority of <vote_majority> votes is required to place <b><target></b> on the pylori", resolvers);
			Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), startVoteProcessStep2, 40L);
		};

		//step 0
		//setVoteStep(OldGameVoteStep.VOTE_PROCESS);
		//changeExclusionMode(nominatedBloodPlayer.isTraveller());
		unlockAll();
		game.broadcast("<gold>there is <vote_alive> players alive", resolvers);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), startVoteProcessStep1, 40L);
	}

	private Runnable slotVoteProcessRunnable(int lastIndex, int startIndex)
	{
		return () -> {
			if (isVoteProcessCanceled()) return;
			List<BloodSlot> slots = getSlotsList();

			int currentIndex = lastIndex + 1;
			if (currentIndex >= slots.size()) currentIndex = 0;

			BloodSlot slot = slots.get(currentIndex);
			slot.lock();

			if (currentIndex == startIndex)
			{
				Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), finishVoteProcess(), 60L);
				return;
			}
			Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), slotVoteProcessRunnable(currentIndex, startIndex), 20L);
		};
	}

	private Runnable finishVoteProcess() {
		return () -> {
			boolean haveEquality = sentenced != null;
			int voteAlive = (int)getAllSeated().filter(Seated::getAlive).count();
			int voteEquality = (haveEquality) ? precedentMajority : -1;
			int voteMajority = (precedentMajority != null)
			? precedentMajority + 1 : Math.ceilDiv(voteAlive, 2);

			// count & power & use token
			int votes = getAllSeated().mapToInt(Seated::useVote).sum();

			TagResolver[] resolvers = new TagResolver[]{
			Placeholder.parsed("last", haveEquality ? sentenced.getName() : ""),
			Placeholder.parsed("target", nominated.getName()),
			Placeholder.parsed("vote_alive", Integer.toString(voteAlive)),
			Placeholder.parsed("vote_count", Integer.toString(votes)),
			Placeholder.parsed("vote_majority", Integer.toString(voteMajority))
			};

			//step 0
			if (isVoteProcessCanceled()) return;
			game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 1.4f);
			game.broadcast("<gold><vote_count> votes", resolvers);

			//step 2
			Runnable finishRunnableStep2 = () -> {
				if (isVoteProcessCanceled()) return;
				//setVoteStep(OldGameVoteStep.NOTHING);
				//changeExclusionMode(false);
				unlockAll();
			};

			//step 1
			Runnable runnableStep1;

			if (votes >= voteMajority)
				// place/replace
				runnableStep1 = () -> {
					if (isVoteProcessCanceled()) return;
					removeNominated();
					setSentenced(nominated, votes);
					game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, 2f);
					game.broadcast((haveEquality)
					? "<gold>this is enough for <b><yellow><target></yellow></b> to replace <yellow><last></yellow> on the pylori"
					: "<gold>this is enough to place <b><yellow><target></yellow></b> on the pylori"
					, resolvers);
					Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), finishRunnableStep2, 60L);
				};

			else if (votes == voteEquality)
				// equality
				runnableStep1 = () -> {
					if (isVoteProcessCanceled()) return;
					removeNominated();
					removeSentenced();
					game.pingSound(Sound.ENTITY_PLAYER_LEVELUP, VOTE_VOLUME, .9f);
					game.broadcast("<gold><b>EQUALITY!</b> <b><yellow><last></yellow></b> steps down from the pylori", resolvers);
					Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), finishRunnableStep2, 60L);
				};

			else
				// no/less
				runnableStep1 = () -> {
					if (isVoteProcessCanceled()) return;
					removeNominated();
					game.pingSound(Sound.BLOCK_ANVIL_LAND, VOTE_VOLUME, .9f);
					game.broadcast((haveEquality)
					? "<gold>this is not enough to replace <red><last></red> on the pylori"
					: "<gold>this is not enough to mount <yellow><target></yellow> on the pylori"
					, resolvers);
					Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), finishRunnableStep2, 60L);
				};

			Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), runnableStep1, 40L);
		};
	}
}
