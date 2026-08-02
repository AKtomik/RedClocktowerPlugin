package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.oldgame.OldBloodGame;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.function.Function;

public class GameToolbox {

	private GameToolbox() {}// is a static class

	// fails
	public static boolean failIf(CommandSender sender, boolean condition, String errorMessage) {
		if (condition) {
			sender.sendRichMessage("<red>"+ errorMessage);
			return true;
		}
		return false;
	}

	@Contract("_, null -> true; _, !null -> false")
	public static boolean failIfNoGame(CommandSender sender, BloodGame game) {
		return failIf(sender, (game == null), "no game setup");
	}
	public static boolean failIfNotStarted(CommandSender sender, BloodGame game) {
		return failIf(sender, !game.isStarted(), "the game is not started");
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

	public static boolean failIfNoPlayers(CommandSender sender, List<Player> players) {
		return failIf(sender, (players.isEmpty()), "there is no player selected");
	}
	public static boolean failIfNoPlayer(CommandSender sender, Player player) {
		return failIf(sender, (player == null), "there is no player selected");
	}

	public static <T> void processEach(
		CommandSender sender,
		List<T> targets,
		Function<T, String> nameOf,
		Function<T, CommandLoopResult> loopAction,
		GameCommandLoopStringRecord stringRecord
	) {
		boolean single = targets.size() <= 1;
		int successCount = 0;

		for (T target : targets) {
			CommandLoopResult result = loopAction.apply(target);
			String name = nameOf.apply(target);

			if (result.success()) {
				successCount++;
			}
			if (single) sender.sendRichMessage(result.message(), Placeholder.parsed("target", name));
		}

		if (!single) {
			sender.sendRichMessage(stringRecord.multipleSuccess(),
			Placeholder.parsed("count", Integer.toString(successCount)),
			Placeholder.parsed("word", successCount > 1 ? stringRecord.wordPlural() : stringRecord.wordSingular())
			);
		}
	}
}
