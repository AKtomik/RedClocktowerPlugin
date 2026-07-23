package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.DataKey;
import io.github.aktomik.redclocktower.RedClocktower;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.Executor;

public class TownHall {
	private final World world;
	private final String townName;
	private PersistentDataContainer pdc;

	// construct
	private TownHall(World world, String townName, PersistentDataContainer pdc) {
		this.world = world;
		this.townName = townName;
		this.pdc = pdc;
	}

	// town save
	private static NamespacedKey townKey(String townName) {
		return new NamespacedKey(RedClocktower.plugin(), "townhall." + townName);
	}

	public static TownHall get(World world, String townName) {
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		PersistentDataContainer pdc = worldData.get(townKey(townName), PersistentDataType.TAG_CONTAINER);
		if (pdc == null) return null;
		return new TownHall(world, townName, pdc);
	}

	public static Set<String> getTownList(World world)
	{
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		Set<String> names = new HashSet<>();

		for (NamespacedKey key : worldData.getKeys())
			if (key.getNamespace().equals(RedClocktower.plugin().namespace()) && key.getKey().startsWith("townhall."))
				names.add(key.getKey().substring("townhall.".length()));
		return names;
	}

	public static TownHall create(World world, String townName) {
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		if (worldData.has(townKey(townName))) return null;
		PersistentDataContainer pdc = worldData.getAdapterContext().newPersistentDataContainer();
		pdc.set(DataKey.TOWN_NAME.key(), PersistentDataType.STRING, townName);// only non defaultable field
		worldData.set(townKey(townName), PersistentDataType.TAG_CONTAINER, pdc);
		return new TownHall(world, townName, pdc);
	}

	public static boolean delete(World world, String townName) {
		PersistentDataContainer worldData = world.getPersistentDataContainer();
		if (!worldData.has(townKey(townName))) return false;
		worldData.remove(townKey(townName));
		return true;
	}

	// every mutator ends with this
	private void save() {
		world.getPersistentDataContainer().set(townKey(townName), PersistentDataType.TAG_CONTAINER, pdc);
	}

	// player selection
	private static Map<CommandSender, TownHall> playerSelection = new HashMap<>();

	public static TownHall getSelection(CommandSender sender) {
		return playerSelection.get(sender);
	}

	public static void setSelection(CommandSender sender, TownHall townHall) {
		playerSelection.put(sender, townHall);
	}

	// access
	public String getTownName() {
		return this.townName;
	}
}