package io.github.aktomik.redclocktower.game;

public enum BloodSlotPlace {
	CHAIR,
	LEVER,
	LAMP,
	HOUSE;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
