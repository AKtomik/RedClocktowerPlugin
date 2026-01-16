package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.DataKey;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BloodSlot {
	// class
	public final World world;
	final PersistentDataContainer pdc;
	private BloodSlot(World world, PersistentDataContainer pdc)
	{
		this.world = world;
		this.pdc = pdc;
	}
	public static BloodSlot get(World world, PersistentDataContainer pdc)
	{
		return new BloodSlot(world, pdc);
	}

	// get/set
	public void setPosition(BloodSlotPlace place, Location pos)
	{
		pdc.set(DataKey.SLOT_LOC.get(place).key, PersistentDataType.INTEGER_ARRAY, new int[] {pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()});
	}
	public Location getPosition(BloodSlotPlace place)
	{
		int[] posArray = pdc.get(DataKey.SLOT_LOC.get(place).key, PersistentDataType.INTEGER_ARRAY);
		if (posArray == null || posArray.length != 3) return null;
		return new Location(world, posArray[0], posArray[1], posArray[2]);
	}
}
