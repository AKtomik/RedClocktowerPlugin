package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.*;
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

	// get & set
	public void setPosition(BloodSlotPlace place, Location pos)
	{
		pdc.set(DataKey.SLOT_LOC.get(place).key(), PersistentDataType.INTEGER_ARRAY, new int[] {pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()});
	}
	public Location getPosition(BloodSlotPlace place)
	{
		int[] posArray = pdc.get(DataKey.SLOT_LOC.get(place).key(), PersistentDataType.INTEGER_ARRAY);
		if (posArray == null || posArray.length != 3) return null;
		return new Location(world, posArray[0], posArray[1], posArray[2]);
	}

	private void setLock(boolean isLocked)
	{
		pdc.set(DataKey.SLOT_LOCK.key(), PersistentDataType.BOOLEAN, isLocked);
	}
	private void clearLock()
	{
		pdc.remove(DataKey.SLOT_LOCK.key());
	}
	public boolean getLock()
	{
		return pdc.getOrDefault(DataKey.SLOT_LOCK.key(), PersistentDataType.BOOLEAN, true);
	}

	// action

	public void changeLock(boolean isLocked)
	{
		setLock(isLocked);
		refreshLock();
	}
	public void lock()
	{
		changeLock(true);
	}
	public void unlock()
	{
		changeLock(false);
	}

	// refresh

	public void refreshLock()
	{
		Location lampLoc = getPosition(BloodSlotPlace.LAMP);
		Location lampLocM1 = lampLoc.clone();
		lampLocM1.setY(lampLoc.getY() - 1);
		Location lampLocM2 = lampLoc.clone();
		lampLocM2.setY(lampLoc.getY() - 2);

		if (world.getBlockData(lampLocM1).getMaterial() != Material.STICKY_PISTON)
		{
			BlockData pistonData = Bukkit.createBlockData("minecraft:sticky_piston[facing=up]");
			world.setBlockData(lampLocM1, pistonData);
		}

		BlockData powerBlock = ((getLock()) ? BlockType.LAPIS_BLOCK : BlockType.REDSTONE_BLOCK).createBlockData();
		world.setBlockData(lampLocM2, powerBlock);
	}

	public void refreshLamp(BloodPlayer bloodPlayerAtSlot)
	{
		Location lampLoc = getPosition(BloodSlotPlace.LAMP);
		Location lampLocP1 = lampLoc.clone();
		lampLocP1.setY(lampLoc.getY() + 1);
		Location leverLoc = getPosition(BloodSlotPlace.LEVER);
		BlockData leverData = world.getBlockData(leverLoc);
		BlockData lampData = BlockType.WAXED_COPPER_BLOCK.createBlockData();;

		if (bloodPlayerAtSlot != null) {
			boolean voting = bloodPlayerAtSlot.getVotePull();
			boolean voken = bloodPlayerAtSlot.getVoteToken();
			boolean alive = bloodPlayerAtSlot.getAlive();
			boolean traveler = bloodPlayerAtSlot.isTraveller();

			lampData = (alive)
				? BlockType.WAXED_COPPER_BULB.createBlockData()
				: BlockType.WAXED_OXIDIZED_COPPER_BULB.createBlockData();

			if (voting)
			{
				if (lampData instanceof Lightable lightable) {
					lightable.setLit(true);
					lightable.copyTo(lampData);
				}
			}

			if (!voken)
			{
				if (alive)
				{
					lampData = BlockType.REDSTONE_LAMP.createBlockData();
				} else {
					lampData = BlockType.NETHERITE_BLOCK.createBlockData();
				}
			}

			if (traveler)
			{
				if (lampData instanceof Powerable powerable) {
					powerable.setPowered(true);
					powerable.copyTo(lampData);
				}
			}

			if (leverData instanceof Powerable powerable) {
				powerable.setPowered(voting);
				powerable.copyTo(leverData);
			}
		}

		world.setBlockData(leverLoc, leverData);
		if (getLock())
		{
			world.setBlockData(lampLoc, lampData);
			world.setBlockData(lampLocP1, BlockType.AIR.createBlockData());
		} else {
			world.setBlockData(lampLocP1, lampData);
		}

		refreshLock();
	}
}
