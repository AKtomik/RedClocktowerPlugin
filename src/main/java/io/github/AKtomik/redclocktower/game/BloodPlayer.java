package io.github.AKtomik.redclocktower.game;

import io.github.AKtomik.redclocktower.DataKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BloodPlayer {

	// class
	final public Player player;
	private final PersistentDataContainer pdc;
	private BloodPlayer(Player player)
	{
		this.player = player;
		this.pdc = player.getPersistentDataContainer();
	}
	public static BloodPlayer Interface(Player player)
	{
		return new BloodPlayer(player);
	}

	// set & get
	private void setAlive(boolean isAlive)
	{
		pdc.set(DataKey.PLAYER_ALIVE.key, PersistentDataType.BOOLEAN, isAlive);
	}
	public boolean getAlive()
	{
		return pdc.getOrDefault(DataKey.PLAYER_ALIVE.key, PersistentDataType.BOOLEAN, true);
	}

	// code
	private void kill()
	{
		setAlive(false);
	}
	private void revive()
	{
		setAlive(true);
	}
}
