package soulboundarmory.util;

public interface Sided {
	boolean isClient();

	default boolean isServer() {
		return !this.isClient();
	}
}
