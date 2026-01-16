package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.utils.brigadier.SubBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class StorytellerSubSettings extends SubBrigadierBase {

	public String name() { return "settings"; }

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.then(Commands.literal("solt_limit")
			.executes(subSlotLimitCheck)
			.then(Commands.argument("new value", IntegerArgumentType.integer(0, 20))
				.executes(subSlotLimitChange)
		));
	}

	Command<CommandSourceStack> subSlotLimitCheck = ctx -> {
		// arguments
		CommandSender sender = ctx.getSource().getSender();
		BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		final int value = game.getSettingsSlotLimit();
		sender.sendRichMessage("slot limit is at <b><value></b>.",
			Placeholder.parsed("value", Integer.toString(value))
		);
		return Command.SINGLE_SUCCESS;
	};

	Command<CommandSourceStack> subSlotLimitChange = ctx -> {
		// arguments
		CommandSender sender = ctx.getSource().getSender();
		BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());
		final int newValue = ctx.getArgument("new value", int.class);

		game.changeSlotLimit(newValue);
		sender.sendRichMessage("set slot limit to <b><aqua><value></aqua></b>.",
			Placeholder.parsed("value", Integer.toString(newValue))
		);
		return Command.SINGLE_SUCCESS;
	};
}
