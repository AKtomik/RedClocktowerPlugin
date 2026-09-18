package io.github.aktomik.redclocktower.utils.pdc;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NamedTextColorDataType implements PersistentDataType<Integer, NamedTextColor> {

	public static final NamedTextColorDataType INSTANCE = new NamedTextColorDataType();

	private NamedTextColorDataType() {}

	@Override
	public Class<Integer> getPrimitiveType() { return Integer.class; }

	@Override
	public Class<NamedTextColor> getComplexType() { return NamedTextColor.class; }

	@Override
	public Integer toPrimitive(NamedTextColor complex, PersistentDataAdapterContext context) {
		return complex.value();
	}

	@Override
	public NamedTextColor fromPrimitive(Integer primitive, PersistentDataAdapterContext context) {
		return NamedTextColor.nearestTo(TextColor.color(primitive));
	}
}