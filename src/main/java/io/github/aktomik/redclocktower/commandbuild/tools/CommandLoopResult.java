package io.github.aktomik.redclocktower.commandbuild.tools;

public record CommandLoopResult<T>(
	T target,
	boolean success,
	String message
) {}
