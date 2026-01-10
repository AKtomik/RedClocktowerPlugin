package io.github.AKtomik.redClocktower;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public abstract class SubBrigadierBase {
    abstract LiteralArgumentBuilder<CommandSourceStack> root();
    LiteralCommandNode<CommandSourceStack> build()
    {
        return root().build();
    };
}