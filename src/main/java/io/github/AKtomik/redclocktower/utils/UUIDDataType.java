package io.github.AKtomik.redclocktower.utils;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

import java.nio.ByteBuffer;
import java.util.UUID;

@NullMarked
public class UUIDDataType implements PersistentDataType<byte[], UUID> {

	public static final UUIDDataType INSTANCE = new UUIDDataType();

	// We just need a singleton, so there's no need to allow instantiation
	private UUIDDataType() {}

	@Override
	public Class<byte[]> getPrimitiveType() {
		return byte[].class;
	}

	@Override
	public Class<UUID> getComplexType() {
		return UUID.class;
	}

	@Override
	public byte[] toPrimitive(UUID complex, PersistentDataAdapterContext context) {
		ByteBuffer bb = ByteBuffer.allocate(Long.BYTES * 2);
		bb.putLong(complex.getMostSignificantBits());
		bb.putLong(complex.getLeastSignificantBits());
		return bb.array();
	}

	@Override
	public UUID fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
		ByteBuffer bb = ByteBuffer.wrap(primitive);
		long firstLong = bb.getLong();
		long secondLong = bb.getLong();
		return new UUID(firstLong, secondLong);
	}
}