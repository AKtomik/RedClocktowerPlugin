package io.github.AKtomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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

public class StorytellerSubPlayer extends SubBrigadierBase {

	public String name() { return "player"; }

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base().executes(subList)

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

	Command<CommandSourceStack> subList = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		// checks
		if (!game.isReady())
		{
			sender.sendRichMessage("<red>the game is not ready!");
			return Command.SINGLE_SUCCESS;
		}

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
		final List<Player> players = ctx.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		// checks
		if (!game.isReady())
		{
			sender.sendRichMessage("<red>the game is not ready!");
			return Command.SINGLE_SUCCESS;
		}
		if (players.isEmpty())
		{
			sender.sendRichMessage("<red>no player found");
			return Command.SINGLE_SUCCESS;
		}

		// the action
		for (Player player : players)
		{
			if (game.isPlayerIn(player))
			{
				sender.sendRichMessage("<gray><b><target></b> is already in game.",
				Placeholder.parsed("target", player.getName())
				);
			} else {
				game.addPlayer(player);
				sender.sendRichMessage("you added <b><target></b>.",
				Placeholder.parsed("target", player.getName())
				);
			}
		}
		return Command.SINGLE_SUCCESS;
	};

	public Command<CommandSourceStack> subRemove = ctx -> {
		final List<Player> players = ctx.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		// checks
		if (!game.isReady())
		{
			sender.sendRichMessage("<red>the game is not ready!");
			return Command.SINGLE_SUCCESS;
		}
		if (players.isEmpty())
		{
			sender.sendRichMessage("<red>no player found");
			return Command.SINGLE_SUCCESS;
		}

		// the action
		for (Player player : players)
		{
			if (!game.isPlayerIn(player))
			{
				sender.sendRichMessage("<gray><b><target></b> is not in game.",
				Placeholder.parsed("target", player.getName())
				);
			} else {
				game.removePlayer(player);
				sender.sendRichMessage("you removed <b><target></b>.",
				Placeholder.parsed("target", player.getName())
				);
			}
		}
		return Command.SINGLE_SUCCESS;
	};

	public Command<CommandSourceStack> subAliveCheck = ctx -> {
		final List<Player> players = ctx.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		// checks
		if (!game.isReady())
		{
			sender.sendRichMessage("<red>the game is not ready!");
			return Command.SINGLE_SUCCESS;
		}
		if (players.isEmpty())
		{
			sender.sendRichMessage("<red>no player found");
			return Command.SINGLE_SUCCESS;
		}

		// the action
		for (Player player : players)
		{
			if (!game.isPlayerIn(player))
			{
				sender.sendRichMessage("<red><b><target></b> is not in game.",
				Placeholder.parsed("target", player.getName())
				);
				continue;
			}

			BloodPlayer bloodPlayer = BloodPlayer.get(player);
			String returnMessage = (bloodPlayer.getAlive())
				? "<b><target></b> is alive."
				: "<b><target></b> is dead.";
			sender.sendRichMessage(returnMessage, Placeholder.parsed("target", player.getName()));
		}
		return Command.SINGLE_SUCCESS;
	};

	public Command<CommandSourceStack> subAliveChange = ctx -> {
		final List<Player> players = ctx.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
		final boolean changeValue = ctx.getArgument("change", boolean.class);
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		// checks
		if (!game.isReady())
		{
			sender.sendRichMessage("<red>the game is not ready!");
			return Command.SINGLE_SUCCESS;
		}
		if (players.isEmpty())
		{
			sender.sendRichMessage("<red>no player found");
			return Command.SINGLE_SUCCESS;
		}

		// the action
		for (Player player : players)
		{
			if (!game.isPlayerIn(player))
			{
				sender.sendRichMessage("<red><b><target></b> is not in game.",
				Placeholder.parsed("target", player.getName())
				);
				continue;
			}

			BloodPlayer bloodPlayer = BloodPlayer.get(player);
			if (changeValue == bloodPlayer.getAlive())
			{
				String returnMessage = (changeValue)
					? "<gray><b><target></b> is already alive."
					: "<gray><b><target></b> is already dead.";
				sender.sendRichMessage(returnMessage, Placeholder.parsed("target", player.getName()));
				continue;
			}

			String returnMessage = (changeValue)
				? "<b><target></b> is now <yellow>alive</yellow>."
				: "<b><target></b> is now <red>dead</red>.";
			bloodPlayer.changeAlive(changeValue);
			sender.sendRichMessage(returnMessage, Placeholder.parsed("target", player.getName()));
		}
		return Command.SINGLE_SUCCESS;
	};
}
