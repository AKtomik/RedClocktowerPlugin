package io.github.AKtomik.redClocktower;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;

import java.util.List;

public abstract class CommandBrigadierBase extends SubBrigadierBase {
    public abstract String name();
    public abstract List<String> aliases();

    public abstract String permission();
    public abstract Component description();

    public LiteralArgumentBuilder<CommandSourceStack> base()
    {
        return Commands.literal(name()).requires(
            sender -> {
                String perm = permission();
                return perm == null || sender.getSender().hasPermission(perm);
            }
        );
    };
}