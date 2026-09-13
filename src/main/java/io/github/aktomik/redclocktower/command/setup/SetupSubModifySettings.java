package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.town.TownHall;
import io.github.aktomik.redclocktower.game.town.TownSetting;
import io.github.aktomik.redclocktower.game.town.TownSettings;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.utils.brigadier.CollectionArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class SetupSubModifySettings extends BrigadierSub {

	public String name() { return "settings"; }

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.argument("setting", CollectionArgument.of(TownSettings.map().keySet(), str -> TownSettings.map().get(str)))
		.executes(settingCheck)
		.then(Commands.argument("value", StringArgumentType.word())
		.executes(settingChange)));
	}

	Command<CommandSourceStack> settingCheck = ctx -> {
		CommandSender sender = ctx.getSource().getSender();
		TownHall townHall = ctx.getArgument("town", TownHall.class);
		TownSetting<?> setting = ctx.getArgument("setting", TownSetting.class);

		sender.sendRichMessage("setting <u><key></u> is on <b><value></b>",
			Placeholder.parsed("key", setting.id()),
			Placeholder.parsed("value", setting.getFormatted(townHall))
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> settingChange = ctx -> {
		CommandSender sender = ctx.getSource().getSender();
		TownHall townHall = ctx.getArgument("town", TownHall.class);
		TownSetting<?> setting = ctx.getArgument("setting", TownSetting.class);
		final String input = ctx.getArgument("new value", String.class);
		setting.setFromString(townHall, input);

		sender.sendRichMessage("setting <u><key></u> set to <aqua><b><value></b>",
			Placeholder.parsed("key", setting.id()),
			Placeholder.parsed("value", setting.getFormatted(townHall))
		);
		return Command.SINGLE_SUCCESS;
	};
}
