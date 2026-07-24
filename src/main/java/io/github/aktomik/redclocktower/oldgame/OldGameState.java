package io.github.aktomik.redclocktower.oldgame;

@Deprecated
public enum OldGameState {
	NOTHING,
	WAITING,
	INGAME,
	ENDED,
	OUT;//not used

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
