package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.BloodGamePlace;
import io.github.aktomik.redclocktower.game.BloodSlot;
import io.github.aktomik.redclocktower.game.BloodSlotPlace;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
import io.github.aktomik.redclocktower.utils.brigadier.SubBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.stream.IntStream;

public class SetupSubSlot extends SubBrigadierBase {
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
			.then(Commands.argument("slot number", IntegerArgumentType.integer(1, 24))
			.suggests((ctx, builder) -> {
				final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
				IntStream.range(1, game.getSlotCount()).forEach(builder::suggest);
				return builder.buildFuture();
			})
				.then(Commands.literal("position")
					.then(Commands.argument("place", EnumArgument.simple(BloodSlotPlace.class, "invalid slot place"))
						.executes(subEditPositionCheck)
						.then(Commands.argument("position", ArgumentTypes.blockPosition())
							.executes(subEditPositionChange))
		))));
	}


	Command<CommandSourceStack> subRemove = ctx -> {
		// arguments
		CommandSender sender = ctx.getSource().getSender();
		BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		// check
		int slotCount = game.getSlotCount();
		if (slotCount == 0)
		{
			sender.sendRichMessage("<red>there is 0 slot");
			return  Command.SINGLE_SUCCESS;
		}

		// action
		game.removeLastSlot();
		sender.sendRichMessage("<b>removing</b> the last slot (now <count> slots)",
			Placeholder.parsed("count", Integer.toString(game.getSlotCount()))
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subAdd = ctx -> {
		// arguments
		CommandSender sender = ctx.getSource().getSender();
		BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		// action
		game.addLastSlot();
		sender.sendRichMessage("<b>adding</b> a new slot (now <count> slots)",
			Placeholder.parsed("count", Integer.toString(game.getSlotCount()))
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subList = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subEditPositionCheck = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final World world = ctx.getSource().getLocation().getWorld();
		final BloodGame game = BloodGame.get(world);
		final int slotIndex = ctx.getArgument("slot number", Integer.class) - 1;
		final BloodSlotPlace place = ctx.getArgument("place", BloodSlotPlace.class);

		// check
		int slotCount = game.getSlotCount();
		if (!(0 <= slotIndex && slotIndex < slotCount))
		{
			sender.sendRichMessage("<red>there is no slot <number> (actually <count> slots)",
				Placeholder.parsed("number", Integer.toString(slotIndex + 1)),
				Placeholder.parsed("count", Integer.toString(slotCount))
			);
			return  Command.SINGLE_SUCCESS;
		}
		BloodSlot slot = game.getSlot(slotIndex);

		// execution
		final Location loc = slot.getPosition(place);
		sender.sendRichMessage("position <b><place></b> of slot <number> is at <x> <y> <z> <hover:show_text:\"Click to teleport\"><click:run_command:/tp @s <x> <y> <z>><green>[tp]",
			Placeholder.parsed("number", Integer.toString(slotIndex + 1)),
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
		final World world = ctx.getSource().getLocation().getWorld();
		final BloodGame game = BloodGame.get(world);
		final int slotIndex = ctx.getArgument("slot number", Integer.class) - 1;
		final BloodSlotPlace place = ctx.getArgument("place", BloodSlotPlace.class);
		final BlockPosition pos = ctx.getArgument("position", BlockPositionResolver.class).resolve(ctx.getSource());

		// check
		int slotCount = game.getSlotCount();
		if (!(0 <= slotIndex && slotIndex < slotCount))
		{
			sender.sendRichMessage("<red>there is no slot <number> (actually <count> slots)",
				Placeholder.parsed("number", Integer.toString(slotIndex + 1)),
				Placeholder.parsed("count", Integer.toString(slotCount))
			);
			return  Command.SINGLE_SUCCESS;
		}
		BloodSlot slot = game.getSlot(slotIndex);

		// execution
		final Location loc = pos.toLocation(world);
		slot.setPosition(place, loc);
		game.setSlot(slotIndex, slot);// don't forget to set it else no effect
		sender.sendRichMessage("set <b><place></b> position of slot <number> at <x> <y> <z>",
			Placeholder.parsed("number", Integer.toString(slotIndex + 1)),
			Placeholder.parsed("x", Integer.toString(loc.getBlockX())),
			Placeholder.parsed("y", Integer.toString(loc.getBlockY())),
			Placeholder.parsed("z", Integer.toString(loc.getBlockZ())),
			Placeholder.parsed("place", place.toString())
		);
		return Command.SINGLE_SUCCESS;
	};
}
