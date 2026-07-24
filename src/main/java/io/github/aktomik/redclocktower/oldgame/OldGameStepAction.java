package io.github.aktomik.redclocktower.oldgame;

import org.jspecify.annotations.NullMarked;

@NullMarked
@Deprecated
public enum OldGameStepAction {
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
