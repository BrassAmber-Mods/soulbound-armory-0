package soulboundarmory.mixin.access.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface PlayerEntityAccess {
    DefaultedList<ItemStack> combinedInventory();
}
