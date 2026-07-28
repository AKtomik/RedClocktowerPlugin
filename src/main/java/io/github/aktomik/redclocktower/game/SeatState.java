package io.github.aktomik.redclocktower.game;

public record SeatState(
	boolean traveller,
	boolean alive,
	boolean voteToken,
	boolean votePull
) {}
