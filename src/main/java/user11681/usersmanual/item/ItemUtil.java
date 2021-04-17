package user11681.usersmanual.item;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import user11681.usersmanual.collections.CollectionUtil;
import user11681.usersmanual.asm.duck.inventory.CombinedInventoryDuck;

public class ItemUtil {
    public static List<ItemStack> getCombinedSingleInventory(final PlayerEntity player) {
        return CollectionUtil.merge(getCombinedInventory(player));
    }

    public static List<DefaultedList<ItemStack>> getCombinedInventory(final PlayerEntity player) {
        return ((CombinedInventoryDuck) player.inventory).getCombinedInventory();
    }

    public static ItemStack getEquippedItemStack(final PlayerEntity player, final Item... validItems) {
        for (final ItemStack itemStack : player.getItemsHand()) {
            if (Arrays.asList(validItems).contains(itemStack.getItem())) {
                return itemStack;
            }
        }

        return null;
    }

    public static ItemStack getEquippedItemStack(final PlayerInventory inventory, final Class<?> clazz) {
        for (final ItemStack itemStack : Arrays.asList(inventory.getMainHandStack(), inventory.offHand.get(0))) {
            if (clazz.isInstance(itemStack.getItem())) {
                return itemStack;
            }
        }

        return null;
    }

    public static ItemStack getRequiredItemStack(final PlayerEntity player, final Class<?>... classes) {
        final ItemStack itemStack = player.getMainHandStack();
        final Item item = itemStack.getItem();

        for (final Class<?> clazz : classes) {
            if (clazz.isInstance(item)) {
                return itemStack;
            }
        }

        return null;
    }

    public static boolean hasItem(final Item item, final PlayerEntity player) {
        for (final ItemStack itemStack : player.inventory.main) {
            if (itemStack.getItem() == item) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasItem(final PlayerEntity player, final Item item, final int min, final int max) {
        final List<ItemStack> merged = getCombinedSingleInventory(player);

        for (int i = min; i < max; i++) {
            final ItemStack itemStack = merged.get(i);

            if (itemStack.getItem() == item) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasItem(final PlayerEntity player, final Class<?>... classes) {
        final List<ItemStack> merged = getCombinedSingleInventory(player);

        for (final ItemStack itemStack : merged) {
            for (final Class<?> clazz : classes) {
                if (clazz.isInstance(itemStack.getItem())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static int getSlotFor(final PlayerInventory inventory, final ItemStack itemStack) {
        for (int i = 0; i < inventory.main.size(); ++i) {
            if (!inventory.main.get(i).isEmpty() && ItemStack.areItemsEqualIgnoreDamage(itemStack, inventory.main.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public static List<Item> getHandItems(final PlayerEntity player) {
        return ImmutableList.of(player.getMainHandStack().getItem(), player.getOffHandStack().getItem());
    }

    public static boolean isItemEquipped(final PlayerEntity player, final Item item) {
        return getHandItems(player).contains(item);
    }
}