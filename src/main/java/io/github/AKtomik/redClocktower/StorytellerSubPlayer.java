package io.github.AKtomik.redClocktower;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StorytellerSubPlayer extends SubBrigadierBase {

	String name() { return "player"; }

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.literal("add")
			.then(Commands.argument("players", ArgumentTypes.players())
			.executes(
			ctx -> {
				final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
				final List<Player> targets = targetResolver.resolve(ctx.getSource());
				final CommandSender sender = ctx.getSource().getSender();

				if (targets.isEmpty())
				{
					sender.sendMessage(Component.text("no player found").color(NamedTextColor.RED));
					return Command.SINGLE_SUCCESS;
				}

				// the action

				if (targets.size() == 1)
					sender.sendRichMessage("you added <b><target></b>.",
					Placeholder.component("target", Component.text(targets.getFirst().getName()))
					);
				else
					sender.sendRichMessage("you added <b><amount></b> players.",
					Placeholder.component("amount", Component.text(targets.size()))
					);
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

				if (targets.isEmpty())
				{
					sender.sendMessage(Component.text("no player found").color(NamedTextColor.RED));
					return Command.SINGLE_SUCCESS;
				}

				// the action

				if (targets.size() == 1)
					sender.sendRichMessage("you removed <b><target></b>.",
					Placeholder.component("target", Component.text(targets.getFirst().getName()))
					);
				else
					sender.sendRichMessage("you removed <b><amount></b> players.",
					Placeholder.component("amount", Component.text(targets.size()))
					);
				return Command.SINGLE_SUCCESS;
			}
			))
		);
	}
}
