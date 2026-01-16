package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.BloodSlotPlace;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
import io.github.aktomik.redclocktower.utils.brigadier.SubBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

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
			.then(Commands.argument("slot number", ArgumentTypes.integerRange())
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
			sender.sendRichMessage("<b>removing</b> the last slot (now <count> slots)");
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
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subEditPositionChange = ctx -> {
		return Command.SINGLE_SUCCESS;
	};
}
