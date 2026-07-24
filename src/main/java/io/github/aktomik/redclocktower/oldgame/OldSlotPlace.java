package io.github.aktomik.redclocktower.oldgame;

public enum OldSlotPlace {
	CHAIR,
	LEVER,
	LAMP,
	HOUSE;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
