package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.aktomik.redclocktower.utils.brigadier.SubBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

public class StorytellerSubVote extends SubBrigadierBase {

	public String name() {
		return "vote";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.literal("nominate")
			.executes(subNominateCheck)
			.then(Commands.argument("player", ArgumentTypes.players()))
				.executes(subNominateChange))
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

	Command<CommandSourceStack> subNominateCheck = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subNominateChange = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

	private int votingStart(CommandContext<CommandSourceStack> ctx, Player player) {
		return Command.SINGLE_SUCCESS;
	};

	private int playerExecution(CommandContext<CommandSourceStack> ctx, Player player) {
		return Command.SINGLE_SUCCESS;
	};
}