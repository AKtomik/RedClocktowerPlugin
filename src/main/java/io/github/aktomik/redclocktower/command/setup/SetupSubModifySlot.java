package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.TownChair;
import io.github.aktomik.redclocktower.game.TownHall;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.stream.IntStream;

public class SetupSubModifySlot extends BrigadierSub {
	public String name() {
		return "slot";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.literal("add_new")
			.executes(subAdd))
		.then(Commands.literal("remove_last")
			.executes(subRemove))
		.then(Commands.literal("edit")
			.then(Commands.argument("chair number", IntegerArgumentType.integer(1, 24))
			.suggests((ctx, builder) -> {
				final TownHall townHall = ctx.getArgument("town", TownHall.class);
				IntStream.range(1, townHall.getChairCount() + 1).forEach(builder::suggest);
				return builder.buildFuture();
			})
				.then(Commands.literal("position")
					.then(Commands.argument("place", EnumArgument.simple(TownChairPlace.class, "invalid chair place"))
						.executes(subEditPositionCheck)
						.then(Commands.argument("position", ArgumentTypes.blockPosition())
							.executes(subEditPositionChange))
		))));
	}


	Command<CommandSourceStack> subRemove = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final TownHall townHall = ctx.getArgument("town", TownHall.class);

		// check
		int chairCount = townHall.getChairCount();
		if (chairCount == 0)
		{
			sender.sendRichMessage("<red>there is 0 chair");
			return  Command.SINGLE_SUCCESS;
		}

		// action
		townHall.removeLastChair();
		sender.sendRichMessage("<b><red>removing</red></b> the last chair (now <count> chairs)",
			Placeholder.parsed("count", Integer.toString(townHall.getChairCount()))
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subAdd = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final TownHall townHall = ctx.getArgument("town", TownHall.class);

		// action
		townHall.addNewChair();
		sender.sendRichMessage("<b><green>adding</green></b> a new chair (now <count> chairs)",
			Placeholder.parsed("count", Integer.toString(townHall.getChairCount()))
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subEditPositionCheck = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final TownHall townHall = ctx.getArgument("town", TownHall.class);
		final int chairIndex = ctx.getArgument("chair number", Integer.class) - 1;
		final TownChairPlace place = ctx.getArgument("place", TownChairPlace.class);

		// check
		int chairCount = townHall.getChairCount();
		if (!(0 <= chairIndex && chairIndex < chairCount))
		{
			sender.sendRichMessage("<red>there is no slot <number> (actually <count> chairs)",
				Placeholder.parsed("number", Integer.toString(chairIndex + 1)),
				Placeholder.parsed("count", Integer.toString(chairCount))
			);
			return  Command.SINGLE_SUCCESS;
		}
		TownChair chair = townHall.getChair(chairIndex);

		// execution
		final Location loc = chair.getPosition(place);
		if (loc == null)
		{
			sender.sendRichMessage("<gray>position <b><place></b> of chair <number> is not placed",
				Placeholder.parsed("place", place.toString())
			);
			return Command.SINGLE_SUCCESS;
		}
		sender.sendRichMessage("position <b><place></b> of chair <number> is at <x> <y> <z> <hover:show_text:\"Click to teleport\"><click:run_command:/tp @s <x> <y> <z>><green>[tp]",
			Placeholder.parsed("number", Integer.toString(chairIndex + 1)),
			Placeholder.parsed("x", Integer.toString(loc.getBlockX())),
			Placeholder.parsed("y", Integer.toString(loc.getBlockY())),
			Placeholder.parsed("z", Integer.toString(loc.getBlockZ())),
			Placeholder.parsed("place", place.toString())
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subEditPositionChange = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final TownHall townHall = ctx.getArgument("town", TownHall.class);
		final int chairIndex = ctx.getArgument("chair number", Integer.class) - 1;
		final TownChairPlace place = ctx.getArgument("place", TownChairPlace.class);
		final BlockPosition pos = ctx.getArgument("position", BlockPositionResolver.class).resolve(ctx.getSource());

		// check
		int chairCount = townHall.getChairCount();
		if (!(0 <= chairIndex && chairIndex < chairCount))
		{
			sender.sendRichMessage("<red>there is no slot <number> (actually <count> chairs)",
				Placeholder.parsed("number", Integer.toString(chairIndex + 1)),
				Placeholder.parsed("count", Integer.toString(chairCount))
			);
			return  Command.SINGLE_SUCCESS;
		}
		TownChair chair = townHall.getChair(chairIndex);

		// execution
		final Location loc = pos.toLocation(townHall.getWorld());
		chair.setPosition(place, loc);
		townHall.setChair(chairIndex, chair);// don't forget to set it else no effect
		sender.sendRichMessage("set <b><place></b> position of slot <number> at <x> <y> <z>",
			Placeholder.parsed("number", Integer.toString(chairIndex + 1)),
			Placeholder.parsed("x", Integer.toString(loc.getBlockX())),
			Placeholder.parsed("y", Integer.toString(loc.getBlockY())),
			Placeholder.parsed("z", Integer.toString(loc.getBlockZ())),
			Placeholder.parsed("place", place.toString())
		);
		return Command.SINGLE_SUCCESS;
	};
}
