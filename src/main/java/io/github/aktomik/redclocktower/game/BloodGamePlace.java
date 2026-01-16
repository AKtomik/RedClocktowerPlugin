package io.github.aktomik.redclocktower.game;

public enum BloodGamePlace {
	CENTER,
	SPAWN;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
