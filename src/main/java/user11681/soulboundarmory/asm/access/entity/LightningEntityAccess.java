package user11681.soulboundarmory.asm.access.entity;

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
