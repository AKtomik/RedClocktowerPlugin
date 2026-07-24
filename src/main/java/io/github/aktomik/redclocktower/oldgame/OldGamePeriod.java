package io.github.aktomik.redclocktower.oldgame;

import org.jspecify.annotations.NullMarked;

@NullMarked
@Deprecated
public enum OldGamePeriod {
	MORNING,
	FREE,
	MEET,
	NIGHT;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}