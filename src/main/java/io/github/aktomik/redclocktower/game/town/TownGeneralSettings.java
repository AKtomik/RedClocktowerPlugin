package io.github.aktomik.redclocktower.game.town;

public enum TownGeneralSettings {
	CAN_PLAYER_DROP,
	CLOCK_TICK_SPEED,
	DO_EXECUTION_REALLY_KILL;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
