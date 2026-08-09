package io.github.aktomik.redclocktower.commandbuild.tools;

import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.oldgame.OldBloodGame;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.function.Function;

public class CommandToolbox {

	private CommandToolbox() {}// is a static class

	// fails
	public static boolean failIf(CommandSender sender, boolean condition, String errorMessage) {
		if (condition) {
			sender.sendRichMessage("<red>"+ errorMessage);
			return true;
		}
		return false;
	}

	public static boolean failIfNoPlayers(CommandSender sender, List<Player> players) {
		return failIf(sender, (players.isEmpty()), "there is no player selected");
	}
	public static boolean failIfNoPlayer(CommandSender sender, Player player) {
		return failIf(sender, (player == null), "there is no player selected");
	}

	@Contract("_, null -> true; _, !null -> false")
	public static boolean failIfNoGame(CommandSender sender, BloodGame game) {
		return failIf(sender, (game == null), "no game setup");
	}
	public static boolean failIfNotStarted(CommandSender sender, BloodGame game) {
		return failIf(sender, !game.isStarted(), "the game is not started");
	}

	public static boolean failIfVoteBusy(CommandSender sender, BloodGame game) {
		return failIf(sender, game.getCircle().isVoteSystemBusy(), "vote or execution is running");
	}

	@Deprecated
	public static boolean failIfNotVotingMoment(CommandSender sender, OldBloodGame game) {
		return failIf(sender, (!game.isVoteMoment()), "this is not the time to vote");
	}
	@Deprecated
	public static boolean failIfNotReady(CommandSender sender, OldBloodGame game) {
		return failIf(sender, (!game.isReady()), "the game is not ready!");
	}

	public static <T> List<CommandLoopResult<T>> processEach(
		List<T> targets,
		Function<T, CommandLoopResult<T>> action
	) {
		return targets.stream()
		.map(action)
		.toList();
	}

	public static <T> void sendProcessResult(
		CommandSender sender,
		List<CommandLoopResult<T>> results,
		Function<T, String> nameOf,
		String successSummary,
		String singularWord,
		String pluralWord
	) {
		if (results.size() == 1) {
			CommandLoopResult<T> result = results.getFirst();
			sender.sendRichMessage(result.message(),
				Placeholder.parsed("target", nameOf.apply(result.target()))
			);
			return;
		}

		long successCount = results.stream().filter(CommandLoopResult::success).count();
		sender.sendRichMessage(successSummary,
			Placeholder.parsed("count", String.valueOf(successCount)),
			Placeholder.parsed("word", successCount > 1 ? pluralWord : singularWord)
		);
	}
}
