package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.town.TownHall;
import io.github.aktomik.redclocktower.game.town.TownHallPlace;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class SetupSubModifyPosition extends BrigadierSub {
	public String name() {
		return "position";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("place", EnumArgument.simple(TownHallPlace.class, "invalid townhall place"))
			.executes(positionCheck)
			.then(Commands.argument("position", ArgumentTypes.blockPosition())
				.executes(positionChange)
		));
	}


	Command<CommandSourceStack> positionCheck = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final TownHall townHall = ctx.getArgument("town", TownHall.class);
		final TownHallPlace place = ctx.getArgument("place", TownHallPlace.class);

		// execution
		final Location loc = townHall.getPosition(place);
		if (loc == null)
		{
			sender.sendRichMessage("<gray>position <b><place></b> is not placed",
				Placeholder.parsed("place", place.toString())
			);
			return Command.SINGLE_SUCCESS;
		}
		sender.sendRichMessage("position <b><place></b> is at <x> <y> <z> <hover:show_text:\"Click to teleport\"><click:run_command:/tp @s <x> <y> <z>><green>[tp]",
			Placeholder.parsed("x", Integer.toString(loc.getBlockX())),
			Placeholder.parsed("y", Integer.toString(loc.getBlockY())),
			Placeholder.parsed("z", Integer.toString(loc.getBlockZ())),
			Placeholder.parsed("place", place.toString())
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> positionChange = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final TownHall townHall = ctx.getArgument("town", TownHall.class);
		final TownHallPlace place = ctx.getArgument("place", TownHallPlace.class);
		final World world = ctx.getSource().getLocation().getWorld();
		final BlockPosition pos = ctx.getArgument("position", BlockPositionResolver.class).resolve(ctx.getSource());

		// execution
		final Location loc = pos.toLocation(world);
		townHall.setPosition(place, loc);
		sender.sendRichMessage("set <b><place></b> position at <x> <y> <z>",
			Placeholder.parsed("x", Integer.toString(loc.getBlockX())),
			Placeholder.parsed("y", Integer.toString(loc.getBlockY())),
			Placeholder.parsed("z", Integer.toString(loc.getBlockZ())),
			Placeholder.parsed("place", place.toString())
		);
		return Command.SINGLE_SUCCESS;
	};
}