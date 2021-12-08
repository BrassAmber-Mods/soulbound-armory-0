package soulboundarmory.network.server;

import net.minecraft.client.MinecraftClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import soulboundarmory.client.gui.screen.SelectionTab;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.network.ItemComponentPacket;

/**
 A client-to-server packet that is sent when a client selects an item.

 @see SelectionTab
 */
public final class C2SSelectItem extends ItemComponentPacket {
    @Override
    protected void execute(ItemComponent<?> storage) {
        var slot = this.message.readInt();

        if (storage.isUnlocked() || storage.canConsume(storage.player.inventory.getStack(slot))) {
            set(storage, slot);

            if (FMLEnvironment.dist == Dist.CLIENT) {
                // Repeat for the client because the screen pauses.
                MinecraftClient.getInstance().execute(() -> set(storage.type().get(MinecraftClient.getInstance().player), slot));
            } else {
                storage.sync();
            }

            storage.component.refresh();
        }
    }

    private static void set(ItemComponent<?> storage, int slot) {
        storage.player.inventory.setStack(slot, storage.stack());
        storage.component.currentItem(storage);
        storage.updateInventory(slot);
    }
}
