package io.github.aktomik.redclocktower.oldgame;

public enum SlotPlace {
	CHAIR,
	LEVER,
	LAMP,
	HOUSE;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
