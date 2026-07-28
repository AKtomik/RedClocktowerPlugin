package io.github.aktomik.redclocktower.game.member;

import io.github.aktomik.redclocktower.game.BloodGame;
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

	// access
	public OfflinePlayer getOffPlayer() {
		return offPlayer;
	}

	@Nullable
	public SeatedPlayer getSeated() {
		return seated;
	}

	// internal link

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
		seated = null;
		leavedSeatEffect();
	}

	// to avoid player being on two storytelling simultaneously
	// is used by BloodGame and should not be used elsewhere

	void attachStorytelling(BloodGame game) {// only one call at BloodGame
		// do not call detachStorytelling here to avoid circular call
		if (storytelling != null) storytelling.removeStoryteller(this);
		this.storytelling = game;
	}

	void detachStorytelling() {// only one call at BloodGame
		if (storytelling == null) return;
		storytelling = null;
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

	// global effect
	private void joinedSeatEffect() {
		// either attachSeat or player join
		refreshAliveEffect(seated.getAlive());
	}

	private void leavedSeatEffect() {
		// either detachSeat or player leave
		clearAliveEffect();
	}
}
