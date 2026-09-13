package io.github.aktomik.redclocktower.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierCommand;
import io.github.aktomik.redclocktower.utils.brigadier.CollectionArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Objects;

public class TestCommand extends BrigadierCommand {

	// register
	public String name() {
		return "temptest";
	}
	public List<String> aliases() {
		return List.of("testtemp");
	}
	public String permission() {
		return "redclocktower.storyteller";
	}
	public String description() { return "dev test command"; }

	// root
	public LiteralArgumentBuilder<CommandSourceStack> root() {
		return base()
		.executes(
		ctx -> {
			Entity executor = ctx.getSource().getExecutor();
			if (!(executor instanceof Player player)) return 0;

			Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
			String teamId = ("testcmd");
			Team oldTeam = board.getTeam(teamId);
			if (oldTeam != null) oldTeam.unregister();
			Team team = board.registerNewTeam(teamId);
			team.color(NamedTextColor.LIGHT_PURPLE);
			team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
			team.setCanSeeFriendlyInvisibles(true);
			team.addPlayer(player);
			return Command.SINGLE_SUCCESS;
		});
	}
}