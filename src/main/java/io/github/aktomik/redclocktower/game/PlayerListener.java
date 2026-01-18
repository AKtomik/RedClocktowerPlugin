package io.github.aktomik.redclocktower.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
		boolean isOwnLever = Objects.equals(loc,slot.getPosition(BloodSlotPlace.LEVER));

		if (!isGameLever) return;
		if (!isOwnLever) {
			// stop redstone
			event.setCancelled(true);
			return;
		}
		if (bloodPlayer.isSlotVoteLock()) {
			// stop redstone
			event.setCancelled(true);
			return;
		}
		if (!bloodPlayer.hasVote()) return;

		// get state
		BlockData data = block.getBlockData();
		boolean powered = !((Powerable)data).isPowered();

		bloodPlayer.changeVotePull(powered);
	}
}

