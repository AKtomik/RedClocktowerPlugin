package io.github.aktomik.redclocktower.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerNameTagEditor {

	private static final Map<UUID, TextDisplay> displays = new HashMap<>();
	private static final Map<UUID, Location> forcePlace = new HashMap<>();

	public static final float ABOVE_HEIGHT = 2f;

	static void createDisplay(Player player, Component displayName) {
		// remove old one if exists
		clearDisplay(player);

		Location loc = player.getLocation().add(0, ABOVE_HEIGHT, 0);
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

	public static void forcePlace(Player player, Location loc) {
		forcePlace.put(player.getUniqueId(), loc);
	}

	public static void forceUnplace(Player player) {
		forcePlace.remove(player.getUniqueId());
	}

	static void updatePosition(Player player) {
		UUID uuid = player.getUniqueId();
		TextDisplay display = displays.get(uuid);
		if (display != null && display.isValid()) {
			Location newLoc;
			Location forceLoc = forcePlace.get(uuid);
			if (player.getGameMode() == GameMode.SPECTATOR)
				newLoc = new Location(player.getWorld(), 0,-100, 0);
			else if (forceLoc != null)
				newLoc = forceLoc;
			else
				 newLoc = player.getLocation().add(0, ABOVE_HEIGHT, 0);
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
}