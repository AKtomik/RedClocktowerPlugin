package io.github.aktomik.redclocktower.command.setup;

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
