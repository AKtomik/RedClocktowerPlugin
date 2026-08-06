package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.commandbuild.arguments.SeatedListArgumentType;
import io.github.aktomik.redclocktower.commandbuild.tools.CommandLoopResult;
import io.github.aktomik.redclocktower.commandbuild.tools.CommandToolbox;
import io.github.aktomik.redclocktower.commandbuild.tools.GameCommand;
import io.github.aktomik.redclocktower.game.BloodPlayer;
import io.github.aktomik.redclocktower.game.Seated;
import io.github.aktomik.redclocktower.game.SeatedPlayer;
import io.github.aktomik.redclocktower.oldgame.OldBloodGame;
import io.github.aktomik.redclocktower.oldgame.OldGameToolbox;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierToolbox;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

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
		.then(Commands.literal("remove")
			.then(Commands.argument("players", ArgumentTypes.players())
				.executes(subRemove)
			)
		)
		.then(Commands.literal("spectator")
			.executes(subSpectatorList)
			.then(Commands.argument("players", ArgumentTypes.players())
				.executes(subSpectatorAdd)
			)
		)
		.then(Commands.literal("storyteller")
			.executes(subStorytellerList)
			.then(Commands.argument("players", ArgumentTypes.players())
				.executes(subStorytellerAdd)
			)
		)

		// modify
		.then(Commands.literal("set")
			.then(Commands.argument("member", new SeatedListArgumentType())
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
//				.then(Commands.literal("traveller")
//					.executes(subTravelerCheck)
//					.then(Commands.argument("change", BoolArgumentType.bool())
//						.executes(subTravelerChange)
//					)
//				)
			)
		);

		// misc
