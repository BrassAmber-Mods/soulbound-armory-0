package net.auoeke.soulboundarmory.capability.soulbound.item;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.auoeke.soulboundarmory.serial.CompoundSerializable;
import net.auoeke.soulboundarmory.util.Util;

public class ItemData implements CompoundSerializable {
    public ItemStorage<?> storage;

    @Override
    public void serializeNBT(NbtCompound tag) {
        MinecraftServer server = Util.server();

        if (server != null && tag.contains("player") && tag.contains("storage_type")) {
            Entity entity = server.getPlayerManager().getPlayer(tag.getUuid("player"));
            StorageType<?> type = StorageType.get(tag.getString("storage_type"));

            if (type != null && entity != null) {
                this.storage = type.get(entity);
            }
        }
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        if (this.storage != null) {
            tag.putUuid("player", this.storage.player.getUuid());
            tag.putString("storage_type", this.storage.type().toString());
        }
    }
}
