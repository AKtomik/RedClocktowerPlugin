package io.github.aktomik.redclocktower.oldgame;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum GameStepAction {
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
