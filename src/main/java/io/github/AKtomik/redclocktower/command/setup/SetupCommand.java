package io.github.AKtomik.redclocktower.command.setup;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.AKtomik.redclocktower.command.storyteller.StorytellerSubGame;
import io.github.AKtomik.redclocktower.command.storyteller.StorytellerSubPlayer;
import io.github.AKtomik.redclocktower.command.storyteller.StorytellerSubTime;
import io.github.AKtomik.redclocktower.utils.CommandBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

public class SetupCommand extends CommandBrigadierBase {

	// register
	public String name() {
		return "setup";
	}
	public List<String> aliases() {
		return List.of("upset", "bloodsetup", "bloodsettings");
	}
	public String permission() {
		return "redclocktower.storyteller";
	}
	public String description() { return "storyteller game preparation for red clocktower"; }

	// root
	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(new SetupSubPlace().root());
	}
}
