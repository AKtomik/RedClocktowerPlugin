package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.RedClocktower;
import io.github.aktomik.redclocktower.command.setup.TownChairPlace;
import io.github.aktomik.redclocktower.command.setup.TownHallPlace;
import io.github.aktomik.redclocktower.game.town.TownHall;
import io.github.aktomik.redclocktower.oldgame.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public class PlayerListener implements Listener {

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

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		TownHall townHall = bloodPlayer.getTownHallShortcut();
		if (townHall == null) return;
		event.setRespawnLocation(townHall.getPosition(TownHallPlace.SPAWN));
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		BloodPlayer bloodPlayer = BloodPlayer.get(player);
		TownHall townHall = bloodPlayer.getTownHallShortcut();
		if (townHall == null) return;
		if (townHall.getSettingsCanPlayerDrop()) return;
		player.sendActionBar(MiniMessage.miniMessage().deserialize("<red>nope!"));
		event.setCancelled(true);
	}
}

