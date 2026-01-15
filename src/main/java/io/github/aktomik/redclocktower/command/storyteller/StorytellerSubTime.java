package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
import io.github.aktomik.redclocktower.utils.brigadier.SubBrigadierBase;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.BloodGamePeriod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
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
			BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
			final BloodGamePeriod period = ctx.getArgument("period", BloodGamePeriod.class);

			//	checks
			if (!game.isStarted())
			{
				sender.sendRichMessage("<red>the game is not started!");
				return Command.SINGLE_SUCCESS;
			}

			// execute
			sender.sendRichMessage("<light_purple>switching to <b><period></b> time",
			Placeholder.parsed("period", period.toString())
			);
			game.switchTime(period, sender);
			return Command.SINGLE_SUCCESS;
		})).executes(ctx -> {
			// arguments
			CommandSender sender = ctx.getSource().getSender();
			BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

			// execute
			BloodGamePeriod gamePeriod = game.getTime();
			sender.sendRichMessage("game is in <b><period></b> time",
			Placeholder.parsed("period", gamePeriod.toString())
			);
			return Command.SINGLE_SUCCESS;
		});
	}
}