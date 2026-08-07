package io.github.aktomik.redclocktower;

import io.github.aktomik.redclocktower.game.town.TownChairPlace;
import io.github.aktomik.redclocktower.game.town.TownGeneralSettings;
import io.github.aktomik.redclocktower.game.town.TownHallPlace;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public enum DataKey {

	// define
	TOWN_NAME,
	TOWN_CHAIRS,

	TOWN_HALL_POS_CENTER,
	TOWN_HALL_POS_SPAWN,
	TOWN_HALL_POS_PYLORI,
	TOWN_HALL_POS_BELL,

	TOWN_HALL_SETTINGS_CAN_PLAYER_DROP_MISC,
	TOWN_HALL_SETTINGS_CAN_PLAYER_DROP_INFO,
	TOWN_HALL_SETTINGS_CAN_PLAYER_OPEN_CHEST,
	TOWN_HALL_SETTINGS_CAN_PLAYER_PULL_OTHERS_LEVER,
	TOWN_HALL_SETTINGS_CLOCK_TICK_SPEED,
	TOWN_HALL_SETTINGS_DO_EXECUTION_REALLY_KILL,

	TOWN_CHAIR_POS_BENCH,
	TOWN_CHAIR_POS_LEVER,
	TOWN_CHAIR_POS_LAMP,
	TOWN_CHAIR_POS_HOUSE,
	;

	// shortcut
	public static final Map<TownHallPlace, DataKey> TOWN_HALL_POS = Map.ofEntries(
		Map.entry(TownHallPlace.CENTER, DataKey.TOWN_HALL_POS_CENTER),
		Map.entry(TownHallPlace.SPAWN, DataKey.TOWN_HALL_POS_SPAWN),
		Map.entry(TownHallPlace.PYLORI, DataKey.TOWN_HALL_POS_PYLORI),
		Map.entry(TownHallPlace.BELL, DataKey.TOWN_HALL_POS_BELL)
	);
	public static final Map<TownChairPlace, DataKey> TOWN_CHAIR_POS = Map.ofEntries(
		Map.entry(TownChairPlace.BENCH, DataKey.TOWN_CHAIR_POS_BENCH),
		Map.entry(TownChairPlace.LEVER, DataKey.TOWN_CHAIR_POS_LEVER),
		Map.entry(TownChairPlace.LAMP, DataKey.TOWN_CHAIR_POS_LAMP),
		Map.entry(TownChairPlace.HOUSE, DataKey.TOWN_CHAIR_POS_HOUSE)
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
