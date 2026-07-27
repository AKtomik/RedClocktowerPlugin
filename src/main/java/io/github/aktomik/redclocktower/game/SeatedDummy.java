package io.github.aktomik.redclocktower.game;

import org.bukkit.Color;

public class SeatedDummy extends Seated {
	public SeatedDummy(int number) {
		super("-seat"+number, "Seat"+number);
	}

	// family override
	@Override
	public String getSeatedTypeString() {
		return "dummy";
	}

	@Override
	public Color getSeatedTypeColor() {
		return Color.ORANGE;
	}
}
