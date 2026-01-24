package io.github.aktomik.redclocktower.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.List;

public class Whosend extends BrigadierCommand {

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
    public String description() {
        return "test command to check executor and sender";
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