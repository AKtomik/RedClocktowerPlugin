package io.github.aktomik.redclocktower.game;

public enum GamePlace {
	CENTER,
	PYLORI,
	SPAWN;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
