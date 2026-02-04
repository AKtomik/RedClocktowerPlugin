package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.RedClocktower;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BellRingEvent;
import org.bukkit.event.player.*;

import java.util.Objects;

public class PlayerListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.refreshNameTag();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.disconnect();
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		BloodGame game = bloodPlayer.getGame();
		if (game == null) return;

		event.setRespawnLocation(game.getPosition(GamePlace.SPAWN));
		Bukkit.getScheduler().runTask(RedClocktower.plugin(), () -> {
			if (!bloodPlayer.isAlive()) bloodPlayer.changeAlive(false);
		});
	}

	@EventHandler(ignoreCancelled = true)
	public void onLeverClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		if (block == null || block.getType() != Material.LEVER) return;

		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		BloodGame game = bloodPlayer.getGame();
		if (game == null) return;
		BloodSlot slot = game.getSlot(bloodPlayer.getSlotIndex());
		Location loc = block.getLocation();
		boolean isGameLever = game.isLevelInSlots(loc);
		boolean isOwnLever = Objects.equals(loc,slot.getPosition(SlotPlace.LEVER));

		if (!isGameLever) return;
		if (!isOwnLever) {
			// stop redstone
			event.setCancelled(true);
			return;
		}

		if (bloodPlayer.isSlotVoteLock()) return;
		if (!bloodPlayer.hasVote()) return;

		// get state
		BlockData data = block.getBlockData();
		boolean powered = !((Powerable)data).isPowered();

		bloodPlayer.changeVotePull(powered);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBellRing(BellRingEvent event) {
		if (!(event.getEntity() instanceof Player player)) {
			return; // not a player
		}

		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		BloodGame bloodGame = bloodPlayer.getGame();
		if (bloodGame == null) return;

		Block bell = event.getBlock();
		if (bell.getLocation().distance(bloodGame.getPosition(GamePlace.BELL)) > 1) return;

		// your condition
		if (Objects.equals(bloodGame.getStorytellerUuid(), player.getUniqueId())) {
			GameAction.next.accept(bloodGame, player);
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();

		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		BloodGame bloodGame = bloodPlayer.getGame();
		if (bloodGame == null) return;
		if (bloodPlayer.isStoryteller()) return;

		event.setCancelled(true);
	}
}

