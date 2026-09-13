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


	public static final TownSetting<Boolean> CAN_PLAYER_HURT_PLAYER = new TownSetting<>(
		"can_player_hurt_player", PersistentDataType.BOOLEAN, false,
		Boolean::parseBoolean, String::valueOf
	);
	public static final TownSetting<Boolean> CAN_PLAYER_HURT_OTHER = new TownSetting<>(
		"can_player_hurt_other", PersistentDataType.BOOLEAN, false,
		Boolean::parseBoolean, String::valueOf
	);
	public static final TownSetting<Boolean> CAN_PLAYER_HURT_SELF = new TownSetting<>(
		"can_player_hurt_self", PersistentDataType.BOOLEAN, false,
		Boolean::parseBoolean, String::valueOf
	);

	public static final TownSetting<Boolean> CAN_PLAYER_PULL_OTHERS_LEVER = new TownSetting<>(
		"can_player_pull_others_lever", PersistentDataType.BOOLEAN, false,
		Boolean::parseBoolean, String::valueOf
	);

	public static final TownSetting<Integer> VOTE_CLOCK_TICK_SPEED = new TownSetting<>(
		"vote_clock_tick_speed", PersistentDataType.INTEGER, 20,
		Integer::parseInt, String::valueOf
	);
	public static final TownSetting<Boolean> DO_EXECUTION_KILL_PLAYER = new TownSetting<>(
		"do_execution_kill_player", PersistentDataType.BOOLEAN, true,
		Boolean::parseBoolean, String::valueOf
	);

	public static Map<String, TownSetting<?>> map() {
		return ALL;
	}

	private TownSettings() {}
}