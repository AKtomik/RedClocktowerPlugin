package io.github.aktomik.redclocktower.game;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum BloodGameDebugAction {
	CLEAN_PLAYERS,
	CLEAN_SLOTS,
	CLEAN_TEAM,
	CLEAN_ALL;


	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
