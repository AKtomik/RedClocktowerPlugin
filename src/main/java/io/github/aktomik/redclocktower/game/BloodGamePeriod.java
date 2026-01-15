package io.github.aktomik.redclocktower.game;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum BloodGamePeriod {
	MORNING,
	FREE,
	MEET,
	NIGHT;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}