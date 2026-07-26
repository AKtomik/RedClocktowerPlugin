package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.aktomik.redclocktower.game.*;
import io.github.aktomik.redclocktower.oldgame.OldBloodGame;
import io.github.aktomik.redclocktower.oldgame.OldBloodPlayer;
import io.github.aktomik.redclocktower.oldgame.OldGameToolbox;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierToolbox;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.IntStream;

public class StorytellerSubSeat extends BrigadierSub {

// build

	public String name() { return "seat"; }

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("chair number", IntegerArgumentType.integer(1, 24))
		.suggests(chairSuggestion)
			.executes(subWho)

			.then(Commands.literal("who")
				.executes(subWho))

			.then(Commands.literal("clear")
				.executes(subClear))

			.then(Commands.literal("place")
				.then(Commands.literal("player")
					.then(Commands.argument("player", ArgumentTypes.player())
						.executes(subPlacePlayer)))
				.then(Commands.literal("dummy")
					.executes(subPlaceDummy)))
		);
	}


	SuggestionProvider<CommandSourceStack> chairSuggestion = (ctx, builder) -> {
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
		if (game == null) return builder.buildFuture();
		final TownHall townHall = game.getTownHall();
		IntStream.range(1, townHall.getChairCount() + 1).forEach(builder::suggest);
		return builder.buildFuture();
	};


	// subs

	Command<CommandSourceStack> subWho = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final World world = ctx.getSource().getLocation().getWorld();
		final BloodGame game = BloodGame.get(world);
		final int chairNumber = ctx.getArgument("chair number", Integer.class);
		final int chairIndex = chairNumber - 1;

		// check
		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
		assert game != null;

		// execute
		final TownChair chair = game.getTownHall().getChair(chairIndex);
		if (chair == null) {
			sender.sendRichMessage("<red>there is no chair <b><number></b>",
				Placeholder.parsed("number", Integer.toString(chairNumber))
			);
			return Command.SINGLE_SUCCESS;
		}
		final BloodSlot slot = game.getSlot(chairIndex);
		if (slot == null) {
			sender.sendRichMessage("<red>there is no slot <b><number></b>",
				Placeholder.parsed("number", Integer.toString(chairNumber))
			);
			return Command.SINGLE_SUCCESS;
		}
		sender.sendRichMessage("there is a slot <b><number></b> in game",
			Placeholder.parsed("number", Integer.toString(chairNumber))
		);
		// implement siter detection
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subClear = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subPlacePlayer = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subPlaceDummy = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

}
