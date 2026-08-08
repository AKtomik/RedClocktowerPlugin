package io.github.aktomik.redclocktower.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerNameTagEditor {

	private static final Map<UUID, TextDisplay> displays = new HashMap<>();

	private PlayerNameTagEditor() {}

	private static void createDisplay(Player player, Component displayName) {
		Bukkit.getLogger().info("create display:"+player+" count:"+displays.size());
		clearDisplay(player);

		TextDisplay textDisplay = player.getWorld().spawn(Objects.requireNonNull(player.getLocation()), TextDisplay.class, text -> {
			text.text(displayName);
			text.setBillboard(Display.Billboard.CENTER);
			text.setAlignment(TextDisplay.TextAlignment.CENTER);
			text.setSeeThrough(false);
			text.setDefaultBackground(false);
			text.setPersistent(false);

			Transformation t = text.getTransformation();
			t.getTranslation().set(0f, .3f, 0f);
			text.setTransformation(t);
		});

 		player.addPassenger(textDisplay);
		displays.put(player.getUniqueId(), textDisplay);
		Bukkit.getLogger().info("puted count:"+displays.size());
	}

	public static void changeDisplay(Player player, Component newName) {
		Bukkit.getLogger().info("change display:"+player+" count:"+displays.size());
		TextDisplay display = displays.get(player.getUniqueId());
		if (display != null && display.isValid()) {
			display.text(newName);
		} else {
			createDisplay(player, newName);
		}
	}

	public static void clearDisplay(OfflinePlayer player) {
		TextDisplay display = displays.remove(player.getUniqueId());
		Bukkit.getLogger().info("cleardisplay player:"+player+" display:"+display+" count:"+displays.size());
		if (display != null) {
			Bukkit.getLogger().info("clearing display:"+display+" count:"+displays.size());
			display.remove();
			Bukkit.getLogger().info("cleared display:"+display+" count:"+displays.size());
		}
	}
}