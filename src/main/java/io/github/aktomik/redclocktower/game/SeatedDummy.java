package io.github.aktomik.redclocktower.game;

import net.kyori.adventure.text.format.NamedTextColor;

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
	public NamedTextColor getSeatedTypeColor() {
		return NamedTextColor.GOLD;
	}
}
