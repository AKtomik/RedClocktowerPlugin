package io.github.aktomik.redclocktower.command.storyteller;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

public class StorytellerCommand extends BrigadierCommand {

    // register
    public String name() {
        return "storyteller";
    }
    public List<String> aliases() {
        return List.of("blood", "redclocktower", "st");
    }
    public String permission() {
        return "redclocktower.storyteller";
    }
    public String description() { return "storyteller action for red clocktower"; }

    // root
    public LiteralArgumentBuilder<CommandSourceStack> root() {
        return base()
        .then(new StorytellerSubGame().root())
        .then(new StorytellerSubTime().root())
        .then(new StorytellerSubSeat().root())
        .then(new StorytellerSubPlayer().root())
        .then(new StorytellerSubVote().root())
        .then(new StorytellerSubNext().root());
    }
}