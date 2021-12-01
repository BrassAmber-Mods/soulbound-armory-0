package soulboundarmory.component.config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import soulboundarmory.component.EntityComponent;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.registry.Packets;

public class ConfigComponent extends EntityComponent<PlayerEntity> {
    public boolean levelupNotifications;

    public ConfigComponent(PlayerEntity player) {
        super(player);
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        if (this.entity.level.isClientSide) {
            Packets.serverConfig.send(new ExtendedPacketBuffer().writeBoolean(this.levelupNotifications));
        }
    }

    @Override
    public void serialize(CompoundNBT tag) {
        tag.putBoolean("levelupNotifications", this.levelupNotifications);
    }
}
