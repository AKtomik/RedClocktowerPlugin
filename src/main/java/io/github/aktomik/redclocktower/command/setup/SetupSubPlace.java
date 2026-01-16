package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.BloodGamePlace;
import io.github.aktomik.redclocktower.utils.brigadier.EnumArgument;
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

public class SetupSubPlace extends SubBrigadierBase {
	public String name() {
		return "place";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("place", EnumArgument.simple(BloodGamePlace.class, "invalid game place"))
			.executes(placeCenterCheck)
			.then(Commands.argument("position", ArgumentTypes.blockPosition())
				.executes(placeCenterChange)
		));
	}


	Command<CommandSourceStack> placeCenterCheck = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final World world = ctx.getSource().getLocation().getWorld();
		final BloodGame game = BloodGame.get(world);
		final BloodGamePlace place = ctx.getArgument("place", BloodGamePlace.class);

		// execution
		final Location loc = game.getPosition(place);
		sender.sendRichMessage("location <b><place></b> is at <x> <y> <z> <hover:show_text:\"Click to teleport\"><click:run_command:/tp @s <x> <y> <z>><green>[tp]",
		Placeholder.parsed("x", Double.toString(loc.getBlockX())),
		Placeholder.parsed("y", Double.toString(loc.getBlockY())),
		Placeholder.parsed("z", Double.toString(loc.getBlockZ())),
		Placeholder.parsed("place", place.toString())
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> placeCenterChange = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final World world = ctx.getSource().getLocation().getWorld();
		final BloodGame game = BloodGame.get(world);
		final BloodGamePlace place = ctx.getArgument("place", BloodGamePlace.class);
		final BlockPosition pos = ctx.getArgument("position", BlockPositionResolver.class).resolve(ctx.getSource());

		// execution
		pos.offset(0, 1, 0);
		final Location loc = pos.toLocation(world);
		game.setPosition(place, loc);
		sender.sendRichMessage("set <b><place></b> location at <x> <y> <z>",
		Placeholder.parsed("x", Double.toString(pos.x())),
		Placeholder.parsed("y", Double.toString(pos.y())),
		Placeholder.parsed("z", Double.toString(pos.z())),
		Placeholder.parsed("place", place.toString())
		);
		return Command.SINGLE_SUCCESS;
	};
}