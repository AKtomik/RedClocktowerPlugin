package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.aktomik.redclocktower.commandbuild.arguments.SeatedArgumentType;
import io.github.aktomik.redclocktower.commandbuild.tools.CommandToolbox;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.Seated;
import io.github.aktomik.redclocktower.game.SlotCircle;
import io.github.aktomik.redclocktower.oldgame.OldBloodGame;
import io.github.aktomik.redclocktower.oldgame.OldGameToolbox;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierToolbox;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StorytellerSubVote extends BrigadierSub {

	public String name() {
		return "vote";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()

		.then(Commands.literal("start")
			.executes(ctx -> votingStart(ctx, null))
			.then(Commands.argument("seated", new SeatedArgumentType())
				.executes(ctx -> votingStart(ctx, SeatedArgumentType.getSeated(ctx, "seated")))
			)
		)
		;
	}


	private int votingStart(CommandContext<CommandSourceStack> ctx, Seated seated) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		if (CommandToolbox.failIfNoGame(sender, game)) return 0;
		if (CommandToolbox.failIfNotStarted(sender, game)) return 0;

		SlotCircle circle = game.getCircle();
//		if (player != null)  nominateChange(ctx, player);
		if (seated != null) circle.setNominated(seated);
		Seated nominated = circle.getNominated();

		if (nominated == null)
		{
			sender.sendRichMessage("<red>there is no nominated player");
			return 0;
		}

		// the action
		circle.startVoteProcess();
		sender.sendRichMessage("<aqua>starting the vote for <b><target></b>",
			Placeholder.parsed("target", nominated.getName())
		);
		return Command.SINGLE_SUCCESS;
	}

}