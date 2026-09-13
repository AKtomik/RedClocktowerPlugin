package io.github.aktomik.redclocktower.game.town;

import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public final class TownSettings {
	// lazy to populate it so TownSetting constructor will do it for me
	static final Map<String, TownSetting<?>> ALL = new HashMap<>();

	public static final TownSetting<Boolean> CAN_PLAYER_DROP_MISC = new TownSetting<>(
		"can_player_drop_misc", PersistentDataType.BOOLEAN, true,
		Boolean::parseBoolean, String::valueOf
	);

	public static final TownSetting<Boolean> CAN_PLAYER_DROP_INFO = new TownSetting<>(
		"can_player_drop_info", PersistentDataType.BOOLEAN, false,
		Boolean::parseBoolean, String::valueOf
	);

	public static final TownSetting<Boolean> CAN_PLAYER_OPEN_CHEST = new TownSetting<>(
		"can_player_open_chest", PersistentDataType.BOOLEAN, true,
		Boolean::parseBoolean, String::valueOf
	);

	public static final TownSetting<Boolean> CAN_PLAYER_PULL_OTHERS_LEVER = new TownSetting<>(
		"can_player_pull_others_lever", PersistentDataType.BOOLEAN, false,
		Boolean::parseBoolean, String::valueOf
	);

	public static Map<String, TownSetting<?>> map() {
		return ALL;
	}

	private TownSettings() {}
}