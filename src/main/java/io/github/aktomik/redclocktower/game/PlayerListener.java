package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.game.town.TownChairPlace;
import io.github.aktomik.redclocktower.game.town.TownHallPlace;
import io.github.aktomik.redclocktower.game.town.TownHall;
import io.github.aktomik.redclocktower.oldgame.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

	// management
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.joinedSeatEffect();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		bloodPlayer.leavedSeatEffect();
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
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		TownHall townHall = bloodPlayer.getSeatedTownHall();
		if (townHall == null) return;
		if (townHall.getSettingsCanPlayerDrop()) return;
		player.sendActionBar(MiniMessage.miniMessage().deserialize("<red>nope!"));
		event.setCancelled(true);
	}
}

