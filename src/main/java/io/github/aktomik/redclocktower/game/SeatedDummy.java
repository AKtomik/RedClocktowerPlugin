package io.github.aktomik.redclocktower.game;

public class SeatedDummy extends Seated {
	public SeatedDummy(BloodSlot slot, int number) {
		super(slot, "-seat"+number, "Seat"+number);
	}
}
