package soulboundarmory.lib.component;

import javax.annotation.Nonnull;
import net.minecraft.nbt.NbtCompound;
import soulboundarmory.serial.CompoundSerializable;

/**
 A persistent attachment to a game object.
 */
public interface Component extends CompoundSerializable {
    /**
     Serialize this component into `tag`.

     @param tag an empty compound tag for this component
     */
    @Override
    void serialize(NbtCompound tag);

    /**
     Deserialize this component from `tag`.
     Invoked only if this component has been previously {@linkplain #serialize() serialized} and its tag is not empty.

     @param tag a tag containing the same information from the last call to {@link #serialize()} on this component
     */
    @Override
    void deserialize(NbtCompound tag);

    /**
     Item components can override this method in order to affect equality between item stacks with the same component.
     The default implementation compares their {@linkplain #serialize() tags}.
     */
    default boolean equals(@Nonnull Component other) {
        return this.serialize().equals(other.serialize());
    }

    /**
     Finish initializing after the object is constructed.
     */
    default void initialize() {}
}
