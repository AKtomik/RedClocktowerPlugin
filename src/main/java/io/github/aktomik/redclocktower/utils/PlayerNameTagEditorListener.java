package io.github.aktomik.redclocktower.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static io.github.aktomik.redclocktower.utils.PlayerNameTagEditor.clearDisplay;

public class PlayerNameTagEditorListener implements Listener {
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Bukkit.getLogger().info("tag quit:"+event.getPlayer());
		clearDisplay(event.getPlayer());
	}
}
