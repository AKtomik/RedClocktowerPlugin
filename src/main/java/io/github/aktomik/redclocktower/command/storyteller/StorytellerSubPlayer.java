package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.*;
import io.github.aktomik.redclocktower.oldgame.OldBloodGame;
import io.github.aktomik.redclocktower.oldgame.OldBloodPlayer;
import io.github.aktomik.redclocktower.oldgame.OldGameToolbox;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierToolbox;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class StorytellerSubPlayer extends BrigadierSub {

// build

	public String name() { return "player"; }

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()

		// list
		.executes(subList)
		.then(Commands.literal("list")
			.executes(subList)
		)

		// manage
		.then(Commands.literal("add")
			.then(Commands.argument("players", ArgumentTypes.players())
				.executes(subAdd)
			)
		)
//		.then(Commands.literal("spectator")
//			.then(Commands.argument("players", ArgumentTypes.players())
//				.executes(subSpectate)
//			)
//		)
//		.then(Commands.literal("storyteller")
//			.executes(subStorytellCheck)
//			.then(Commands.argument("player", ArgumentTypes.player())
//				.executes(subStorytellChange)
//			)
//		)
		.then(Commands.literal("remove")
			.then(Commands.argument("players", ArgumentTypes.players())
				.executes(subRemove)
			)
		)

		// modify
		.then(Commands.literal("set")
			.then(Commands.argument("players", ArgumentTypes.players())
//				.then(Commands.literal("traveller")
//					.executes(subTravelerCheck)
//					.then(Commands.argument("change", BoolArgumentType.bool())
//						.executes(subTravelerChange)
//					)
//				)
				.then(Commands.literal("alive")
					.executes(subAliveCheck)
					.then(Commands.argument("change", BoolArgumentType.bool())
						.executes(subAliveChange)
					)
				)
//				.then(Commands.literal("voken")
//					.executes(subTokenCheck)
//					.then(Commands.argument("change", BoolArgumentType.bool())
//						.executes(subTokenChange)
//					)
//				)
//				.then(Commands.literal("voting")
//					.executes(subVotingCheck)
//					.then(Commands.argument("change", BoolArgumentType.bool())
//						.executes(subVotingChange)
//					)
//				)
			)
		)

		// misc
		.then(Commands.literal("givehand")
			.executes(subGiveHand));
	}

	// subs

	Command<CommandSourceStack> subList = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		// checks
		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
		assert game != null;

		// the action
		List<Seated> seatedList = game.getAllSeated().toList();
		int emptySlotsAmount = game.getSlotCount() - seatedList.size();
		if (seatedList.isEmpty())
		{
			sender.sendRichMessage("<white>there is not player in game");
			return Command.SINGLE_SUCCESS;
		}
		sender.sendRichMessage("<white>there is <player_amount> players in game:",
			Placeholder.parsed("player_amount", Integer.toString(seatedList.size()))
		);

		for (Seated seated : seatedList)
		{

			String logo = (seated.getAlive()) ? "<white>♟ " : "<gray>☠ ";
			sender.sendRichMessage(
				"<logo> <type> <name>",
				Placeholder.parsed("logo", logo),
				Placeholder.parsed("type", seated.getSeatedTypeString()),
				Placeholder.component("name", Component.text(seated.getDisplayName()).color(seated.getSeatedTypeColor()))
			);
		}

		if (emptySlotsAmount > 0)
		{
			sender.sendRichMessage("<gray><i><empty_amount> slots are empty",
				Placeholder.parsed("empty_amount", Integer.toString(emptySlotsAmount))
			);
		} else {
			sender.sendRichMessage("<gray><i><b>the game is full");
		}

		return Command.SINGLE_SUCCESS;
	};


	Command<CommandSourceStack> subAdd = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);

		// checks
		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
		assert game != null;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		for (Player player : players)
		{
			BloodPlayer bloodPlayer = BloodPlayer.get(player);
			if (bloodPlayer.getSeatedGame() == game)
			{
				sender.sendRichMessage("<gray><b><target></b> is already in game",
					Placeholder.parsed("target", player.getName())
				);
				continue;
			}
			if (bloodPlayer.getStorytellingGame() != null)
			{
				sender.sendRichMessage("<gray><b><target></b> is a storyteller",
					Placeholder.parsed("target", player.getName())
				);
				continue;
			}
			if (game.isFull())
			{
				sender.sendRichMessage("<red><b><target></b> can't be added because the game is full",
					Placeholder.parsed("target", player.getName())
				);
				continue;
			}

			final Seated seated = new SeatedPlayer(player);
			game.getSlot(game.getFirstEmptySlotIndex()).assign(seated);
			sender.sendRichMessage("you added <b><target></b>",
			Placeholder.parsed("target", player.getName())
			);
		}
		return Command.SINGLE_SUCCESS;
	};



	Command<CommandSourceStack> subSpectate = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final OldBloodGame game = OldBloodGame.get(ctx);

		// checks
		if (OldGameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (OldGameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

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
		final OldBloodGame game = OldBloodGame.get(ctx);

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
		final OldBloodGame game = OldBloodGame.get(ctx);

		// checks
		if (OldGameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (OldGameToolbox.failIfNoPlayer(sender, player)) return Command.SINGLE_SUCCESS;

		// the action
		game.changeStoryteller(player);
		sender.sendRichMessage("<b><target></b> is now the storyteller.",
		Placeholder.parsed("target", player.getName())
		);
		return Command.SINGLE_SUCCESS;
	};


	public final Command<CommandSourceStack> subRemove = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);

		// checks
		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
		assert game != null;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		for (Player player : players)
		{
			BloodPlayer bloodPlayer = BloodPlayer.get(player);
			if (bloodPlayer.getSeatedGame() == game)
			{
				// todo
				sender.sendRichMessage("you removed player <b><target></b>.",
					Placeholder.parsed("target", player.getName())
				);
				continue;
			}

			sender.sendRichMessage("<gray><b><target></b> is not in game.",
			Placeholder.parsed("target", player.getName())
			);
		}
		return Command.SINGLE_SUCCESS;
	};

	public final Command<CommandSourceStack> subTravelerCheck = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final OldBloodGame game = OldBloodGame.get(ctx);

		// checks
		if (OldGameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (OldGameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		OldGameToolbox.forEachValidPlayer(sender, game, players, (player, bp) -> {
			sender.sendRichMessage(
			bp.isTraveller()
			? "<b><target></b> is a traveller."
			: "<b><target></b> is not a traveller.",
			Placeholder.parsed("target", player.getName())
			);
		});
		return Command.SINGLE_SUCCESS;
	};

	public final Command<CommandSourceStack> subTravelerChange = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final boolean changeValue = BrigadierToolbox.resolveBool("change", ctx);
		final OldBloodGame game = OldBloodGame.get(ctx);

		// checks
		if (OldGameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (OldGameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		OldGameToolbox.forEachValidPlayer(sender, game, players, (player, bp) -> {
			if (bp.isTraveller() == changeValue) {
				sender.sendRichMessage(
				changeValue
				? "<gray><b><target></b> is already a traveller."
				: "<gray><b><target></b> is already not a traveller.",
				Placeholder.parsed("target", player.getName())
				);
				return;
			}

			bp.changeTraveller(changeValue);
			sender.sendRichMessage(
			changeValue
			? "<b><target></b> is now <yellow>a traveller</yellow>."
			: "<b><target></b> is <red>not a traveller</red> anymore.",
			Placeholder.parsed("target", player.getName())
			);
		});
		return Command.SINGLE_SUCCESS;
	};

	public final Command<CommandSourceStack> subAliveCheck = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);

		// checks
		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
		assert game != null;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		GameToolbox.forEachValidPlayer(sender, game, players, (player, seated) -> {
			sender.sendRichMessage(
			seated.getAlive()
			? "<b><target></b> is alive."
			: "<b><target></b> is dead.",
			Placeholder.parsed("target", player.getName())
			);
		});
		return Command.SINGLE_SUCCESS;
	};

	public final Command<CommandSourceStack> subAliveChange = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
		final List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		final boolean changeValue = BrigadierToolbox.resolveBool("change", ctx);

		// checks
		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
		assert game != null;
		if (GameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		GameToolbox.forEachValidPlayer(sender, game, players, (player, seated) -> {
			if (seated.getAlive() == changeValue) {
				sender.sendRichMessage(
				changeValue
				? "<gray><b><target></b> is already alive."
				: "<gray><b><target></b> is already dead.",
				Placeholder.parsed("target", player.getName())
				);
				return;
			}

			seated.setAlive(changeValue);
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
		final OldBloodGame game = OldBloodGame.get(ctx);

		// checks
		if (OldGameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (OldGameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		OldGameToolbox.forEachValidPlayer(sender, game, players, (player, bp) -> {
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
		final OldBloodGame game = OldBloodGame.get(ctx);

		// checks
		if (OldGameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (OldGameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		OldGameToolbox.forEachValidPlayer(sender, game, players, (player, bp) -> {
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
		final OldBloodGame game = OldBloodGame.get(ctx);

		// checks
		if (OldGameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (OldGameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		OldGameToolbox.forEachValidPlayer(sender, game, players, (player, bp) -> {
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
		final OldBloodGame game = OldBloodGame.get(ctx);

		// checks
		if (OldGameToolbox.failIfNotReady(sender, game)) return Command.SINGLE_SUCCESS;
		if (OldGameToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		// the action
		OldGameToolbox.forEachValidPlayer(sender, game, players, (player, bp) -> {
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

	public final Command<CommandSourceStack> subGiveHand = ctx -> {
		final CommandSender sender = ctx.getSource().getSender();
		final OldBloodGame game = OldBloodGame.get(ctx);

		Player executorPlayer = (Player)ctx.getSource().getExecutor();
		ItemStack heldItem = executorPlayer.getInventory().getItemInMainHand();

		if (heldItem.getType() == Material.AIR)
		{
			sender.sendRichMessage("<red>you dont have anything in hand");
			return Command.SINGLE_SUCCESS;
		}

		heldItem.setAmount(1);
		game.getAllPlayers().forEach(loopPlayer -> loopPlayer.give(heldItem));

		sender.sendRichMessage(
		"give <item><r> to all players in game.",
		Placeholder.component("item", heldItem.displayName())
		);
		return Command.SINGLE_SUCCESS;
	};
}
