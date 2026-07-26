package io.github.aktomik.redclocktower.game;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

public class BloodPlayer {

	private final OfflinePlayer offPlayer;
	private SeatedPlayer seated;
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

	void joinSeat(SeatedPlayer seated) {
		leaveSeat();
		this.seated = seated;
	}

	void leaveSeat() {
		if (seated == null) return;
		seated.detachSlot();
		seated = null;
	}
}
