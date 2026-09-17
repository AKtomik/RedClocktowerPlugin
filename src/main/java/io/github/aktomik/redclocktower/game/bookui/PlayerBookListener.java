package io.github.aktomik.redclocktower.game.bookui;

import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.BloodPlayer;
import io.github.aktomik.redclocktower.game.Seated;
import io.github.aktomik.redclocktower.game.items.BloodItems;
import io.github.aktomik.redclocktower.game.items.CustomItems;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerBookListener implements Listener {

	static final int BOOK_LINES_COUNT = 14;

	@EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
		if (!List.of(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR).contains(event.getAction())) return;
		if (CustomItems.getId(event.getItem()) == null) return;

		event.setCancelled(true);

		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		BloodGame game = bloodPlayer.getRelatedGame();
		if (game == null)
		{
			return;
		}

		if (CustomItems.is(event.getItem(), BloodItems.BOOK_MEMBERS_LIST.id()))
		{
			Component bookTitle = Component.text("Player List");
			Component bookAuthor = Component.text("Server");

			List<Seated> seatedList = game.getCircle().getAllSeated().toList();
			List<ArrayList<Component>> pagesLines = Collections.nCopies(Math.ceilDiv(seatedList.size(), BOOK_LINES_COUNT), new ArrayList<>() { });
			for (int i = 0; i <seatedList.size(); i++)
			{
				Seated seated = seatedList.get(i);
				String numberText = String.valueOf(seated.getSlot().getIndex() + 1);
				pagesLines.get(Math.floorDiv(i, BOOK_LINES_COUNT)).add(Component.empty()
					.append(Component.text(numberText))
					.append(Component.text((numberText.length() == 1) ? ".  - " : " - "))
					.append(Component.text(seated.getName()))
				);
			}

			List<Component> pages = pagesLines.stream().map(
					lines -> Component.text("\n".repeat(5 - Math.floorDiv(lines.size(), 2)))
					.append(Component.join(JoinConfiguration.newlines(), lines)
				)).collect(Collectors.toUnmodifiableList());

			Book book = Book.book(bookTitle, bookAuthor, pages);
			player.openBook(book);
		}
	}
}
