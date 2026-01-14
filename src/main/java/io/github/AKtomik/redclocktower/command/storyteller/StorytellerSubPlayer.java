package io.github.AKtomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.AKtomik.redclocktower.utils.SubBrigadierBase;
import io.github.AKtomik.redclocktower.game.BloodGame;
import io.github.AKtomik.redclocktower.game.BloodPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class StorytellerSubPlayer extends SubBrigadierBase {

// build

	public String name() { return "player"; }

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.requires(ctx -> getGame(ctx).isReady())
		.executes(subList)

		.then(Commands.literal("list")
			.executes(subList))

		.then(Commands.literal("add")
			.then(Commands.argument("players", ArgumentTypes.players())
			.executes(subAdd)))

		.then(Commands.literal("remove")
			.then(Commands.argument("players", ArgumentTypes.players())
			.executes(subRemove)))

		.then(Commands.literal("alive")
			.then(Commands.argument("players", ArgumentTypes.players())
			.executes(subAliveCheck)
			.then(Commands.argument("change", BoolArgumentType.bool())
			.executes(subAliveChange))));
	}

	// utils

	private BloodGame getGame(CommandSourceStack ctx) {
		return BloodGame.get(ctx.getLocation().getWorld());
	}
	private List<Player> resolvePlayers(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		return ctx.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
	}
	private boolean resolveChange(CommandContext<CommandSourceStack> ctx) {
		return ctx.getArgument("change", boolean.class);
	}

	private boolean failIfNotReady(CommandSender sender, BloodGame game) {
		if (!game.isReady()) {
			sender.sendRichMessage("<red>the game is not ready!");
			return true;
		}
		return false;
	}
	private boolean failIfEmpty(CommandSender sender, List<Player> players) {
		if (players.isEmpty()) {
			sender.sendRichMessage("<red>the game is not ready!");
			return true;
		}
		return false;
	}

	private void forEachValidPlayer(
	CommandSender sender,
	BloodGame game,
	List<Player> players,
	BiConsumer<Player, BloodPlayer> action
	) {
		for (Player player : players) {
			if (!game.isPlayerIn(player)) {
				sender.sendRichMessage(
				"<red><b><target></b> is not in game.",
				Placeholder.parsed("target", player.getName())
				);
				continue;
			}
			action.accept(player, BloodPlayer.get(player));
		}
	}

	// subs

	Command<CommandSourceStack> subList = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = getGame(ctx.getSource());

		// checks
		if (failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;

		// the action
		sender.sendRichMessage("<white>all players in game:");
		for (OfflinePlayer offlinePlayer : game.getAllPlayersAsOffline())
		{
			if (offlinePlayer.isOnline())
			{
				Player player = Objects.requireNonNull(offlinePlayer.getPlayer());
				BloodPlayer bloodPlayer = BloodPlayer.get(player);
				String lifeStringColor = (bloodPlayer.isAlive()) ? "<white>♟ " : "<gray>☠ ";
				sender.sendRichMessage("<life_color><target>",
					Placeholder.parsed("life_color", lifeStringColor),
					Placeholder.parsed("target", player.getName())
				);
			} else {
				String playerName = offlinePlayer.getName();
				if (playerName == null) playerName = "<unknow>";
				sender.sendRichMessage("<hover:show_text:\"<red>this player is offline\"><#ff6600>⚠ <target>",
					Placeholder.parsed("target", playerName)
				);
			}
		}
		return Command.SINGLE_SUCCESS;
	};


	Command<CommandSourceStack> subAdd = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = resolvePlayers(ctx);
		final BloodGame game = getGame(ctx.getSource());

		// checks
		if (failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (failIfEmpty(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		for (Player player : players)
		{
			if (game.isPlayerIn(player))
			{
				sender.sendRichMessage("<gray><b><target></b> is already in game.",
				Placeholder.parsed("target", player.getName())
				);
				continue;
			}
			game.addPlayer(player);
			sender.sendRichMessage("you added <b><target></b>.",
			Placeholder.parsed("target", player.getName())
			);
		}
		return Command.SINGLE_SUCCESS;
	};

	public Command<CommandSourceStack> subRemove = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = resolvePlayers(ctx);
		final BloodGame game = getGame(ctx.getSource());

		// checks
		if (failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (failIfEmpty(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		for (Player player : players)
		{
			if (!game.isPlayerIn(player))
			{
				sender.sendRichMessage("<gray><b><target></b> is not in game.",
				Placeholder.parsed("target", player.getName())
				);
				continue;
			}
			game.removePlayer(player);
			sender.sendRichMessage("you removed <b><target></b>.",
			Placeholder.parsed("target", player.getName())
			);
		}
		return Command.SINGLE_SUCCESS;
	};

	public Command<CommandSourceStack> subAliveCheck = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = resolvePlayers(ctx);
		final BloodGame game = getGame(ctx.getSource());

		// checks
		if (failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (failIfEmpty(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		forEachValidPlayer(sender, game, players, (player, bp) -> {
			sender.sendRichMessage(
			bp.getAlive()
				? "<b><target></b> is alive."
				: "<b><target></b> is dead.",
			Placeholder.parsed("target", player.getName())
			);
		});
		return Command.SINGLE_SUCCESS;
	};

	public Command<CommandSourceStack> subAliveChange = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = resolvePlayers(ctx);
		final boolean changeValue = resolveChange(ctx);
		final BloodGame game = getGame(ctx.getSource());

		// checks
		if (failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (failIfEmpty(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		forEachValidPlayer(sender, game, players, (player, bp) -> {
			if (bp.getAlive() == changeValue) {
				sender.sendRichMessage(
				changeValue
				? "<gray><b><target></b> is already alive."
				: "<gray><b><target></b> is already dead.",
				Placeholder.parsed("target", player.getName())
				);
				return;
			}

			bp.changeAlive(changeValue);
			sender.sendRichMessage(
			changeValue
			? "<b><target></b> is now <yellow>alive</yellow>."
			: "<b><target></b> is now <red>dead</red>.",
			Placeholder.parsed("target", player.getName())
			);
		});
		return Command.SINGLE_SUCCESS;
	};
}
