package user11681.soulboundarmory.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import user11681.soulboundarmory.asm.mixin.entity.player.PlayerInventoryAccess;

public class ItemUtil {
    public static List<ItemStack> inventory(PlayerEntity player) {
        return inventoryStream(player).collect(Collectors.toList());
    }

    public static Stream<ItemStack> inventoryStream(PlayerEntity player) {
        return ((PlayerInventoryAccess) player.inventory).compartments().stream().flatMap(NonNullList::stream);
    }

    public static List<Item> handItems(LivingEntity player) {
        return Arrays.asList(player.getMainHandItem().getItem(), player.getOffhandItem().getItem());
    }

    public static ItemStack equippedStack(PlayerEntity player, Item... validItems) {
        for (ItemStack itemStack : player.getHandSlots()) {
            if (Arrays.asList(validItems).contains(itemStack.getItem())) {
                return itemStack;
            }
        }

        return null;
    }

    public static ItemStack equippedStack(PlayerInventory inventory, Class<?> clazz) {
        for (ItemStack itemStack : Arrays.asList(inventory.getCarried(), inventory.offhand.get(0))) {
            if (clazz.isInstance(itemStack.getItem())) {
                return itemStack;
            }
        }

        return null;
    }

    public static ItemStack getRequiredItemStack(PlayerEntity player, Class<?>... classes) {
        ItemStack itemStack = player.getMainHandItem();
        Item item = itemStack.getItem();

        for (Class<?> clazz : classes) {
            if (clazz.isInstance(item)) {
                return itemStack;
            }
        }

        return null;
    }

    public static int matchingSlot(PlayerInventory inventory, ItemStack itemStack) {
        for (int i = 0; i < inventory.items.size(); ++i) {
            if (!inventory.items.get(i).isEmpty() && ItemStack.isSameIgnoreDurability(itemStack, inventory.items.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public static boolean isEquipped(PlayerEntity player, Item item) {
        return handItems(player).contains(item);
    }

    public static boolean hasItem(PlayerEntity player, Class<?> baseClass) {
        return inventoryStream(player).anyMatch(baseClass::isInstance);
    }

    public static boolean hasItem(Item item, PlayerEntity player) {
        for (ItemStack itemStack : player.inventory.items) {
            if (itemStack.getItem() == item) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasItem(PlayerEntity player, Item item, int min, int max) {
        List<ItemStack> merged = user11681.soulboundarmory.util.ItemUtil.inventory(player);

        for (int i = min; i < max; i++) {
            ItemStack itemStack = merged.get(i);

            if (itemStack.getItem() == item) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasItem(PlayerEntity player, Class<?>... classes) {
        List<ItemStack> merged = user11681.soulboundarmory.util.ItemUtil.inventory(player);

        for (ItemStack itemStack : merged) {
            for (Class<?> clazz : classes) {
                if (clazz.isInstance(itemStack.getItem())) {
                    return true;
                }
            }
        }

        return false;
    }
}
