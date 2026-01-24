package io.github.aktomik.redclocktower.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.BloodPlayer;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

public class Tagme extends BrigadierCommand {

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
        .requires(ctx -> ctx.getExecutor() instanceof Player)
        .then(
        Commands.argument("display name", StringArgumentType.word())
        .suggests((ctx, builder) -> {
            Player player = (Player)ctx.getSource().getExecutor();
            assert player != null;
            builder.suggest(player.getName());
            BloodPlayer bloodPlayer = BloodPlayer.get(player);
            String displayName = bloodPlayer.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) builder.suggest(bloodPlayer.getDisplayName());
            return builder.buildFuture();
        })
        .executes(
        ctx -> {
            Player player = (Player)ctx.getSource().getExecutor();
			assert player != null;
            BloodPlayer bloodPlayer = BloodPlayer.get(player);
            String displayName = StringArgumentType.getString(ctx, "display name");

            if (displayName.equalsIgnoreCase(player.getName()))
            {
                bloodPlayer.clearDisplayName();
                bloodPlayer.refreshNameTag();
                player.sendRichMessage("<white>changing your display name back to default.");
                return Command.SINGLE_SUCCESS;
            }

            bloodPlayer.setDisplayName(displayName);
            bloodPlayer.refreshNameTag();

			player.sendRichMessage("<white>changing your display name to <b><name></b>.", Placeholder.parsed("name", displayName));
            return Command.SINGLE_SUCCESS;
        })
        );
    }
}
