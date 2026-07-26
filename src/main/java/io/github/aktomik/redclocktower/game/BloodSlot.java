package io.github.aktomik.redclocktower.game;

public class BloodSlot {

	// construct
	private final TownChair townChair;
	private BloodPlayer bloodPlayer;
	private boolean locked;

	public BloodSlot(TownChair townChair) {
		this.townChair = townChair;
	}
	public BloodSlot(TownChair townChair, BloodPlayer bloodPlayer) {
		this.townChair = townChair;
		LinkBloodPlayer(bloodPlayer);
	}

	// link player
	public void LinkBloodPlayer(BloodPlayer bloodPlayer) {
		this.bloodPlayer = bloodPlayer;
	}

	public void UnlinkBloodPlayer() {
		this.bloodPlayer = null;
	}

	// access
	public BloodPlayer getBloodPlayer() {
		return bloodPlayer;
	}

//	public BloodPlayer getName() {
//		return bloodPlayer;
//	}
}
