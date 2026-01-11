package io.github.AKtomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.AKtomik.redclocktower.brigadier.EnumArgument;
import io.github.AKtomik.redclocktower.brigadier.SubBrigadierBase;
import io.github.AKtomik.redclocktower.game.BloodGame;
import io.github.AKtomik.redclocktower.game.BloodGameAction;
import io.github.AKtomik.redclocktower.game.BloodGameState;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
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
			BloodGame game = BloodGame.WorldGame(ctx.getSource().getLocation().getWorld());
			final BloodGameAction gameAction = ctx.getArgument("action", BloodGameAction.class);

			// execution
			sender.sendRichMessage("<light_purple>running action <b><action></b>",
			Placeholder.parsed("action", gameAction.toString())
			);
			game.doAction(gameAction);

			// return
			return Command.SINGLE_SUCCESS;
		})).executes(ctx -> {
			// arguments
			CommandSender sender = ctx.getSource().getSender();
			BloodGame game = BloodGame.WorldGame(ctx.getSource().getLocation().getWorld());

			BloodGameState gameState = game.getState();
			sender.sendRichMessage("game is in state <b><state></b>",
			Placeholder.component("state", Component.text(gameState.toString()))
			);

			return Command.SINGLE_SUCCESS;
		});
	}
}