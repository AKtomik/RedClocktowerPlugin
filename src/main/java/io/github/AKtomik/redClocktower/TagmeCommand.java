package io.github.AKtomik.redClocktower;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.List;

public class TagmeCommand extends CommandBrigadierBase {

    // register
    public String name() {
        return "tagme";
    }
    public List<String> aliases() {
        return List.of("iam");
    }

    // root
    public LiteralArgumentBuilder<CommandSourceStack> root() {
        return Commands.
            literal(name())
            .executes(
                ctx -> {
                    Entity executor = ctx.getSource().getExecutor();
                    CommandSender sender = ctx.getSource().getExecutor();
                    assert (sender != null);
                    sender.sendMessage(Component.text("hi you bridge sender"));
                    if (executor != null)
                    {
                        executor.sendMessage(Component.text("hi you bridge executor"));
                    }
                    return Command.SINGLE_SUCCESS;
                }
            );
    }
}
