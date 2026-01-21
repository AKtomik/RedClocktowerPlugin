package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.GameToolbox;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierToolbox;
import io.github.aktomik.redclocktower.utils.brigadier.SubBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StorytellerSubVote extends SubBrigadierBase {

	public String name() {
		return "vote";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()

		.then(Commands.literal("nominate")
			.executes(this::nominateCheck)
			.then(Commands.argument("player", ArgumentTypes.player())
				.executes(ctx -> nominateChange(ctx, BrigadierToolbox.resolvePlayer(ctx)))))

		.then(Commands.literal("cancel")
			.executes(this::votingCancel))

		.then(Commands.literal("start")
			.executes(ctx -> votingStart(ctx, null))
			.then(Commands.argument("player", ArgumentTypes.player())
				.executes(ctx -> votingStart(ctx, BrigadierToolbox.resolvePlayer(ctx)))))

		.then(Commands.literal("pylori")
			.executes(this::pyloriCheck)
			.then(Commands.argument("player", ArgumentTypes.player())
				.executes(ctx -> pyloriChange(ctx, BrigadierToolbox.resolvePlayer(ctx)))))

		.then(Commands.literal("mount")
			.executes(ctx -> playerMount(ctx, null))
			.then(Commands.argument("player", ArgumentTypes.player())
				.executes(ctx -> playerMount(ctx, BrigadierToolbox.resolvePlayer(ctx)))))

		.then(Commands.literal("execute")
			.executes(ctx -> playerExecution(ctx, null, true))
			.then(Commands.argument("player", ArgumentTypes.player())
				.executes(ctx -> playerExecution(ctx, BrigadierToolbox.resolvePlayer(ctx), true))
				.then(Commands.argument("is deadly", BoolArgumentType.bool())
					.executes(ctx -> playerExecution(ctx, BrigadierToolbox.resolvePlayer(ctx), BrigadierToolbox.resolveBool("is deadly", ctx))))))
		;
	}

	private int nominateCheck(CommandContext<CommandSourceStack> ctx) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		// the action
		Player player = game.getNominatedPlayer();
		if (player == null)
			sender.sendRichMessage("there is no one actually nominated.");
		else
			sender.sendRichMessage("<b><target></b> is actually nominated.",
				Placeholder.parsed("target", player.getName())
			);
		return Command.SINGLE_SUCCESS;
	}
	private int nominateChange(CommandContext<CommandSourceStack> ctx, Player player) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNotVotingMoment(sender, game)) return Command.SINGLE_SUCCESS;
		if (!game.isPlayerIn(player))
		{
			sender.sendRichMessage("<red><b><target></b> is not in game.",  Placeholder.parsed("target", player.getName()));
			return Command.SINGLE_SUCCESS;
		}

		// the action
		game.changeNominatedPlayer(player);
		sender.sendRichMessage("<b><target></b> is <gold><b>nominated</b></gold>.",
			Placeholder.parsed("target", player.getName())
		);
		return Command.SINGLE_SUCCESS;
	}

	private int pyloriCheck(CommandContext<CommandSourceStack> ctx) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		// the action
		Player player = game.getPyloriPlayer();
		if (player == null)
			sender.sendRichMessage("there is no one on the pylori.");
		else
			sender.sendRichMessage("<b><target></b> is on the pylori.",
				Placeholder.parsed("target", player.getName())
			);
		return Command.SINGLE_SUCCESS;
	}
	private int pyloriChange(CommandContext<CommandSourceStack> ctx, Player player) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNotVotingMoment(sender, game)) return Command.SINGLE_SUCCESS;
		if (!game.isPlayerIn(player))
		{
			sender.sendRichMessage("<red><b><target></b> is not in game.",  Placeholder.parsed("target", player.getName()));
			return Command.SINGLE_SUCCESS;
		}

		// the action
		game.changePyloriPlayer(player, 0);
		sender.sendRichMessage("<b><target></b> is now <red><b>on the pylori</b></red>.",
			Placeholder.parsed("target", player.getName())
		);
		return Command.SINGLE_SUCCESS;
	}

	private int votingStart(CommandContext<CommandSourceStack> ctx, Player player) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		if (player != null)  nominateChange(ctx, player);
		Player nominatedPlayer = game.getNominatedPlayer();

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNotVotingMoment(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfVoteBusy(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIf(sender, nominatedPlayer == null, "there is no nominated player!")) return Command.SINGLE_SUCCESS;
		assert (nominatedPlayer != null);

		// the action
		game.startVoteProcess();
		sender.sendRichMessage("<aqua>starting the vote for <b><target></b>.",
			Placeholder.parsed("target", nominatedPlayer.getName())
		);
		return Command.SINGLE_SUCCESS;
	}

	private int votingCancel(CommandContext<CommandSourceStack> ctx) {

		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNotVotingMoment(sender, game)) return Command.SINGLE_SUCCESS;

		// the action
		sender.sendRichMessage("<dark_gray>canceling the last vote action...");
		game.cancelVoteProcess(sender);
		return Command.SINGLE_SUCCESS;
	}

	private int playerMount(CommandContext<CommandSourceStack> ctx, Player player) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		if (player != null)  pyloriChange(ctx, player);
		Player pyloriPlayer = game.getPyloriPlayer();

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNotVotingMoment(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfVoteBusy(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIf(sender, pyloriPlayer == null, "there is no one on the pylori!")) return Command.SINGLE_SUCCESS;
		assert (pyloriPlayer != null);

		// the action
		game.mountBeforeExecution();
		sender.sendRichMessage("<aqua>teleport <b><target></b> to the pylori.",
		Placeholder.parsed("target", pyloriPlayer.getName())
		);
		return Command.SINGLE_SUCCESS;
	}

	private int playerExecution(CommandContext<CommandSourceStack> ctx, Player player, boolean isReal) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		if (player != null)  pyloriChange(ctx, player);
		Player pyloriPlayer = game.getPyloriPlayer();

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNotVotingMoment(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfVoteBusy(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIf(sender, pyloriPlayer == null, "there is no one on the pylori!")) return Command.SINGLE_SUCCESS;
		assert (pyloriPlayer != null);

		// the action
		game.startExecuteProcess(isReal);
		sender.sendRichMessage("<aqua><u>executing <b><target></b>.",
			Placeholder.parsed("target", pyloriPlayer.getName())
		);
		return Command.SINGLE_SUCCESS;
	}
}