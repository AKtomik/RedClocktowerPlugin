package io.github.AKtomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.AKtomik.redclocktower.brigadier.EnumArgument;
import io.github.AKtomik.redclocktower.brigadier.SubBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.function.Consumer;

public class StorytellerSubGame extends SubBrigadierBase {
	public String name() {
		return "game";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("state", EnumArgument.simple(BloodGameState.class, "Invalid blood period"))
		.executes(ctx -> {
			World world;
			CommandSender sender = ctx.getSource().getSender();

			Location location = ctx.getSource().getLocation();
			world = location.getWorld();
			final BloodGameState gameState = ctx.getArgument("state", BloodGameState.class);

			sender.sendMessage(
			Component.text("switching to state ").color(NamedTextColor.LIGHT_PURPLE)
			.append(Component.text(gameState.toString()).color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
			.append(Component.text("").color(NamedTextColor.LIGHT_PURPLE))
			);
			gameEnterState.get(gameState).accept(world);
			return Command.SINGLE_SUCCESS;
		}));
	}


	static Map<BloodGameState, Consumer<World>> gameEnterState = Map.ofEntries(
	Map.entry(BloodGameState.SETUP, (world) -> {
		world.setGameRule(GameRules.ADVANCE_TIME, false);
	}),
	Map.entry(BloodGameState.RESET, (world) -> {
		StorytellerSubGame.gameEnterState.get(BloodGameState.SETUP);
		world.setTime(12000);
	}),
	Map.entry(BloodGameState.START, (world) -> {
		StorytellerSubGame.gameEnterState.get(BloodGameState.RESET);
		Bukkit.getServer().broadcast(
		Component.text("are you ready to bleed?").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
		);
	}),
	Map.entry(BloodGameState.FINISH, (world) -> {
		StorytellerSubGame.gameEnterState.get(BloodGameState.RESET);
		Bukkit.getServer().broadcast(
		Component.text("the game is over!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
		);
	}));
}


@NullMarked
enum BloodGameState {
	SETUP,
	RESET,
	START,
	FINISH;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}