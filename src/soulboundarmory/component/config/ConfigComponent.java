package soulboundarmory.component.config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import soulboundarmory.component.EntityComponent;

public class ConfigComponent extends EntityComponent<PlayerEntity> {
    public boolean levelupNotifications;

    public ConfigComponent(PlayerEntity player) {
        super(player);
    }

    @Override
    public void serialize(CompoundNBT tag) {
        tag.putBoolean("levelupNotifications", this.levelupNotifications);
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        this.levelupNotifications = tag.getBoolean("levelupNotifications");
    }
}
