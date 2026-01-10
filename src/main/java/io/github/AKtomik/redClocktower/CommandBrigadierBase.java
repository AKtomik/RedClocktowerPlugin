package io.github.AKtomik.redClocktower;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;

import java.util.List;

public abstract class CommandBrigadierBase extends SubBrigadierBase {
    abstract List<String> aliases();
    abstract String permission();
    abstract Component description();

    LiteralArgumentBuilder<CommandSourceStack> base()
    {
        return Commands.literal(name()).requires(
            sender -> {
                String perm = permission();
                return perm == null || sender.getSender().hasPermission(perm);
            }
        );
    };
}