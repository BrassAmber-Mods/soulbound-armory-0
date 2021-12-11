package soulboundarmory.serial;

import net.minecraft.nbt.NbtCompound;

public interface CompoundSerializable {
    void deserialize(NbtCompound arg);

    default void serialize(NbtCompound tag) {}

    /**
     @return a new compound tag after passing it to {@link #serialize(NbtCompound)}.
     */
    default NbtCompound serialize() {
        var tag = new NbtCompound();
        this.serialize(tag);

        return tag;
    }
}
