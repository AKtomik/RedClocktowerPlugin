package io.github.aktomik.redclocktower.game;

public abstract class Seated {

	private BloodSlot slot;

	private final String unicName;
	private String customName;

	private boolean traveller = false;
	private boolean alive = true;
	private boolean voteToken = true;
	private boolean votePull = false;

	// construct
	Seated(BloodSlot slot, String unicName) {
		this.slot = slot;
		this.unicName = unicName;
	}

	Seated(BloodSlot slot, String unicName, String customName) {
		this(slot, unicName);
		setCustomName(customName);
	}

	// access
	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public String getDisplayName() {
		return (customName == null) ? unicName : customName;
	}


	// states/set
	public void setTraveller(boolean traveller) {
		this.traveller = traveller;
		applySeatState();
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
		applySeatState();
	}

	public void setVoteToken(boolean voteToken) {
		this.voteToken = voteToken;
		applySeatState();
	}

	public void setVotePull(boolean votePull) {
		this.votePull = votePull;
		applySeatState();
	}

	// states/get
	public boolean getTraveller() {
		return traveller;
	}

	public boolean getAlive() {
		return alive;
	}

	public boolean getVotePull() {
		return votePull;
	}

	public boolean getVoteToken() {
		return voteToken;
	}

	public boolean canVote() {
		return (!slot.isVoteLocked() && (alive || voteToken));
	}

	public SeatState getSeatState() {
		return new SeatState(traveller, alive, voteToken, votePull);
	}

	// slot
	private void applySeatState() {
		slot.setState(getSeatState());
	}

	void detachSlot() {
		if (slot == null) throw new RuntimeException("seated try to detachSlot but does not have a slot");
		slot.empty();
		slot = null;
	}

	void attachSlot(BloodSlot newSlot) {
		if (slot != null) throw new RuntimeException("seated try to attachSlot but already have a slot");
		slot = newSlot;
	}
}
