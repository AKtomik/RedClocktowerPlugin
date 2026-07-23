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
import org.bukkit.entity.Player;

public class SetupSubDelete extends BrigadierSub {
	public String name() {
		return "delete";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("town name", StringArgumentType.word())
			.executes(ctx -> {
				// arguments
				CommandSender sender = ctx.getSource().getSender();
				String townName = StringArgumentType.getString(ctx, "town name");
				World world = ctx.getSource().getLocation().getWorld();
				Player player = (Player)ctx.getSource().getExecutor();

				// execute
				boolean success = TownHall.delete(world, townName);
				if (!success)
				{
					sender.sendRichMessage("<red>there is no townhall named <b><name></b>.",
						Placeholder.parsed("name", townName)
					);
					return Command.SINGLE_SUCCESS;
				}

				sender.sendRichMessage("townhall <b><name></b> <red>deleted</red>!",
					Placeholder.parsed("name", townName)
				);
				return Command.SINGLE_SUCCESS;
			}
			)
		);
	}

}
