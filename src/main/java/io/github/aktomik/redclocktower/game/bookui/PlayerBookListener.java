package io.github.aktomik.redclocktower.game.bookui;

import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.BloodPlayer;
import io.github.aktomik.redclocktower.game.Seated;
import io.github.aktomik.redclocktower.game.items.BloodItems;
import io.github.aktomik.redclocktower.game.items.CustomItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerBookListener implements Listener {
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
			ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta meta = (BookMeta)book.getItemMeta();

			meta.title(Component.text("Player List"));
			meta.author(Component.text("Server"));

			ArrayList<Component> seatedText = new ArrayList() { };
			for (Seated seated : game.getCircle().getAllSeated().toList())
			{
				String numberText = String.valueOf(seated.getSlot().getIndex() + 1);
				seatedText.add(Component.empty()
					.append(Component.text(numberText))
					.append(Component.text((numberText.length() > 1) ? " - " : "  - "))
					.append(Component.text(seated.getName()))
				);
			}

			Component page = Component.text("\n".repeat(5 - Math.floorDiv(seatedText.size(), 2)))
				.append(Component.join(JoinConfiguration.newlines(), seatedText));

			meta.pages(List.of(page));
			book.setItemMeta(meta);

			player.openBook(book);
		}
	}
}
