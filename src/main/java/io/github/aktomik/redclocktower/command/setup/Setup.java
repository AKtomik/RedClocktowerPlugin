package io.github.aktomik.redclocktower.command.setup;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.TownHall;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Setup extends BrigadierCommand {

	// register
	public String name() {
		return "setup";
	}
	public List<String> aliases() {
		return List.of("townhall", "town", "hall");
	}
	public String permission() {
		return "redclocktower.storyteller";
	}
	public String description() { return "create and setup townhall before red clocktower game"; }

	// root
	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		// manage
		.then(new SetupSubCreate().root())
		.then(new SetupSubSelect().root())
		.then(new SetupSubRemove().root())
		.then(new SetupSubList().root())

		// action
		.then(new SetupSubPlace().root())
		.then(new SetupSubSettings().root())
		.then(new SetupSubDebug().root())
		.then(new SetupSubSlot().root())

		// empty
		.executes(ctx -> {
			// arguments
			CommandSender sender = ctx.getSource().getSender();

			// execute
			TownHall townHall = TownHall.getSelection(sender);

			if (townHall == null)
			{
				sender.sendRichMessage("<gray>you don't have any townhall selected.");
				return Command.SINGLE_SUCCESS;
			}

			sender.sendRichMessage("<gray>you are editing the townhall <b><name></b>.",
			Placeholder.parsed("name", townHall.getTownName())
			);
			return Command.SINGLE_SUCCESS;
		}
		);
	}
}
