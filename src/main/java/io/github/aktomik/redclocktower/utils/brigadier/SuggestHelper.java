package io.github.aktomik.redclocktower.utils.brigadier;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public final class SuggestHelper {
	public static CompletableFuture<Suggestions> filtered(Collection<String> candidates, SuggestionsBuilder builder) {
		String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
		candidates.stream()
		.filter(c -> c.toLowerCase(Locale.ROOT).startsWith(remaining))
		.forEach(builder::suggest);
		return builder.buildFuture();
	}
	private SuggestHelper() {}
}
