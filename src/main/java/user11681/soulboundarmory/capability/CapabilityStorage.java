package user11681.soulboundarmory.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import user11681.soulboundarmory.serial.CompoundSerializable;

public class CapabilityStorage<T extends CompoundSerializable> implements Capability.IStorage<T> {
    @Override
    public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();

        instance.deserializeNBT(tag);

        return tag;
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, Direction side, INBT tag) {
        instance.serializeNBT((CompoundNBT) tag);
    }
}
