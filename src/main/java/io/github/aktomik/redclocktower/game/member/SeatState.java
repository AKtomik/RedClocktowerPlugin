package io.github.aktomik.redclocktower.game.member;

public record SeatState(
	boolean traveller,
	boolean alive,
	boolean voteToken,
	boolean votePull
) {}
