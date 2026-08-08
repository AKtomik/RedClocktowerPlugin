package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.game.town.TownChair;
import io.github.aktomik.redclocktower.game.town.TownHall;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SlotCircle {

	private final TownHall townHall;
	private final BloodSlot[] slots;
	SlotCircle(BloodGame game, TownHall townHall) {
		this.townHall = townHall;
		List<TownChair> chairs = townHall.getAllChairs().toList();
		this.slots = IntStream.range(0, chairs.size())
			.mapToObj(i -> new BloodSlot(game, chairs.get(i), i))
			.toArray(BloodSlot[]::new);
		// game is not saved here and that cool
	}

	// global simple interfaces
	public List<BloodSlot> getSlotsList()
	{
		return Arrays.stream(slots).toList();
	}

	public Stream<BloodSlot> getSlotsStream()
	{
		return Arrays.stream(slots);
	}

	public Integer getSlotCount()
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

	// vote interfaces
	public boolean isExclusionVote() {
		return false;
	}
}
