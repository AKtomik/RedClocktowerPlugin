package io.github.AKtomik.redclocktower.game;

public enum BloodGameState {
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
