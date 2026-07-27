package io.github.aktomik.redclocktower.game;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class BloodGame {

	// manage
	private final TownHall townHall;
	private final BloodSlot[] slots;
	private final ArrayList<BloodPlayer> storytellers = new ArrayList<>();
	private boolean started = false;
	private boolean dead = false;
	private BloodGame(TownHall townHall) {
		this.townHall = townHall;
		slots = townHall.getAllChairs().map(BloodSlot::new).toArray(BloodSlot[]::new);
	}

	private static final Map<Integer, BloodGame> townToGameMap = new HashMap<>();
	private static final Map<World, BloodGame> worldToGameMap = new HashMap<>();

	@Nullable
	public static BloodGame get(TownHall townHall) {
		return townToGameMap.get(townHall.getHash());
	}

	@Nullable
	public static BloodGame get(World world) {
		return worldToGameMap.get(world);
	}

	public static BloodGame create(TownHall townHall) {
		BloodGame game = new BloodGame(townHall);
		townToGameMap.put(townHall.getHash(), game);
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

	// storyteller
	public void addStoryteller(BloodPlayer bloodPlayer) {
		bloodPlayer.attachStorytelling(this);
		storytellers.add(bloodPlayer);
	}

	public void removeStoryteller(BloodPlayer bloodPlayer) {
		bloodPlayer.detachStorytelling();
		storytellers.remove(bloodPlayer);
	}

	public Player[] getOnlineStorytellers() {
		return storytellers.stream().map(BloodPlayer::getOffPlayer).filter(OfflinePlayer::isOnline).map(Player.class::cast).toArray(Player[]::new);
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
