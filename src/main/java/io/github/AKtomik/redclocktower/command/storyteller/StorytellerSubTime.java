package io.github.AKtomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.AKtomik.redclocktower.brigadier.EnumArgument;
import io.github.AKtomik.redclocktower.brigadier.SubBrigadierBase;
import io.github.AKtomik.redclocktower.game.BloodGame;
import io.github.AKtomik.redclocktower.game.BloodGamePeriod;
import io.github.AKtomik.redclocktower.game.BloodGameState;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class StorytellerSubTime extends SubBrigadierBase {

	public String name() {
		return "time";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("period", EnumArgument.simple(BloodGamePeriod.class, "Invalid game period"))
		.executes(ctx -> {
			// arguments
			CommandSender sender = ctx.getSource().getSender();
			BloodGame game = BloodGame.WorldGame(ctx.getSource().getLocation().getWorld());
			final BloodGamePeriod period = ctx.getArgument("period", BloodGamePeriod.class);

			//	checks
			if (!game.isPlaying())
			{
				sender.sendRichMessage("<red>the game is not started!");
				return Command.SINGLE_SUCCESS;
			}

			// execute
			sender.sendRichMessage("<light_purple>switching to <b><period></b> time",
			Placeholder.parsed("period", period.toString())
			);
			game.switchTime(period);
			return Command.SINGLE_SUCCESS;
		})).executes(ctx -> {
			// arguments
			CommandSender sender = ctx.getSource().getSender();
			BloodGame game = BloodGame.WorldGame(ctx.getSource().getLocation().getWorld());

			// execute
			BloodGamePeriod gamePeriod = game.getTime();
			sender.sendRichMessage("game is in <b><period></b> time",
			Placeholder.component("period", Component.text(gamePeriod.toString()))
			);
			return Command.SINGLE_SUCCESS;
		});
	}
}