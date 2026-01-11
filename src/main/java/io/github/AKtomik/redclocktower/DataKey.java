package io.github.AKtomik.redclocktower;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public enum DataKey {

	// define
	GAME_STATE("game_state"),
	GAME_PERIOD("game_period"),
	GAME_ROUND_COUNT("game_round_count"),
	GAME_ROUND_ID("game_round_id"),
	GAME_PLAYERS_UUID("game_players_uuid"),
	//PLAYER_ROUND_ID("player_round_id"),
	PLAYER_ALIVE("player_alive");

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
