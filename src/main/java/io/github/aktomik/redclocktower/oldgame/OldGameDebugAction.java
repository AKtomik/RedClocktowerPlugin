package io.github.aktomik.redclocktower.oldgame;

import org.jspecify.annotations.NullMarked;

@NullMarked
@Deprecated
public enum OldGameDebugAction {
	CLEAN_PLAYERS,
	CLEAN_SLOTS,
	CLEAN_TEAM,
	CLEAN_ALL;


	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
