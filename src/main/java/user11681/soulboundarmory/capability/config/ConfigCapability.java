package user11681.soulboundarmory.capability.config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;
import user11681.soulboundarmory.serial.CompoundSerializable;

public class ConfigCapability implements CompoundSerializable {
    public PlayerEntity player;
    public boolean levelupNotifications;

    @OnlyIn(Dist.CLIENT)
    @Override
    public void deserializeNBT(CompoundNBT tag) {
        Packets.serverConfig.send(new ExtendedPacketBuffer().writeBoolean(this.levelupNotifications));
    }

    @Override
    public void serializeNBT(CompoundNBT tag) {
        tag.putBoolean("levelupNotifications", this.levelupNotifications);
    }
}
