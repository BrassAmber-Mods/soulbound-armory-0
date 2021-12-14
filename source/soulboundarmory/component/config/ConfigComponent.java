package soulboundarmory.component.config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import soulboundarmory.config.Configuration;
import soulboundarmory.lib.component.EntityComponent;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

public final class ConfigComponent implements EntityComponent<ConfigComponent> {
    public final PlayerEntity player;
    public boolean levelupNotifications;
    public boolean glint;

    public ConfigComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void spawn() {
        if (this.player.world.isClient) {
            var configuration = Configuration.instance().client;
            Packets.serverConfig.send(new ExtendedPacketBuffer().writeBoolean(configuration.levelupNotifications).writeBoolean(configuration.enchantmentGlint));
        }
    }

    @Override
    public void serialize(NbtCompound tag) {}

    @Override
    public void deserialize(NbtCompound tag) {}
}
