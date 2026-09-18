package io.github.aktomik.redclocktower.utils.pdc;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TextColorDataType implements PersistentDataType<Integer, TextColor> {

	public static final TextColorDataType INSTANCE = new TextColorDataType();

	private TextColorDataType() {}

	@Override
	public Class<Integer> getPrimitiveType() { return Integer.class; }

	@Override
	public Class<TextColor> getComplexType() { return TextColor.class; }

	@Override
	public Integer toPrimitive(TextColor complex, PersistentDataAdapterContext context) {
		return complex.value();
	}

	@Override
	public TextColor fromPrimitive(Integer primitive, PersistentDataAdapterContext context) {
		return TextColor.color(primitive);
	}
}