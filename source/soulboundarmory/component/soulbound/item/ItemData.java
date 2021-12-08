package soulboundarmory.component.soulbound.item;

import net.minecraft.nbt.NbtCompound;
import soulboundarmory.serial.CompoundSerializable;
import soulboundarmory.util.Util;

public class ItemData implements CompoundSerializable {
    public ItemComponent<?> storage;

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
            var type = ItemComponentType.get(tag.getString("storage_type"));

            if (type != null && entity != null) {
                this.storage = type.get(entity);
            }
        }
    }
}
