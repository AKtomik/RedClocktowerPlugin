package io.github.aktomik.redclocktower.utils.brigadier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public abstract class SubBrigadierBase {
    public abstract String name();
    public abstract LiteralArgumentBuilder<CommandSourceStack> root();

    public LiteralCommandNode<CommandSourceStack> build()
    {
        return root().build();
    }
    public LiteralArgumentBuilder<CommandSourceStack> base()
    {
        return Commands.literal(name());
    }
}