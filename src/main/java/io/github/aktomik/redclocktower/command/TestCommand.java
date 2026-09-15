package io.github.aktomik.redclocktower.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Random;

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
			ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta meta = (BookMeta) book.getItemMeta();

			meta.title(Component.text("Menu"));
			meta.author(Component.text("Server"));

			List<String> happenings = List.of("ff", "hi", "this is a random message", "this is a longer random message", "your playing minecraft?", "cat >");
			String happening = happenings.get(new Random().nextInt(happenings.size() - 1));

			Component page = Component.text("hello here a book menu!\n\n").color(NamedTextColor.BLACK).decorate(TextDecoration.BOLD)
			.append(Component.text(happening, NamedTextColor.DARK_PURPLE))
			.append(Component.text("\n\n"))
			.append(Component.text("reroll").clickEvent(ClickEvent.runCommand("/testtemp")));

			meta.pages(List.of(page));
			book.setItemMeta(meta);

			player.openBook(book);
			return Command.SINGLE_SUCCESS;
		});
	}
}