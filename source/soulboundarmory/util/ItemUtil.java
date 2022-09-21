package soulboundarmory.util;

import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import soulboundarmory.mixin.mixin.access.PlayerInventoryAccess;

public class ItemUtil {
    public static Stream<ItemStack> inventory(PlayerEntity player) {
        return ((PlayerInventoryAccess) player.getInventory()).combinedInventory().stream().flatMap(DefaultedList::stream);
    }

    public static Stream<ItemStack> handStacks(Entity entity) {
        return Util.stream(entity.getHandItems());
    }
}
