package io.github.AKtomik.redclocktower.game;

import io.github.AKtomik.redclocktower.DataKey;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginBase;

public class BloodGame {

	// class
	final public World world;
	private final PersistentDataContainer pdc;
	private BloodGame(World world)
	{
		this.world = world;
		this.pdc = world.getPersistentDataContainer();
	}
	public static BloodGame WorldGame(World world)
	{
		return new BloodGame(world);
	}

	// get/set
	public void setGameState(BloodGameState gameState)
	{
		pdc.set(DataKey.GAME_STATE.key, PersistentDataType.INTEGER, gameState.ordinal());
	}

	public BloodGameState getGameState()
	{
		int ordinal = pdc.getOrDefault(DataKey.GAME_STATE.key, PersistentDataType.INTEGER, BloodGameState.NOTHING.ordinal());
		return BloodGameState.values()[ordinal];
	}

	// if
	public boolean isPlaying()
	{
		return getGameState() == BloodGameState.INGAME;
	}
}