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
    protected void execute(ItemComponent<?> component) {
        var slot = this.message.readInt();

        if (component.isUnlocked() || component.canConsume(component.player.getInventory().getStack(slot))) {
            set(component, slot);

            if (FMLEnvironment.dist == Dist.CLIENT) {
                // Repeat for the client because the screen pauses.
                MinecraftClient.getInstance().execute(() -> set(component.type().of(MinecraftClient.getInstance().player), slot));
            } else {
                component.synchronize();
            }

            component.component.refresh();
        }
    }

    private static void set(ItemComponent<?> storage, int slot) {
        storage.player.getInventory().setStack(slot, storage.stack());
        storage.component.currentItem(storage);
        storage.updateInventory(slot);
    }
}
