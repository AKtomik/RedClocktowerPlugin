package io.github.aktomik.redclocktower.game;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum BloodGameAction {
	SETUP,
	START,
	FINISH,
	REPLAY,
	RESET,
	CLEAR,
	BRUTAL_CLEAN;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
