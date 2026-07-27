package io.github.aktomik.redclocktower.game;

import org.bukkit.OfflinePlayer;

public class SeatedPlayer extends Seated {

	OfflinePlayer offPlayer;

	// construct
	public SeatedPlayer(OfflinePlayer offPlayer) {
		super(offPlayer.getName());
		this.offPlayer = offPlayer;
	}

	public SeatedPlayer(OfflinePlayer offPlayer, String customName) {
		super(offPlayer.getName(), customName);
		this.offPlayer = offPlayer;
	}

	// internal link
	// this will avoid same player having two attached seats
	@Override
	void attached(BloodSlot newSlot) {
		BloodPlayer bloodPlayer = BloodPlayer.get(offPlayer);
		bloodPlayer.attachSeat(this);
		super.attached(newSlot);
	}

	@Override
	void detached() {
		BloodPlayer bloodPlayer = BloodPlayer.get(offPlayer);
		bloodPlayer.detachSeat();
		super.detached();
	}

	// access
	public OfflinePlayer getOffPlayer() {
		return offPlayer;
	}

	// state
	@Override
	public void setAlive(boolean alive) {
		super.setAlive(alive);
	}
}
