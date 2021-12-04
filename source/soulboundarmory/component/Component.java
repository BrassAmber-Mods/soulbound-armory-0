package soulboundarmory.component;

import net.minecraft.nbt.NbtCompound;

public interface Component {
    void serialize(NbtCompound tag);

    /**
     Invoked only if this component has been previously serialized with the entity to which it belongs.
     */
    void deserialize(NbtCompound tag);

    default NbtCompound serialize() {
        var tag = new NbtCompound();
        this.serialize(tag);

        return tag;
    }
}
