package io.github.AKtomik.redclocktower.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.AKtomik.redclocktower.brigadier.CommandBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Entity;

import java.util.List;

public class RichmessageCommand extends CommandBrigadierBase {

	// register
	public String name() {
		return "richmessage";
	}
	public List<String> aliases() {
		return List.of("richmsg");
	}
	public String permission() {
		return "redclocktower.player";
	}
	public String description() { return "test command to send rich message to yourself"; }

	// root
	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("rich message", StringArgumentType.greedyString())
		.executes(
		ctx -> {
			Entity executor = ctx.getSource().getExecutor();
			if (executor == null) return Command.SINGLE_SUCCESS;
			String rawMessage = StringArgumentType.getString(ctx, "rich message");

			executor.sendRichMessage(rawMessage);
			return Command.SINGLE_SUCCESS;
		}
		));
	}
}