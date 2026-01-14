package io.github.AKtomik.redclocktower.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.AKtomik.redclocktower.game.BloodPlayer;
import io.github.AKtomik.redclocktower.utils.CommandBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

public class TagmedefaultCommand extends CommandBrigadierBase {

    // register
    public String name() {
        return "tagmedefault";
    }
    public List<String> aliases() {
        return List.of("iamdefault");
    }
    public String permission() {
        return "redclocktower.player";
    }
    public String description() { return "define your name back to default"; }

    // root
    public LiteralArgumentBuilder<CommandSourceStack> root() {
        return base()
        .requires(ctx -> ctx.getExecutor() instanceof Player)
        .executes(
        ctx -> {
            Player player = (Player)ctx.getSource().getExecutor();
			assert player != null;
            BloodPlayer bloodPlayer = BloodPlayer.get(player);

            bloodPlayer.clearDisplayName();
            bloodPlayer.refreshNameTag();
            player.sendRichMessage("<white>changing your display name back to default.");
            return Command.SINGLE_SUCCESS;
        });
    }
}
