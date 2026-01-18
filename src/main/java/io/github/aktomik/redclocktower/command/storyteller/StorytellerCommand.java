package io.github.aktomik.redclocktower.command.storyteller;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.command.setup.SetupSubSettings;
import io.github.aktomik.redclocktower.utils.brigadier.CommandBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;

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
    public String description() { return "storyteller action for red clocktower"; }

    // root
    public LiteralArgumentBuilder<CommandSourceStack> root() {
        return base()
        .then(new StorytellerSubTime().root())
        .then(new StorytellerSubGame().root())
        .then(new StorytellerSubPlayer().root())
        .then(new StorytellerSubVote().root());
    }
}