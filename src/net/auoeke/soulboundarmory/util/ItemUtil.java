package net.auoeke.soulboundarmory.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.auoeke.soulboundarmory.asm.mixin.entity.player.PlayerInventoryAccess;

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

    public static ItemStack equippedStack(PlayerEntity player, Item... validItems) {
        for (ItemStack itemStack : player.getItemsHand()) {
            if (Arrays.asList(validItems).contains(itemStack.getItem())) {
                return itemStack;
            }
        }

        return null;
    }

    public static ItemStack equippedStack(PlayerInventory inventory, Class<?> clazz) {
        for (ItemStack itemStack : Arrays.asList(inventory.getMainHandStack(), inventory.offHand.get(0))) {
            if (clazz.isInstance(itemStack.getItem())) {
                return itemStack;
            }
        }

        return null;
    }

    public static ItemStack getRequiredItemStack(PlayerEntity player, Class<?>... classes) {
        ItemStack itemStack = player.getMainHandStack();
        Item item = itemStack.getItem();

        for (Class<?> clazz : classes) {
            if (clazz.isInstance(item)) {
                return itemStack;
            }
        }

        return null;
    }

    public static int matchingSlot(PlayerInventory inventory, ItemStack itemStack) {
        return IntStream
            .range(0, inventory.main.size())
            .filter(slot -> !inventory.main.get(slot).isEmpty() && ItemStack.areItemsEqualIgnoreDamage(itemStack, inventory.main.get(slot)))
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
        List<ItemStack> merged = inventory(player);

        return IntStream.range(min, max).mapToObj(merged::get).anyMatch(itemStack -> itemStack.getItem() == item);
    }

    public static boolean has(PlayerEntity player, Class<?>... classes) {
        return inventoryStream(player).anyMatch(itemStack -> Stream.of(classes).anyMatch(clazz -> clazz.isInstance(itemStack.getItem())));
    }

    public static boolean has(PlayerEntity player, Item item) {
        return inventoryStream(player).anyMatch(stack -> stack.getItem() == item);
    }
}
