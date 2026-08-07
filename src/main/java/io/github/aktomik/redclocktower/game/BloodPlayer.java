package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.game.town.TownHall;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BloodPlayer {

	private final OfflinePlayer offPlayer;
	private SeatedPlayer seated;
	private BloodGame storytelling;
	private BloodGame spectating;
	private BloodPlayer(OfflinePlayer offlinePlayer) {
		this.offPlayer = offlinePlayer;
	}

	private static final Map<OfflinePlayer, BloodPlayer> playerToBloodPlayerMap = new HashMap<>();

	@NullMarked
	public static BloodPlayer get(OfflinePlayer offlinePlayer) {
		// find
		BloodPlayer playerFound = playerToBloodPlayerMap.get(offlinePlayer);
		if (playerFound != null) return playerFound;

		// create
		BloodPlayer playerCreated = new BloodPlayer(offlinePlayer);
		playerToBloodPlayerMap.put(offlinePlayer, playerCreated);
		return playerCreated;
	}

	// ACCESS

	public OfflinePlayer getOffPlayer() {
		return offPlayer;
	}

	@Nullable
	public SeatedPlayer getSeated() {
		return seated;
	}

	@Nullable
	public BloodGame getSeatedGame() {
		if (seated == null) return null;
		return seated.getSlot().getGame();
	}

	@Nullable
	public TownHall getSeatedTownHall() {
		if (seated == null) return null;
		return seated.getSlot().getGame().getTownHall();
	}

	@Nullable
	public BloodGame getStorytellingGame() {
		return storytelling;
	}

	@Nullable
	public BloodGame getSpectatingGame() {
		return spectating;
	}

	// LINKS

	// to avoid player being on two seats simultaneously
	// is used by SeatedPlayer and should not be used elsewhere

	void attachSeat(SeatedPlayer seated) {// only one call at SeatedPlayer
		// do not call detachSeat here to avoid circular call
		if (this.seated != null) this.seated.getSlot().empty();
		this.seated = seated;
		joinedSeatEffect();
	}

	void detachSeat() {// only one call at SeatedPlayer
		if (seated == null) return;
		leavedSeatEffect();
		seated = null;
	}

	// to avoid player being on two storytelling simultaneously
	// is used by BloodGame and should not be used elsewhere
	// it is technically tolerated to be storytelling and playing a game

	void attachStorytelling(BloodGame game) {// only one call at BloodGame
		// do not call detachStorytelling here to avoid circular call
		if (storytelling != null) storytelling.removeStoryteller(this);
		if (spectating != null) spectating.removeSpectator(this);
		this.storytelling = game;
		// needed for invisibility view
		storytelling.getTeam().addPlayer(offPlayer);
	}

	void detachStorytelling() {// only one call at BloodGame
		if (storytelling == null) return;
		// needed for invisibility view
		storytelling.getTeam().removePlayer(offPlayer);
		// then clear the pointer
		storytelling = null;
	}

	// to avoid player being on two spectating simultaneously
	// is used by BloodGame and should not be used elsewhere

	void attachSpectating(BloodGame game) {// only one call at BloodGame
		// do not call detachStorytelling here to avoid circular call
		if (spectating != null) spectating.removeSpectator(this);
		if (storytelling != null) storytelling.removeStoryteller(this);
		this.spectating = game;
		// needed for invisibility view
		spectating.getTeam().addPlayer(offPlayer);
	}

	void detachSpectating() {// only one call at BloodGame
		if (spectating == null) return;
		// needed for invisibility view
		spectating.getTeam().removePlayer(offPlayer);
		// then clear the pointer
		spectating = null;
	}

	// STATE & EFFECTS

	// global effect
	void joinedSeatEffect() {
		// either attachSeat or player join
		if (seated == null) return;
		refreshAliveEffect(seated.getAlive());
		seated.getSlot().getGame().getTeam().addPlayer(offPlayer);
	}

	void leavedSeatEffect() {
		// either detachSeat or player leave
		if (seated == null) return;
		clearAliveEffect();
		seated.getSlot().getGame().getTeam().removePlayer(offPlayer);
	}

	// state effect
	protected void clearAliveEffect() {
		if (!(offPlayer instanceof Player player)) return;
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
	}

	protected void refreshAliveEffect(boolean alive) {
		if (!(offPlayer instanceof Player player)) return;
		if (alive)
		{
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 9, true, false, false));
		}
	}
}
