package io.github.aktomik.redclocktower.game;

public enum GameVoteStep
{
	NOTHING,
	NOMINATION,//UNUSED
	VOTE_PROCESS,
	CANCEL_VOTE_PROCESS,
	PYLORI_PLACE,
	CANCEL_PYLORI_PLACE,
	PYLORI_MOUNT,
	EXECUTION_PROCESS,
	CANCEL_EXECUTION_PROCESS;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
