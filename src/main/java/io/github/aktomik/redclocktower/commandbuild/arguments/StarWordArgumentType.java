package io.github.aktomik.redclocktower.commandbuild.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;

import java.util.Collection;
import java.util.List;

public class StarWordArgumentType implements ArgumentType<String> {

	public static StarWordArgumentType starWord() {
		return new StarWordArgumentType();
	}

	@Override
	public String parse(StringReader reader) {
		int start = reader.getCursor();
		while (reader.canRead() && isAllowed(reader.peek())) {
			reader.skip();
		}
		return reader.getString().substring(start, reader.getCursor());
	}

	private static boolean isAllowed(char c) {
		return StringReader.isAllowedInUnquotedString(c) || c == '*';
	}

	@Override
	public Collection<String> getExamples() {
		return List.of("word", "words_with_underscores", "*", "word_with*star");
	}
}
