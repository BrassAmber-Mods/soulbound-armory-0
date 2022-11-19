package soulboundarmory.mixin.access;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface PlayerEntityAccess {
	DefaultedList<ItemStack> combinedInventory();
}
