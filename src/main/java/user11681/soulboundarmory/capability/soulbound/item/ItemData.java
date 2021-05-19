package user11681.soulboundarmory.capability.soulbound.item;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import user11681.soulboundarmory.util.Util;

public class ItemData extends ItemComponent implements AutoSyncedComponent<ItemData> {
    public ItemStorage<?> storage;

    public ItemData(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void writeToNbt(CompoundNBT tag) {
        MinecraftServer server = Util.getServer();

        if (server != null && tag.contains("player") && tag.contains("storage_type")) {
            Entity entity = server.getPlayerManager().getPlayer(tag.getUUID("player"));
            StorageType<? extends ItemStorage<?>> type = StorageType.registry.get(tag.getString("storage_type"));

            if (type != null && entity != null) {
                this.storage = type.get(entity);
            }
        }
    }

    @Override
    public void readFromNbt(CompoundNBT tag) {
        if (this.storage != null) {
            tag.putUUID("player", this.storage.player.getUUID());
            tag.putString("storage_type", this.storage.getType().toString());
        }

        return tag;
    }

    @Override
    public boolean isComponentEqual(Component component) {
        return component instanceof ItemData && ItemStack.areItemsEqual(((ItemData) component).itemStack, this.itemStack);
    }
}
