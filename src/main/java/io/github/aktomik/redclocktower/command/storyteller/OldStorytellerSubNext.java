package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.oldgame.OldGameAction;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.oldgame.OldBloodGame;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

@Deprecated
public class OldStorytellerSubNext extends BrigadierSub {
	public String name() {
		return "next";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.executes(ctx -> {
			// arguments
			CommandSender sender = ctx.getSource().getSender();
			OldBloodGame game = OldBloodGame.get(ctx.getSource().getLocation().getWorld());

			OldGameAction.next.accept(game, sender);
			return Command.SINGLE_SUCCESS;
		});
	}
}