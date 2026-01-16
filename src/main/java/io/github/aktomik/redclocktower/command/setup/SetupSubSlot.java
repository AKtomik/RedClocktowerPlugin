package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.BloodSlotPlace;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
import io.github.aktomik.redclocktower.utils.brigadier.SubBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;

public class SetupSubSlot extends SubBrigadierBase {
	public String name() {
		return "slot";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.literal("add_new")
			.executes(subAdd))
		.then(Commands.literal("remove_last")
			.executes(subRemove))
		.then(Commands.literal("edit")
			.then(Commands.argument("slot number", ArgumentTypes.integerRange())
				.then(Commands.literal("position")
					.then(Commands.argument("place", EnumArgument.simple(BloodSlotPlace.class, "invalid slot place"))
						.executes(subEditPositionCheck)
						.then(Commands.argument("position", ArgumentTypes.blockPosition())
							.executes(subEditPositionChange))
		))));
	}


	Command<CommandSourceStack> subRemove = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subAdd = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subEditPositionCheck = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subEditPositionChange = ctx -> {
		return Command.SINGLE_SUCCESS;
	};
}
