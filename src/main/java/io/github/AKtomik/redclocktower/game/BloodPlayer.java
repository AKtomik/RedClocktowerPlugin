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
	public static BloodPlayer get(Player player)
	{
		return new BloodPlayer(player);
	}

	// set & get
	private void setAlive(boolean isAlive)
	{
		pdc.set(DataKey.PLAYER_ALIVE.key, PersistentDataType.BOOLEAN, isAlive);
		refreshAlive();
	}
	public boolean getAlive()
	{
		return pdc.getOrDefault(DataKey.PLAYER_ALIVE.key, PersistentDataType.BOOLEAN, true);
	}

	// refresh
	private void refreshAlive()
	{
		if (getAlive())
		{

		} else {

		}
	}

	// game link
	void joinGame(BloodGame game)
	{
	}

	void quitGame(BloodGame game)
	{
	}

	// if
	public boolean IsAlive()
	{
		return getAlive();
	}

	// code
	private void Kill()
	{
		setAlive(false);
	}
	private void Revive()
	{
		setAlive(true);
	}
}
