package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.aktomik.redclocktower.game.*;
import io.github.aktomik.redclocktower.game.Seated;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.stream.IntStream;

public class StorytellerSubSeat extends BrigadierSub {

// build

	public String name() { return "seat"; }

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("slot number", IntegerArgumentType.integer(1, 24))
		.suggests(slotSuggestion)
			.executes(subWho)

			.then(Commands.literal("who")
				.executes(subWho))

			.then(Commands.literal("empty")
				.executes(subEmpty))

			.then(Commands.literal("assign")
				.then(Commands.literal("player")
					.then(Commands.argument("player", ArgumentTypes.player())
						.executes(subAssignPlayer)))
				.then(Commands.literal("dummy")
					.executes(subAssignDummy)))
		);
	}


	SuggestionProvider<CommandSourceStack> slotSuggestion = (ctx, builder) -> {
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
		if (game == null) return builder.buildFuture();
		IntStream.range(1, game.getSlotCount() + 1).forEach(builder::suggest);
		return builder.buildFuture();
	};


	// subs

	Command<CommandSourceStack> subWho = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
		assert game != null;

		final int slotNumber = ctx.getArgument("slot number", Integer.class);
		final int slotIndex = slotNumber - 1;
		if (!game.isValidSlot(slotIndex)) {
			sender.sendRichMessage("<red>there is no slot <b><number></b>",
				Placeholder.parsed("number", Integer.toString(slotNumber))
			);
			return Command.SINGLE_SUCCESS;
		}
		final BloodSlot slot = game.getSlot(slotIndex);

		// execute
		if (slot.isOccupied())
		{
			sender.sendRichMessage("the slot <b><number></b> is <gold>occupied",
				Placeholder.parsed("number", Integer.toString(slotNumber))
			);
		} else {
			sender.sendRichMessage("the slot <b><number></b> is <yellow>empty",
				Placeholder.parsed("number", Integer.toString(slotNumber))
			);
		}
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subEmpty = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
		assert game != null;

		final int slotNumber = ctx.getArgument("slot number", Integer.class);
		final int slotIndex = slotNumber - 1;
		if (!game.isValidSlot(slotIndex)) {
			sender.sendRichMessage("<red>there is no slot <b><number></b>",
			Placeholder.parsed("number", Integer.toString(slotNumber))
			);
			return Command.SINGLE_SUCCESS;
		}
		final BloodSlot slot = game.getSlot(slotIndex);

		if (!slot.isOccupied())
		{
			sender.sendRichMessage("<gray>the slot <b><number></b> is already empty",
				Placeholder.parsed("number", Integer.toString(slotNumber))
			);
			return Command.SINGLE_SUCCESS;
		}

		game.emptySlot(slotIndex);
		sender.sendRichMessage("slot <b><number></b> <red>emptied</red>",
		Placeholder.parsed("number", Integer.toString(slotNumber))
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subAssignPlayer = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		sender.sendRichMessage("TODO");
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subAssignDummy = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
		assert game != null;

		final int slotNumber = ctx.getArgument("slot number", Integer.class);
		final int slotIndex = slotNumber - 1;
		if (!game.isValidSlot(slotIndex)) {
			sender.sendRichMessage("<red>there is no slot <b><number></b>",
			Placeholder.parsed("number", Integer.toString(slotNumber))
			);
			return Command.SINGLE_SUCCESS;
		}
		final BloodSlot slot = game.getSlot(slotIndex);

		if (slot.isOccupied())
		{
			sender.sendRichMessage("<red>the slot <b><number></b> is occupied",
			Placeholder.parsed("number", Integer.toString(slotNumber))
			);
			return Command.SINGLE_SUCCESS;
		}

		final Seated dummy = new Seated(slot, "-seat"+slotNumber);
		game.assignSlot(slotIndex, dummy);
		sender.sendRichMessage("<light_purple>dummy</light_purple> added to the slot <b><number></b>",
			Placeholder.parsed("number", Integer.toString(slotNumber))
		);
		return Command.SINGLE_SUCCESS;
	};

}
