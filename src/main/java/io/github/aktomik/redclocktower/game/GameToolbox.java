package io.github.aktomik.redclocktower.game;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;

public class GameToolbox {

	private GameToolbox() {}// is a static class

	public static boolean failIf(CommandSender sender, boolean condition, String errorMessage) {
		if (condition) {
			sender.sendRichMessage("<red>"+ errorMessage);
			return true;
		}
		return false;
	}

	public static boolean failIfNotVotingMoment(CommandSender sender, BloodGame game) {
		return failIf(sender, (!game.isVoteMoment()), "this is not the time to vote");
	}
	public static boolean failIfVoteBusy(CommandSender sender, BloodGame game) {
		return failIf(sender, game.isVoteSystemBusy(), "vote or execution is running");
	}
	public static boolean failIfNotReady(CommandSender sender, BloodGame game) {
		return failIf(sender, (!game.isReady()), "the game is not ready!");
	}
	public static boolean failIfNoPlayers(CommandSender sender, List<Player> players) {
		return failIf(sender, (players.isEmpty()), "there is no player selected!");
	}
	public static boolean failIfNoPlayer(CommandSender sender, Player player) {
		return failIf(sender, (player == null), "there is no player selected!");
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
