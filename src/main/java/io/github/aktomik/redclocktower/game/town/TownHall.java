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
	// fields
	private final World world;
	private final String townName;
	private final PersistentDataContainer pdc;
	public record TownKey(UUID worldId, String townName) {}
	private final TownKey key;

	// avoid creating more than one townhall object by townhall (fixes)
	private static final Map<TownKey, TownHall> townCreatedObjects = new HashMap<>();

	// construct
	private TownHall(World world, String townName, PersistentDataContainer pdc) {
		this.key = new TownKey(world.getUID(), townName);
		if (townCreatedObjects.containsKey(key)) throw new RuntimeException("already a constructed TownHall object in townCreatedObjects");
		townCreatedObjects.put(key, this);
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
	public TownKey getKey() {
		return key;
	}
	public String getStringId() {
		return world.getName()+"-"+townName;
	}
	public String getStringId(String prefix) {
		return prefix + "-" + getStringId();
	}

	// static town
	private static NamespacedKey namespaceKey(String townName) {
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
		// find in objects
		TownKey key = new TownKey(world.getUID(), townName);
		if (townCreatedObjects.containsKey(key))
			return townCreatedObjects.get(key);
		// find in pdc
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		PersistentDataContainer pdc = worldData.get(namespaceKey(townName), PersistentDataType.TAG_CONTAINER);
		if (pdc != null) return new TownHall(world, townName, pdc);
		// not found
		return null;
	}

	@Nullable
	public static TownHall create(World world, String townName) {
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		if (worldData.has(namespaceKey(townName))) return null;
		PersistentDataContainer pdc = worldData.getAdapterContext().newPersistentDataContainer();
		pdc.set(DataKey.TOWN_NAME.key(), PersistentDataType.STRING, townName);// the single non defaultable field
		worldData.set(namespaceKey(townName), PersistentDataType.TAG_CONTAINER, pdc);
		return new TownHall(world, townName, pdc);
	}

	@Nullable
	public static TownHall clone(TownHall originalTownHall, String townName) {
		PersistentDataContainer worldData = originalTownHall.getWorld().getPersistentDataContainer();
		if (worldData.has(namespaceKey(townName))) return null;
		PersistentDataContainer pdc = worldData.getAdapterContext().newPersistentDataContainer();
		originalTownHall.getPdc().copyTo(pdc, true);
		pdc.set(DataKey.TOWN_NAME.key(), PersistentDataType.STRING, townName);// the single non defaultable field
		worldData.set(namespaceKey(townName), PersistentDataType.TAG_CONTAINER, pdc);
		return new TownHall(originalTownHall.getWorld(), townName, pdc);
	}

	public static void delete(World world, String townName) {
		TownHall townHall = find(world, townName);
		if (townHall == null) return;
		townCreatedObjects.remove(townHall.key);
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		worldData.remove(namespaceKey(townName));
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
	public void setSettingsCanPlayerDropMisc(boolean bool)
	{
		pdc.set(DataKey.TOWN_HALL_SETTINGS_CAN_PLAYER_DROP_MISC.key(), PersistentDataType.BOOLEAN, bool);
		save();
	}
	public boolean getSettingsCanPlayerDropMisc()
	{
		return pdc.getOrDefault(DataKey.TOWN_HALL_SETTINGS_CAN_PLAYER_DROP_MISC.key(), PersistentDataType.BOOLEAN, true);
	}

	public void setSettingsCanPlayerDropInfo(boolean bool)
	{
		pdc.set(DataKey.TOWN_HALL_SETTINGS_CAN_PLAYER_DROP_INFO.key(), PersistentDataType.BOOLEAN, bool);
		save();
	}
	public boolean getSettingsCanPlayerDropInfo()
	{
		return pdc.getOrDefault(DataKey.TOWN_HALL_SETTINGS_CAN_PLAYER_DROP_INFO.key(), PersistentDataType.BOOLEAN, false);
	}

	public void setSettingsCanPlayerOpenChest(boolean bool)
	{
		pdc.set(DataKey.TOWN_HALL_SETTINGS_CAN_PLAYER_OPEN_CHEST.key(), PersistentDataType.BOOLEAN, bool);
		save();
	}
	public boolean getSettingsCanPlayerOpenChest()
	{
		return pdc.getOrDefault(DataKey.TOWN_HALL_SETTINGS_CAN_PLAYER_OPEN_CHEST.key(), PersistentDataType.BOOLEAN, true);
	}

	public void setSettingsCanPlayerPullOthersLever(boolean bool)
	{
		pdc.set(DataKey.TOWN_HALL_SETTINGS_CAN_PLAYER_PULL_OTHERS_LEVER.key(), PersistentDataType.BOOLEAN, bool);
		save();
	}
	public boolean getSettingsCanPlayerPullOthersLever()
	{
		return pdc.getOrDefault(DataKey.TOWN_HALL_SETTINGS_CAN_PLAYER_PULL_OTHERS_LEVER.key(), PersistentDataType.BOOLEAN, false);
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
		world.getPersistentDataContainer().set(namespaceKey(townName), PersistentDataType.TAG_CONTAINER, pdc);
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