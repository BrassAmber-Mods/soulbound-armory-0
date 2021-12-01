package soulboundarmory.mixin.access.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface PlayerEntityAccess {
    NonNullList<ItemStack> combinedInventory();
}
