package io.github.aktomik.redclocktower.oldgame;

@Deprecated
public enum OldGamePlace {
	CENTER,
	PYLORI,
	BELL,
	SPAWN;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
