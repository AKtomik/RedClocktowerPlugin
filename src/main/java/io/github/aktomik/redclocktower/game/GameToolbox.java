package io.github.aktomik.redclocktower.game;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;

public class GameToolbox {

	private GameToolbox() {};// is a static class

	public static boolean failIfNotReady(CommandSender sender, BloodGame game) {
		if (!game.isReady()) {
			sender.sendRichMessage("<red>the game is not ready!");
			return true;
		}
		return false;
	}
	public static boolean failIfEmpty(CommandSender sender, List<Player> players) {
		if (players.isEmpty()) {
			sender.sendRichMessage("<red>there is no player•s selected!");
			return true;
		}
		return false;
	}

	public static void forEachValidPlayer(
	CommandSender sender,
	BloodGame game,
	List<Player> players,
	BiConsumer<Player, BloodPlayer> action
	) {
		for (Player player : players) {
			if (!game.isPlayerIn(player)) {
				sender.sendRichMessage(
				"<red><b><target></b> is not in game.",
				Placeholder.parsed("target", player.getName())
				);
				continue;
			}
			action.accept(player, BloodPlayer.get(player));
		}
	}
}
