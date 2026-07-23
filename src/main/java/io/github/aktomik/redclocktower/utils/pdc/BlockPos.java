package io.github.aktomik.redclocktower.utils.pdc;

import org.bukkit.Location;
import org.bukkit.World;

public record BlockPos(int x, int y, int z) {
	public BlockPos(Location loc) {
		this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	public Location toLocation(World world) {
		return new Location(world, x, y, z);
	}
}
