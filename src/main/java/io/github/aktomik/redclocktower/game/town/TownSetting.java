package io.github.aktomik.redclocktower.game.town;

import io.github.aktomik.redclocktower.RedClocktower;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TownSetting<T> {

	private final String id;
	private final NamespacedKey key;

	private final PersistentDataType<?, T> type;
	private final T defaultValue;

	private final Function<String, T> parser;
	private final Function<T, String> formatter;
	private final Supplier<List<String>> suggestions;

	TownSetting(String id, PersistentDataType<?, T> type, T defaultValue,
	Function<String, T> parser, Function<T, String> formatter, Supplier<List<String>> suggestions) {
		this.id = id;
		this.key = new NamespacedKey(RedClocktower.plugin(), "townhall.settings." + id);
		this.type = type;
		this.defaultValue = defaultValue;
		this.parser = parser;
		this.formatter = formatter;
		this.suggestions = suggestions;
		TownSettings.ALL.put(id, this);
	}
	public TownSetting(String id, PersistentDataType<?, T> type, T defaultValue,
					   Function<String, T> parser, Function<T, String> formatter) {
		this(id, type, defaultValue, parser, formatter, List::of);
	}

	public String id() { return id; }
	public NamespacedKey key() { return key; }
	public PersistentDataType<?, T> type() { return type; }
	public T defaultValue() { return defaultValue; }
	public List<String> suggestions() { return suggestions.get(); }

	public void set(TownHall townHall, T value) {
		townHall.setSetting(this, value);
	}
	public T get(TownHall townHall) {
		return townHall.getSetting(this);
	}

	public void setFromString(TownHall townHall, String input) throws IllegalArgumentException {
		T value;
		try {
			value = parser.apply(input);
		} catch (Exception e) {
			throw new IllegalArgumentException("wrong input for this type");
		}
		set(townHall, value);
	}
	public String getFormatted(TownHall townHall) {
		return formatter.apply(get(townHall));
	}
}