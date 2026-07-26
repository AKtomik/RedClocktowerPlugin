package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.game.seated.SeatedBase;

public class BloodSlot {

	// construct
	private final TownChair townChair;
	private SeatedBase seated;
	private boolean voteLocked;

	BloodSlot(TownChair townChair) {
		this.townChair = townChair;
	}

	// process

	// access
	public SeatedBase getSeated() {
		return seated;
	}
	public boolean isOccupied() {
		return getSeated() != null;
	}

	// assign
	void assign(SeatedBase seated) {
		this.seated = seated;
	}

	void empty() {
		this.seated = null;
	}
}
