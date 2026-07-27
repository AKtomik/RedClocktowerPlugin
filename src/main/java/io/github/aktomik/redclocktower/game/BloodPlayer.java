package io.github.aktomik.redclocktower.game;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;

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

	@NullMarked
	public SeatedPlayer getSeated() {
		return seated;
	}

	// attribution

	// to avoid player being on two seats simultaneously
	// is used by SeatedPlayer and should not be used elsewhere

	void attachSeat(SeatedPlayer seated) {
		detachSeat();
		this.seated = seated;
	}

	void detachSeat() {
		if (seated == null) return;
		seated.detachSlot();
		seated = null;
	}

	// to avoid player being on two storytelling simultaneously
	// is used by BloodGame and should not be used elsewhere

	void attachStorytelling(BloodGame game) {
		// do not call detachStorytelling here to avoid circular call
		if (storytelling != null) storytelling.removeStoryteller(this);
		this.storytelling = game;
	}

	void detachStorytelling() {
		if (storytelling == null) return;
		storytelling = null;
	}
}
