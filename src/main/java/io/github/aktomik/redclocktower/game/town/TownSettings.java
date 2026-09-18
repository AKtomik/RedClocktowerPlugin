package io.github.aktomik.redclocktower.game.town;

import io.github.aktomik.redclocktower.utils.pdc.NamedTextColorDataType;
import io.github.aktomik.redclocktower.utils.pdc.TextColorDataType;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Function;

public final class TownSettings {

	private static TownSetting<Boolean> ofBoolean(String id, boolean defaultValue) {
		return new TownSetting<>(id, PersistentDataType.BOOLEAN, defaultValue,
		Boolean::parseBoolean, String::valueOf, List.of("true", "false"));
	}

	private static TownSetting<Integer> ofInteger(String id, int defaultValue) {
		return new TownSetting<>(id, PersistentDataType.INTEGER, defaultValue,
		Integer::parseInt, String::valueOf);
	}

	private static TownSetting<String> ofString(String id, String defaultValue) {
		return new TownSetting<>(id, PersistentDataType.STRING, defaultValue,
		Function.identity(), Function.identity());
	}

	private static <E extends Enum<E>> TownSetting<E> ofEnum(String id, Class<E> enumClass, E defaultValue, PersistentDataType<?, E> pdcType) {
		return new TownSetting<>(id, pdcType, defaultValue,
		s -> Enum.valueOf(enumClass, s.toUpperCase(Locale.ROOT)),
		Enum::name,
		Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toList());
	}


	// lazy to populate it so TownSetting constructor will do it for me
	static final Map<String, TownSetting<?>> ALL = new HashMap<>();

	public static final TownSetting<String> TOWN_DISPLAY_NAME = ofString("town_display_name", "blood");
	public static final TownSetting<NamedTextColor> TOWN_DISPLAY_COLOR = new TownSetting<NamedTextColor>("town_display_color",
		NamedTextColorDataType.INSTANCE, NamedTextColor.WHITE,
		NamedTextColor.NAMES::value, NamedTextColor.NAMES::key,
		NamedTextColor.NAMES.keys().stream().toList()
	);

	public static final TownSetting<Boolean> CAN_PLAYER_DROP_MISC = ofBoolean("can_player_drop_misc", true);
	public static final TownSetting<Boolean> CAN_PLAYER_DROP_INFO = ofBoolean("can_player_drop_info", false);
	public static final TownSetting<Boolean> CAN_PLAYER_OPEN_CHEST = ofBoolean("can_player_open_chest", true);

	public static final TownSetting<Boolean> ALLOW_PLAYER_HIT_PLAYER = ofBoolean("allow_player_hit_player", true);
	public static final TownSetting<Boolean> ALLOW_PLAYER_HIT_OTHER = ofBoolean("allow_player_hit_other", true);
	public static final TownSetting<Boolean> ALLOW_PLAYER_DAMAGED = ofBoolean("allow_player_damaged", true);

	public static final TownSetting<Boolean> CAN_PLAYER_PULL_EACHOTHER_LEVER = ofBoolean("can_player_pull_eachother_lever", false);

	public static final TownSetting<Integer> VOTE_CLOCK_TICK_SPEED = ofInteger("vote_clock_tick_speed", 20);
	public static final TownSetting<Boolean> DO_EXECUTION_KILL_PLAYER = ofBoolean("do_execution_kill_player", true);


	private TownSettings() {}

	public static Map<String, TownSetting<?>> map() {
		return ALL;
	}
}