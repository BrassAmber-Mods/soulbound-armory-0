package user11681.soulboundarmory.component.config;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import user11681.soulboundarmory.SoulboundArmoryClient;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;

public class ConfigComponent implements AutoSyncedComponent {
    protected PlayerEntity player;
    protected boolean addToOffhand;
    protected boolean levelupNotifications;

    @SuppressWarnings({"VariableUseSideOnly", "LocalVariableDeclarationSideOnly"})
    public ConfigComponent(final PlayerEntity player) {
        this.player = player;

        if (player.world.isClient) {
            final Configuration.Client configuration = Configuration.instance().client;

            this.addToOffhand = configuration.addToOffhand;
            this.levelupNotifications = configuration.levelupNotifications;
        }
    }

    public boolean getAddToOffhand() {
        return this.addToOffhand;
    }

    public void setAddToOffhand(final boolean addToOffhand) {
        this.addToOffhand = addToOffhand;
    }

    public boolean getLevelupNotifications() {
        return this.levelupNotifications;
    }

    public void setLevelupNotifications(final boolean levelupNotifications) {
        this.levelupNotifications = levelupNotifications;
    }

    @Override
    public void writeToNbt(final NbtCompound tag) {
        tag.putBoolean("addToOffhand", this.addToOffhand);
        tag.putBoolean("levelupNotifications", this.levelupNotifications);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void readFromNbt(final NbtCompound tag) {
        SoulboundArmoryClient.packetRegistry.sendToServer(Packets.serverConfig, new ExtendedPacketBuffer().writeBoolean(this.addToOffhand).writeBoolean(this.levelupNotifications));
    }

    public PlayerEntity getEntity() {
        return this.player;
    }
}
