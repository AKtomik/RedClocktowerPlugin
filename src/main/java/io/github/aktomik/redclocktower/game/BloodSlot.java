package io.github.aktomik.redclocktower.game;

import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

public class BloodSlot {
	// class
	final PersistentDataContainer pdc;
	private BloodSlot(PersistentDataContainer pdc)
	{
		this.pdc = pdc;
	}
	public static BloodSlot get(PersistentDataContainer pdc)
	{
		return new BloodSlot(pdc);
	}
}
