package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.commandbuild.tools.GameCommand;
import io.github.aktomik.redclocktower.game.GamePeriod;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class StorytellerSubTime extends BrigadierSub {

	public String name() {
		return "time";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.executes(subTimeCheck)
		.then(Commands.argument("period", EnumArgument.simple(GamePeriod.class, "Invalid game period"))
			.executes(subTimeChange)
		)
		;
		}

	Command<CommandSourceStack> subTimeCheck = GameCommand.wrap(((ctx, sender, game) -> {
		GamePeriod gamePeriod = game.getPeriod();
		sender.sendRichMessage("game is in <b><period></b> time",
		Placeholder.parsed("period", gamePeriod.toString())
		);
		return Command.SINGLE_SUCCESS;
	}));

	Command<CommandSourceStack> subTimeChange = GameCommand.wrap(((ctx, sender, game) -> {
		// arguments
		final GamePeriod period = ctx.getArgument("period", GamePeriod.class);

		//	checks
		if (!game.isStarted()) {
			sender.sendRichMessage("<red>the game is not started!");
			return Command.SINGLE_SUCCESS;
		}

		// execute
		sender.sendRichMessage("<light_purple>switching to <b><period></b> time",
		Placeholder.parsed("period", period.toString())
		);
		game.switchPeriod(period, sender);
		return Command.SINGLE_SUCCESS;
	}));
}