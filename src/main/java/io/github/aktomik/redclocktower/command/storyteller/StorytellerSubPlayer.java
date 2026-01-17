package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.GameToolbox;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierToolbox;
import io.github.aktomik.redclocktower.utils.brigadier.SubBrigadierBase;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.BloodPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class StorytellerSubPlayer extends SubBrigadierBase {

// build

	public String name() { return "player"; }

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.executes(subList)

		.then(Commands.literal("list")
			.executes(subList))

		.then(Commands.literal("add")
			.then(Commands.argument("players", ArgumentTypes.players())
				.executes(subAdd)))

		.then(Commands.literal("spectator")
			.then(Commands.argument("players", ArgumentTypes.players())
				.executes(subSpectate)))

		.then(Commands.literal("storyteller")
			.executes(subStorytellCheck)
			.then(Commands.argument("player", ArgumentTypes.player())
				.executes(subStorytellChange)))

		.then(Commands.literal("remove")
			.then(Commands.argument("players", ArgumentTypes.players())
				.executes(subRemove)))

		.then(Commands.literal("alive")
			.then(Commands.argument("players", ArgumentTypes.players())
				.executes(subAliveCheck)
				.then(Commands.argument("change", BoolArgumentType.bool())
					.executes(subAliveChange))))

		.then(Commands.literal("voken")
			.then(Commands.argument("players", ArgumentTypes.players())
				.executes(subTokenCheck)
				.then(Commands.argument("change", BoolArgumentType.bool())
					.executes(subTokenChange))))

		.then(Commands.literal("voting")
			.then(Commands.argument("players", ArgumentTypes.players())
				.executes(subVotingCheck)
				.then(Commands.argument("change", BoolArgumentType.bool())
					.executes(subVotingChange))));
	}

	// subs

	Command<CommandSourceStack> subList = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;

		// the action
		List<OfflinePlayer> allPlayersAsOffline = game.getAllPlayersAsOffline();
		int playerAmount = allPlayersAsOffline.size();
		int emptyAmount = game.remainingSlots();
		if (playerAmount == 0)
		{
			sender.sendRichMessage("<white>there is not player in game");
			return Command.SINGLE_SUCCESS;
		}
		sender.sendRichMessage("<white>there is <player_amount> players in game:",
			Placeholder.parsed("player_amount", Integer.toString(playerAmount))
		);

		for (OfflinePlayer offlinePlayer : allPlayersAsOffline)
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

		if (emptyAmount > 0)
		{
			sender.sendRichMessage("<gray><i><empty_amount> slots are empty",
				Placeholder.parsed("empty_amount", Integer.toString(emptyAmount))
			);
		} else {
			sender.sendRichMessage("<gray><i><b>the game is full");
		}

		return Command.SINGLE_SUCCESS;
	};


	Command<CommandSourceStack> subAdd = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

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
			if (game.isFull())
			{
				sender.sendRichMessage("<red><b><target></b> can't be added because the game is full.",
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



	Command<CommandSourceStack> subSpectate = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		for (Player player : players)
		{
			if (game.isPlayerIn(player))
			{
				sender.sendRichMessage("<red><b><target></b> is already in the game as a player.",
				Placeholder.parsed("target", player.getName())
				);
				continue;
			}
			game.addSpectator(player);
			sender.sendRichMessage("you added <b><target></b> as a spectator.",
			Placeholder.parsed("target", player.getName())
			);
		}
		return Command.SINGLE_SUCCESS;
	};


	Command<CommandSourceStack> subStorytellCheck = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx);

		// the action
		Player player = game.getStoryteller();
		if (player == null)
			sender.sendRichMessage("this game does not have a storyteller.");
		else
			sender.sendRichMessage("<b><target></b> is the storyteller.",
			Placeholder.parsed("target", player.getName())
			);
		return Command.SINGLE_SUCCESS;
	};


	Command<CommandSourceStack> subStorytellChange = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final Player player = BrigadierToolbox.resolvePlayer(ctx);
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNoPlayer(sender, player)) return Command.SINGLE_SUCCESS;

		// the action
		game.changeStoryteller(player);
		sender.sendRichMessage("<b><target></b> is now the storyteller.",
		Placeholder.parsed("target", player.getName())
		);
		return Command.SINGLE_SUCCESS;
	};


	public final Command<CommandSourceStack> subRemove = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

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

	public final Command<CommandSourceStack> subAliveCheck = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		GameToolbox.forEachValidPlayer(sender, game, players, (player, bp) -> {
			sender.sendRichMessage(
			bp.getAlive()
				? "<b><target></b> is alive."
				: "<b><target></b> is dead.",
			Placeholder.parsed("target", player.getName())
			);
		});
		return Command.SINGLE_SUCCESS;
	};

	public final Command<CommandSourceStack> subAliveChange = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final boolean changeValue = BrigadierToolbox.resolveBool("change", ctx);
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		GameToolbox.forEachValidPlayer(sender, game, players, (player, bp) -> {
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

	public final Command<CommandSourceStack> subTokenCheck = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		GameToolbox.forEachValidPlayer(sender, game, players, (player, bp) -> {
			sender.sendRichMessage(
			bp.getVoteToken()
			? "<b><target></b> still have a vote token."
			: "<b><target></b> don't have a vote token.",
			Placeholder.parsed("target", player.getName())
			);
		});
		return Command.SINGLE_SUCCESS;
	};

	public final Command<CommandSourceStack> subTokenChange = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final boolean changeValue = BrigadierToolbox.resolveBool("change", ctx);
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		GameToolbox.forEachValidPlayer(sender, game, players, (player, bp) -> {
			if (bp.getVoteToken() == changeValue) {
				sender.sendRichMessage(
				changeValue
				? "<gray><b><target></b> already have a vote token."
				: "<gray><b><target></b> already don't have a vote token.",
				Placeholder.parsed("target", player.getName())
				);
				return;
			}

			bp.changeVoteToken(changeValue);
			sender.sendRichMessage(
			changeValue
			? "<green>giving back</green> the vote token of <b><target></b>."
			: "<red>taking back</red> the vote token of <b><target></b>.",
			Placeholder.parsed("target", player.getName())
			);
		});
		return Command.SINGLE_SUCCESS;
	};

	public final Command<CommandSourceStack> subVotingCheck = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		GameToolbox.forEachValidPlayer(sender, game, players, (player, bp) -> {
			sender.sendRichMessage(
			bp.getVotePull()
			? "<b><target></b> is voting."
			: "<b><target></b> is not voting.",
			Placeholder.parsed("target", player.getName())
			);
		});
		return Command.SINGLE_SUCCESS;
	};

	public final Command<CommandSourceStack> subVotingChange = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final boolean changeValue = BrigadierToolbox.resolveBool("change", ctx);
		final BloodGame game = BloodGame.get(ctx);

		// checks
		if (GameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		GameToolbox.forEachValidPlayer(sender, game, players, (player, bp) -> {
			if (bp.getVotePull() == changeValue) {
				sender.sendRichMessage(
				changeValue
				? "<gray><b><target></b> is already voting."
				: "<gray><b><target></b> is already not voting.",
				Placeholder.parsed("target", player.getName())
				);
				return;
			}

			bp.changeVotePull(changeValue);
			sender.sendRichMessage(
			changeValue
			? "<b><target></b> is now <gold>voting</gold>."
			: "<b><target></b> is <yellow>not voting</yellow> anymore.",
			Placeholder.parsed("target", player.getName())
			);
		});
		return Command.SINGLE_SUCCESS;
	};
}
