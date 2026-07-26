package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class SetupSubModify extends BrigadierSub {
	public String name() {
		return "modify";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("town", new TownArgumentType())
		.then(new SetupSubModifyPosition().root())
		.then(new SetupSubModifySettings().root())
		.then(new SetupSubModifyChair().root())
		);
	}

}
