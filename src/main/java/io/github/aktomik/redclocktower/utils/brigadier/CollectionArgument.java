package io.github.aktomik.redclocktower.utils.brigadier;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;


public final class CollectionArgument<T>
implements CustomArgumentType.Converted<T, String> {

	private static final DynamicCommandExceptionType INVALID_VALUE =
	new DynamicCommandExceptionType(name -> new LiteralMessage("unknown value: '" + name + "'"));

	private final Set<String> values;
	private final Function<String, T> mapper;

	private CollectionArgument(Collection<String> values, Function<String, T> mapper) {
		this.values = new HashSet<>(values);
		this.mapper = mapper;
	}

	@Override
	public @NonNull T parse(StringReader reader) throws CommandSyntaxException {
		String input = reader.readUnquotedString();
		if (!values.contains(input)) {
			throw INVALID_VALUE.createWithContext(reader, input);
		}
		return mapper.apply(input);
	}
	@Override
	public @NonNull T convert(@NonNull String input) throws CommandSyntaxException {
		if (!values.contains(input)) {
			throw INVALID_VALUE.create(input);
		}
		return mapper.apply(input);
	}

	@Override
	public @NonNull ArgumentType<String> getNativeType() {
		return StringArgumentType.word();
	}

	@Override
	public <S> @NonNull CompletableFuture<Suggestions> listSuggestions(@NonNull CommandContext<S> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemaining().toLowerCase();
		for (String value : values) {
			if (value.toLowerCase().startsWith(remaining)) {
				builder.suggest(value);
			}
		}
		return builder.buildFuture();
	}

	public static <T> CollectionArgument<T> of(Collection<String> values, Function<String, T> mapper) {
		return new CollectionArgument<>(values, mapper);
	}
}