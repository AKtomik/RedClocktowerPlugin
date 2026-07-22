package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.DataKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class TownHall {
	private final World world;
	private final PersistentDataContainer pdc;

	// constructor
	private TownHall(World world, PersistentDataContainer pdc)
	{
		this.world = world;
		this.pdc = pdc;
	}

	// town static
	private static Map<String, PersistentDataContainer> getTowns(World world)
	{
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		List<PersistentDataContainer> townsData = worldData.getOrDefault(DataKey.WORLD_TOWNS.key(), PersistentDataType.LIST.dataContainers(), List.of());
		Map<String, PersistentDataContainer> townsMap = new HashMap<>();
		for (PersistentDataContainer loopPdc : townsData)
		{
			String loopName = loopPdc.get(DataKey.TOWN_NAME.key(), PersistentDataType.STRING);
			townsMap.put(loopName, loopPdc);
		}
		return townsMap;
	}

	public static Set<String> getTownNames(World world) { return getTowns(world).keySet(); }

	private static PersistentDataContainer getTown(World world, String townName)
	{
		return getTowns(world).get(townName);
	}

	// townhall static
	public static TownHall get(World world, String townName)
	{
		PersistentDataContainer pdc = getTown(world, townName);
		if (pdc == null) return null;
		return new TownHall(world, pdc);
	}

	public static TownHall create(World world, String townName)
	{
		// extract
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		List<PersistentDataContainer> townsData = worldData.getOrDefault(DataKey.WORLD_TOWNS.key(), PersistentDataType.LIST.dataContainers(), List.of());
		townsData = new ArrayList<>(townsData);
		// create
		PersistentDataContainer pdc = worldData.getAdapterContext().newPersistentDataContainer();
		pdc.set(DataKey.TOWN_NAME.key(), PersistentDataType.STRING, townName);
		// add
		townsData.add(pdc);
		worldData.set(DataKey.WORLD_TOWNS.key(), PersistentDataType.LIST.dataContainers(), townsData);
		// return
		return new TownHall(world, pdc);
	}
}
