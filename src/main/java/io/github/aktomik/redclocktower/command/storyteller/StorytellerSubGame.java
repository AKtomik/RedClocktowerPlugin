package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.oldgame.OldBloodGame;
import io.github.aktomik.redclocktower.oldgame.OldGameStepAction;
import io.github.aktomik.redclocktower.oldgame.OldGameState;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class StorytellerSubGame extends BrigadierSub {
	public String name() {
		return "game";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("action", EnumArgument.simple(OldGameStepAction.class, "Invalid game step action"))
		.executes(ctx -> {
			// arguments
			CommandSender sender = ctx.getSource().getSender();
			OldBloodGame game = OldBloodGame.get(ctx.getSource().getLocation().getWorld());
			final OldGameStepAction gameAction = ctx.getArgument("action", OldGameStepAction.class);

			// execution
			sender.sendRichMessage("<dark_gray>running step <b><action></b>...",
			Placeholder.parsed("action", gameAction.toString())
			);
			game.doStep(gameAction, sender);
			return Command.SINGLE_SUCCESS;
		})).executes(ctx -> {
			// arguments
			CommandSender sender = ctx.getSource().getSender();
			OldBloodGame game = OldBloodGame.get(ctx.getSource().getLocation().getWorld());

			// execute
			OldGameState gameState = game.getState();
			sender.sendRichMessage("game is in state <b><state></b>",
			Placeholder.parsed("state", gameState.toString())
			);
			return Command.SINGLE_SUCCESS;
		});
	}
}