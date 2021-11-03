package user11681.soulboundarmory.capability;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import user11681.soulboundarmory.serial.CompoundSerializable;

public class CapabilityStorage<T extends CompoundSerializable> implements Capability.IStorage<T> {
    @Override
    public NbtElement writeNBT(Capability<T> capability, T instance, Direction side) {
        NbtCompound tag = new NbtCompound();

        instance.deserializeNBT(tag);

        return tag;
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, Direction side, NbtElement tag) {
        instance.serializeNBT((NbtCompound) tag);
    }
}
