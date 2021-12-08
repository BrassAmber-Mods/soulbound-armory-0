package soulboundarmory.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import soulboundarmory.component.soulbound.item.ItemStorage;

public interface SoulboundItem {
    default void bindSlot(ItemStack stack, Entity entity, int slot) {
        if (entity instanceof PlayerEntity) {
            var storage = ItemStorage.get(entity, stack).get();

            if (storage.boundSlot() != -1) {
                storage.bindSlot(slot);
            }
        }
    }
}
