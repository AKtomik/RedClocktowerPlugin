package io.github.aktomik.redclocktower;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public enum DataKey {

	// define
	GAME_STATE("game_state"),
	GAME_PERIOD("game_period"),

	GAME_ROUND_COUNT("game_round_count"),
	GAME_ROUND_ID("game_round_id"),

	GAME_SLOTS_PDC("game_slots_pdc"),//UNUSED
	GAME_SLOTS_UUID("game_slots_uuid"),
	GAME_STORYTELLER_UUID("game_storyteller_uuid"),

	GAME_LOC_CENTER("game_loc_center"),
	GAME_LOC_SPAWN("game_loc_spawn"),//UNUSED

	GAME_SETTINGS_SLOT_LIMIT("game_settings_slot_limit"),


	PLAYER_GAME_WORLD_NAME("player_game_world_name"),
	PLAYER_GAME_ROUND_ID("player_game_round_id"),
	PLAYER_GAME_SLOT_INDEX("player_game_slot_index"),

	PLAYER_ALIVE("player_alive"),
	PLAYER_VOTE_TOKEN("player_vote_token"),
	PLAYER_VOTE_PULL("player_vote_pull"),

	PLAYER_DISPLAY_NAME("player_display_name");

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
