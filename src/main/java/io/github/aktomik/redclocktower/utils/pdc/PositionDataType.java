package io.github.aktomik.redclocktower.utils.pdc;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PositionDataType implements PersistentDataType<int[], BlockPos> {

	public static final PositionDataType INSTANCE = new PositionDataType();

	private PositionDataType() {}

	@Override
	public Class<int[]> getPrimitiveType() { return int[].class; }

	@Override
	public Class<BlockPos> getComplexType() { return BlockPos.class; }

	@Override
	public int[] toPrimitive(BlockPos complex, PersistentDataAdapterContext context) {
		return new int[] { complex.x(), complex.y(), complex.z() };
	}

	@Override
	public BlockPos fromPrimitive(int[] primitive, PersistentDataAdapterContext context) {
		return new BlockPos(primitive[0], primitive[1], primitive[2]);
	}
}