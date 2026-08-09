package io.github.aktomik.redclocktower.game;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.scoreboard.Team;

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

	@Override
	public void addToTeam(Team team) {
		team.addEntry(getId());
	}

	@Override
	public void removeFromTeam(Team team) {
		team.addEntry(getId());
	}
}
