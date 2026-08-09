package io.github.aktomik.redclocktower.utils.renametag;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.github.aktomik.redclocktower.RedClocktower;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static io.github.aktomik.redclocktower.utils.renametag.PlayerRenameTag.*;

public class PlayerRenameTagListener implements Listener {
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
		Bukkit.getScheduler().runTask(RedClocktower.plugin(), () -> {
			if (event.getPlayer().isOnline()) refreshVisible(event.getPlayer());
		});
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		refreshVisible(event.getPlayer());
	}

	@EventHandler
	public void onRespawn(PlayerPostRespawnEvent event) {
		Player player = event.getPlayer();
		syncDisplay(player);
		refreshVisible(player);
	}
}
