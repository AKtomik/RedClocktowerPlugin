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
		.then(new SetupSubRemove().root())
		.then(new SetupSubList().root())

		// modify
		.then(new SetupSubModify().root());
	}
}
