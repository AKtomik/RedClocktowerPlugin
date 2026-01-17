package io.github.aktomik.redclocktower;

import io.github.aktomik.redclocktower.game.BloodGamePlace;
import io.github.aktomik.redclocktower.game.BloodSlotPlace;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public enum DataKey {

	// define
	GAME_STATE("game_state"),
	GAME_PERIOD("game_period"),

	GAME_ROUND_COUNT("game_round_count"),
	GAME_ROUND_ID("game_round_id"),

	GAME_SLOTS_PDC("game_slots_pdc"),
	GAME_SLOTS_UUID("game_slots_uuid"),
	GAME_STORYTELLER_UUID("game_storyteller_uuid"),

	GAME_LOC_CENTER("game_loc_center"),
	GAME_LOC_SPAWN("game_loc_spawn"),//UNUSED

	GAME_VOTE_NOMINATOR_INDEX("game_vote_nominator_index"),//UNUSED
	GAME_VOTE_NOMINATED_UUID("game_vote_nominated_index"),
	GAME_VOTE_PYLORI_UUID("game_vote_pylori_index"),

	GAME_SETTINGS_SLOT_LIMIT("game_settings_slot_limit"),


	SLOT_NAME("slot_name"),//UNUSED
	SLOT_LOCK("slot_lock"),
	SLOT_LOC_CHAIR("slot_loc_chair"),
	SLOT_LOC_LEVER("slot_loc_lever"),
	SLOT_LOC_LAMP("slot_loc_lamp"),
	SLOT_LOC_HOUSE("slot_loc_house"),


	PLAYER_GAME_WORLD_NAME("player_game_world_name"),
	PLAYER_GAME_ROUND_ID("player_game_round_id"),
	PLAYER_GAME_SLOT_INDEX("player_game_slot_index"),
	PLAYER_GAME_SPECTATOR("player_game_spectator"),
	PLAYER_GAME_STORYTELLER("player_game_storyteller"),

	PLAYER_ALIVE("player_alive"),
	PLAYER_VOTE_TOKEN("player_vote_token"),
	PLAYER_VOTE_PULL("player_vote_pull"),
	PLAYER_TRAVELLER("player_traveller"),

	PLAYER_DISPLAY_NAME("player_display_name");

	// shortcut
	public static Map<BloodGamePlace, DataKey> GAME_LOC = Map.ofEntries(
		Map.entry(BloodGamePlace.CENTER, DataKey.GAME_LOC_CENTER),
		Map.entry(BloodGamePlace.SPAWN, DataKey.GAME_LOC_SPAWN)
	);
	public static Map<BloodSlotPlace, DataKey> SLOT_LOC = Map.ofEntries(
		Map.entry(BloodSlotPlace.CHAIR, DataKey.SLOT_LOC_CHAIR),
		Map.entry(BloodSlotPlace.LEVER, DataKey.SLOT_LOC_LEVER),
		Map.entry(BloodSlotPlace.LAMP, DataKey.SLOT_LOC_LAMP),
		Map.entry(BloodSlotPlace.HOUSE, DataKey.SLOT_LOC_HOUSE)
	);

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
