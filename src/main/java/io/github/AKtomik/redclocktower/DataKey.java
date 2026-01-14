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
	GAME_STORYTELLER_UUID("game_storyteller_uuid"),
	PLAYER_GAME_WORLD_NAME("player_game_world_name"),
	PLAYER_GAME_ROUND_ID("player_game_round_id"),
	PLAYER_GAME_SLOT("player_game_slot"),//UNUSED
	PLAYER_DISPLAY_NAME("player_display_name"),
	PLAYER_ALIVE("player_alive"),
	PLAYER_VOTE_TOKEN("player_vote_token"),
	PLAYER_VOTE_PULL("player_vote_pull");

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
