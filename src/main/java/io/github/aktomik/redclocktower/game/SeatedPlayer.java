package io.github.aktomik.redclocktower.game;

import org.bukkit.OfflinePlayer;

public class SeatedPlayer extends Seated {
	OfflinePlayer offPlayer;
	public SeatedPlayer(BloodSlot slot, OfflinePlayer offPlayer) {
		super(slot, offPlayer.getName());
		BloodPlayer bloodPlayer = BloodPlayer.get(offPlayer);
		bloodPlayer.attachSeat(this);
	}

	public SeatedPlayer(BloodSlot slot, OfflinePlayer offPlayer, String customName) {
		super(slot, offPlayer.getName(), customName);
		BloodPlayer bloodPlayer = BloodPlayer.get(offPlayer);
		bloodPlayer.attachSeat(this);
	}

	public OfflinePlayer getOffPlayer() {
		return offPlayer;
	}

	@Override
	public void setAlive(boolean alive) {
		super.setAlive(alive);
	}
}
