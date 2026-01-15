package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.utils.brigadier.SubBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;

public class StorytellerSubSettings extends SubBrigadierBase {

	public String name() { return "settings"; }

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.executes(subSlotLimitCheck)
		.then(Commands.argument("new value", ArgumentTypes.integerRange())
			.executes(subSlotLimitChange)
		);
	}

	Command<CommandSourceStack> subSlotLimitCheck = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subSlotLimitChange = ctx -> {
		return Command.SINGLE_SUCCESS;
	};
}