//		.then(Commands.literal("givehand")
//			.executes(subGiveHand))
	}

	// subs

	Command<CommandSourceStack> subList = GameCommand.wrap((ctx, sender, game) -> {
		List<Seated> seatedList = game.getAllSeated().toList();
		int emptySlotsAmount = game.getCircle().getSlotCount() - seatedList.size();
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
				Placeholder.component("name", Component.text(seated.getName()).color(seated.getSeatedTypeColor()))
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
	});


	Command<CommandSourceStack> subAdd = GameCommand.wrap((ctx, sender, game) -> {
		List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		if (CommandToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		List<CommandLoopResult<Player>> results = CommandToolbox.processEach(players, player -> {
			BloodPlayer bloodPlayer = BloodPlayer.get(player);

			if (bloodPlayer.getSeatedGame() == game)
				return new CommandLoopResult<>(player, false, "<red><b><target></b> is already in game");
			if (bloodPlayer.getStorytellingGame() != null)
				return new CommandLoopResult<>(player, false, "<gray><b><target></b> is a storyteller");
			if (game.getCircle().isFull())
				return new CommandLoopResult<>(player, false, "<red>the game is full");

			game.getCircle().getSlot(game.getCircle().getFirstEmptySlotIndex())
			.assign(new SeatedPlayer(player));

			return new CommandLoopResult<>(player, true, "<b><target></b> added");
		});

		CommandToolbox.sendProcessResult(sender, results, Player::getName,
			"you added <b><count></b> <word>",
			"player", "players"
		);
		return Command.SINGLE_SUCCESS;
	});

	Command<CommandSourceStack> subRemove = GameCommand.wrap((ctx, sender, game) -> {
		List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		if (CommandToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		List<CommandLoopResult<Player>> results = CommandToolbox.processEach(players, player -> {
			BloodPlayer bloodPlayer = BloodPlayer.get(player);

			if (bloodPlayer.getStorytellingGame() == game)
			{
				game.removeStoryteller(bloodPlayer);
				return new CommandLoopResult<>(player, true, "<b><target></b> is not storytelling this game anymore");
			}
			if (bloodPlayer.getSpectatingGame() == game)
			{
				game.removeSpectator(bloodPlayer);
				return new CommandLoopResult<>(player, true, "<b><target></b> is not spectating this game anymore");
			}
			if (bloodPlayer.getSeatedGame() == game)
			{
				Seated seated = bloodPlayer.getSeated();
				Objects.requireNonNull(seated).getSlot().empty();
				return new CommandLoopResult<>(player, true, "player <b><target></b> removed from the game");
			}
			return new CommandLoopResult<>(player, false, "<red><b><target></b> is not in game");
		});

		CommandToolbox.sendProcessResult(sender, results, Player::getName,
		"you removed <b><count></b> <word>",
		"player", "players"
		);
		return Command.SINGLE_SUCCESS;
	});


	Command<CommandSourceStack> subSpectatorList = GameCommand.wrap((ctx, sender, game) -> {
		List<OfflinePlayer> spectators = game.getAllSpectators().toList();
		if (spectators.isEmpty())
			sender.sendRichMessage("this game does not have any spectator");
		else {
			List<TextComponent> spectatorsComponent = spectators.stream().map(
			offlinePlayer -> Component.text(Objects.requireNonNull(offlinePlayer.getName()))).toList();
			if (spectators.size() == 1)
				sender.sendRichMessage("<b><target></b> is spectating this game",
					Placeholder.component("target", spectatorsComponent.getFirst())
				);
			else {
				JoinConfiguration joinConfig = JoinConfiguration.builder()
					.separator(Component.text(", "))
					.lastSeparator(Component.text(" and "))
					.lastSeparatorIfSerial(Component.text(","))
					.build();
				sender.sendRichMessage("<targets> are spectating this game",
					Placeholder.component("targets", Component.join(joinConfig, spectatorsComponent))
				);
			}
		}
		return Command.SINGLE_SUCCESS;
	});

	Command<CommandSourceStack> subSpectatorAdd = GameCommand.wrap((ctx, sender, game) -> {
		List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		if (CommandToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		List<CommandLoopResult<Player>> results = CommandToolbox.processEach(players, player -> {
			BloodPlayer bloodPlayer = BloodPlayer.get(player);

			if (bloodPlayer.getSpectatingGame() != null)
				return new CommandLoopResult<>(player, false, "<red><b><target></b> is already spectating");
			if (bloodPlayer.getSeatedGame() != null)
				return new CommandLoopResult<>(player, false, "<red><b><target></b> is playing");

			game.addSpectator(bloodPlayer);

			return new CommandLoopResult<>(player, true, "<b><target></b> is now spectating this game");
		});

		CommandToolbox.sendProcessResult(sender, results, Player::getName,
		"you added <b><count></b> <word>",
		"spectator", "spectators"
		);
		return Command.SINGLE_SUCCESS;
	});


	Command<CommandSourceStack> subStorytellerList = GameCommand.wrap((ctx, sender, game) -> {
		List<OfflinePlayer> storytellers = game.getAllStorytellers().toList();
		if (storytellers.isEmpty())
			sender.sendRichMessage("this game does not have a storyteller");
		else {
			List<TextComponent> storytellersComponent = storytellers.stream().map(
				offlinePlayer -> Component.text(Objects.requireNonNull(offlinePlayer.getName()))).toList();
			if (storytellers.size() == 1)
				sender.sendRichMessage("<b><target></b> is storytelling this game",
					Placeholder.component("target", storytellersComponent.getFirst())
				);
			else {
				JoinConfiguration joinConfig = JoinConfiguration.builder()
					.separator(Component.text(", "))
					.lastSeparator(Component.text(" and "))
					.lastSeparatorIfSerial(Component.text(","))
					.build();
				sender.sendRichMessage("<targets> are storytelling this game",
					Placeholder.component("targets", Component.join(joinConfig, storytellersComponent))
				);
			}
		}
		return Command.SINGLE_SUCCESS;
	});

	Command<CommandSourceStack> subStorytellerAdd = GameCommand.wrap((ctx, sender, game) -> {
		List<Player> players = BrigadierToolbox.resolvePlayers(ctx);
		if (CommandToolbox.failIfNoPlayers(sender, players)) return Command.SINGLE_SUCCESS;

		List<CommandLoopResult<Player>> results = CommandToolbox.processEach(players, player -> {
			BloodPlayer bloodPlayer = BloodPlayer.get(player);

			if (bloodPlayer.getStorytellingGame() != null)
				return new CommandLoopResult<>(player, false, "<gray><b><target></b> is already storytelling");
			if (bloodPlayer.getSeatedGame() != null)
				return new CommandLoopResult<>(player, false, "<gray><b><target></b> is playing");

			game.addStoryteller(bloodPlayer);

			return new CommandLoopResult<>(player, true, "<b><target></b> is now storytelling this game");
		});

		CommandToolbox.sendProcessResult(sender, results, Player::getName,
		"you added <b><count></b> <word>",
		"storyteller", "storytellers"
		);
		return Command.SINGLE_SUCCESS;
	});


	Command<CommandSourceStack> subAliveCheck = GameCommand.wrap((ctx, sender, game) -> {
		final List<Seated> seatedList = SeatedListArgumentType.getSeatedList(ctx, "member");

		List<CommandLoopResult<Seated>> results = CommandToolbox.processEach(seatedList, seated ->
			new CommandLoopResult<>(seated, seated.getAlive(), "<b><target></b> is "+ (seated.getAlive() ? "alive" : "dead"))
		);

		CommandToolbox.sendProcessResult(sender, results, Seated::getName,
		"<b><count></b> <word> alive",
		"member is", "members are"
		);
		return Command.SINGLE_SUCCESS;
	});

	Command<CommandSourceStack> subAliveChange = GameCommand.wrap((ctx, sender, game) -> {
		final List<Seated> seatedList = SeatedListArgumentType.getSeatedList(ctx, "member");
		final boolean changeValue = BrigadierToolbox.resolveBool("change", ctx);
		final String changeString = changeValue ? "alive" : "dead";

		List<CommandLoopResult<Seated>> results = CommandToolbox.processEach(seatedList, seated -> {
			if (seated.getAlive() == changeValue)
				return new CommandLoopResult<>(seated, false, "<gray><b><target></b> is already "+changeString);

			seated.setAlive(changeValue);
			return new CommandLoopResult<>(seated, true, "<b><target></b> is now "+changeString);
		}
		);

		CommandToolbox.sendProcessResult(sender, results, Seated::getName,
		"<b><count></b> <word> set "+changeString,
		"member", "members"
		);
		return Command.SINGLE_SUCCESS;
	});

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
