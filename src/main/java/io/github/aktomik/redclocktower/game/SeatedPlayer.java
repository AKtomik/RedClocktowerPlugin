package io.github.aktomik.redclocktower.game;

import org.bukkit.OfflinePlayer;

public class SeatedPlayer extends Seated {
	OfflinePlayer player;
	public SeatedPlayer(BloodSlot slot, OfflinePlayer player) {
		super(slot, player.getName());
	}

	public SeatedPlayer(BloodSlot slot, OfflinePlayer player, String customName) {
		super(slot, player.getName(), customName);
	}

	@Override
	public void setAlive(boolean alive) {
		super.setAlive(alive);
	}
}
