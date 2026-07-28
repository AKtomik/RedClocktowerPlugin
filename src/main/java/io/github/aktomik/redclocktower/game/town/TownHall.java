package io.github.aktomik.redclocktower.game.town;

import io.github.aktomik.redclocktower.DataKey;
import io.github.aktomik.redclocktower.RedClocktower;
import io.github.aktomik.redclocktower.utils.pdc.BlockPos;
import io.github.aktomik.redclocktower.utils.pdc.PositionDataType;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TownHall {
	private final World world;
	private final String townName;
	private final PersistentDataContainer pdc;

	// construct
	private TownHall(World world, String townName, PersistentDataContainer pdc) {
		this.world = world;
		this.townName = townName;
		this.pdc = pdc;
	}

	// access
	public String getTownName() {
		return this.townName;
	}
	public World getWorld() {
		return this.world;
	}
	public PersistentDataContainer getPdc() {
		return this.pdc;
	}
	public int getHash() {
		return this.pdc.hashCode();
	}

	// static town
	private static NamespacedKey townKey(String townName) {
		return new NamespacedKey(RedClocktower.plugin(), "townhall." + townName);
	}

	public static Set<String> getWorldTowns(World world)
	{
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		Set<String> names = new HashSet<>();

		for (NamespacedKey key : worldData.getKeys())
			if (key.getNamespace().equals(RedClocktower.plugin().namespace()) && key.getKey().startsWith("townhall."))
				names.add(key.getKey().substring("townhall.".length()));
		return names;
	}

	@Nullable
	public static TownHall find(World world, String townName) {
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		PersistentDataContainer pdc = worldData.get(townKey(townName), PersistentDataType.TAG_CONTAINER);
		if (pdc == null) return null;
		return new TownHall(world, townName, pdc);
	}

	@Nullable
	public static TownHall create(World world, String townName) {
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		if (worldData.has(townKey(townName))) return null;
		PersistentDataContainer pdc = worldData.getAdapterContext().newPersistentDataContainer();
		pdc.set(DataKey.TOWN_NAME.key(), PersistentDataType.STRING, townName);// only non defaultable field
		worldData.set(townKey(townName), PersistentDataType.TAG_CONTAINER, pdc);
		return new TownHall(world, townName, pdc);
	}

	@Nullable
	public static TownHall clone(TownHall townHall, String townName) {
		PersistentDataContainer worldData = townHall.getWorld().getPersistentDataContainer();
		if (worldData.has(townKey(townName))) return null;
		PersistentDataContainer pdc = worldData.getAdapterContext().newPersistentDataContainer();
		townHall.getPdc().copyTo(pdc, true);
		pdc.set(DataKey.TOWN_NAME.key(), PersistentDataType.STRING, townName);// only non defaultable field
		worldData.set(townKey(townName), PersistentDataType.TAG_CONTAINER, pdc);
		return new TownHall(townHall.getWorld(), townName, pdc);
	}

	public static boolean delete(World world, String townName) {
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		if (!worldData.has(townKey(townName))) return false;
		worldData.remove(townKey(townName));
		return true;
	}

	// data/position
	public void setPosition(TownHallPlace place, Location pos)
	{
		pdc.set(DataKey.TOWN_HALL_POS.get(place).key(), PositionDataType.INSTANCE, new BlockPos(pos));
		save();
	}
	public Location getPosition(TownHallPlace place)
	{
		BlockPos blockPos = pdc.get(DataKey.TOWN_HALL_POS.get(place).key(), PositionDataType.INSTANCE);
		if (blockPos == null) return null;
		return blockPos.toLocation(world);
	}

	// data/settings
	public void setSettingsCanPlayerDrop(boolean bool)
	{
		pdc.set(DataKey.TOWN_HALL_SETTINGS_CAN_PLAYER_DROP.key(), PersistentDataType.BOOLEAN, bool);
		save();
	}
	public boolean getSettingsCanPlayerDrop()
	{
		return pdc.getOrDefault(DataKey.TOWN_HALL_SETTINGS_CAN_PLAYER_DROP.key(), PersistentDataType.BOOLEAN, false);
	}

	// data/chair
	private void setChairsPdc(List<PersistentDataContainer> pdcs)
	{
		pdc.set(DataKey.TOWN_CHAIRS.key(), PersistentDataType.LIST.dataContainers(), pdcs);
		save();
	}
	private void clearChairsPdc()
	{
		pdc.remove(DataKey.TOWN_CHAIRS.key());
	}
	private List<PersistentDataContainer> getChairsPdc()
	{
		return pdc.getOrDefault(DataKey.TOWN_CHAIRS.key(), PersistentDataType.LIST.dataContainers(), List.of());
	}

	public int getChairCount()
	{
		return getChairsPdc().size();
	}

	@Nullable
	public TownChair getChair(int index)
	{
		// TownChair is not mutable
		// you need to save it with saveChair
		return TownChair.get(this, getChairsPdc().get(index));
	}

	public Stream<TownChair> getAllChairs()
	{
		return IntStream.range(0, getChairCount())
		.mapToObj(this::getChair);
	}

	public void saveChair(int index, TownChair townChair)
	{
		List<PersistentDataContainer> slotsPdc = new ArrayList<>(getChairsPdc());
		slotsPdc.set(index, townChair.getPdc());
		setChairsPdc(slotsPdc);
	}

	public void addNewChair()
	{
		List<PersistentDataContainer> slotsPdc = new ArrayList<>(getChairsPdc());
		slotsPdc.add(pdc.getAdapterContext().newPersistentDataContainer());
		setChairsPdc(slotsPdc);
	}
	public void removeLastChair()
	{
		List<PersistentDataContainer> slotsPdc = new ArrayList<>(getChairsPdc());
		slotsPdc.remove(slotsPdc.size() - 1);
		setChairsPdc(slotsPdc);
	}

	// every mutator ends with this
	private void save() {
		world.getPersistentDataContainer().set(townKey(townName), PersistentDataType.TAG_CONTAINER, pdc);
	}

	// player selection
	private static final Map<CommandSender, TownHall> playerSelection = new HashMap<>();

	@Deprecated public static TownHall getSelection(CommandSender sender) {
		return playerSelection.get(sender);
	}

	@Deprecated public static void setSelection(CommandSender sender, TownHall townHall) {
		playerSelection.put(sender, townHall);
	}
}