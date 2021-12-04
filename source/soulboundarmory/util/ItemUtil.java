package soulboundarmory.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import soulboundarmory.mixin.mixin.entity.player.PlayerInventoryAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class ItemUtil {
    public static List<ItemStack> inventory(PlayerEntity player) {
        return inventoryStream(player).collect(Collectors.toList());
    }

    public static Stream<ItemStack> inventoryStream(PlayerEntity player) {
        return ((PlayerInventoryAccess) player.inventory).compartments().stream().flatMap(DefaultedList::stream);
    }

    public static List<Item> handItems(LivingEntity entity) {
        return Arrays.asList(entity.getMainHandStack().getItem(), entity.getOffHandStack().getItem());
    }

    public static Stream<ItemStack> handStacks(LivingEntity entity) {
        return StreamSupport.stream(entity.getItemsHand().spliterator(), false);
    }

    public static Stream<ItemStack> handStacks(PlayerInventory inventory) {
        return Stream.of(inventory.getMainHandStack(), inventory.offHand.get(0));
    }

    public static ItemStack equippedStack(PlayerEntity player, Item... validItems) {
        return handStacks(player).filter(itemStack -> Arrays.asList(validItems).contains(itemStack.getItem())).findAny().orElse(null);
    }

    public static ItemStack equippedStack(PlayerInventory inventory, Class<?> type) {
        return handStacks(inventory).filter(stack -> type.isInstance(stack.getItem())).findAny().orElse(null);
    }

    public static ItemStack getRequiredItemStack(PlayerEntity player, Class<?>... types) {
        var itemStack = player.getMainHandStack();
        var item = itemStack.getItem();

        for (var type : types) {
            if (type.isInstance(item)) {
                return itemStack;
            }
        }

        return null;
    }

    public static int matchingSlot(PlayerInventory inventory, ItemStack itemStack) {
        return IntStream.range(0, inventory.main.size())
            .filter(slot -> !inventory.main.get(slot).isEmpty() && ItemStack.areItemsEqual(itemStack, inventory.main.get(slot)))
            .findFirst()
            .orElse(-1);
    }

    public static boolean isEquipped(PlayerEntity player, Item item) {
        return handItems(player).contains(item);
    }

    public static boolean has(PlayerEntity player, Class<?> baseClass) {
        return inventoryStream(player).anyMatch(stack -> baseClass.isInstance(stack.getItem()));
    }

    public static boolean has(PlayerEntity player, Item item, int min, int max) {
        return IntStream.range(min, max).mapToObj(inventory(player)::get).anyMatch(itemStack -> itemStack.getItem() == item);
    }

    public static boolean has(PlayerEntity player, Class<?>... classes) {
        return inventoryStream(player).anyMatch(itemStack -> Stream.of(classes).anyMatch(clazz -> clazz.isInstance(itemStack.getItem())));
    }

    public static boolean has(PlayerEntity player, Item item) {
        return inventoryStream(player).anyMatch(stack -> stack.getItem() == item);
    }
}
