package soulboundarmory.component.config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import soulboundarmory.component.Component;

public final class ConfigComponent implements Component {
    public final PlayerEntity entity;
    public boolean levelupNotifications;

    public ConfigComponent(PlayerEntity player) {
        this.entity = player;
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
