package io.github.aktomik.redclocktower.game;

public enum VoteStep
{
	NOTHING,
	VOTE_PROCESS,
	EXECUTION_PROCESS,
	CANCEL;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
