package io.github.aktomik.redclocktower.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
		.executes(subOpen)
		.then(Commands.literal("open")
			.executes(subOpen)
		)
		.then(Commands.literal("rand")
			.executes(subRand)
		);
	}

	final Random rand = new Random();
	Book book;

	public void createBook() {
		Component bookTitle = Component.text("Menu");
		Component bookAuthor = Component.text("Server");

		List<String> happenings = List.of("ff", "hi", "this is a random message", "this is a longer random message", "your playing minecraft?", "cat >");
		String happening = happenings.get(rand.nextInt(happenings.size() - 1));

		Component page = Component.text("hello here a book menu!\n\n").color(NamedTextColor.BLACK).decorate(TextDecoration.BOLD)
		.append(Component.text(happening, NamedTextColor.DARK_PURPLE))
		.append(Component.text("\n\n"))
		.append(Component.text("reroll").clickEvent(ClickEvent.runCommand("/testtemp rand")))
		.append(Component.text("\n"))
		.append(Component.text("purrr"));

		List<Component> pages = (List.of(page));
		book = Book.book(bookTitle, bookAuthor, pages);
	}

	final Command<CommandSourceStack> subOpen = ctx -> {
		Entity executor = ctx.getSource().getExecutor();
		if (!(executor instanceof Player player)) return 0;

		createBook();
		player.openBook(book);
		return Command.SINGLE_SUCCESS;
	};

	final Command<CommandSourceStack> subRand = ctx -> {
		createBook();
		return Command.SINGLE_SUCCESS;
	};
}
