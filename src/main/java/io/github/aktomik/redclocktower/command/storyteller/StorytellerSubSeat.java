package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.BloodSlot;
import io.github.aktomik.redclocktower.game.GameToolbox;
import io.github.aktomik.redclocktower.game.TownHall;
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
		.suggests((ctx, builder) -> {
			final TownHall townHall = BloodGame.get(ctx.getSource().getLocation().getWorld()).getTownHall();
			IntStream.range(1, townHall.getChairCount() + 1).forEach(builder::suggest);
			return builder.buildFuture();
		})
			.executes(subWho)

			.then(Commands.literal("who")
				.executes(subWho))

			.then(Commands.literal("clear")
				.executes(subWho))

			.then(Commands.literal("place")
				.then(Commands.literal("player")
					.then(Commands.argument("player", ArgumentTypes.player())
						.executes(subPlacePlayer)))
				.then(Commands.literal("dummy")
					.executes(subPlaceDummy)))
		);
	}

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
		final BloodSlot slot = game.getSlot(chairIndex);
		if (slot == null) {
			sender.sendRichMessage("<red>there is no slot <b><slot></b>",
				Placeholder.parsed("slot", Integer.toString(chairNumber))
			);
			return Command.SINGLE_SUCCESS;
		}
		sender.sendRichMessage("there is a slot <b><slot></b> in game",
			Placeholder.parsed("slot", Integer.toString(chairNumber))
		);
		// implement siter detection
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subPlacePlayer = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subPlaceDummy = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

}
