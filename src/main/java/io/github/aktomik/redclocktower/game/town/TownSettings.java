package io.github.aktomik.redclocktower.game.town;

import io.github.aktomik.redclocktower.RedClocktower;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public enum TownSettings {
	CAN_PLAYER_DROP_MISC(new TownSetting<>(
		key("can_player_drop_misc"), PersistentDataType.BOOLEAN, true,
		Boolean::parseBoolean, String::valueOf)
	),

	CAN_PLAYER_DROP_INFO(new TownSetting<>(
		key("can_player_drop_info"), PersistentDataType.BOOLEAN, false,
		Boolean::parseBoolean, String::valueOf)
	),

	CAN_PLAYER_OPEN_CHEST(new TownSetting<>(
		key("can_player_open_chest"), PersistentDataType.BOOLEAN, true,
		Boolean::parseBoolean, String::valueOf)
	),

	CAN_PLAYER_PULL_OTHERS_LEVER(new TownSetting<>(
		key("can_player_pull_others_lever"), PersistentDataType.BOOLEAN, false,
		Boolean::parseBoolean, String::valueOf)
	);

	private final TownSetting<?> setting;
	TownSettings(TownSetting<?> setting) { this.setting = setting; }
	public TownSetting<?> setting() { return setting; }

	private static NamespacedKey key(String id) {
		return new NamespacedKey(RedClocktower.plugin(), "townhall.settings." + id);
	}
}