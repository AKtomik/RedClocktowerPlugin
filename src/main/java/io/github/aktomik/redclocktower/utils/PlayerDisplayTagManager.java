package io.github.aktomik.redclocktower.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDisplayTagManager implements Listener {

	private static final Map<UUID, TextDisplay> displays = new HashMap<>();

	public static final float aboveHeight = 2f;

	static void createDisplay(Player player, Component displayName) {
		// remove old one if exists
		clearDisplay(player);

		Location loc = player.getLocation().add(0, aboveHeight, 0);
		TextDisplay textDisplay = player.getWorld().spawn(loc, TextDisplay.class,  text -> {
			text.text(displayName);
			text.setBillboard(Display.Billboard.CENTER);
			text.setAlignment(TextDisplay.TextAlignment.CENTER);
			text.setSeeThrough(false);
			text.setDefaultBackground(false);
		});

		displays.put(player.getUniqueId(), textDisplay);
	}

	public static void changeDisplay(Player player, Component newName) {
		TextDisplay display = displays.get(player.getUniqueId());
		if (display != null && display.isValid()) {
			display.text(newName);
		} else {
			createDisplay(player, newName);
		}
	}

	public static void clearDisplay(Player player) {
		TextDisplay display = displays.remove(player.getUniqueId());
		if (display != null && display.isValid()) {
			display.remove();
		}
	}

	static void updatePosition(Player player) {
		TextDisplay display = displays.get(player.getUniqueId());
		if (display != null && display.isValid()) {
			Location newLoc = player.getLocation().add(0, aboveHeight, 0);
			display.teleport(newLoc);
		}
	}

	public static void startUpdateTask(Plugin plugin) {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				updatePosition(player);
			}
		}, 0L, 1L);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		clearDisplay(event.getPlayer());
	}
}