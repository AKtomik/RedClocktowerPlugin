package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.items.BloodItems;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StorytellerSubBook extends BrigadierSub {
	public String name() {
		return "book";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.literal("give")
			.then(Commands.argument("blood item id", EnumArgument.simple(BloodItems.class, "wrong blood item id"))
				.executes(subGive)
			)
		);
	}

	// subs

	Command<CommandSourceStack> subGive = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final BloodItems bloodItem = ctx.getArgument("blood item id", BloodItems.class);
		Player player = (Player)ctx.getSource().getExecutor();

		// execute
		sender.sendRichMessage("<light_purple>giving you the book");
		player.give(bloodItem.item());

		return Command.SINGLE_SUCCESS;
	};
}