package io.github.aktomik.redclocktower.game;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum GameTeam {
	BAD,
	GOOD,
	NEUTRAL;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}