package io.github.aktomik.redclocktower.game;

public enum GameState {
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
