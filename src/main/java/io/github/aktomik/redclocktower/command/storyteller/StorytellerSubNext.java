package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.commandbuild.tools.CommandToolbox;
import io.github.aktomik.redclocktower.commandbuild.tools.GameCommand;
import io.github.aktomik.redclocktower.game.GameAction;
import io.github.aktomik.redclocktower.oldgame.OldGameAction;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.oldgame.OldBloodGame;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class StorytellerSubNext extends BrigadierSub {
	public String name() {
		return "next";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.executes(
			GameCommand.wrap((ctx, sender, game) -> {
				if (CommandToolbox.failIfNotStarted(sender, game)) return 0;
				GameAction.next.accept(game, sender);
				return Command.SINGLE_SUCCESS;
			})
		)
		;
	}
}