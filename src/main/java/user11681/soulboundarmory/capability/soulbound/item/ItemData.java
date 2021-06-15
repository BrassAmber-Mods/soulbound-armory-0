package user11681.soulboundarmory.capability.soulbound.item;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import user11681.soulboundarmory.serial.CompoundSerializable;
import user11681.soulboundarmory.util.Util;

public class ItemData implements CompoundSerializable {
    public ItemStorage<?> storage;

    @Override
    public void serializeNBT(CompoundNBT tag) {
        MinecraftServer server = Util.server();

        if (server != null && tag.contains("player") && tag.contains("storage_type")) {
            Entity entity = server.getPlayerList().getPlayer(tag.getUUID("player"));
            StorageType<?> type = StorageType.get(tag.getString("storage_type"));

            if (type != null && entity != null) {
                this.storage = type.get(entity);
            }
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        if (this.storage != null) {
            tag.putUUID("player", this.storage.player.getUUID());
            tag.putString("storage_type", this.storage.type().toString());
        }
    }
}
