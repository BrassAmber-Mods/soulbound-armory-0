package soulboundarmory.component.config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import soulboundarmory.component.EntityComponent;

public class ConfigComponent extends EntityComponent<PlayerEntity> {
    public boolean levelupNotifications;

    public ConfigComponent(PlayerEntity player) {
        super(player);
    }

    @Override
    public void serialize(NbtCompound tag) {
        tag.putBoolean("levelupNotifications", this.levelupNotifications);
    }

    @Override
    public void deserialize(NbtCompound tag) {
        this.levelupNotifications = tag.getBoolean("levelupNotifications");
    }
}
