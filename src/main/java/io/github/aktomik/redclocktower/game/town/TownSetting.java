package io.github.aktomik.redclocktower.game.town;

import io.github.aktomik.redclocktower.RedClocktower;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Function;

public final class TownSetting<T> {

	private final String id;
	private final NamespacedKey key;

	private final PersistentDataType<?, T> type;
	private final T defaultValue;

	private final Function<String, T> parser;
	private final Function<T, String> formatter;

	TownSetting(String id, PersistentDataType<?, T> type, T defaultValue,
					   Function<String, T> parser, Function<T, String> formatter) {
		this.id = id;
		this.key = new NamespacedKey(RedClocktower.plugin(), "townhall.settings." + id);
		this.type = type;
		this.defaultValue = defaultValue;
		this.parser = parser;
		this.formatter = formatter;
		TownSettings.ALL.put(id, this);
	}

	public String id() { return id; }
	public NamespacedKey key() { return key; }
	public PersistentDataType<?, T> type() { return type; }
	public T defaultValue() { return defaultValue; }

	public void setFromString(TownHall townHall, String input) {
		townHall.setSetting(this, parser.apply(input));
	}
	public String getFormatted(TownHall townHall) {
		return formatter.apply(townHall.getSetting(this));
	}
}