package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.oldgame.OldBloodGame;
import io.github.aktomik.redclocktower.oldgame.OldBloodPlayer;
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

	public static boolean failIfNoGame(CommandSender sender, BloodGame game) {
		return failIf(sender, (game == null), "this is not the time to vote");
	}

	@Deprecated
	public static boolean failIfNotVotingMoment(CommandSender sender, OldBloodGame game) {
		return failIf(sender, (!game.isVoteMoment()), "this is not the time to vote");
	}
	@Deprecated
	public static boolean failIfVoteBusy(CommandSender sender, OldBloodGame game) {
		return failIf(sender, game.isVoteSystemBusy(), "vote or execution is running");
	}
	@Deprecated
	public static boolean failIfNotReady(CommandSender sender, OldBloodGame game) {
		return failIf(sender, (!game.isReady()), "the game is not ready!");
	}

	@Deprecated
	public static boolean failIfNoPlayers(CommandSender sender, List<Player> players) {
		return failIf(sender, (players.isEmpty()), "there is no player selected!");
	}
	@Deprecated
	public static boolean failIfNoPlayer(CommandSender sender, Player player) {
		return failIf(sender, (player == null), "there is no player selected!");
	}

	@Deprecated
	public static void forEachValidPlayer(
	CommandSender sender,
	OldBloodGame game,
	List<Player> players,
	BiConsumer<Player, OldBloodPlayer> action
	) {
		for (Player player : players) {
			if (!game.isPlayerIn(player)) {
				sender.sendRichMessage(
				"<red><b><target></b> is not in game.",
				Placeholder.parsed("target", player.getName())
				);
				continue;
			}
			action.accept(player, OldBloodPlayer.get(player));
		}
	}
}
