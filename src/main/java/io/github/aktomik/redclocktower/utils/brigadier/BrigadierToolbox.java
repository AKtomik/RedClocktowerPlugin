package io.github.aktomik.redclocktower.utils.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BrigadierToolbox {

	private BrigadierToolbox() {}// is a static class

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
