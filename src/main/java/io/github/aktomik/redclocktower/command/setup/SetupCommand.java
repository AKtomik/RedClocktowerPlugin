package io.github.aktomik.redclocktower.command.setup;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.utils.brigadier.CommandBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

public class SetupCommand extends CommandBrigadierBase {

	// register
	public String name() {
		return "setup";
	}
	public List<String> aliases() {
		return List.of("upset", "bloodsetup");
	}
	public String permission() {
		return "redclocktower.storyteller";
	}
	public String description() { return "storyteller game preparation for red clocktower"; }

	// root
	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(new SetupSubPlace().root())
		.then(new SetupSubSlot().root());
	}
}
