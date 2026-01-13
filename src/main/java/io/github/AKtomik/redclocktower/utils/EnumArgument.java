package io.github.AKtomik.redclocktower.utils;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;

@NullMarked
public final class EnumArgument<E extends Enum<E>>
implements CustomArgumentType.Converted<E, String> {

	private final Class<E> enumClass;
	private final Function<E, String> nameMapper;
	private final SimpleCommandExceptionType invalidValueError;

	public EnumArgument(
	Class<E> enumClass,
	Function<E, String> nameMapper,
	String errorMessage
	) {
		this.enumClass = enumClass;
		this.nameMapper = nameMapper;
		this.invalidValueError = new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(text(errorMessage)));
	}

	public EnumArgument(
	Class<E> enumClass,
	Function<E, String> nameMapper
	) {
		this(enumClass, nameMapper, "Invalid value");
	}

	@Override
	public E convert(String nativeType) throws CommandSyntaxException {
		try {
			return Enum.valueOf(
			enumClass,
			nativeType.toUpperCase(Locale.ROOT)
			);
		} catch (IllegalArgumentException ex) {
			throw invalidValueError.createWithContext(new StringReader(nativeType));
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(
	CommandContext<S> context,
	SuggestionsBuilder builder
	) {
		String remaining = builder.getRemainingLowerCase();

		for (E value : enumClass.getEnumConstants()) {
			String name = nameMapper.apply(value);

			if (name.startsWith(remaining)) {
				builder.suggest(name);
			}
		}

		return builder.buildFuture();
	}

	@Override
	public ArgumentType<String> getNativeType() {
		return StringArgumentType.word();
	}

	public static <E extends Enum<E>> EnumArgument<E> simple(
	Class<E> enumClass
	) {
		return new EnumArgument<>(
		enumClass,
		e -> e.name().toLowerCase(Locale.ROOT)
		);
	}

	public static <E extends Enum<E>> EnumArgument<E> simple(
	Class<E> enumClass,
	String errorMessage
	) {
		return new EnumArgument<>(
		enumClass,
		e -> e.name().toLowerCase(Locale.ROOT),
		errorMessage
		);
	}
}