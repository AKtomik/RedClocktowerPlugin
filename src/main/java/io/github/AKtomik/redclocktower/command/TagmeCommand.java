package io.github.AKtomik.redclocktower.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.AKtomik.redclocktower.game.BloodPlayer;
import io.github.AKtomik.redclocktower.utils.CommandBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
    public String description() { return "define your name"; }

    // root
    public LiteralArgumentBuilder<CommandSourceStack> root() {
        return base()
        .then(
        Commands.argument("myname", StringArgumentType.word())
        .requires(ctx -> ctx.getExecutor() instanceof Player)
        .executes(
        ctx -> {
            Player player = (Player)ctx.getSource().getExecutor();
			assert player != null;
            BloodPlayer bloodPlayer = BloodPlayer.get(player);
            String displayName = StringArgumentType.getString(ctx, "myname");

            bloodPlayer.setDisplayName(displayName);
            bloodPlayer.refreshNameTag();

			player.sendRichMessage("<green>changing your display name to <b><name></b>", Placeholder.parsed("name", displayName));
            return Command.SINGLE_SUCCESS;
        })
        );
    }
}
