package io.github.AKtomik.redclocktower;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public enum DataKey {

	// define
	GAME_PERIOD("game_period"),
	GAME_STATE("game_state"),
	PLAYER_LIFE("player_life");

	// system
	public final String path;
	public NamespacedKey key;

	DataKey(String path) {
		this.path = path;
	}

	public static void init(JavaPlugin plugin) {
		for (DataKey dk : values()) {
			dk.key = new NamespacedKey(plugin, dk.path);
		}
	}
}
