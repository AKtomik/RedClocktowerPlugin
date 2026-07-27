package io.github.aktomik.redclocktower.game;

import org.bukkit.OfflinePlayer;

public class SeatedPlayer extends Seated {
	OfflinePlayer offPlayer;
	public SeatedPlayer(OfflinePlayer offPlayer) {
		super(offPlayer.getName());
		BloodPlayer bloodPlayer = BloodPlayer.get(offPlayer);
	}

	public SeatedPlayer(OfflinePlayer offPlayer, String customName) {
		super(offPlayer.getName(), customName);
		BloodPlayer bloodPlayer = BloodPlayer.get(offPlayer);
	}

	public OfflinePlayer getOffPlayer() {
		return offPlayer;
	}

	@Override
	public void setAlive(boolean alive) {
		super.setAlive(alive);
	}
}
