package soulboundarmory.component.soulbound.item;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import soulboundarmory.serial.CompoundSerializable;
import soulboundarmory.util.Util;

public class ItemData implements CompoundSerializable {
    public ItemStorage<?> storage;

    @Override
    public void serializeNBT(NbtCompound tag) {
        if (this.storage != null) {
            tag.putUuid("player", this.storage.player.getUuid());
            tag.putString("storageType", this.storage.type().id().toString());
        }
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        var server = Util.server();

        if (server != null && tag.contains("player") && tag.contains("storage_type")) {
            var entity = server.getPlayerManager().getPlayer(tag.getUuid("player"));
            var type = StorageType.get(tag.getString("storage_type"));

            if (type != null && entity != null) {
                this.storage = type.get(entity);
            }
        }
    }
}
