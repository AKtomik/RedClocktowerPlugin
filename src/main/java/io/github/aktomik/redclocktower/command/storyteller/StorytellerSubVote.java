package io.github.aktomik.redclocktower.command.storyteller;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.aktomik.redclocktower.commandbuild.arguments.SeatedArgumentType;
import io.github.aktomik.redclocktower.commandbuild.tools.CommandToolbox;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.Seated;
import io.github.aktomik.redclocktower.game.SlotCircle;
import io.github.aktomik.redclocktower.game.VoteStep;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierSub;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class StorytellerSubVote extends BrigadierSub {

	public String name() {
		return "vote";
	}

	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()

		.then(Commands.literal("nominate")
			.executes(this::nominateCheck)
			.then(Commands.argument("seated", new SeatedArgumentType())
				.executes(ctx -> nominateChange(ctx, SeatedArgumentType.getSeated(ctx, "seated")))
			)
		)

		.then(Commands.literal("pylori")
			.executes(this::pyloriCheck)
			.then(Commands.argument("seated", new SeatedArgumentType())
				.executes(ctx -> pyloriChange(ctx, SeatedArgumentType.getSeated(ctx, "seated")))
			)
		)

		.then(Commands.literal("start")
			.executes(ctx -> votingStart(ctx, null))
			.then(Commands.argument("seated", new SeatedArgumentType())
				.executes(ctx -> votingStart(ctx, SeatedArgumentType.getSeated(ctx, "seated")))
			)
		)

		.then(Commands.literal("execute")
			.executes(ctx -> executionStart(ctx, null))
				.then(Commands.argument("seated", new SeatedArgumentType())
					.executes(ctx -> executionStart(ctx, SeatedArgumentType.getSeated(ctx, "seated")))
			)
		)

		.then(Commands.literal("cancel")
			.executes(this::cancelProcess)
		)
		;
	}


	private int nominateCheck(CommandContext<CommandSourceStack> ctx) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		if (CommandToolbox.failIfNoGame(sender, game)) return 0;
		if (CommandToolbox.failIfNotStarted(sender, game)) return 0;

		Seated seated = game.getCircle().getNominated();
		if (seated == null)
			sender.sendRichMessage("there is no one actually nominated");
		else
			sender.sendRichMessage("<b><target></b> is actually nominated",
				Placeholder.parsed("target", seated.getName())
			);
		return Command.SINGLE_SUCCESS;
	}

	private int nominateChange(CommandContext<CommandSourceStack> ctx, Seated seated) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		if (CommandToolbox.failIfNoGame(sender, game)) return 0;
		if (CommandToolbox.failIfNotStarted(sender, game)) return 0;

		game.getCircle().setNominated(seated);
		sender.sendRichMessage("<b><target></b> is <gold><b>nominated</b></gold>.",
			Placeholder.parsed("target", seated.getName())
		);
		return Command.SINGLE_SUCCESS;
	}


	private int pyloriCheck(CommandContext<CommandSourceStack> ctx) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		if (CommandToolbox.failIfNoGame(sender, game)) return 0;
		if (CommandToolbox.failIfNotStarted(sender, game)) return 0;

		Seated seated = game.getCircle().getSentenced();
		if (seated == null)
			sender.sendRichMessage("there is no one on the pylori");
		else
			sender.sendRichMessage("<b><target></b> is on the pylori",
				Placeholder.parsed("target", seated.getName())
			);
		return Command.SINGLE_SUCCESS;
	}

	private int pyloriChange(CommandContext<CommandSourceStack> ctx, Seated seated) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		if (CommandToolbox.failIfNoGame(sender, game)) return 0;
		if (CommandToolbox.failIfNotStarted(sender, game)) return 0;

		game.getCircle().setSentenced(seated, game.getCircle().getVoteMajority());
		sender.sendRichMessage("<b><target></b> put on the <red><b>pylori</b></red>.",
			Placeholder.parsed("target", seated.getName())
		);
		return Command.SINGLE_SUCCESS;
	}


	private int votingStart(CommandContext<CommandSourceStack> ctx, Seated seated) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		if (CommandToolbox.failIfNoGame(sender, game)) return 0;
		if (CommandToolbox.failIfNotStarted(sender, game)) return 0;
		if (CommandToolbox.failIfVoteBusy(sender, game)) return 0;

		SlotCircle circle = game.getCircle();
		if (seated != null) circle.setNominated(seated);
		Seated nominated = circle.getNominated();

		if (nominated == null)
		{
			sender.sendRichMessage("<red>there is no nominated player");
			return 0;
		}

		// the action
		sender.sendRichMessage("<aqua>starting the vote for <b><target></b>",
			Placeholder.parsed("target", nominated.getName())
		);
		circle.startVoteProcess();
		return Command.SINGLE_SUCCESS;
	}

	private int executionStart(CommandContext<CommandSourceStack> ctx, Seated seated) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		if (CommandToolbox.failIfNoGame(sender, game)) return 0;
		if (CommandToolbox.failIfNotStarted(sender, game)) return 0;
		if (CommandToolbox.failIfVoteBusy(sender, game)) return 0;

		SlotCircle circle = game.getCircle();
		if (seated != null) circle.setNominated(seated);
		Seated sentenced = circle.getSentenced();

		if (sentenced == null)
		{
			sender.sendRichMessage("<red>there is no player on the pylori");
			return 0;
		}

		// the action
		sender.sendRichMessage("<aqua>starting the execution of <b><target></b>",
			Placeholder.parsed("target", sentenced.getName())
		);
		circle.startExecuteProcess(false);
		return Command.SINGLE_SUCCESS;
	}


	private int cancelProcess(CommandContext<CommandSourceStack> ctx) {
		final CommandSender sender = ctx.getSource().getSender();
		final BloodGame game = BloodGame.get(ctx.getSource().getLocation().getWorld());

		if (CommandToolbox.failIfNoGame(sender, game)) return 0;
		if (CommandToolbox.failIfNotStarted(sender, game)) return 0;
		if (CommandToolbox.failIfVoteBusy(sender, game)) return 0;

		SlotCircle circle = game.getCircle();

		switch (circle.getVoteStep()) {
			case VoteStep.NOTHING:
			{
				if (circle.getNominated() != null)
				{
					circle.removeNominated();
					sender.sendRichMessage("<aqua>the nomination was <red>canceled</red>.");
				} else if (circle.getSentenced() != null) {
					circle.removeSentenced();
					sender.sendRichMessage("<aqua>the pylori was <red>cleared</red>.");
				} else {
					// changeExclusionMode(false);
					circle.unlockAll();
					sender.sendRichMessage("<aqua>reseting votes pistons.<white> there is nothing else to cancel.");
				}
			} break;

			case VoteStep.VOTE_PROCESS:
			{
				circle.removeNominated();
				// changeExclusionMode(false);
				circle.unlockAll();
				circle.setVoteStep(VoteStep.CANCEL);
				sender.sendRichMessage("<aqua><red>canceling</red> the vote...");
			} break;

			case VoteStep.EXECUTION_PROCESS:
			{
				circle.setVoteStep(VoteStep.CANCEL);
				sender.sendRichMessage("<aqua><red>canceling</red> the execution...");
			} break;

			case VoteStep.CANCEL:
			{
				circle.setVoteStep(VoteStep.NOTHING);
				sender.sendRichMessage("<aqua><red>force</red> the cancel");
			} break;
		}

		return Command.SINGLE_SUCCESS;
	}
}