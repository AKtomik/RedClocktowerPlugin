package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.TownHall;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class SetupSubClone extends BrigadierSub {
	public String name() {
		return "clone";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("town", new TownArgumentType())
		.then(Commands.argument("new town name", StringArgumentType.word())
			.executes(ctx -> {
				// arguments
				CommandSender sender = ctx.getSource().getSender();
				TownHall townHall = ctx.getArgument("town", TownHall.class);
				String townName = StringArgumentType.getString(ctx, "new town name");

				// execute
				TownHall newTownHall = TownHall.clone(townHall, townName);
				if (newTownHall == null)
				{
					sender.sendRichMessage("<red>there is already a townhall named <b><name></b>.",
						Placeholder.parsed("name", townName)
					);
					return Command.SINGLE_SUCCESS;
				}

				sender.sendRichMessage("townhall <b><origin_name></b> <green>cloned</green> to  <b><new_name></b>.",
					Placeholder.parsed("origin_name", townName),
					Placeholder.parsed("new_name", townName)
				);
				return Command.SINGLE_SUCCESS;
			}
			)
		));
	}

}
