package user11681.soulboundarmory.asm.access.entity;

public interface LightningEntityAccess {
    default int getAmbientTick() {
        return 0;
    }

    default void setAmbientTick(final int i) {

    }

    default int getRemainingActions() {
        return 0;
    }

    default void setRemainingActions(final int i) {

    }
}
