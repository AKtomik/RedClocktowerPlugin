package io.github.aktomik.redclocktower.game.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum BloodItems {

	BOOK_MEMBERS_LIST("book_members_list",
		createItem(Component.text("Player List").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
	),
	BOOK_STORYTELLER("book_storyteller",
		createItem(Component.text("Storyteller Toolbox").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
	);

	private static ItemStack createItem(ItemStack item, Component name) {
		item.editMeta(m -> m.displayName(name));
		return item;
	}
	private static ItemStack createItem(Material material, Component name) {
		return createItem(new ItemStack(material), name);
	}
	private static ItemStack createItem(Component name) {
		return createItem(Material.WRITTEN_BOOK, name);
	}


	final String id;
	final ItemStack item;
	BloodItems(String id, ItemStack item) {
		this.id = id;
		this.item = item;
	}

	public String id() {
		return id;
	}
	public ItemStack item() {
		return item;
	}
}
