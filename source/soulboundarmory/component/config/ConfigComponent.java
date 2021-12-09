package soulboundarmory.component.config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import soulboundarmory.lib.component.Component;

public final class ConfigComponent implements Component {
    public final PlayerEntity entity;
    public boolean levelupNotifications;
    public boolean glint;

    public ConfigComponent(PlayerEntity player) {
        this.entity = player;
    }

    @Override
    public void serialize(NbtCompound tag) {}

    @Override
    public void deserialize(NbtCompound tag) {}
}
