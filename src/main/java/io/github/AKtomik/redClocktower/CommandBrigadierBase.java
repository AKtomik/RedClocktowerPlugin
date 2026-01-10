package io.github.AKtomik.redClocktower;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public abstract class CommandBrigadierBase extends SubBrigadierBase {
    public abstract String name();
    public abstract String[] aliases();
}