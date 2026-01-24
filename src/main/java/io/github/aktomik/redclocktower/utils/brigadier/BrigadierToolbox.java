package io.github.aktomik.redclocktower.utils.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.eclipse.sisu.bean.LifecycleManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BrigadierToolbox {

	private BrigadierToolbox() {}// is a static class

	public static void loadCommands(Plugin plugin, List<BrigadierCommand> brigadierCommands) {

		plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
			for (BrigadierCommand brigadier : brigadierCommands)
			{
				LiteralCommandNode<CommandSourceStack> build = brigadier.build();
				commands.registrar().register(build, brigadier.aliases());
			}
		});
	}

	@Nullable
	public static Player resolvePlayer(String argumentKey, CommandContext<CommandSourceStack> ctx) {
		try {
			return ctx.getArgument(argumentKey, PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
		} catch (CommandSyntaxException e) {
			if (Objects.equals(e.getMessage(), "No player was found")) return null;
			throw new RuntimeException(e);
		}
	}
	public static Player resolvePlayer(CommandContext<CommandSourceStack> ctx) {
		return resolvePlayer("player", ctx);
	}

	public static List<Player> resolvePlayers(String argumentKey, CommandContext<CommandSourceStack> ctx) {
		try {
			return ctx.getArgument(argumentKey, PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
		} catch (CommandSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	public static List<Player> resolvePlayers(CommandContext<CommandSourceStack> ctx) {
		return resolvePlayers("players", ctx);
	}

	public static boolean resolveBool(String argumentKey, CommandContext<CommandSourceStack> ctx) {
		return ctx.getArgument(argumentKey, boolean.class);
	}
	public static boolean resolveBool(CommandContext<CommandSourceStack> ctx) {
		return resolveBool("bool", ctx);
	}
}
