package io.github.AKtomik.redclocktower.game;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum BloodGameAction {
	SETUP,
	SETOUT,
	RESET,
	START,
	FINISH,
	CLEAN;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
