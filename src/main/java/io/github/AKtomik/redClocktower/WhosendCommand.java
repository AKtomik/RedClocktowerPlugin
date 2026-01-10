package io.github.AKtomik.redClocktower;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.List;

public class WhosendCommand extends CommandBrigadierBase {

    // register
    public String name() {
        return "whosend";
    }
    public List<String> aliases() {
        return List.of();
    }
    public String permission() {
        return "redclocktower.player";
    }
    public Component description() {
        return Component.text("test command to check executor and sender");
    }

    // root
    public LiteralArgumentBuilder<CommandSourceStack> root() {
        return base()
            .executes(
                ctx -> {
                    Entity executor = ctx.getSource().getExecutor();
                    CommandSender sender = ctx.getSource().getSender();
                    sender.sendMessage(
                        Component.text("you are the command ")
                        .append(Component.text("sender").decorate(TextDecoration.BOLD))
                    );
                    if (executor != null)
                    {
                        executor.sendMessage(
                            Component.text("you are the command ")
                            .append(Component.text("executor").decorate(TextDecoration.BOLD))
                        );
                    }
                    return Command.SINGLE_SUCCESS;
                }
            );
    }
}
