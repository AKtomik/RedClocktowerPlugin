package io.github.aktomik.redclocktower.game.town;

public enum TownChairPlace {
	BENCH,
	LEVER,
	LAMP;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
