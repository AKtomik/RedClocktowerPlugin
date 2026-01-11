package io.github.AKtomik.redClocktower;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;

import java.util.List;

public class StorytellerCommand extends CommandBrigadierBase {

    // register
    public String name() {
        return "storyteller";
    }
    public List<String> aliases() {
        return List.of("blood", "redclocktower");
    }
    public String permission() {
        return "redclocktower.storyteller";
    }
    public Component description() {
        return Component.text("storyteller action for red clocktower");
    }

    // root
    public LiteralArgumentBuilder<CommandSourceStack> root() {
        return base()
        .then(new StorytellerSubTime().root())
        .then(new StorytellerSubGame().root());
    }
}