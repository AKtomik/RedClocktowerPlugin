package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.town.TownHall;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class SetupSubModifySettings extends BrigadierSub {

	public String name() { return "settings"; }

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.literal("can_player_drop")
			.executes(canPlayerDropCheck)
			.then(Commands.argument("new value", BoolArgumentType.bool())
				.executes(canPlayerDropChange)
		));
	}

	Command<CommandSourceStack> canPlayerDropCheck = ctx -> {
		CommandSender sender = ctx.getSource().getSender();
		TownHall townHall = ctx.getArgument("town", TownHall.class);

		final boolean value = townHall.getSettingsCanPlayerDrop();
		sender.sendRichMessage("player drop ability is <b><value></b>.",
			Placeholder.parsed("value", (value) ? "enabled" : "disabled")
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> canPlayerDropChange = ctx -> {
		CommandSender sender = ctx.getSource().getSender();
		TownHall townHall = ctx.getArgument("town", TownHall.class);
		final boolean value = ctx.getArgument("new value", boolean.class);

		townHall.setSettingsCanPlayerDrop(value);
		sender.sendRichMessage("set player drop ability to <b><aqua><value></aqua></b>.",
		Placeholder.parsed("value", (value) ? "enabled" : "disabled")
		);
		return Command.SINGLE_SUCCESS;
	};
}
