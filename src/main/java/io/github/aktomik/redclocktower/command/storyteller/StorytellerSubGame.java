package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
import io.github.aktomik.redclocktower.utils.brigadier.SubBrigadierBase;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.BloodGameAction;
import io.github.aktomik.redclocktower.game.BloodGameState;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class StorytellerSubGame extends SubBrigadierBase {
	public String name() {
		return "game";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("action", EnumArgument.simple(BloodGameAction.class, "Invalid game action"))
		.executes(ctx -> {
			// arguments
			CommandSender sender = ctx.getSource().getSender();
			BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
			final BloodGameAction gameAction = ctx.getArgument("action", BloodGameAction.class);

			// execution
			sender.sendRichMessage("<dark_gray>running action <b><action></b>...",
			Placeholder.parsed("action", gameAction.toString())
			);
			game.doAction(gameAction, sender);
			return Command.SINGLE_SUCCESS;
		})).executes(ctx -> {
			// arguments
			CommandSender sender = ctx.getSource().getSender();
			BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

			// execute
			BloodGameState gameState = game.getState();
			sender.sendRichMessage("game is in state <b><state></b>",
			Placeholder.parsed("state", gameState.toString())
			);
			return Command.SINGLE_SUCCESS;
		});
	}
}