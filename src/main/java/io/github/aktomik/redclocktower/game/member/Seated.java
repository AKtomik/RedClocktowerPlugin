package io.github.aktomik.redclocktower.game.member;

import net.kyori.adventure.text.format.NamedTextColor;

public abstract class Seated {

	private BloodSlot slot;

	private final String unicName;
	private String customName;

	private boolean traveller = false;
	private boolean alive = true;
	private boolean voteToken = true;
	private boolean votePull = false;

	private final int votePower = 1;// we will be able to change vote power here

	// construct
	Seated(String unicName) {
		this.unicName = unicName;
	}

	Seated(String unicName, String customName) {
		this(unicName);
		setCustomName(customName);
	}

	// internal link
	// this will avoid same seat having two attached slots
	void attached(BloodSlot newSlot) {
		if (slot != null) slot.empty();
		slot = newSlot;
	}

	void detached() {
		slot = null;
	}

	// family override
	public abstract String getSeatedTypeString();

	public abstract NamedTextColor getSeatedTypeColor();

	// access
	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public String getDisplayName() {
		return (customName == null) ? unicName : customName;
	}

	public BloodSlot getSlot() {
		return slot;
	}

	// get states
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
		return (!slot.isVoteLocked() && haveVote());
	}

	public boolean haveVote() {
		return (alive || voteToken);
	}

	public SeatState getSeatState() {
		return new SeatState(traveller, alive, voteToken, votePull);
	}

	public int getVotePower() {
		return votePower;
	}

	// set states (overridable)
	public void setTraveller(boolean traveller) {
		this.traveller = traveller;
		applySeatState();
	}

	public void setAlive(boolean alive) {
		if (alive) voteToken = true;
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

	// slot
	private void applySeatState() {
		slot.refreshState(getSeatState());
	}
}
