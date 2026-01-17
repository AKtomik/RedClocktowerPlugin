package io.github.aktomik.redclocktower.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.BloodPlayer;
import io.github.aktomik.redclocktower.utils.brigadier.CommandBrigadierBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class VoteCommand extends CommandBrigadierBase {

	// register
	public String name() {
		return "vote";
	}
	public List<String> aliases() {
		return List.of();
	}
	public String permission() {
		return "redclocktower.player";
	}
	public String description() {
		return "trigger your vote";
	}

	// root
	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.requires(ctx -> ctx.getExecutor() instanceof Player)
		.executes(ctx -> exe(ctx, null))
		.then(Commands.argument("trigger", BoolArgumentType.bool())
			.executes(ctx -> exe(ctx, ctx.getArgument("trigger", Boolean.class)))
		);
	}

	private int exe(CommandContext<CommandSourceStack> ctx, Boolean trigger) {
		final CommandSender sender = ctx.getSource().getSender();
		Player player = (Player)ctx.getSource().getExecutor();
		assert player != null;
		final BloodPlayer bloodPlayer = BloodPlayer.get(player);

		// checks
		final BloodGame game = bloodPlayer.getGame();
		if (game == null)
		{
			sender.sendRichMessage("<red>you are not in a blood game.");
			return Command.SINGLE_SUCCESS;
		}
		if (!game.isVoteMoment())
		{
			sender.sendRichMessage("<red>it's not the moment to vote.");
			return Command.SINGLE_SUCCESS;
		}
		if (!bloodPlayer.canVote())
		{
			sender.sendRichMessage("<red>you don't have any vote.");
			return Command.SINGLE_SUCCESS;
		}

		// actions
		if (trigger == null) trigger = !bloodPlayer.getVotePull();
		bloodPlayer.changeVotePull(trigger);
		sender.sendRichMessage((trigger)
		? "you are now <yellow><b>voting</b></yellow>."
		: "you are <red>not voting</red> anymore."
		);

		return Command.SINGLE_SUCCESS;
	};
}
