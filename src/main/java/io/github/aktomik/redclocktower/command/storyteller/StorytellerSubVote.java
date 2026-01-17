package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
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
			.then(Commands.argument("players", ArgumentTypes.players()))
				.executes(ctx -> votingStart(ctx, ctx.getArgument("player", Player.class))))
		.then(Commands.literal("execute")
			.executes(ctx -> playerExecution(ctx, null))
			.then(Commands.argument("players", ArgumentTypes.players()))
				.executes(ctx -> playerExecution(ctx, ctx.getArgument("player", Player.class))))
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
	};

	private int nominateChange(CommandContext<CommandSourceStack> ctx, Player player) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIf(sender, !game.isVoteMoment(), "this is not the time to vote")) return Command.SINGLE_SUCCESS;
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
	};

	private int votingStart(CommandContext<CommandSourceStack> ctx, Player player) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		if (player != null)  nominateChange(ctx, player);
		Player nominatedPlayer = game.getNominatedPlayer();

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIf(sender, !game.isVoteMoment(), "this is not the time to vote")) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIf(sender, nominatedPlayer == null, "there is no nominated player!")) return Command.SINGLE_SUCCESS;
		assert (nominatedPlayer != null);

		// the action
		game.startingVoteProcess();
		sender.sendRichMessage("<aqua>starting the vote for <b><target></b>.",
			Placeholder.parsed("target", nominatedPlayer.getName())
		);
		return Command.SINGLE_SUCCESS;
	};

	private int votingCancel(CommandContext<CommandSourceStack> ctx) {

		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIf(sender, !game.isVoteMoment(), "this is not the time to vote")) return Command.SINGLE_SUCCESS;

		// the action
		game.cancelVoteProcess();
		game.removeNominatedPlayer();
		sender.sendRichMessage("<aqua>vote & nomination <red>canceled</red>.");
		return Command.SINGLE_SUCCESS;
	};

	private int playerExecution(CommandContext<CommandSourceStack> ctx, Player player) {
		return Command.SINGLE_SUCCESS;
	};
}