package soulboundarmory.serial;

import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface CompoundSerializable extends INBTSerializable<NbtCompound> {
    default void serializeNBT(NbtCompound tag) {}

    @Override
    default NbtCompound serializeNBT() {
        var tag = new NbtCompound();
        this.serializeNBT(tag);

        return tag;
    }
}
