package io.github.aktomik.redclocktower.game.town;

import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public final class TownSettings {

	private static TownSetting<Boolean> ofBoolean(String id, boolean defaultValue) {
		return new TownSetting<>(id, PersistentDataType.BOOLEAN, defaultValue,
		Boolean::parseBoolean, String::valueOf, () -> List.of("true", "false"));
	}

	private static TownSetting<Integer> ofInteger(String id, int defaultValue) {
		return new TownSetting<>(id, PersistentDataType.INTEGER, defaultValue,
		Integer::parseInt, String::valueOf);
	}

	private static <E extends Enum<E>> TownSetting<E> ofEnum(String id, Class<E> enumClass, E defaultValue, PersistentDataType<?, E> pdcType) {
		return new TownSetting<>(id, pdcType, defaultValue,
		s -> Enum.valueOf(enumClass, s.toUpperCase(Locale.ROOT)),
		Enum::name,
		() -> Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toList());
	}


	// lazy to populate it so TownSetting constructor will do it for me
	static final Map<String, TownSetting<?>> ALL = new HashMap<>();

	public static final TownSetting<Boolean> CAN_PLAYER_DROP_MISC = ofBoolean("can_player_drop_misc", true);
	public static final TownSetting<Boolean> CAN_PLAYER_DROP_INFO = ofBoolean("can_player_drop_info", false);
	public static final TownSetting<Boolean> CAN_PLAYER_OPEN_CHEST = ofBoolean("can_player_open_chest", true);

	public static final TownSetting<Boolean> ALLOW_PLAYER_HURT_PLAYER = ofBoolean("allow_player_hurt_player", true);
	public static final TownSetting<Boolean> ALLOW_PLAYER_HURT_OTHER = ofBoolean("allow_player_hurt_other", true);
	public static final TownSetting<Boolean> ALLOW_PLAYER_HURTED = ofBoolean("allow_player_hurted", true);

	public static final TownSetting<Boolean> CAN_PLAYER_PULL_OTHERS_LEVER = ofBoolean("can_player_pull_others_lever", false);

	public static final TownSetting<Integer> VOTE_CLOCK_TICK_SPEED = ofInteger("vote_clock_tick_speed", 20);
	public static final TownSetting<Boolean> DO_EXECUTION_KILL_PLAYER = ofBoolean("do_execution_kill_player", true);


	private TownSettings() {}

	public static Map<String, TownSetting<?>> map() {
		return ALL;
	}
}