package io.github.AKtomik.redclocktower.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.AKtomik.redclocktower.brigadier.CommandBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
    public String permission() {
        return "redclocktower.player";
    }
    public Component description() {
        return Component.text("define your name");
    }

    // root
    public LiteralArgumentBuilder<CommandSourceStack> root() {
        return base()
        .then(
        Commands.argument("myname", StringArgumentType.word())
        .executes(
        ctx -> {
            Entity executor = ctx.getSource().getExecutor();
            assert (executor != null);
            String displayName = StringArgumentType.getString(ctx, "myname");
            executor.sendMessage(
            Component.text("changing your display name to ").color(NamedTextColor.GREEN)
            .append(Component.text(displayName).decorate(TextDecoration.BOLD))
            );
            return Command.SINGLE_SUCCESS;
        })
        );
    }
}
