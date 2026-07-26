package io.github.aktomik.redclocktower.game;

import org.bukkit.World;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class BloodGame {

	// manage
	private final TownHall townHall;
	private final BloodSlot[] slots;
	private boolean started = false;
	private boolean dead = false;
	private BloodGame(TownHall townHall) {
		this.townHall = townHall;
		slots = townHall.getAllChairs().map(BloodSlot::new).toArray(BloodSlot[]::new);
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

	public void kill() {
		// this will remove the game from statics references so it is not accessible anymore
		// remove it from others references too so it can be garbage collected
		townToGameMap.values().remove(this);
		worldToGameMap.values().remove(this);
		dead = true;
	}

	// relations
	public TownHall getTownHall() {
		return townHall;
	}

	public Stream<Seated> getAllSeated() {
		return Arrays.stream(slots).filter(BloodSlot::isOccupied).map(BloodSlot::getSeated);
	}

	// slot
	public Integer getSlotCount()
	{
		return slots.length;
	}

	public boolean isValidSlot(int index)
	{
		return index >= 0 && index < slots.length;
	}

	public BloodSlot getSlot(int index)
	{
		return slots[index];
	}

	public void assignSlot(int index, Seated seated)
	{
		getSlot(index).assign(seated);
	}

	public void emptySlot(int index)
	{
		getSlot(index).empty();
	}

	// state
	public void start() {
		this.started = true;
		// put back all seated to life
	}

	public void finish(GameTeam winTeam) {
		this.started = false;
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isDead() {
		return dead;
	}
}
