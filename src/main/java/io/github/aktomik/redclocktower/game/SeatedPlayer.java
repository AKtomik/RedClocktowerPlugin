package io.github.aktomik.redclocktower.game;

import org.bukkit.OfflinePlayer;

public class SeatedPlayer extends Seated {
	OfflinePlayer player;
	public SeatedPlayer(BloodSlot slot, OfflinePlayer player) {
		super(slot, player.getName());
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.attachSeat(this);
	}

	public SeatedPlayer(BloodSlot slot, OfflinePlayer player, String customName) {
		super(slot, player.getName(), customName);
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.attachSeat(this);
	}

	@Override
	public void setAlive(boolean alive) {
		super.setAlive(alive);
	}
}
