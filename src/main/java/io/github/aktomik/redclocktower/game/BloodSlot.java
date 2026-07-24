package io.github.aktomik.redclocktower.game;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

public class BloodSlot {

	private final TownChair townChair;
	private BloodPlayer bloodPlayer;
	public BloodSlot(TownChair townChair) {
		this.townChair = townChair;
	}

	// access
	public BloodPlayer getBloodPlayer() {
		return bloodPlayer;
	}
}
