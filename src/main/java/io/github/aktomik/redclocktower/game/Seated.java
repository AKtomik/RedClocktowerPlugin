package io.github.aktomik.redclocktower.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.scoreboard.Team;

import static io.github.aktomik.redclocktower.utils.MiscUtils.digitInCircle;

public abstract class Seated {

	private BloodSlot slot;
	private final String id;
	private String name;

	private boolean traveller = false;
	private boolean alive = true;
	private boolean voteToken = true;
	private boolean votePull = false;

	private final int votePower = 1;// we will be able to change vote power here

	// construct
	Seated(String id, String name) {
		this.id = id;
		this.name = name;
	}

	Seated(String id) {
		this(id, id);
	}

	// internal link
	// this will avoid same seat having two attached slots
	void attached(BloodSlot newSlot) {
		if (slot != null) slot.empty();
		slot = newSlot;
		addToTeam(newSlot.getGame().getTeam());
	}

	void detached() {
		if (slot == null) return;
		removeFromTeam(slot.getGame().getTeam());
		slot = null;
	}

	// family override
	public abstract String getSeatedTypeString();

	public abstract NamedTextColor getSeatedTypeColor();

	public abstract void addToTeam(Team team);

	public abstract void removeFromTeam(Team team);

	// access
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		slot.refreshLabel();
	}

	public BloodSlot getSlot() {
		return slot;
	}

	// get states
	public boolean getTraveller() {
		return traveller;
	}

	public boolean getAlive() {
		return alive;
	}

	public boolean getVotePull() {
		return votePull;
	}

	public boolean getVoteToken() {
		return voteToken;
	}

	public boolean canVote() {
		return (!slot.isVoteLocked() && (haveVote() && !getSlot().getGame().getCircle().isExclusionVote()));
	}

	public boolean haveVote() {
		return (alive || voteToken);
	}

	public SeatState getSeatState() {
		return new SeatState(traveller, alive, voteToken, votePull);
	}

	public int getVotePower() {
		return votePower;
	}

	public int useVote() {
		if (!votePull || !haveVote()) return 0;
		if (!alive) setVoteToken(false);
		return votePower;
	}

	// set states (overridable)
	public void setTraveller(boolean traveller) {
		this.traveller = traveller;
		applySeatState();
	}

	public void setAlive(boolean alive) {
		if (alive) voteToken = true;
		this.alive = alive;
		applySeatState();
	}

	public void setVoteToken(boolean voteToken) {
		this.voteToken = voteToken;
		applySeatState();
	}

	public void setVotePull(boolean votePull) {
		this.votePull = votePull;
		applySeatState();
	}

	// slot
	protected void applySeatState() {
		slot.refreshState(getSeatState());
		slot.refreshLabel();
	}

	protected void onSlotLabelRefresh() {}

	// name content
	public TextColor getTextStateColor() {
		if (alive) {
			if (votePull) return NamedTextColor.YELLOW;
			else return NamedTextColor.RED;
		} else {
			if (votePull) return NamedTextColor.AQUA;
			else if (!voteToken) return NamedTextColor.DARK_GRAY;
			else return NamedTextColor.BLUE;
		}
	}

	public Component getInGameName() {
		if (alive) return Component.text(name);
		else return Component.text("☠ " + name).color(NamedTextColor.GRAY);
	}

	public Component getTextToken() {
		String token = (traveller) ? "✳" : "✴";
		return Component.text(token).color(getTextStateColor());
	}

	public Component getTextDigit() {
		return Component.text(digitInCircle(getSlot().getIndex() + 1)).color(getTextStateColor());
	}
}
