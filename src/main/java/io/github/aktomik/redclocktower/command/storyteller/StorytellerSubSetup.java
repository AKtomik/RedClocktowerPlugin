package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.command.setup.TownArgumentType;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.TownHall;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class StorytellerSubSetup extends BrigadierSub {
	public String name() {
		return "setup";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("town", new TownArgumentType())
		.executes(ctx -> {
			// arguments
			final CommandSender sender = ctx.getSource().getSender();
			final World world = ctx.getSource().getLocation().getWorld();
			final TownHall townHall = ctx.getArgument("town", TownHall.class);

			// check
			if (BloodGame.get(townHall) != null)
			{
				sender.sendRichMessage("<red>the townhall <b><town></b> is already setup",
					Placeholder.parsed("town", townHall.getTownName())
				);
				return Command.SINGLE_SUCCESS;
			}
			if (BloodGame.get(world) != null)
			{
				sender.sendRichMessage("<red>there is another game setup in this world");
				return Command.SINGLE_SUCCESS;
			}

			// execute
			BloodGame.create(townHall);
			sender.sendRichMessage("setup townhall <b><aqua><town></aqua></b> for a game",
				Placeholder.parsed("town", townHall.getTownName())
			);
			return Command.SINGLE_SUCCESS;
		})).executes(ctx -> {
			// arguments
			final CommandSender sender = ctx.getSource().getSender();
			final World world = ctx.getSource().getLocation().getWorld();

			// execute
			BloodGame game = BloodGame.get(world);
			if (game == null)
			{
				sender.sendRichMessage("there is no game in this world");
				return Command.SINGLE_SUCCESS;
			}
			sender.sendRichMessage("game of the townhall <b><town></b> is setup on this world",
				Placeholder.parsed("town", game.getTownHall().getTownName())
			);
			return Command.SINGLE_SUCCESS;
		});
	}
}