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

	private static final String SELECTOR_ALL = "*";// "\"*\""

	@Override
	public @NonNull ArgumentType<String> getNativeType() {
		// this is the only way of having [*] accepted
		// here greedyString is weirdly not eat it all when valid
		// so that cool for us
		return StringArgumentType.greedyString();
		// the other solution is to use StringArgumentType.string() and then ["*"]
	}

	@Override
	public List<Seated> parse(@NonNull StringReader reader) throws CommandSyntaxException {
		throw new UnsupportedOperationException("requiring a CommandSourceStack source");
	}

	@Override
	public <S> List<Seated> parse(@NonNull StringReader reader, S source) throws CommandSyntaxException {
		if (!(source instanceof CommandSourceStack sourceStack))
			throw new UnsupportedOperationException("the source is not a CommandSourceStack");

		World world = sourceStack.getLocation().getWorld();
		BloodGame game = BloodGame.get(world);
		if (game == null) throw ERROR_NO_GAME.create();

		String input = readWord(reader);
		Stream<Seated> seatedStream = game.getAllSeated();

		if (Objects.equals(input, SELECTOR_ALL)) {
			List<Seated> foundSeated = seatedStream.toList();
			if (foundSeated.isEmpty()) throw ERROR_EMPTY.create();
			return foundSeated;// size of >=1
		}

		List<Seated> foundSeated = seatedStream.filter(seated1 -> Objects.equals(seated1.getName(), input)).toList();
		if (foundSeated.isEmpty()) throw ERROR_UNKNOWN_SEATED.create(input);
		if (foundSeated.size() > 1) throw new IllegalStateException("found multiples seated with the same name");
		return foundSeated;// size of 1
	}

	private static String readWord(StringReader reader) {
		int start = reader.getCursor();
		while (reader.canRead() && (StringReader.isAllowedInUnquotedString(reader.peek()) || reader.peek() == '*')) {
			reader.skip();
		}
		return reader.getString().substring(start, reader.getCursor());
	}

	@Override
	public <S> @NonNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, @NonNull SuggestionsBuilder builder) {
		if (context.getSource() instanceof CommandSourceStack sourceStack) {
			World world = sourceStack.getLocation().getWorld();
			BloodGame game = BloodGame.get(world);
			if (game == null) return builder.buildFuture();
			Stream.concat(game.getAllSeated().map(Seated::getName), Stream.of(SELECTOR_ALL))
			.filter(str -> str.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
			.forEach(builder::suggest);
		}
		return builder.buildFuture();
	}

	@SuppressWarnings("unchecked")
	public static List<Seated> getSeatedList(CommandContext<?> ctx, String name) {
		return ctx.getArgument(name, List.class);
	}
}