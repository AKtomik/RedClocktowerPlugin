package io.github.aktomik.redclocktower.utils.renametag;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerRenameTag {

	private static final Map<UUID, TextDisplay> displays = new HashMap<>();

	private PlayerRenameTag() {}

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
 		player.addPassenger(display);// will also hide the name tag
		displays.put(player.getUniqueId(), display);
	}

	static void syncDisplay(Player player) {
		TextDisplay display = displays.get(player.getUniqueId());
		if (display != null && display.isValid()) {
			display.teleport(player);
			player.addPassenger(display);
		}
	}

	static void refreshVisible(Player player) {
		TextDisplay display = displays.get(player.getUniqueId());
		if (display != null && display.isValid()) {
			display.setVisibleByDefault(!player.isDead() && display.text() != Component.empty() && player.getGameMode() != GameMode.SPECTATOR);
		}
	}

	public static void changeDisplay(Player player, Component newName) {
		TextDisplay display = displays.get(player.getUniqueId());
		if (display != null && display.isValid()) {
			display.text(newName);
		} else {
			createDisplay(player, newName);
		}
		refreshVisible(player);
		syncDisplay(player);// not necessary
	}

	public static void setDisplayBackColor(Player player, @Nullable Color color) {
		TextDisplay display = displays.get(player.getUniqueId());
		if (display == null || !display.isValid()) return;
		display.setDefaultBackground(false);
		if (color != null) display.setBackgroundColor(color);
	}

	public static void resetDisplayBackColor(Player player) {
		TextDisplay display = displays.get(player.getUniqueId());
		if (display == null || !display.isValid()) return;
		display.setDefaultBackground(true);
	}

	public static void clearDisplay(OfflinePlayer player) {
		TextDisplay display = displays.remove(player.getUniqueId());
		if (display != null) {
			display.remove();
		}
	}
}