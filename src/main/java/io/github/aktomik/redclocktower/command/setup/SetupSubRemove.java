package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.TownHall;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class SetupSubRemove extends BrigadierSub {
	public String name() {
		return "remove";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("town", new TownArgumentType())
			.executes(ctx -> {
				// arguments
				CommandSender sender = ctx.getSource().getSender();
				TownHall townHall = ctx.getArgument("town", TownHall.class);

				// execute
				TownHall.delete(townHall.getWorld(), townHall.getTownName());
				sender.sendRichMessage("townhall <b><name></b> <red>deleted</red>.",
					Placeholder.parsed("name", townHall.getTownName())
				);
				return Command.SINGLE_SUCCESS;
			}
			)
		);
	}

}
