package io.github.aktomik.redclocktower.game;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

public class BloodPlayer {

	Player player;
	private BloodPlayer(Player player) {
		this.player = player;
	}

	private static final Map<Player, BloodPlayer> playerToBloodPlayerMap = new HashMap<>();

	@NullMarked
	public static BloodPlayer get(Player player) {
		// find
		BloodPlayer playerFound = playerToBloodPlayerMap.get(player);
		if (playerFound != null) return playerFound;

		// create
		BloodPlayer playerCreated = new BloodPlayer(player);
		playerToBloodPlayerMap.put(player, playerCreated);
		return playerCreated;
	}

	// access
	public Player getPlayer() {
		return player;
	}
}
