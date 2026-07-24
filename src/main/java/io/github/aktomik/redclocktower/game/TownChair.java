package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.DataKey;
import io.github.aktomik.redclocktower.command.setup.TownChairPlace;
import io.github.aktomik.redclocktower.command.setup.TownHallPlace;
import io.github.aktomik.redclocktower.utils.pdc.BlockPos;
import io.github.aktomik.redclocktower.utils.pdc.PositionDataType;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataContainer;

public class TownChair {

	private final TownHall townHall;
	private final PersistentDataContainer pdc;

	private TownChair(TownHall townHall, PersistentDataContainer pdc)
	{
		this.townHall = townHall;
		this.pdc = pdc;
	}

	public static TownChair get(TownHall townHall, PersistentDataContainer pdc) {
		return new TownChair(townHall, pdc);
	}

	public PersistentDataContainer getPdc() {
		return pdc;
	}

	// data/position
	public void setPosition(TownChairPlace place, Location pos)
	{
		pdc.set(DataKey.TOWN_CHAIR_LOC.get(place).key(), PositionDataType.INSTANCE, new BlockPos(pos));
	}
	public Location getPosition(TownChairPlace place)
	{
		BlockPos blockPos = pdc.get(DataKey.TOWN_CHAIR_LOC.get(place).key(), PositionDataType.INSTANCE);
		if (blockPos == null) return null;
		return blockPos.toLocation(townHall.getWorld());
	}
}
