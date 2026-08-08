package io.github.aktomik.redclocktower.utils;

import net.kyori.adventure.text.Component;
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
		clearDisplay(player);

		TextDisplay display = player.getWorld().spawn(Objects.requireNonNull(player.getLocation()), TextDisplay.class, text -> {
			text.text(displayName);
			text.setBillboard(Display.Billboard.CENTER);
			text.setAlignment(TextDisplay.TextAlignment.CENTER);
			text.setSeeThrough(false);
			text.setDefaultBackground(false);
			text.setPersistent(false);

			Transformation trans = text.getTransformation();
			trans.getTranslation().set(0f, .3f, 0f);
			text.setTransformation(trans);
		});
 		player.addPassenger(display);// will hide the name tag too
		displays.put(player.getUniqueId(), display);
	}

	static void syncDisplay(Player player) {
		TextDisplay display = displays.get(player.getUniqueId());
		if (display != null && display.isValid()) {
			display.teleport(player);
			player.addPassenger(display);
		}
	}

	static void setVisibleDisplay(Player player, boolean visible) {
		TextDisplay display = displays.get(player.getUniqueId());
		if (display != null && display.isValid()) {
			display.setVisibleByDefault(visible);
		}
	}

	public static void changeDisplay(Player player, Component newName) {
		TextDisplay display = displays.get(player.getUniqueId());
		if (display != null && display.isValid()) {
			display.text(newName);
		} else {
			createDisplay(player, newName);
		}
	}

	public static void clearDisplay(OfflinePlayer player) {
		TextDisplay display = displays.remove(player.getUniqueId());
		if (display != null) {
			display.remove();
		}
	}
}