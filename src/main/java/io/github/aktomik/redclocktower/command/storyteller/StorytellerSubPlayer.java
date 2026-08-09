package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.commandbuild.arguments.SeatedArgumentType;
import io.github.aktomik.redclocktower.commandbuild.arguments.SeatedListArgumentType;
import io.github.aktomik.redclocktower.commandbuild.tools.CommandLoopResult;
import io.github.aktomik.redclocktower.commandbuild.tools.CommandToolbox;
import io.github.aktomik.redclocktower.commandbuild.tools.GameCommand;
import io.github.aktomik.redclocktower.game.BloodPlayer;
import io.github.aktomik.redclocktower.game.Seated;
import io.github.aktomik.redclocktower.game.SeatedPlayer;
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
			.then(Commands.argument("members", new SeatedListArgumentType())
				.then(Commands.literal("alive")
					.executes(subAliveCheck)
					.then(Commands.argument("change", BoolArgumentType.bool())
						.executes(subAliveChange)
					)
				)
				.then(Commands.literal("voken")
					.executes(subTokenCheck)
					.then(Commands.argument("change", BoolArgumentType.bool())
						.executes(subTokenChange)
					)
				)
				.then(Commands.literal("voting")
					.executes(subVotingCheck)
					.then(Commands.argument("change", BoolArgumentType.bool())
						.executes(subVotingChange)
					)
				)
				.then(Commands.literal("traveller")
					.executes(subTravellerCheck)
					.then(Commands.argument("change", BoolArgumentType.bool())
						.executes(subTravellerChange)
					)
				)
			)
		)

		// name
		.then(Commands.literal("rename")
			.then(Commands.argument("member", new SeatedArgumentType())
				.then(Commands.argument("new name", StringArgumentType.word())
					.executes(subNameChange)
				)
			)
		)
		.then(Commands.literal("unname")
			.then(Commands.argument("member", new SeatedArgumentType())
				.executes(subNameClear)
			)
		)

		// give
		.then(Commands.literal("give")
			.then(Commands.literal("hand")
				.executes(subGiveHand)
			)
			.then(Commands.literal("item")
				.then(Commands.argument("item", ArgumentTypes.itemStack())
					.executes(subGiveItem)
				)
			)
		);
	}

	// subs

	final Command<CommandSourceStack> subList = GameCommand.wrap((ctx, sender, game) -> {
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


	final Command<CommandSourceStack> subAdd = GameCommand.wrap((ctx, sender, game) -> {
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
			.assign(new SeatedPlayer(bloodPlayer));

			return new CommandLoopResult<>(player, true, "added <b><target></b>");
		});

		CommandToolbox.sendProcessResult(sender, results, Player::getName,
			"you added <b><count></b> <word>",
			"player", "players"
		);
		return Command.SINGLE_SUCCESS;
	});

	final Command<CommandSourceStack> subRemove = GameCommand.wrap((ctx, sender, game) -> {
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
				return new CommandLoopResult<>(player, true, "removed player <b><target></b> from the game");
			}
			return new CommandLoopResult<>(player, false, "<red><b><target></b> is not in game");
		});

		CommandToolbox.sendProcessResult(sender, results, Player::getName,
		"you removed <b><count></b> <word>",
		"player", "players"
		);
		return Command.SINGLE_SUCCESS;
	});


	final Command<CommandSourceStack> subSpectatorList = GameCommand.wrap((ctx, sender, game) -> {
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

	final Command<CommandSourceStack> subSpectatorAdd = GameCommand.wrap((ctx, sender, game) -> {
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


	final Command<CommandSourceStack> subStorytellerList = GameCommand.wrap((ctx, sender, game) -> {
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

	final Command<CommandSourceStack> subStorytellerAdd = GameCommand.wrap((ctx, sender, game) -> {
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


	// set
	final Command<CommandSourceStack> subAliveCheck = GameCommand.wrap((ctx, sender, game) -> {
		final List<Seated> seatedList = SeatedListArgumentType.getSeatedList(ctx, "members");

		List<CommandLoopResult<Seated>> results = CommandToolbox.processEach(seatedList, seated ->
			new CommandLoopResult<>(seated, seated.getAlive(), "<b><target></b> is "+ (seated.getAlive() ? "alive" : "dead"))
		);

		CommandToolbox.sendProcessResult(sender, results, Seated::getName,
		"<b><count></b> <word> alive",
		"member is", "members are"
		);
		return Command.SINGLE_SUCCESS;
	});

	final Command<CommandSourceStack> subAliveChange = GameCommand.wrap((ctx, sender, game) -> {
		final List<Seated> seatedList = SeatedListArgumentType.getSeatedList(ctx, "members");
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


	final Command<CommandSourceStack> subVotingCheck = GameCommand.wrap((ctx, sender, game) -> {
		final List<Seated> seatedList = SeatedListArgumentType.getSeatedList(ctx, "members");

		List<CommandLoopResult<Seated>> results = CommandToolbox.processEach(seatedList, seated ->
		new CommandLoopResult<>(seated, seated.getVotePull(), "<b><target></b> is "+ (seated.getVotePull() ? "voting" : "not voting"))
		);

		CommandToolbox.sendProcessResult(sender, results, Seated::getName,
		"<b><count></b> <word> voting",
		"member is", "members are"
		);
		return Command.SINGLE_SUCCESS;
	});

	final Command<CommandSourceStack> subVotingChange = GameCommand.wrap((ctx, sender, game) -> {
		final List<Seated> seatedList = SeatedListArgumentType.getSeatedList(ctx, "members");
		final boolean changeValue = BrigadierToolbox.resolveBool("change", ctx);
		final String changeString = changeValue ? "voting" : "not voting";

		List<CommandLoopResult<Seated>> results = CommandToolbox.processEach(seatedList, seated -> {
			if (seated.getVotePull() == changeValue)
				return new CommandLoopResult<>(seated, false, "<gray><b><target></b> is already "+changeString);

			seated.setVotePull(changeValue);
			return new CommandLoopResult<>(seated, true, "<b><target></b> is now "+changeString);
		}
		);

		CommandToolbox.sendProcessResult(sender, results, Seated::getName,
		"<b><count></b> <word> now "+changeString,
		"member is", "members are"
		);
		return Command.SINGLE_SUCCESS;
	});


	final Command<CommandSourceStack> subTokenCheck = GameCommand.wrap((ctx, sender, game) -> {
		final List<Seated> seatedList = SeatedListArgumentType.getSeatedList(ctx, "members");

		List<CommandLoopResult<Seated>> results = CommandToolbox.processEach(seatedList, seated ->
		new CommandLoopResult<>(seated, seated.getVoteToken(), "<b><target></b> "+ (seated.getVoteToken() ? "still" : "don't")+" have their vote token")
		);

		CommandToolbox.sendProcessResult(sender, results, Seated::getName,
		"<b><count></b> <word> have their vote token",
		"member", "members"
		);
		return Command.SINGLE_SUCCESS;
	});

	final Command<CommandSourceStack> subTokenChange = GameCommand.wrap((ctx, sender, game) -> {
		final List<Seated> seatedList = SeatedListArgumentType.getSeatedList(ctx, "members");
		final boolean changeValue = BrigadierToolbox.resolveBool("change", ctx);
		final String changeString = changeValue ? "giving back" : "taking back";

		List<CommandLoopResult<Seated>> results = CommandToolbox.processEach(seatedList, seated -> {
			if (seated.getVoteToken() == changeValue)
				return new CommandLoopResult<>(seated, false,
				"<gray><b><target></b> already "+ ((changeValue) ? "have" : "don't have") +" their vote token");

			seated.setVoteToken(changeValue);
			return new CommandLoopResult<>(seated, true, changeString + " the vote token of <b><target></b>");
		}
		);

		CommandToolbox.sendProcessResult(sender, results, Seated::getName,
		changeString+" <b><count></b> <word>",
		"vote token", "vote tokens"
		);
		return Command.SINGLE_SUCCESS;
	});


	final Command<CommandSourceStack> subTravellerCheck = GameCommand.wrap((ctx, sender, game) -> {
		final List<Seated> seatedList = SeatedListArgumentType.getSeatedList(ctx, "members");

		List<CommandLoopResult<Seated>> results = CommandToolbox.processEach(seatedList, seated ->
		new CommandLoopResult<>(seated, seated.getTraveller(), "<b><target></b> " + (seated.getTraveller() ? "is" : "isn't") + " a traveller")
		);

		CommandToolbox.sendProcessResult(sender, results, Seated::getName,
		"<b><count></b> <word>",
		"member is a traveller", "members are travellers"
		);
		return Command.SINGLE_SUCCESS;
	});

	final Command<CommandSourceStack> subTravellerChange = GameCommand.wrap((ctx, sender, game) -> {
		final List<Seated> seatedList = SeatedListArgumentType.getSeatedList(ctx, "members");
		final boolean changeValue = BrigadierToolbox.resolveBool("change", ctx);
		final String changeString = changeValue ? "a traveller" : "not a traveller";

		List<CommandLoopResult<Seated>> results = CommandToolbox.processEach(seatedList, seated -> {
			if (seated.getTraveller() == changeValue)
				return new CommandLoopResult<>(seated, false, "<gray><b><target></b> is already "+changeString);

			seated.setTraveller(changeValue);
			return new CommandLoopResult<>(seated, true, "<b><target></b> is now "+changeString);
		}
		);

		CommandToolbox.sendProcessResult(sender, results, Seated::getName,
		(changeValue) ? "<b><count></b> <word> now travelling" : "<b><count></b> <word> not travelling anymore",
		"member is", "members are"
		);
		return Command.SINGLE_SUCCESS;
	});

	// name
	final Command<CommandSourceStack> subNameChange = GameCommand.wrap((ctx, sender, game) -> {
		final Seated seated = SeatedArgumentType.getSeated(ctx, "member");
		final String newName = StringArgumentType.getString(ctx, "new name");

		String oldName = seated.getName();
		seated.setName(newName);

		sender.sendRichMessage("you renamed <old_name> to <b><new_name></b>",
			Placeholder.parsed("old_name", oldName), Placeholder.parsed("new_name", newName)
		);
		return Command.SINGLE_SUCCESS;
	});

	final Command<CommandSourceStack> subNameClear = GameCommand.wrap((ctx, sender, game) -> {
		final Seated seated = SeatedArgumentType.getSeated(ctx, "member");

		seated.setName(seated.getId());

		sender.sendRichMessage("you cleared the custom name of <b><default_name></b>",
			Placeholder.parsed("default_name", seated.getName())
		);
		return Command.SINGLE_SUCCESS;
	});

	// item
	public final Command<CommandSourceStack> subGiveHand = GameCommand.wrap((ctx, sender, game) -> {
		Player executorPlayer = Objects.requireNonNull((Player)ctx.getSource().getExecutor());
		ItemStack item = executorPlayer.getInventory().getItemInMainHand();

		if (item.getType() == Material.AIR)
		{
			sender.sendRichMessage("<red>you dont have anything in hand");
			return Command.SINGLE_SUCCESS;
		}

		int itemAmount = item.getAmount();
		long playerCount = game.getOnlinePlayers().count();
		game.getOnlinePlayers().forEach(loopPlayer -> loopPlayer.give(item));

		sender.sendRichMessage(
			"gave <amount> <item> to <count> players",
			Placeholder.component("item", item.displayName()),
			Placeholder.parsed("amount", Integer.toString(itemAmount)),
			Placeholder.parsed("count", Long.toString(playerCount)),
			Placeholder.parsed("word", (playerCount > 1) ? "players" : "player")
		);
		return Command.SINGLE_SUCCESS;
	});

	public final Command<CommandSourceStack> subGiveItem = GameCommand.wrap((ctx, sender, game) -> {
		ItemStack item = ctx.getArgument("item", ItemStack.class);

		int itemAmount = item.getAmount();
		long playerCount = game.getOnlinePlayers().count();
		game.getOnlinePlayers().forEach(loopPlayer -> loopPlayer.give(item));

		sender.sendRichMessage(
			"gave <amount> <item> to <count> players",
			Placeholder.component("item", item.displayName()),
			Placeholder.parsed("amount", Integer.toString(itemAmount)),
			Placeholder.parsed("count", Long.toString(playerCount)),
			Placeholder.parsed("word", (playerCount > 1) ? "players" : "player")
		);
		return Command.SINGLE_SUCCESS;
	});
}
