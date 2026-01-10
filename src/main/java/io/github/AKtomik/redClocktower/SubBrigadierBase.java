package io.github.AKtomik.redClocktower;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public abstract class SubBrigadierBase {
    abstract String name();
    abstract LiteralArgumentBuilder<CommandSourceStack> root();

    LiteralCommandNode<CommandSourceStack> build()
    {
        return root().build();
    };
    LiteralArgumentBuilder<CommandSourceStack> base()
    {
        return Commands.literal(name());
    };
}