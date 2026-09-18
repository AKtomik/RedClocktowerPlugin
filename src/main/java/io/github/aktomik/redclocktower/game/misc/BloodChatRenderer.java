package io.github.aktomik.redclocktower.game.misc;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;


public enum BloodChatRenderer implements ChatRenderer {

	MUGGLE {// outside a blood game
		@Override
		public @NonNull Component render(
		@NonNull Player source,
		@NonNull Component displayName,
		@NonNull Component message,
		@NonNull Audience viewer
		) {
			return Component.empty();
		}
	},

	IN_GAME {// inside a blood game
		@Override
		public @NonNull Component render(
		@NonNull Player source,
		@NonNull Component displayName,
		@NonNull Component message,
		@NonNull Audience viewer
		) {
			return Component.empty();
		}
	}
}
