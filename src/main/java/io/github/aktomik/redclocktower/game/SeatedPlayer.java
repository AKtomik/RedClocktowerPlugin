package io.github.aktomik.redclocktower.game;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;

public class SeatedPlayer extends Seated {

	private final OfflinePlayer offPlayer;

	// construct
	public SeatedPlayer(OfflinePlayer offPlayer) {
		super(offPlayer.getName());
		this.offPlayer = offPlayer;
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
		BloodPlayer bloodPlayer = BloodPlayer.get(offPlayer);
		bloodPlayer.attachSeat(this);
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
		BloodPlayer bloodPlayer = BloodPlayer.get(offPlayer);
		bloodPlayer.refreshAliveEffect(alive);
	}
}
