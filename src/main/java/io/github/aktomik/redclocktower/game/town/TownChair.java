package io.github.aktomik.redclocktower.game.town;

import io.github.aktomik.redclocktower.DataKey;
import io.github.aktomik.redclocktower.utils.pdc.BlockPos;
import io.github.aktomik.redclocktower.utils.pdc.PositionDataType;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;

public class TownChair {
	// TownChair is an interface to the chair pdc
	// it is the role of BloodSlot to store attributes about the slot

	// it is theoretically possible to have multiple TownChair objects referring to the same pdc
	// you should avoid those cases. but you still have to only use pdc to save or load data about the chair

	private final TownHall townHall;
	private final PersistentDataContainer pdc;

	private TownChair(TownHall townHall, PersistentDataContainer pdc)
	{
		this.townHall = townHall;
		this.pdc = pdc;
	}

	@NullMarked
	static TownChair get(TownHall townHall, PersistentDataContainer pdc) {
		return new TownChair(townHall, pdc);
	}

	// access
	public TownHall getTownHall() {
		return townHall;
	}
	public PersistentDataContainer getPdc() {
		return pdc;
	}

	// data/position
	public void setPosition(TownChairPlace place, Location pos)
	{
		pdc.set(DataKey.TOWN_CHAIR_POS.get(place).key(), PositionDataType.INSTANCE, new BlockPos(pos));
	}
	public Location getPosition(TownChairPlace place)
	{
		BlockPos blockPos = pdc.get(DataKey.TOWN_CHAIR_POS.get(place).key(), PositionDataType.INSTANCE);
		if (blockPos == null) return null;
		return blockPos.toLocation(townHall.getWorld());
	}
}
