package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.*;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
import io.github.aktomik.redclocktower.utils.brigadier.SubBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class SetupSubDebug extends SubBrigadierBase {
	public String name() {
		return "debug";
	}


	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("action", EnumArgument.simple(BloodGameDebugAction.class, "Invalid game debug place"))
			.executes(ctx -> {
				// arguments
				CommandSender sender = ctx.getSource().getSender();
				BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
				final BloodGameDebugAction gameAction = ctx.getArgument("action", BloodGameDebugAction.class);

				// execute
				sender.sendRichMessage("<yellow>REMINDER: Using the /setup debug command can lead to unexpected behavior and should only be used as a last resort.");
				sender.sendRichMessage("<yellow>ADVICE: if possible stop and clear the game before");
				sender.sendRichMessage("<#ff6600>running debug <b><action></b>...",
				Placeholder.parsed("action", gameAction.toString())
				);
				game.doDebug(gameAction, sender);
				return Command.SINGLE_SUCCESS;
			}
			)
		);
	}

}
