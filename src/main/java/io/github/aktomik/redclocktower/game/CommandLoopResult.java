package io.github.aktomik.redclocktower.game;

public record CommandLoopResult<T>(
	T target,
	boolean success,
	String message
) {}
