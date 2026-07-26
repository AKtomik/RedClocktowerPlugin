package io.github.aktomik.redclocktower.command.setup;

public enum TownChairPlace {
	BENCH,
	LEVER,
	LAMP,
	HOUSE;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
