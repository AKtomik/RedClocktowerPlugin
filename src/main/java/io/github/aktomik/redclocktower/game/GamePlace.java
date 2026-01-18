package io.github.aktomik.redclocktower.game;

public enum GamePlace {
	CENTER,
	SPAWN;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
