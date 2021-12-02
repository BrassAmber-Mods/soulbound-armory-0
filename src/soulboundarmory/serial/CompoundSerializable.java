package soulboundarmory.serial;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface CompoundSerializable extends INBTSerializable<CompoundNBT> {
    default void serializeNBT(CompoundNBT tag) {}

    @Override
    default CompoundNBT serializeNBT() {
        var tag = new CompoundNBT();
        this.serializeNBT(tag);

        return tag;
    }
}
