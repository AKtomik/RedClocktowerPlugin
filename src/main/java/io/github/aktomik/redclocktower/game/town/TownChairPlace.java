package io.github.aktomik.redclocktower.game.town;

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
