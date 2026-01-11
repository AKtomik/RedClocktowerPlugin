package io.github.AKtomik.redclocktower.game;

public enum BloodGameState {
	NOTHING,
	SETUP,//()
	WAITING,//()
	INGAME,
	ENDED;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
