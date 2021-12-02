package soulboundarmory.component.soulbound.item;

import soulboundarmory.serial.CompoundSerializable;
import soulboundarmory.util.Util;
import net.minecraft.nbt.CompoundNBT;

public class ItemData implements CompoundSerializable {
    public ItemStorage<?> storage;

    @Override
    public void serializeNBT(CompoundNBT tag) {
        if (this.storage != null) {
            tag.putUniqueId("player", this.storage.player.getUniqueID());
            tag.putString("storageType", this.storage.type().id().toString());
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        var server = Util.server();

        if (server != null && tag.contains("player") && tag.contains("storage_type")) {
            var entity = server.getPlayerList().getPlayerByUUID(tag.getUniqueId("player"));
            var type = StorageType.get(tag.getString("storage_type"));

            if (type != null && entity != null) {
                this.storage = type.get(entity);
            }
        }
    }
}
