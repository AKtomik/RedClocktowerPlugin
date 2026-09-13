package io.github.aktomik.redclocktower.game;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.scoreboard.Team;

public class SeatedPlayer extends Seated {

	private final BloodPlayer bloodPlayer;

	// construct
	public SeatedPlayer(BloodPlayer bloodPlayer) {
		super(bloodPlayer.getOfflinePlayer().getName());
		this.bloodPlayer = bloodPlayer;
	}

	// access
	public BloodPlayer getBloodPlayer() {
		return bloodPlayer;
	}

	// family override
	@Override
	public String getSeatedTypeString() {
		return "player";
	}

	@Override
	public NamedTextColor getSeatedTypeColor() {
		return NamedTextColor.YELLOW;
	}

	// internal link
	// this will avoid same player having two attached seats
	@Override
	void attached(BloodSlot newSlot) {
		super.attached(newSlot);
		bloodPlayer.attachSeat(this);
	}

	@Override
	void detached() {
		bloodPlayer.detachSeat();
		super.detached();
	}

	// votes
	@Override
	public void enableGlower(NamedTextColor color) {
		super.enableGlower(color);
		getSlot().getGame().getTeam().color(getGlow());
		bloodPlayer.refreshGlowerEffect(isGlowing());
	}

	@Override
	public void disableGlow() {
		super.disableGlow();
		bloodPlayer.refreshGlowerEffect(isGlowing());
	}

	// state
	@Override
	public void setAlive(boolean alive) {
		super.setAlive(alive);
		bloodPlayer.refreshAliveEffect(alive);
	}

	// name
	@Override
	protected void onSlotLabelRefresh() {
		super.onSlotLabelRefresh();
		bloodPlayer.onSeatLabelRefresh();
	}
}
