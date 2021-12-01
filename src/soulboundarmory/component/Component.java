package soulboundarmory.component;

import net.minecraft.nbt.CompoundNBT;

public interface Component {
    void serialize(CompoundNBT tag);

    /**
     Invoked only if this component has been previously serialized with the entity to which it belongs.
     */
    void deserialize(CompoundNBT tag);

    default CompoundNBT serialize() {
        var tag = new CompoundNBT();
        this.serialize(tag);

        return tag;
    }
}
