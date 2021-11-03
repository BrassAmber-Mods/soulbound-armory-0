package user11681.soulboundarmory.asm.access.entity;

public interface LightningEntityAccess {
    default int ambientTick() {
        return 0;
    }

    default void ambientTick(int flashes) {}

    default int remainingActions() {
        return 0;
    }

    default void remainingActions(int flashes) {}
}
