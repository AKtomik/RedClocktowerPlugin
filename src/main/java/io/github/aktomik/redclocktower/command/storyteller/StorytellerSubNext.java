package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.GameAction;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class StorytellerSubNext extends BrigadierSub {
	public String name() {
		return "next";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.executes(ctx -> {
			// arguments
			CommandSender sender = ctx.getSource().getSender();
			BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

			GameAction.next.accept(game, sender);
			return Command.SINGLE_SUCCESS;
		});
	}
}