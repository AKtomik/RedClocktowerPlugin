package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.TownHall;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SetupSubList extends BrigadierSub {
	public String name() {
		return "list";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
			.executes(ctx -> {
				// arguments
				CommandSender sender = ctx.getSource().getSender();
				World world = ctx.getSource().getLocation().getWorld();

				// execute
				List<String> townList = TownHall.getWorldTowns(ctx.getSource().getLocation().getWorld()).stream().toList();
				if (townList.isEmpty())
					sender.sendRichMessage("<white>there is no townhall in the world <b><world></b>.",
					Placeholder.parsed("world", world.getName()));
				else
					sender.sendRichMessage("<white>list of townhalls in the world <b><world></b>:\n<list>.",
						Placeholder.parsed("world", world.getName()),
						Placeholder.parsed("list", String.join(", ", townList))
					);
				return Command.SINGLE_SUCCESS;
			}
			);
	}

}
