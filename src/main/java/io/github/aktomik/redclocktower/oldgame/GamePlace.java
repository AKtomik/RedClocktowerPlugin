package io.github.aktomik.redclocktower.oldgame;

public enum GamePlace {
	CENTER,
	PYLORI,
	BELL,
	SPAWN;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
