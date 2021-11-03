package user11681.soulboundarmory.serial;

import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface CompoundSerializable extends INBTSerializable<NbtCompound> {
    @Override
    void deserializeNBT(NbtCompound tag);

    default void serializeNBT(NbtCompound tag) {}

    default NbtCompound tag() {
        NbtCompound tag = new NbtCompound();
        this.serializeNBT(tag);

        return tag;
    }

    @Override
    default NbtCompound serializeNBT() {
        NbtCompound tag = new NbtCompound();

        this.serializeNBT(tag);

        return tag;
    }
}
