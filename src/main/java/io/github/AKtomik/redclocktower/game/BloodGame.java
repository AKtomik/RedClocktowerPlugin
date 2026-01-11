package io.github.AKtomik.redclocktower.game;

import io.github.AKtomik.redclocktower.DataKey;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginBase;

public class BloodGame {

	public static void SetGameState(World world, BloodGameState gameState)
	{
		PersistentDataContainer pdc = world.getPersistentDataContainer();
		pdc.set(DataKey.GAME_STATE.key, PersistentDataType.INTEGER, gameState.ordinal());
	}

	public static BloodGameState GetGameState(World world)
	{
		PersistentDataContainer pdc = world.getPersistentDataContainer();
		int ordinal = pdc.getOrDefault(DataKey.GAME_STATE.key, PersistentDataType.INTEGER, BloodGameState.NOTHING.ordinal());
		return BloodGameState.values()[ordinal];
	}
}
