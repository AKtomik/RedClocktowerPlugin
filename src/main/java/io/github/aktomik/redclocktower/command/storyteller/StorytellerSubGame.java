package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.town.TownArgumentType;
import io.github.aktomik.redclocktower.game.*;
import io.github.aktomik.redclocktower.game.BloodPlayer;
import io.github.aktomik.redclocktower.game.town.TownHall;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StorytellerSubGame extends BrigadierSub {
	public String name() {
		return "game";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.literal("setup")
			.then(Commands.argument("town", new TownArgumentType())
				.executes(subSetup)
			)
		)
		.then(Commands.literal("start")
			.executes(subStart)
		)
		.then(Commands.literal("finish")
			.then(Commands.argument("win team", EnumArgument.simple(GameTeam.class, "this is not a team"))
				.executes(subFinish)
			)
		)
		.then(Commands.literal("clear")
			.executes(subClear)
		);
	}

	// subs

	Command<CommandSourceStack> subSetup = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final World world = ctx.getSource().getLocation().getWorld();
		final TownHall townHall = ctx.getArgument("town", TownHall.class);

		// check
		if (BloodGame.get(townHall) != null)
		{
			sender.sendRichMessage("<red>the townhall <b><town></b> is already setup",
			Placeholder.parsed("town", townHall.getTownName())
			);
			return Command.SINGLE_SUCCESS;
		}
		if (BloodGame.get(world) != null)
		{
			sender.sendRichMessage("<red>there is another game setup in this world");
			return Command.SINGLE_SUCCESS;
		}

		// execute
		sender.sendRichMessage("<light_purple>setup townhall <b><aqua><town></aqua></b> for a game",
			Placeholder.parsed("town", townHall.getTownName())
		);
		BloodGame game = BloodGame.create(townHall);
		if (sender instanceof Player player)
		{
			game.addStoryteller(BloodPlayer.get(player));
			sender.sendRichMessage("<dark_purple>you are storytelling this game");
		}
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subStart = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		// check
		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
		if (game.isStarted())
		{
			sender.sendRichMessage("<gray>the game is already started");
			return Command.SINGLE_SUCCESS;
		}

		// execute
		sender.sendRichMessage("<light_purple>starting the game");
		game.start();
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subFinish = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
		final GameTeam winTeam = ctx.getArgument("win team", GameTeam.class);

		// check
		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;
		if (GameToolbox.failIfNotStarted(sender, game)) return Command.SINGLE_SUCCESS;

		// execute
		sender.sendRichMessage("<light_purple>finishing the game");
		game.finish(winTeam);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subClear = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		// check
		if (GameToolbox.failIfNoGame(sender, game)) return Command.SINGLE_SUCCESS;

		// execute
		sender.sendRichMessage("<light_purple>clearing the game");
		game.kill();
		return Command.SINGLE_SUCCESS;
	};
}