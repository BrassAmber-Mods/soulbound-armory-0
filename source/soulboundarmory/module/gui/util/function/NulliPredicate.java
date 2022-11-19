package soulboundarmory.module.gui.util.function;

import java.util.function.BooleanSupplier;

@FunctionalInterface
public interface NulliPredicate extends BooleanSupplier {
	static NulliPredicate ofTrue() {
		return () -> true;
	}

	static NulliPredicate ofFalse() {
		return () -> false;
	}

	static NulliPredicate of(boolean result) {
		return () -> result;
	}

	default NulliPredicate and(NulliPredicate condition) {
		return () -> this.getAsBoolean() && condition.getAsBoolean();
	}

	default NulliPredicate or(NulliPredicate condition) {
		return () -> this.getAsBoolean() || condition.getAsBoolean();
	}
}
