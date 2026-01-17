package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.aktomik.redclocktower.game.BloodGame;
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
			.then(Commands.argument("player", ArgumentTypes.players()))
				.executes(this::nominateChange))
		.then(Commands.literal("cancel")
			.executes(this::votingCancel))
		.then(Commands.literal("start")
			.executes(ctx -> votingStart(ctx, null))
			.then(Commands.argument("player", ArgumentTypes.players()))
				.executes(ctx -> votingStart(ctx, ctx.getArgument("player", Player.class))))
		.then(Commands.literal("execute")
			.executes(ctx -> playerExecution(ctx, null))
			.then(Commands.argument("player", ArgumentTypes.players()))
				.executes(ctx -> playerExecution(ctx, ctx.getArgument("player", Player.class))))
		;
	}

	private int nominateCheck(CommandContext<CommandSourceStack> ctx) {
		return Command.SINGLE_SUCCESS;
	};

	private int nominateChange(CommandContext<CommandSourceStack> ctx) {
		return Command.SINGLE_SUCCESS;
	};

	private int votingStart(CommandContext<CommandSourceStack> ctx, Player player) {
		return Command.SINGLE_SUCCESS;
	};

	private int votingCancel(CommandContext<CommandSourceStack> ctx) {
		return Command.SINGLE_SUCCESS;
	};

	private int playerExecution(CommandContext<CommandSourceStack> ctx, Player player) {
		return Command.SINGLE_SUCCESS;
	};
}