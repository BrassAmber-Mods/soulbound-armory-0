package net.auoeke.soulboundarmory.capability;

import net.auoeke.soulboundarmory.serial.CompoundSerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class CapabilityStorage<T extends CompoundSerializable> implements Capability.IStorage<T> {
    @Override
    public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
        var tag = new CompoundNBT();
        instance.deserializeNBT(tag);

        return tag;
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, Direction side, INBT tag) {
        instance.serializeNBT((CompoundNBT) tag);
    }
}
