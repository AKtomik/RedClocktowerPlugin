package io.github.aktomik.redclocktower.game.town;

public enum TownHallPlace {
	CENTER,
	PYLORI,
	BELL,
	SPAWN;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
