package io.github.aktomik.redclocktower.game.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum BloodItems {

	BOOK_MEMBERS_LIST("book_members_list", Material.WRITTEN_BOOK,
		Component.text("Player List").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD)),
	BOOK_STORYTELLER("book_storyteller", Material.WRITTEN_BOOK,
		Component.text("Storyteller Toolbox").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	final String id;
	final ItemStack item;
	BloodItems(String id, Material material, Component name) {
		this.id = id;
		this.item = CustomItems.create(id, material, name);
	}

	public String id() {
		return id;
	}
	public ItemStack item() {
		return item;
	}
}
