package io.github.AKtomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.AKtomik.redclocktower.brigadier.SubBrigadierBase;
import io.github.AKtomik.redclocktower.game.BloodGame;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StorytellerSubPlayer extends SubBrigadierBase {

	public String name() { return "player"; }

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.literal("add")
			.then(Commands.argument("players", ArgumentTypes.players())
			.executes(
			ctx -> {
				final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
				final List<Player> targets = targetResolver.resolve(ctx.getSource());
				final CommandSender sender = ctx.getSource().getSender();
				final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

				// checks
				if (!game.isReady())
				{
					sender.sendRichMessage("<red>the game is not ready!");
					return Command.SINGLE_SUCCESS;
				}
				if (targets.isEmpty())
				{
					sender.sendRichMessage("<red>no player found");
					return Command.SINGLE_SUCCESS;
				}

				// the action
				for (Player player : targets)
				{
					if (game.isPlayerIn(player))
					{
						sender.sendRichMessage("<red><b><target></b> is already in game.",
						Placeholder.parsed("target", player.getName())
						);
					} else {
						game.addPlayer(player);
						sender.sendRichMessage("you added <b><target></b>.",
						Placeholder.parsed("target", player.getName())
						);
					}
				}

//				if (targets.size() == 1)
//					sender.sendRichMessage("you added <b><target></b>.",
//					Placeholder.parsed("target", targets.getFirst().getName())
//					);
//				else
//					sender.sendRichMessage("you added <b><amount></b> players.",
//					Placeholder.parsed("amount", Integer.toString(targets.size()))
//					);
				return Command.SINGLE_SUCCESS;
			}
			)))

		.then(Commands.literal("remove")
			.then(Commands.argument("players", ArgumentTypes.players())
			.executes(
			ctx -> {
				final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
				final List<Player> targets = targetResolver.resolve(ctx.getSource());
				final CommandSender sender = ctx.getSource().getSender();
				final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

				// checks
				if (!game.isReady())
				{
					sender.sendRichMessage("<red>the game is not ready!");
					return Command.SINGLE_SUCCESS;
				}
				if (targets.isEmpty())
				{
					sender.sendRichMessage("<red>no player found");
					return Command.SINGLE_SUCCESS;
				}

				// the action
				for (Player player : targets)
				{
					if (!game.isPlayerIn(player))
					{
						sender.sendRichMessage("<red><b><target></b> is not in game.",
						Placeholder.parsed("target", player.getName())
						);
					} else {
						game.removePlayer(player);
						sender.sendRichMessage("you removed <b><target></b>.",
						Placeholder.parsed("target", player.getName())
						);
					}
				}

//				if (targets.size() == 1)
//					sender.sendRichMessage("you removed <b><target></b>.",
//					Placeholder.parsed("target", targets.getFirst().getName())
//					);
//				else
//					sender.sendRichMessage("you removed <b><amount></b> players.",
//					Placeholder.parsed("amount", Integer.toString(targets.size()))
//					);
				return Command.SINGLE_SUCCESS;
			}
			))
		);
	}
}
