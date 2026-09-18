package io.github.aktomik.redclocktower.game;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.github.aktomik.redclocktower.game.town.*;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BellRingEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

	@EventHandler
	public void onPostRespawn(PlayerPostRespawnEvent event) {
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		if (bloodPlayer.getSeated() == null) return;
		bloodPlayer.refreshMemberEffects();
	}

	// blocks
	@EventHandler(ignoreCancelled = true)
	public void onLeverClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		if (block == null || block.getType() != Material.LEVER) return;
		if (event.getPlayer().isSneaking()) return;

		Location loc = block.getLocation();
		World world = loc.getWorld();
		BloodGame game = BloodGame.get(world);
		if (game == null) return;

		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);

		BloodSlot slotLever = null;
		for (BloodSlot slot : game.getCircle().getSlotsList()) {
			if (slot.getChair().getPosition(TownChairPlace.LEVER).equals(loc)) {
				slotLever = slot;
				break;
			}
		}
		if (slotLever == null) return;

		Seated seatedLever = slotLever.getSeated();
		if (
			!Boolean.TRUE.equals(TownSettings.CAN_PLAYER_PULL_EACHOTHER_LEVER.get(game.getTownHall()))
			&& seatedLever != bloodPlayer.getSeated() && bloodPlayer.getStorytellingGame() != game
		) {
			// cancel the lever and the vote
			event.setCancelled(true);
			return;
		}

		// get state
		Powerable powerable = (Powerable)block.getBlockData();
		boolean powered = !powerable.isPowered();

		// fake the event (to avoid redstone event)
		event.setCancelled(true);
		powerable.setPowered(powered);
		world.setBlockData(loc, powerable);
		world.playSound(loc, Sound.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.5f, powered ? 0.6f : 0f);

		// allow the lever, but don't change vote
		if (seatedLever == null || !seatedLever.canVote()) return;

		// change vote
		seatedLever.setVotePull(powered);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBellRing(BellRingEvent event) {
		if (!(event.getEntity() instanceof Player player)) return; // not a player

		Block block = event.getBlock();
		Location loc = block.getLocation();
		World world = loc.getWorld();
		BloodGame game = BloodGame.get(world);
		if (game == null) return;

		if (!block.getLocation().equals(game.getTownHall().getPosition(TownHallPlace.BELL))) return;

		if (BloodPlayer.get(player).getStorytellingGame() == game) {
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
		boolean allowed = townHall.getSetting(isSensitive ? TownSettings.CAN_PLAYER_DROP_INFO : TownSettings.CAN_PLAYER_DROP_MISC);
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

		if (Boolean.TRUE.equals(TownSettings.CAN_PLAYER_OPEN_CHEST.get(townHall))) return;

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

	// damage
	@EventHandler
	public void onHurt(EntityDamageEvent event) {
		if (event.getDamageSource().getCausingEntity() instanceof Player attacker) {
			BloodPlayer bloodPlayer = BloodPlayer.get(attacker);
			TownHall townHall = bloodPlayer.getSeatedTownHall();
			if (townHall != null) {
				boolean sameSide = (event.getEntity() instanceof Player victim) && Objects.equals(BloodPlayer.get(victim).getSeatedTownHall(), townHall);
				TownSetting<?> setting = sameSide ? TownSettings.ALLOW_PLAYER_HIT_PLAYER : TownSettings.ALLOW_PLAYER_HIT_OTHER;
				if (attacker.getGameMode() != GameMode.CREATIVE && !Boolean.TRUE.equals(setting.get(townHall))) {
					event.setCancelled(true);
				}
				return;
			}
		}
		if (event.getEntity() instanceof Player victim) {
			BloodPlayer bloodPlayer = BloodPlayer.get(victim);
			TownHall townHall = bloodPlayer.getSeatedTownHall();
			if (townHall != null && !Boolean.TRUE.equals(TownSettings.ALLOW_PLAYER_DAMAGED.get(townHall))) {
				event.setCancelled(true);
			}
		}
	}
	
	//@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	@EventHandler
	public void onChat(AsyncChatEvent event) {
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		SeatedPlayer seatedPlayer = bloodPlayer.getSeated();
		if (seatedPlayer == null) return;
		BloodGame game = seatedPlayer.getSlot().getGame();
		TownHall townHall = game.getTownHall();

		event.viewers().clear();
		event.viewers().addAll(game.getAllOnline().toList());
		event.renderer((source, displayName, message, viewer) -> {
			String townDisplayName = townHall.getDisplayName();
			NamedTextColor townDisplayColor = townHall.getSetting(TownSettings.TOWN_DISPLAY_COLOR);
			return Component.text()
			.append((
					Component.text("[")
					.append(Component.text(townDisplayName).color(townDisplayColor))
					.append(Component.text("]"))
				).color((TextColor.lerp(.5f, townDisplayColor, NamedTextColor.BLACK)))
			)
			.append(Component.text(" <"))
			.append(Component.text(bloodPlayer.getName()).color(seatedPlayer.getPrefixColor()))
			.append(Component.text("> "))
			.append(message)
			.build();
		}
		);
	}
}