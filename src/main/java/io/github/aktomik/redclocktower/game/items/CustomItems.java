package io.github.aktomik.redclocktower.game.items;

import io.github.aktomik.redclocktower.RedClocktower;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class CustomItems {

	private static final NamespacedKey ITEM_ID = new NamespacedKey(RedClocktower.plugin(), "item_id");

	public static ItemStack create(String id, Material material, Component name) {
		ItemStack item = new ItemStack(material);

		item.editMeta(meta -> {
			meta.itemName(name);
			meta.getPersistentDataContainer().set(
			ITEM_ID,
			PersistentDataType.STRING,
			id
			);
		});

		return item;
	}

	public static String getId(ItemStack item) {
		if (item == null || !item.hasItemMeta()) {
			return null;
		}

		return item.getItemMeta()
		.getPersistentDataContainer()
		.get(ITEM_ID, PersistentDataType.STRING);
	}

	public static boolean is(ItemStack item, String id) {
		return id.equals(getId(item));
	}
}