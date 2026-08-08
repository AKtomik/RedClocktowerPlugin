package io.github.aktomik.redclocktower.utils;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static io.github.aktomik.redclocktower.utils.PlayerNameTagEditor.*;

public class PlayerNameTagEditorListener implements Listener {
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		clearDisplay(event.getPlayer());
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		syncDisplay(event.getPlayer());
	}

	@EventHandler
	public void onGameModeChange(PlayerGameModeChangeEvent event) {
		setVisibleDisplay(event.getPlayer(), event.getNewGameMode() != GameMode.SPECTATOR);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		setVisibleDisplay(event.getPlayer(), false);
	}

	@EventHandler
	public void onRespawn(PlayerPostRespawnEvent event) {
		Player player = event.getPlayer();
		syncDisplay(player);
		setVisibleDisplay(player, player.getGameMode() != GameMode.SPECTATOR);
	}
}
