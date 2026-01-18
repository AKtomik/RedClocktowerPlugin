package io.github.aktomik.redclocktower.game;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum BloodGameStepAction {
	SETUP,
	START,
	FINISH,
	REPLAY,
	RESET,
	CLEAR;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
