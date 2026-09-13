package io.github.aktomik.redclocktower.command.setup;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.aktomik.redclocktower.game.town.TownHall;
import io.github.aktomik.redclocktower.game.town.TownSetting;
import io.github.aktomik.redclocktower.game.town.TownSettings;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.utils.brigadier.CollectionArgument;
import io.github.aktomik.redclocktower.utils.brigadier.SuggestHelper;
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
		.suggests(valueSuggestions)
		.executes(settingChange)));
	}

	Command<CommandSourceStack> settingCheck = ctx -> {
		CommandSender sender = ctx.getSource().getSender();
		TownHall townHall = ctx.getArgument("town", TownHall.class);
		TownSetting<?> setting = ctx.getArgument("setting", TownSetting.class);

		sender.sendRichMessage("setting <key> is on <b><value></b>",
			Placeholder.parsed("key", setting.id()),
			Placeholder.parsed("value", setting.getFormatted(townHall))
		);
		return Command.SINGLE_SUCCESS;
	};

	SuggestionProvider<CommandSourceStack> valueSuggestions = (ctx, builder) -> {
		TownSetting<?> setting = ctx.getArgument("setting", TownSetting.class);
		return SuggestHelper.filtered(setting.suggestions(), builder);
	};

	Command<CommandSourceStack> settingChange = ctx -> {
		CommandSender sender = ctx.getSource().getSender();
		TownHall townHall = ctx.getArgument("town", TownHall.class);
		TownSetting<?> setting = ctx.getArgument("setting", TownSetting.class);
		final String input = ctx.getArgument("value", String.class);
		try {
			setting.setFromString(townHall, input);
		} catch (IllegalArgumentException e) {
			sender.sendRichMessage("<red>input <b><input></b> is wrong",
				Placeholder.parsed("input", input)
			);
			return 0;
		}

		sender.sendRichMessage("setting <key> set to <aqua><b><value></b>",
			Placeholder.parsed("key", setting.id()),
			Placeholder.parsed("value", setting.getFormatted(townHall))
		);
		return Command.SINGLE_SUCCESS;
	};
}
