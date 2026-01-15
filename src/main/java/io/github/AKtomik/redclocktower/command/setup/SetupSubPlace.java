package io.github.AKtomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.AKtomik.redclocktower.game.BloodGame;
import io.github.AKtomik.redclocktower.game.BloodGameAction;
import io.github.AKtomik.redclocktower.utils.SubBrigadierBase;
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
		.then(Commands.literal("center")
		.executes(PlaceCenterCheck)
		.then(Commands.argument("location", ArgumentTypes.blockPosition())
		.executes(PlaceCenterChange)
		));
	}


	Command<CommandSourceStack> PlaceCenterCheck = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final World world = ctx.getSource().getLocation().getWorld();
		final BloodGame game = BloodGame.get(world);

		// execution
		final Location loc = game.getLocationCenter();
		sender.sendRichMessage("location <b><locname></b> is at <x> <y> <z> <hover:show_text:\"Click to teleport\"><click:run_command:/tp @s <x> <y> <z>><green>[tp]",
		Placeholder.parsed("x", Double.toString(loc.getBlockX())),
		Placeholder.parsed("y", Double.toString(loc.getBlockY())),
		Placeholder.parsed("z", Double.toString(loc.getBlockZ())),
		Placeholder.parsed("locname", "center")
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> PlaceCenterChange = ctx -> {
		// arguments
		final CommandSender sender = ctx.getSource().getSender();
		final World world = ctx.getSource().getLocation().getWorld();
		final BloodGame game = BloodGame.get(world);
		final BlockPosition pos = ctx.getArgument("location", BlockPositionResolver.class).resolve(ctx.getSource());
		final Location loc = pos.toLocation(world);

		// execution
		game.setLocationCenter(loc);
		sender.sendRichMessage("set <b><locname></b> location at <x> <y> <z>",
		Placeholder.parsed("x", Double.toString(pos.x())),
		Placeholder.parsed("y", Double.toString(pos.y())),
		Placeholder.parsed("z", Double.toString(pos.z())),
		Placeholder.parsed("locname", "center")
		);
		return Command.SINGLE_SUCCESS;
	};
}