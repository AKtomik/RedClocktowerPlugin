package io.github.AKtomik.redclocktower.game;

public enum BloodGameState {
	NOTHING,
	SETUP,//not used
	WAITING,//not used
	INGAME,
	ENDED;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
