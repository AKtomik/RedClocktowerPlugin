package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.RedClocktower;
import io.github.aktomik.redclocktower.game.town.TownChair;
import io.github.aktomik.redclocktower.game.town.TownHall;
import io.github.aktomik.redclocktower.oldgame.OldBloodPlayer;
import io.github.aktomik.redclocktower.oldgame.OldBloodSlot;
import io.github.aktomik.redclocktower.oldgame.OldGameVoteStep;
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
	public final Stream<BloodSlot> getSlotsStream()
	{
		return Arrays.stream(slots);
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

	// vote interfaces
	public boolean isExclusionVote() {
		return false;
	}

	// vote process
	public void startVoteProcess()
	{
	}
}
