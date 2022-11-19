package soulboundarmory.mixin.access;

public interface LightningEntityAccess {
	default int life() {
		return 0;
	}

	default void life(int flashes) {}

	default int flashes() {
		return 0;
	}

	default void flashes(int flashes) {}
}
