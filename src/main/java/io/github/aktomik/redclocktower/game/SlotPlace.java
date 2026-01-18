package io.github.aktomik.redclocktower.game;

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
