package io.github.aktomik.redclocktower.commandbuild.tools;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface GameCommand {
	int run(CommandContext<CommandSourceStack> ctx, CommandSender sender, BloodGame game) throws CommandSyntaxException;

	static Command<CommandSourceStack> wrap(GameCommand command) {
		return ctx -> {
			CommandSender sender = ctx.getSource().getSender();
			BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
			if (CommandToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
			return command.run(ctx, sender, game);
		};
	}
}