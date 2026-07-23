package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.aktomik.redclocktower.game.TownHall;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public class TownArgumentType implements CustomArgumentType<TownHall, String> {

	public static final DynamicCommandExceptionType ERROR_UNKNOWN_TOWN =
	new DynamicCommandExceptionType(name ->
		new LiteralMessage("there is no townhall named "+ name+".")
	);

	@Override
	public TownHall parse(StringReader reader) throws CommandSyntaxException {
		throw new IllegalStateException("TownArgumentType requires a CommandSourceStack");
	}

	@Override
	public ArgumentType<String> getNativeType() {
		return StringArgumentType.word();
	}

	@Override
	public <S> TownHall parse(StringReader reader, S source) throws CommandSyntaxException {
		if (!(source instanceof CommandSourceStack sourceStack))
			throw new IllegalStateException("Unexpected source type: " + source);

		String name = reader.readUnquotedString();
		World world = sourceStack.getLocation().getWorld();
		TownHall town = TownHall.get(world, name);
		if (town == null) throw ERROR_UNKNOWN_TOWN.create(name);
		return town;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(
	CommandContext<S> context, SuggestionsBuilder builder) {
		if (context.getSource() instanceof CommandSourceStack sourceStack) {
			World world = sourceStack.getLocation().getWorld();
			TownHall.getTownList(world)
				.stream().filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
				.forEach(builder::suggest);
		}
		return builder.buildFuture();
	}
}