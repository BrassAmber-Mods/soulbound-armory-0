package user11681.soulboundarmory.component.soulbound.item;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import user11681.soulboundarmory.util.Util;

public class ItemData extends ItemComponent implements AutoSyncedComponent<ItemData> {
    public ItemStorage<?> storage;

    public ItemData(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        MinecraftServer server = Util.getServer();

        if (server != null && tag.contains("player") && tag.contains("storage_type")) {
            Entity entity = server.getPlayerManager().getPlayer(tag.getUuid("player"));
            StorageType<? extends ItemStorage<?>> type = StorageType.registry.get(tag.getString("storage_type"));

            if (type != null && entity != null) {
                this.storage = type.get(entity);
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if (this.storage != null) {
            tag.putUuid("player", this.storage.player.getUuid());
            tag.putString("storage_type", this.storage.getType().toString());
        }

        return tag;
    }

    @Override
    public boolean isComponentEqual(Component component) {
        return component instanceof ItemData && ItemStack.areItemsEqual(((ItemData) component).itemStack, this.itemStack);
    }
}
