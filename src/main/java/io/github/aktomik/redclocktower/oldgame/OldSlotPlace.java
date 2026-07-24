package io.github.aktomik.redclocktower.oldgame;

@Deprecated
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
