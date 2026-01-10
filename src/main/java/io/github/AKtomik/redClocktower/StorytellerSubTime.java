package io.github.AKtomik.redClocktower;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class StorytellerSubTime extends SubBrigadierBase {

	public String name() {
		return "time";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return Commands.literal("time")
		.then(Commands.argument("period", EnumArgument.simple(BloodDayPeriod.class))
		.executes(ctx -> {
			World world;
			CommandSender sender = ctx.getSource().getSender();
			Entity executor = ctx.getSource().getExecutor();

			Location location = ctx.getSource().getLocation();
			world = location.getWorld();
			final BloodDayPeriod dayPeriod = ctx.getArgument("period", BloodDayPeriod.class);

			sender.sendMessage(
			Component.text("switching to time ").color(NamedTextColor.LIGHT_PURPLE)
			.append(Component.text(dayPeriod.toString()).color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
			.append(Component.text("").color(NamedTextColor.LIGHT_PURPLE))
			);
			dayPeriodsStartAction.get(dayPeriod).accept(world);
			return Command.SINGLE_SUCCESS;
		}));
	}

	Map<BloodDayPeriod, Consumer<World>> dayPeriodsStartAction = Map.ofEntries(
	Map.entry(BloodDayPeriod.MORNING, (world) -> {
		Bukkit.getServer().broadcast(
		Component.text("it's the morning!").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
		);
		Bukkit.getServer().broadcast(
		Component.text("everyone to the townhall").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
		);
		world.setTime(0);
	}),
	Map.entry(BloodDayPeriod.FREE, (world) -> {
		Bukkit.getServer().broadcast(
		Component.text("wonder time").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
		);
		Bukkit.getServer().broadcast(
		Component.text("you are free to go").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
		);
		world.setTime(6000);
	}),
	Map.entry(BloodDayPeriod.MEET, (world) -> {
		Bukkit.getServer().broadcast(
		Component.text("debate time").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
		);
		Bukkit.getServer().broadcast(
		Component.text("everyone to the townhall").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
		);
		world.setTime(12000);
	}),
	Map.entry(BloodDayPeriod.NIGHT, (world) -> {
		Bukkit.getServer().broadcast(
		Component.text("the moon is rising...").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
		);
		Bukkit.getServer().broadcast(
		Component.text("go to your house and sleep well").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
		);
		world.setTime(18000);
	})
	);
}


@NullMarked
enum BloodDayPeriod {
	MORNING,
	FREE,
	MEET,
	NIGHT;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}