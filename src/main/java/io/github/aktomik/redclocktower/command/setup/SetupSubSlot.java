package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.utils.brigadier.SubBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class SetupSubSlot extends SubBrigadierBase {
	public String name() {
		return "slot";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.literal("add_new").executes(subAdd))
		.then(Commands.literal("remove_last").executes(subRemove))
		.then(Commands.literal("edit")
			.then(Commands.argument("slot number", ArgumentTypes.integerRange())
				.then(Commands.literal("position")
					.then(Commands.argument("position", StringArgumentType.word())
					.executes(subEditPosition))
		)));
	}


	Command<CommandSourceStack> subRemove = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subAdd = ctx -> {
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subEditPosition = ctx -> {
		return Command.SINGLE_SUCCESS;
	};
}
