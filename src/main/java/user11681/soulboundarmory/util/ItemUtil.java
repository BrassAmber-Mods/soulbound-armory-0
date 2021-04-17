package user11681.soulboundarmory.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import user11681.soulboundarmory.asm.mixin.entity.player.PlayerInventoryAccess;

public class ItemUtil {
    public static List<ItemStack> inventory(PlayerEntity player) {
        return inventoryStream(player).collect(Collectors.toList());
    }

    public static Stream<ItemStack> inventoryStream(PlayerEntity player) {
        return ((PlayerInventoryAccess) player.getInventory()).combinedInventory().stream().flatMap(DefaultedList::stream);
    }

    public static boolean hasItem(PlayerEntity player, Class<?> baseClass) {
        return inventoryStream(player).anyMatch(baseClass::isInstance);
    }

    public static List<Item> handItems(Entity entity) {
        List<Item> items = new ArrayList<>(2);

        entity.getItemsHand().forEach((ItemStack stack) -> items.add(stack.getItem()));

        return items;
    }
}
