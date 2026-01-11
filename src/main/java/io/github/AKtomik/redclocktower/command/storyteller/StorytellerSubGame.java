package io.github.AKtomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.AKtomik.redclocktower.brigadier.EnumArgument;
import io.github.AKtomik.redclocktower.brigadier.SubBrigadierBase;
import io.github.AKtomik.redclocktower.game.BloodGame;
import io.github.AKtomik.redclocktower.game.BloodGameAction;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.command.CommandSender;

public class StorytellerSubGame extends SubBrigadierBase {
	public String name() {
		return "game";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("action", EnumArgument.simple(BloodGameAction.class, "Invalid blood period"))
		.executes(ctx -> {
			// arguments
			CommandSender sender = ctx.getSource().getSender();
			Location location = ctx.getSource().getLocation();
			World world = location.getWorld();
			BloodGame game = BloodGame.WorldGame(world);
			final BloodGameAction gameAction = ctx.getArgument("action", BloodGameAction.class);


			sender.sendMessage(
			Component.text("doing action ").color(NamedTextColor.LIGHT_PURPLE)
			.append(Component.text(gameAction.toString()).color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
			.append(Component.text("").color(NamedTextColor.LIGHT_PURPLE))
			);
			game.doAction(gameAction);
			return Command.SINGLE_SUCCESS;
		})).executes(ctx -> {

			return Command.SINGLE_SUCCESS;
		});
	}
}