package user11681.soulboundarmory.serial;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface CompoundSerializable extends INBTSerializable<CompoundNBT> {
    @Override
    void deserializeNBT(CompoundNBT tag);

    default void serializeNBT(CompoundNBT tag) {}

    default CompoundNBT tag() {
        CompoundNBT tag = new CompoundNBT();
        this.serializeNBT(tag);

        return tag;
    }

    @Override
    default CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();

        this.serializeNBT(tag);

        return tag;
    }
}
