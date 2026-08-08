package io.github.aktomik.redclocktower.utils;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static io.github.aktomik.redclocktower.utils.PlayerNameTagEditor.clearDisplay;
import static io.github.aktomik.redclocktower.utils.PlayerNameTagEditor.syncDisplay;
import static io.github.aktomik.redclocktower.utils.PlayerNameTagEditor.setVisibleDisplay;

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
}
