package io.github.aktomik.redclocktower;

import io.github.aktomik.redclocktower.command.setup.TownChairPlace;
import io.github.aktomik.redclocktower.command.setup.TownGeneralSettings;
import io.github.aktomik.redclocktower.command.setup.TownHallPlace;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public enum DataKey {

	// define
	TOWN_NAME,
	TOWN_SETTINGS,
	TOWN_SEATS,

	TOWN_HALL_LOC_CENTER,
	TOWN_HALL_LOC_SPAWN,
	TOWN_HALL_LOC_PYLORI,
	TOWN_HALL_LOC_BELL,

	TOWN_HALL_SETTINGS_CAN_PLAYER_DROP,
	TOWN_HALL_SETTINGS_CLOCK_TICK_SPEED,
	TOWN_HALL_SETTINGS_DO_EXECUTION_REALLY_KILL,

	TOWN_CHAIR_LOC_CHAIR,
	TOWN_CHAIR_LOC_LEVER,
	TOWN_CHAIR_LOC_LAMP,
	TOWN_CHAIR_LOC_HOUSE,
	;

	// shortcut
	public static final Map<TownHallPlace, DataKey> TOWN_HALL_LOC = Map.ofEntries(
		Map.entry(TownHallPlace.CENTER, DataKey.TOWN_HALL_LOC_CENTER),
		Map.entry(TownHallPlace.SPAWN, DataKey.TOWN_HALL_LOC_SPAWN),
		Map.entry(TownHallPlace.PYLORI, DataKey.TOWN_HALL_LOC_PYLORI),
		Map.entry(TownHallPlace.BELL, DataKey.TOWN_HALL_LOC_BELL)
	);
	public static final Map<TownGeneralSettings, DataKey> TOWN_HALL_SETTINGS = Map.ofEntries(
		Map.entry(TownGeneralSettings.CAN_PLAYER_DROP, DataKey.TOWN_HALL_SETTINGS_CAN_PLAYER_DROP),
		Map.entry(TownGeneralSettings.CLOCK_TICK_SPEED, DataKey.TOWN_HALL_SETTINGS_CLOCK_TICK_SPEED),
		Map.entry(TownGeneralSettings.DO_EXECUTION_REALLY_KILL, DataKey.TOWN_HALL_SETTINGS_DO_EXECUTION_REALLY_KILL)
	);
	public static final Map<TownChairPlace, DataKey> TOWN_CHAIR_LOC = Map.ofEntries(
		Map.entry(TownChairPlace.CHAIR, DataKey.TOWN_CHAIR_LOC_CHAIR),
		Map.entry(TownChairPlace.LEVER, DataKey.TOWN_CHAIR_LOC_LEVER),
		Map.entry(TownChairPlace.LAMP, DataKey.TOWN_CHAIR_LOC_LAMP),
		Map.entry(TownChairPlace.HOUSE, DataKey.TOWN_CHAIR_LOC_HOUSE)
	);

	// system
	public NamespacedKey key() {
		return key;
	}
	private NamespacedKey key;

	DataKey() {}

	public static void init(JavaPlugin plugin) {
		for (DataKey dk : values()) {
			dk.key = new NamespacedKey(plugin, dk.name().toLowerCase());
		}
	}
}
