package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.aktomik.redclocktower.game.*;
import io.github.aktomik.redclocktower.game.Seated;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierToolbox;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
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
				.executes(subWho)
			)
			.then(Commands.literal("empty")
				.executes(subEmpty)
			)
			.then(Commands.literal("assign")
				.then(Commands.literal("player")
					.then(Commands.argument("player", ArgumentTypes.player())
						.executes(subAssignPlayer)))
				.then(Commands.literal("dummy")
					.executes(subAssignDummy)
				)
			)
			.then(Commands.literal("switch")
				.then(Commands.argument("second slot number", IntegerArgumentType.integer(1, 24))
				.suggests(slotSuggestion)
					.executes(subSwitch)
				)
			)
		);
	}

	// sug

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
		final Seated seated = slot.getSeated();
		if (seated == null)
		{
			sender.sendRichMessage("the slot <b><number></b> is <yellow>empty",
				Placeholder.parsed("number", Integer.toString(slotNumber))
			);
		} else {
			String seatedTypeString = Map.of(
				SeatedDummy.class, "dummy",
				SeatedPlayer.class, "player"
			).getOrDefault(seated.getClass(), "unknown");
			sender.sendRichMessage("at slot <b><number></b> there is <type> <name>",
				Placeholder.parsed("number", Integer.toString(slotNumber)),
				Placeholder.parsed("name", seated.getDisplayName()),
				Placeholder.parsed("type", seatedTypeString)
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

		game.getSlot(slotIndex).empty();
		sender.sendRichMessage("slot <b><number></b> <red>emptied</red>",
		Placeholder.parsed("number", Integer.toString(slotNumber))
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subAssignPlayer = ctx -> {
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

		final Player player = BrigadierToolbox.resolvePlayer(ctx);
		if (GameToolbox.failIfNoPlayer(sender, player)) return Command.SINGLE_SUCCESS;

		final Seated seated = new SeatedPlayer(player);
		game.getSlot(slotIndex).assign(seated);
		sender.sendRichMessage("player <yellow><name></yellow> added to the slot <b><number></b>",
			Placeholder.parsed("number", Integer.toString(slotNumber)),
			Placeholder.parsed("name", seated.getDisplayName())
		);
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

		final Seated seated = new SeatedDummy(slotNumber);
		game.getSlot(slotIndex).assign(seated);
		sender.sendRichMessage("dummy <light_purple><name></light_purple> added to the slot <b><number></b>",
			Placeholder.parsed("number", Integer.toString(slotNumber)),
			Placeholder.parsed("name", seated.getDisplayName())
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subSwitch = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
		assert game != null;

		final int firstSlotNumber = ctx.getArgument("slot number", Integer.class);
		final int firstSlotIndex = firstSlotNumber - 1;
		if (!game.isValidSlot(firstSlotIndex)) {
			sender.sendRichMessage("<red>there is no slot <b><number></b>",
			Placeholder.parsed("number", Integer.toString(firstSlotNumber))
			);
			return Command.SINGLE_SUCCESS;
		}
		final BloodSlot firstSlot = game.getSlot(firstSlotIndex);

		final int secondSlotNumber = ctx.getArgument("second slot number", Integer.class);
		final int secondSlotIndex = secondSlotNumber - 1;
		if (!game.isValidSlot(secondSlotIndex)) {
			sender.sendRichMessage("<red>there is no slot <b><number></b>",
			Placeholder.parsed("number", Integer.toString(secondSlotNumber))
			);
			return Command.SINGLE_SUCCESS;
		}
		final BloodSlot secondSlot = game.getSlot(secondSlotIndex);

		final boolean firstOccupied = firstSlot.isOccupied();
		final boolean secondOccupied = secondSlot.isOccupied();
		if (!firstOccupied && !secondOccupied)
		{
			sender.sendRichMessage("<gray>both slots <first_number> and <second_number> are empty",
				Placeholder.parsed("first_number", Integer.toString(firstSlotNumber)),
				Placeholder.parsed("second_number", Integer.toString(secondSlotNumber))
			);
			return Command.SINGLE_SUCCESS;
		}

		final Seated firstSeated = firstSlot.getSeated();
		final Seated secondSeated = secondSlot.getSeated();
		if (firstOccupied)  secondSlot.assign(firstSeated);
		if (secondOccupied) firstSlot.assign(secondSeated);

		// message
		Component switchedText = Component.text("switched ");
		if (firstOccupied) {
			Component firstText = game.getMini().deserialize(
				"<type> <name> to slot <number>",
				Placeholder.parsed("type", firstSeated.getSeatedTypeString()),
				Placeholder.component("name", Component.text(firstSeated.getDisplayName()).color(firstSeated.getSeatedTypeColor())),
				Placeholder.parsed("number", Integer.toString(secondSlotNumber))
			);
			switchedText = switchedText.append(firstText);
			if (secondOccupied)
				switchedText = switchedText.append(Component.text(" and "));
		}
		if (secondOccupied) {
			Component secondText = game.getMini().deserialize(
			"<type> <name> to slot <number>",
				Placeholder.parsed("type", secondSeated.getSeatedTypeString()),
				Placeholder.component("name", Component.text(secondSeated.getDisplayName()).color(secondSeated.getSeatedTypeColor())),
				Placeholder.parsed("number", Integer.toString(firstSlotNumber))
			);
			switchedText = switchedText.append(secondText);
		}
		sender.sendMessage(switchedText);
		return Command.SINGLE_SUCCESS;
	};
}
