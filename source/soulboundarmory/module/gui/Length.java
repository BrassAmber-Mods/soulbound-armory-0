package soulboundarmory.module.gui;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import soulboundarmory.util.Util;

public class Length {
	public IntSupplier value = Util.zeroSupplier;
	public Type type = Type.PARENT_PROPORTION;

	private DoubleSupplier base = () -> 1;

	public void base(double value) {
		this.base(() -> value);
	}

	public void base(int value) {
		this.base(() -> value);
	}

	public void base(DoubleSupplier value) {
		this.base = value;
		this.type = Type.PARENT_PROPORTION;
	}

	public void base(IntSupplier value) {
		this.base = value::getAsInt;
		this.type = Type.EXACT;
	}

	public void type(Type type) {
		this.type = type;
	}

	public DoubleSupplier base() {
		return this.base;
	}

	public enum Type {
		EXACT,
		PARENT_PROPORTION,
		CHILD_SUM
	}
}
