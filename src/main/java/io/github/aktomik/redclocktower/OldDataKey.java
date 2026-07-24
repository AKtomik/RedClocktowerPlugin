package io.github.aktomik.redclocktower;

import io.github.aktomik.redclocktower.oldgame.OldGamePlace;
import io.github.aktomik.redclocktower.oldgame.OldSlotPlace;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public enum OldDataKey {

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
	GAME_LOC_PYLORI("game_loc_pylori"),
	GAME_LOC_BELL("game_loc_bell"),

	GAME_VOTE_STEP("game_vote_step"),
	GAME_VOTE_NOMINATOR_UUID("game_vote_nominator_uuid"),//UNUSED
	GAME_VOTE_NOMINATED_UUID("game_vote_nominated_uuid"),
	GAME_VOTE_PYLORI_UUID("game_vote_pylori_uuid"),
	GAME_VOTE_PYLORI_AGAINST("game_vote_pylori_against"),

	GAME_SETTINGS_SLOT_LIMIT("game_settings_slot_limit"),


	SLOT_NAME("slot_name"),//UNUSED
	SLOT_LOCK("slot_lock"),
	SLOT_EXCLUSION("slot_exclusion"),
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
	public static final Map<OldGamePlace, OldDataKey> GAME_LOC = Map.ofEntries(
		Map.entry(OldGamePlace.CENTER, OldDataKey.GAME_LOC_CENTER),
		Map.entry(OldGamePlace.SPAWN, OldDataKey.GAME_LOC_SPAWN),
		Map.entry(OldGamePlace.PYLORI, OldDataKey.GAME_LOC_PYLORI),
		Map.entry(OldGamePlace.BELL, OldDataKey.GAME_LOC_BELL)
	);
	public static final Map<OldSlotPlace, OldDataKey> SLOT_LOC = Map.ofEntries(
		Map.entry(OldSlotPlace.CHAIR, OldDataKey.SLOT_LOC_CHAIR),
		Map.entry(OldSlotPlace.LEVER, OldDataKey.SLOT_LOC_LEVER),
		Map.entry(OldSlotPlace.LAMP, OldDataKey.SLOT_LOC_LAMP),
		Map.entry(OldSlotPlace.HOUSE, OldDataKey.SLOT_LOC_HOUSE)
	);

	// system
	final String path;
	public NamespacedKey key() {
		return key;
	}
	private NamespacedKey key;

	OldDataKey(String path) {
		this.path = path;
	}

	public static void init(JavaPlugin plugin) {
		for (OldDataKey dk : values()) {
			dk.key = new NamespacedKey(plugin, dk.path);
		}
	}
}
