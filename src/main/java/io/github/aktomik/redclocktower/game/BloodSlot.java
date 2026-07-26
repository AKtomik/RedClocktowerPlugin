package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.command.setup.TownChairPlace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Powerable;

public class BloodSlot {

	// construct
	private final TownChair townChair;
	private Seated seated;
	private boolean voteLocked;

	BloodSlot(TownChair townChair) {
		this.townChair = townChair;
		this.seated = null;
		refreshBlock(null);
		refreshPiston(voteLocked);
	}

	// seated
	public Seated getSeated() {
		return seated;
	}
	public boolean isOccupied() {
		return getSeated() != null;
	}

	public void assign(Seated seated) {
		this.seated = seated;
		refreshBlock(seated.getSeatState());
	}
	public void empty() {
		this.seated = null;
		refreshBlock(null);
	}

	// state
	void setState(SeatState state) {
		// right now seat block state is not stored and that cool
		// seatState = state;
		refreshBlock(state);
	}

	public void setVoteLocked(boolean locked) {
		voteLocked = locked;
		refreshPiston(locked);
	}

	public boolean isVoteLocked() {
		return voteLocked;
	}

	private void refreshBlock(SeatState state) {
		World world = townChair.getTownHall().getWorld();
		Location lampPosDown = townChair.getPosition(TownChairPlace.LAMP);
		Location lampPosUp = lampPosDown.clone();
		lampPosUp.setY(lampPosDown.getY() + 1);
		Location leverLoc = townChair.getPosition(TownChairPlace.LEVER);

		BlockData leverData = world.getBlockData(leverLoc);
		BlockData lampData = BlockType.WAXED_COPPER_BLOCK.createBlockData();

		if (seated != null) {
			assert state != null;

			// exclusion vote case here

			if (state.traveller())
			{
				if (state.alive())
				{
					if (state.votePull()) {
						lampData = BlockType.GLOWSTONE.createBlockData();
					} else {
						lampData = BlockType.WAXED_COPPER_GRATE.createBlockData();
					}
				} else {
					if (!state.voteToken()) {
						lampData = BlockType.NETHERITE_BLOCK.createBlockData();
					} else {
						if (state.votePull()) {
							lampData = BlockType.SEA_LANTERN.createBlockData();
						} else {
							lampData = BlockType.WAXED_OXIDIZED_COPPER_GRATE.createBlockData();
						}
					}
				}
			}
			else
			{
				if (state.alive()) {
					lampData = BlockType.WAXED_COPPER_BULB.createBlockData();
				} else {
					if (!state.voteToken()) {
						lampData = BlockType.NETHERITE_BLOCK.createBlockData();
					} else {
						lampData = BlockType.WAXED_OXIDIZED_COPPER_BULB.createBlockData();
					}
				}

				if (state.votePull() && lampData instanceof Lightable lightable) {
					lightable.setLit(true);
					lightable.copyTo(lampData);
				}
			}


			if (leverData instanceof Powerable powerable) {
				powerable.setPowered(state.votePull());
				powerable.copyTo(leverData);
			}
		}

		world.setBlockData(leverLoc, leverData);
		if (voteLocked)
		{
			world.setBlockData(lampPosDown, lampData);
			world.setBlockData(lampPosUp, BlockType.AIR.createBlockData());
		} else {
			world.setBlockData(lampPosUp, lampData);
		}
	}

	private void refreshPiston(boolean locked) {
		World world = townChair.getTownHall().getWorld();
		Location lampPosDown = townChair.getPosition(TownChairPlace.LAMP);
		Location lampPosMinus1 = lampPosDown.clone();
		lampPosMinus1.setY(lampPosDown.getY() - 1);
		Location lampPosMinus2 = lampPosDown.clone();
		lampPosMinus2.setY(lampPosDown.getY() - 2);

		if (world.getBlockData(lampPosMinus1).getMaterial() != Material.STICKY_PISTON)
		{
			BlockData pistonData = Bukkit.createBlockData("minecraft:sticky_piston[facing=up]");
			world.setBlockData(lampPosMinus1, pistonData);
		}

		BlockData powerBlock = ((locked) ? BlockType.LAPIS_BLOCK : BlockType.REDSTONE_BLOCK).createBlockData();
		world.setBlockData(lampPosMinus2, powerBlock);
		// but if it's a redstone lamp, it will refresh and light off
		// it is patchable by replacing the block ~5 ticks after piston
	}
}
