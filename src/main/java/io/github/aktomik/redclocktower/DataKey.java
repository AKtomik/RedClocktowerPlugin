package io.github.aktomik.redclocktower;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public enum DataKey {

	// define
	WORLD_TOWNS("world_towns"),
	TOWN_NAME("town_name"),
	TOWN_SETTINGS("town_settings"),
	TOWN_SEATS("town_seats");

	// system
	final String path;
	public NamespacedKey key() {
		return key;
	}
	private NamespacedKey key;

	DataKey(String path) {
		this.path = path;
	}

	public static void init(JavaPlugin plugin) {
		for (DataKey dk : values()) {
			dk.key = new NamespacedKey(plugin, dk.path);
		}
	}
}
