package soulboundarmory.util;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import soulboundarmory.mixin.mixin.entity.player.PlayerInventoryAccess;

public class ItemUtil {
    public static Stream<ItemStack> inventory(PlayerEntity player) {
        return ((PlayerInventoryAccess) player.inventory).combinedInventory().stream().flatMap(DefaultedList::stream);
    }

    public static Stream<ItemStack> handStacks(Entity entity) {
        return StreamSupport.stream(entity.getItemsHand().spliterator(), false);
    }

    public static Stream<ItemStack> handStacks(PlayerInventory inventory) {
        return Stream.of(inventory.getMainHandStack(), inventory.offHand.get(0));
    }

    public static ItemStack equippedStack(Entity player, Item... validItems) {
        return handStacks(player).filter(itemStack -> Arrays.asList(validItems).contains(itemStack.getItem())).findAny().orElse(null);
    }

    public static ItemStack equippedStack(PlayerInventory inventory, Class<?> type) {
        return handStacks(inventory).filter(stack -> type.isInstance(stack.getItem())).findAny().orElse(null);
    }

    public static int matchingSlot(PlayerInventory inventory, ItemStack itemStack) {
        return IntStream.range(0, inventory.main.size())
            .filter(slot -> !inventory.main.get(slot).isEmpty() && ItemStack.areItemsEqual(itemStack, inventory.main.get(slot)))
            .findFirst()
            .orElse(-1);
    }
}
