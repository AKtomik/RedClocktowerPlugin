package io.github.aktomik.redclocktower.game;

import org.bukkit.World;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BloodGame {

	// manage
	private final TownHall townHall;
	private Map<Integer, BloodSlot> slots = new HashMap<>();
	private BloodGame(TownHall townHall) {
		this.townHall = townHall;
	}

	private static final Map<TownHall, BloodGame> townToGameMap = new HashMap<>();
	private static final Map<World, BloodGame> worldToGameMap = new HashMap<>();

	@Nullable
	public static BloodGame get(TownHall townHall) {
		return townToGameMap.get(townHall);
	}

	@Nullable
	public static BloodGame get(World world) {
		return worldToGameMap.get(world);
	}

	public static BloodGame create(TownHall townHall) {
		BloodGame game = new BloodGame(townHall);
		townToGameMap.put(townHall, game);
		worldToGameMap.put(townHall.getWorld(), game);
		return game;
	}

	public void delete() {
		// this will remove the game from statics references so it is not accessible anymore
		// remove it from others references too so it can be garbage collected
		townToGameMap.values().remove(this);
		worldToGameMap.values().remove(this);
	}

	// access
	public TownHall getTownHall() {
		return townHall;
	}

	// slot
	public Set<Integer> getSlotIndexes()
	{
		return slots.keySet();
	}

	public BloodSlot getSlot(int index)
	{
		return slots.get(index);
	}

	public void assignChair(int index)
	{
		TownChair townChair = townHall.getChair(index);
		if (townChair == null) return;
		BloodSlot slot = new BloodSlot(townChair);
		slots.put(index, slot);
	}

	public void assignChair(int index, BloodPlayer player)
	{
		TownChair townChair = townHall.getChair(index);
		if (townChair == null) return;
		BloodSlot slot = new BloodSlot(townChair, player);
		slots.put(index, slot);
	}

	public void emptyChair(int index)
	{
		slots.remove(index);
	}
}
