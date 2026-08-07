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
		.then(Commands.literal("can_player_drop_misc")
			.executes(canPlayerDropMiscCheck)
			.then(Commands.argument("new value", BoolArgumentType.bool())
				.executes(canPlayerDropMiscChange)
			)
		)
		.then(Commands.literal("can_player_drop_info")
			.executes(canPlayerDropInfoCheck)
			.then(Commands.argument("new value", BoolArgumentType.bool())
				.executes(canPlayerDropInfoChange)
			)
		)
		.then(Commands.literal("can_player_open_chest")
			.executes(canPlayerOpenChestCheck)
			.then(Commands.argument("new value", BoolArgumentType.bool())
				.executes(canPlayerOpenChestChange)
			)
		);
	}

	Command<CommandSourceStack> canPlayerDropMiscCheck = ctx -> {
		CommandSender sender = ctx.getSource().getSender();
		TownHall townHall = ctx.getArgument("town", TownHall.class);

		final boolean value = townHall.getSettingsCanPlayerDropMisc();
		sender.sendRichMessage("player drop ability for misc items is <b><value></b>.",
			Placeholder.parsed("value", (value) ? "enabled" : "disabled")
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> canPlayerDropMiscChange = ctx -> {
		CommandSender sender = ctx.getSource().getSender();
		TownHall townHall = ctx.getArgument("town", TownHall.class);
		final boolean value = ctx.getArgument("new value", boolean.class);

		townHall.setSettingsCanPlayerDropMisc(value);
		sender.sendRichMessage("set player drop ability for misc items to <b><aqua><value></aqua></b>.",
		Placeholder.parsed("value", (value) ? "enabled" : "disabled")
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> canPlayerDropInfoCheck = ctx -> {
		CommandSender sender = ctx.getSource().getSender();
		TownHall townHall = ctx.getArgument("town", TownHall.class);

		final boolean value = townHall.getSettingsCanPlayerDropInfo();
		sender.sendRichMessage("player drop ability for info items is <b><value></b>.",
		Placeholder.parsed("value", (value) ? "enabled" : "disabled")
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> canPlayerDropInfoChange = ctx -> {
		CommandSender sender = ctx.getSource().getSender();
		TownHall townHall = ctx.getArgument("town", TownHall.class);
		final boolean value = ctx.getArgument("new value", boolean.class);

		townHall.setSettingsCanPlayerDropInfo(value);
		sender.sendRichMessage("set player drop ability for info items to <b><aqua><value></aqua></b>.",
		Placeholder.parsed("value", (value) ? "enabled" : "disabled")
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> canPlayerOpenChestCheck = ctx -> {
		CommandSender sender = ctx.getSource().getSender();
		TownHall townHall = ctx.getArgument("town", TownHall.class);

		final boolean value = townHall.getSettingsCanPlayerDropMisc();
		sender.sendRichMessage("player open chest ability is <b><value></b>.",
		Placeholder.parsed("value", (value) ? "enabled" : "disabled")
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> canPlayerOpenChestChange = ctx -> {
		CommandSender sender = ctx.getSource().getSender();
		TownHall townHall = ctx.getArgument("town", TownHall.class);
		final boolean value = ctx.getArgument("new value", boolean.class);

		townHall.setSettingsCanPlayerDropMisc(value);
		sender.sendRichMessage("set player open chest ability to <b><aqua><value></aqua></b>.",
		Placeholder.parsed("value", (value) ? "enabled" : "disabled")
		);
		return Command.SINGLE_SUCCESS;
	};
}
