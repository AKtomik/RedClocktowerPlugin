package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.game.town.TownChairPlace;
import io.github.aktomik.redclocktower.game.town.TownHall;
import io.github.aktomik.redclocktower.game.town.TownHallPlace;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BellRingEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

public class GameListener implements Listener {

	private static void cancelAndNotify(Player player, Cancellable event) {
		player.sendActionBar(MiniMessage.miniMessage().deserialize("<red>nope!"));
		event.setCancelled(true);
	}

	// management
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.onServerJoined();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.onServerLeaved();
	}

	// spawn
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		TownHall townHall = bloodPlayer.getSeatedTownHall();
		if (townHall == null) return;
		event.setRespawnLocation(townHall.getPosition(TownHallPlace.SPAWN));
	}

	// blocks
	@EventHandler(ignoreCancelled = true)
	public void onLeverClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		if (block == null || block.getType() != Material.LEVER) return;

		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		SeatedPlayer seatedPlayer = bloodPlayer.getSeated();
		if (seatedPlayer == null) return;// not in game

		BloodSlot playerSlot = seatedPlayer.getSlot();
		BloodGame game = playerSlot.getGame();
		SlotCircle circle = game.getCircle();
		Location loc = block.getLocation();

		boolean isGameLever = false;
		boolean isOwnLever = false;
		for (BloodSlot slot : circle.getSlotsStream().toList()) {
			if (slot.getChair().getPosition(TownChairPlace.LEVER).equals(loc)) {
				isGameLever = true;
				isOwnLever = (slot.getSeated() == seatedPlayer);
				break;
			}
		}

		if (!isGameLever) return;
		if (!isOwnLever && !game.getTownHall().getSettingsCanPlayerPullOthersLever()) {
			// cancel the lever and the vote
			event.setCancelled(true);
			return;
		}

		// get state
		BlockData data = block.getBlockData();
		boolean powered = !((Powerable)data).isPowered();

		// allow the lever, but cancel the vote
		if (powered && !seatedPlayer.canVote()) return;

		// change vote
		seatedPlayer.setVotePull(powered);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBellRing(BellRingEvent event) {
		if (!(event.getEntity() instanceof Player player)) return; // not a player

		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		BloodGame game = bloodPlayer.getRelatedGame();
		if (game == null) return;

		Block bellBlock = event.getBlock();
		if (!bellBlock.getLocation().equals(game.getTownHall().getPosition(TownHallPlace.BELL))) return;

		if (bloodPlayer.getStorytellingGame() == game) {
			GameAction.next.accept(game, player);
		} else {
			event.setCancelled(true);
		}
	}

	// items
	private static boolean isItemTransferBlocked(Player player, ItemStack item) {
		if (item == null || item.getType() == Material.AIR) return false;

		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		TownHall townHall = bloodPlayer.getSeatedTownHall();
		if (townHall == null) return false;

		boolean isSensitive = BloodGame.SENSITIVE_INFO_ITEM.contains(item.getType());
		boolean allowed = isSensitive ? townHall.getSettingsCanPlayerDropInfo() : townHall.getSettingsCanPlayerDropMisc();
		return !allowed;
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if (isItemTransferBlocked(event.getPlayer(), event.getItemDrop().getItemStack()))
			cancelAndNotify(event.getPlayer(), event);
	}

	private static @Nullable ItemStack getMovedItem(InventoryClickEvent event) {
		Inventory topInv = event.getView().getTopInventory();
		ItemStack movedItem = null;

		if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
		&& event.getClickedInventory() != null
		&& !event.getClickedInventory().equals(topInv))
		{
			// shift-click from player inv into the chest
			movedItem = event.getCurrentItem();
		}
		else {
			if (event.getClickedInventory() != null
			&& event.getClickedInventory().equals(topInv)) {
				event.getCursor();
				if (event.getCursor().getType() != Material.AIR) {
					// manual place: cursor holds an item, clicking a slot in the top (chest) inventory
					movedItem = event.getCursor();
				}
			}
		}
		return movedItem;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if (!(event.getWhoClicked() instanceof Player player)) return;
		ItemStack item = getMovedItem(event);

		if (isItemTransferBlocked(player, item))
			cancelAndNotify(player, event);
	}
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event)
	{
		if (!(event.getWhoClicked() instanceof Player player)) return;

		Inventory topInv = event.getView().getTopInventory();
		boolean touchesTop = event.getRawSlots().stream()
		.anyMatch(rawSlot -> rawSlot < topInv.getSize());
		if (!touchesTop) return;

		ItemStack item = event.getOldCursor();
		if (isItemTransferBlocked(player, item))
			cancelAndNotify(player, event);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (!(event.getPlayer() instanceof Player player)) return;
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		TownHall townHall = bloodPlayer.getSeatedTownHall();
		if (townHall == null) return;

		if (townHall.getSettingsCanPlayerOpenChest()) return;

		switch (event.getInventory().getType()) {
			case CHEST,
				 BARREL,
				 SHULKER_BOX,
				 ENDER_CHEST,
				 HOPPER,
				 FURNACE,
				 BLAST_FURNACE,
				 SMOKER,
				 BREWING:
				cancelAndNotify(player, event);
				break;
			default:
				break;
		}
	}

	// world
	public void OnWorldUnload(WorldUnloadEvent event) {
		BloodGame game = BloodGame.get(event.getWorld());
		if (game == null) return;
		game.kill();
	}
}

