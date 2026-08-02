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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class SeatedListArgumentType implements CustomArgumentType<List<Seated>, String> {
	private static final SimpleCommandExceptionType ERROR_NO_GAME = new SimpleCommandExceptionType(
		MessageComponentSerializer.message().serialize(Component.text("can't find member because there is no game"))
	);

	private static final DynamicCommandExceptionType ERROR_UNKNOWN_SEATED = new DynamicCommandExceptionType(name ->
		MessageComponentSerializer.message().serialize(Component.text("there is no member named "+ name+" here"))
	);

	private static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(
	MessageComponentSerializer.message().serialize(Component.text("no member found"))
	);

	@Override
	public List<Seated> parse(@NonNull StringReader reader) throws CommandSyntaxException {
		throw new UnsupportedOperationException("requiring a CommandSourceStack source");
	}

	@Override
	public @NonNull ArgumentType<String> getNativeType() {
		return StringArgumentType.string();
	}

	@Override
	public <S> List<Seated> parse(@NonNull StringReader reader, S source) throws CommandSyntaxException {
		if (!(source instanceof CommandSourceStack sourceStack))
			throw new UnsupportedOperationException("the source is not a CommandSourceStack");

		World world = sourceStack.getLocation().getWorld();
		BloodGame game = BloodGame.get(world);
		if (game == null) throw ERROR_NO_GAME.create();

		String input = reader.readUnquotedString();
		Stream<Seated> seatedStream = game.getAllSeated();

		if (Objects.equals(input, "*")) {
			List<Seated> foundSeated = seatedStream.toList();
			if (foundSeated.isEmpty()) throw ERROR_EMPTY.create();
			return foundSeated;// size of >=1
		}

		List<Seated> foundSeated = seatedStream.filter(seated1 -> Objects.equals(seated1.getName(), input)).toList();
		if (foundSeated.isEmpty()) throw ERROR_UNKNOWN_SEATED.create(input);
		if (foundSeated.size() > 1) throw new IllegalStateException("found multiples seated with the same name");
		return foundSeated;// size of 1
	}

	@Override
	public <S> @NonNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, @NonNull SuggestionsBuilder builder) {
		if (context.getSource() instanceof CommandSourceStack sourceStack) {
			World world = sourceStack.getLocation().getWorld();
			BloodGame game = BloodGame.get(world);
			if (game == null) return builder.buildFuture();
			game.getAllSeated().map(Seated::getName)
				.forEach(builder::suggest);
			builder.suggest("*");
		}
		return builder.buildFuture();
	}

	@SuppressWarnings("unchecked")
	public static List<Seated> getSeatedList(CommandContext<?> ctx, String name) {
		return ctx.getArgument(name, List.class);
	}
}