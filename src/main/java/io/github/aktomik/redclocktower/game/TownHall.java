package io.github.aktomik.redclocktower.game;

import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;

public class TownHall {
	public final World world;
	private final PersistentDataContainer pdc;

	private TownHall(World world)//String townName
	{
		this.world = world;
		this.pdc = world.getPersistentDataContainer();
	}

	public static TownHall get(World world)
	{
		return new TownHall(world);
	}
}
