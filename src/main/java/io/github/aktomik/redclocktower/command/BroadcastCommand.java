package io.github.aktomik.redclocktower.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class BroadcastCommand extends BrigadierCommand {

	// register
	public String name() {
		return "broadcast";
	}
	public List<String> aliases() {
		return List.of("richbroadcast");
	}
	public String permission() {
		return "redclocktower.storyteller";
	}
	public String description() { return "broadcast a message with mini rich"; }

	// root
	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("rich message", StringArgumentType.greedyString())
		.executes(
		ctx -> {
			Entity executor = ctx.getSource().getExecutor();
			if (executor == null) return Command.SINGLE_SUCCESS;
			String rawMessage = StringArgumentType.getString(ctx, "rich message");

			Bukkit.getServer().broadcast(MiniMessage.miniMessage().deserialize(rawMessage));
			for (Player player : Bukkit.getOnlinePlayers()) {
				Location loc = Objects.requireNonNull(player.getLocation());
				player.playSound(loc, Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.MASTER, .5f, 1f);
			}
			return Command.SINGLE_SUCCESS;
		}
		));
	}
}