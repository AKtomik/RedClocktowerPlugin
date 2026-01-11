package io.github.AKtomik.redclocktower.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.AKtomik.redclocktower.brigadier.CommandBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
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
            sender.sendRichMessage("you are the command <b>sender</b>");
            if (executor != null)
            {
                sender.sendRichMessage("you are the command <b>executor</b>");
            }
            return Command.SINGLE_SUCCESS;
        }
        );
    }
}