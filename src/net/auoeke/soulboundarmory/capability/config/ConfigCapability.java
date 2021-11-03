package net.auoeke.soulboundarmory.capability.config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.registry.Packets;
import net.auoeke.soulboundarmory.serial.CompoundSerializable;

public class ConfigCapability implements CompoundSerializable {
    public boolean levelupNotifications;

    public ConfigCapability(PlayerEntity player) {
        if (!player.world.isClient) {
            Packets.serverConfig.send(player, new ExtendedPacketBuffer());
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void deserializeNBT(NbtCompound tag) {
        Packets.serverConfig.send(new ExtendedPacketBuffer().writeBoolean(this.levelupNotifications));
    }

    @Override
    public void serializeNBT(NbtCompound tag) {
        tag.putBoolean("levelupNotifications", this.levelupNotifications);
    }
}
