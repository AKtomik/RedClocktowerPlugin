package io.github.aktomik.redclocktower.game;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SeatedArgumentType implements CustomArgumentType<Seated, String> {
	private static final SimpleCommandExceptionType ERROR_BAD_SOURCE = new SimpleCommandExceptionType(
		MessageComponentSerializer.message().serialize(Component.text("The source needs to be a CommandSourceStack!"))
	);

	private static final SimpleCommandExceptionType ERROR_NO_GAME = new SimpleCommandExceptionType(
		MessageComponentSerializer.message().serialize(Component.text("can't find member because there is no game"))
	);

	private static final DynamicCommandExceptionType ERROR_UNKNOWN_SEATED = new DynamicCommandExceptionType(name ->
		MessageComponentSerializer.message().serialize(Component.text("there is no member named "+ name+" here"))
	);

	@Override
	public Seated parse(@NonNull StringReader reader) throws CommandSyntaxException {
		throw new UnsupportedOperationException("SeatedArgumentType requires a CommandSourceStack");
	}

	@Override
	public @NonNull ArgumentType<String> getNativeType() {
		return StringArgumentType.word();
	}

	@Override
	public <S> Seated parse(@NonNull StringReader reader, S source) throws CommandSyntaxException {
		if (!(source instanceof CommandSourceStack sourceStack))
			throw ERROR_BAD_SOURCE.create();

		World world = sourceStack.getLocation().getWorld();
		BloodGame game = BloodGame.get(world);
		if (game == null) throw ERROR_NO_GAME.create();

		String input = reader.readUnquotedString();
		List<Seated> foundSeated = game.getAllSeated().filter(seated1 -> Objects.equals(seated1.getName(), input)).toList();
		if (foundSeated.isEmpty()) throw ERROR_UNKNOWN_SEATED.create(input);
		if (foundSeated.size() > 1) throw new IllegalStateException("found multiples seated with the same name");
		return foundSeated.getFirst();
	}

	@Override
	public <S> @NonNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, @NonNull SuggestionsBuilder builder) {
		if (context.getSource() instanceof CommandSourceStack sourceStack) {
			World world = sourceStack.getLocation().getWorld();
			BloodGame game = BloodGame.get(world);
			if (game == null) return builder.buildFuture();
			game.getAllSeated().map(Seated::getName)
				.forEach(builder::suggest);
		}
		return builder.buildFuture();
	}
}