package transfarmer.soulboundarmory.capability.soulbound;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import transfarmer.soulboundarmory.capability.config.PlayerConfigProvider;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;
import transfarmer.soulboundarmory.util.CollectionUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;

public class SoulItemHelper {
    public static final UUID ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final UUID REACH_DISTANCE_UUID = UUID.fromString("CD407CC4-2214-4ECA-B4B6-7DCEE2DABA33");
    private static boolean datumEquality;

    public static IItemCapability getCapability(final Class<? extends IItemCapability> cls, final EntityPlayer player) {
        if (cls == IWeapon.class) {
            return WeaponProvider.get(player);
        } else if (cls == IItemCapability.class) {
            return ToolProvider.get(player);
        }

        return null;
    }

    public static ICapabilityEnchantable getCapability(final EntityPlayer player, @Nullable Item item) {
        if (item == null) {
            item = player.getHeldItemMainhand().getItem();

            if (!(item instanceof ISoulItem)) {
                item = player.getHeldItemOffhand().getItem();
            }
        }

        return item instanceof ItemSoulWeapon || item == Items.WOODEN_SWORD
                ? WeaponProvider.get(player)
                : item instanceof IItemSoulTool || item == Items.WOODEN_PICKAXE
                ? ToolProvider.get(player)
                : null;
    }

    public static ICapabilityEnchantable getCapability(final EntityPlayer player, ItemStack itemStack) {
        return getCapability(player, itemStack.getItem());
    }

    public static boolean isSoulWeaponEquipped(final EntityPlayer player) {
        return player.getHeldItemMainhand().getItem() instanceof ItemSoulWeapon
                || player.getHeldItemOffhand().getItem() instanceof ItemSoulWeapon;
    }

    public static boolean addItemStack(final ItemStack itemStack, final EntityPlayer player, boolean hasReservedSlot) {
        final InventoryPlayer inventory = player.inventory;

        if (!(itemStack.getItem() instanceof ISoulItem)) {
            hasReservedSlot = false;
        }

        if (!hasReservedSlot) {
            final int weaponBoundSlot = WeaponProvider.get(player).getBoundSlot();
            final int toolBoundSlot = ToolProvider.get(player).getBoundSlot();
            final int slot = inventory.storeItemStack(itemStack);
            final int size = inventory.mainInventory.size();
            final List<ItemStack> mergedInventory = CollectionUtil.fromCollections(NonNullList.create(), inventory.mainInventory, inventory.offHandInventory);

            if (slot != -1) {
                final ItemStack slotStack = slot != 40 ? mergedInventory.get(slot) : mergedInventory.get(size);
                final int transferred = Math.min(slotStack.getMaxStackSize() - slotStack.getCount(), itemStack.getCount());

                itemStack.setCount(itemStack.getCount() - transferred);
                slotStack.setCount(slotStack.getCount() + transferred);

                if (!itemStack.isEmpty()) {
                    return addItemStack(itemStack, player, false);
                }

                return true;
            }

            for (int index = 0; index < mergedInventory.size(); index++) {
                if (index != weaponBoundSlot && index != toolBoundSlot && mergedInventory.get(index).isEmpty()) {
                    if (index != size) {
                        return inventory.add(index, itemStack);
                    }

                    if (player.getHeldItemOffhand().isEmpty() && PlayerConfigProvider.get(player).getAddToOffhand()) {
                        inventory.setInventorySlotContents(size + 4, itemStack);
                        itemStack.setCount(0);
                    }
                }
            }

            return false;
        }

        final int boundSlot = getCapability(player, itemStack.getItem()).getBoundSlot();

        if (boundSlot >= 0 && inventory.getStackInSlot(boundSlot).isEmpty()) {
            return inventory.add(boundSlot, itemStack);
        }

        return addItemStack(itemStack, player, false);
    }

    public static boolean addItemStack(final ItemStack itemStack, final EntityPlayer player) {
        return addItemStack(itemStack, player, true);
    }

    public static boolean areDataEqual(ItemStack itemStack0, ItemStack itemStack1) {
        datumEquality = true;

        itemStack0.getAttributeModifiers(MAINHAND).forEach((String key, AttributeModifier modifier0) -> {
            if (!datumEquality) return;

            itemStack1.getAttributeModifiers(MAINHAND).get(key).forEach((AttributeModifier modifier1) -> {
                if (!datumEquality) return;

                if (!modifier0.getID().equals(modifier1.getID()) || modifier0.getAmount() != modifier1.getAmount()) {
                    datumEquality = false;
                }
            });
        });

        if (datumEquality) {
            final NBTTagList enchantmentTagList0 = itemStack0.getEnchantmentTagList();
            final NBTTagList enchantmentTagList1 = itemStack1.getEnchantmentTagList();
            final int listEntries = Math.max(enchantmentTagList0.tagCount(), enchantmentTagList1.tagCount());

            for (int i = 0; i < listEntries; i++) {
                final NBTTagCompound tagCompound0 = enchantmentTagList0.getCompoundTagAt(i);
                final NBTTagCompound tagCompound1 = enchantmentTagList1.getCompoundTagAt(i);

                if (tagCompound0.getSize() == tagCompound1.getSize()) {
                    final int compoundEntries = Math.max(tagCompound0.getSize(), tagCompound1.getSize());

                    for (int j = 0; j < compoundEntries; j++) {
                        for (final String key : tagCompound1.getKeySet()) {
                            if (tagCompound0.getShort(key) != tagCompound1.getShort(key)) {
                                return false;
                            }
                        }
                    }
                } else {
                    return false;
                }
            }
        }

        return datumEquality;
    }

    public static boolean hasSoulWeapon(final EntityPlayer player) {
        final ItemStack[] inventory = player.inventory.mainInventory.toArray(new ItemStack[player.inventory.getSizeInventory() + 1]);
        inventory[player.inventory.getSizeInventory()] = player.getHeldItemOffhand();

        for (final ItemStack itemStack : inventory) {
            if (itemStack != null && itemStack.getItem() instanceof ItemSoulWeapon) {
                return true;
            }
        }

        return false;
    }

    public static void removeSoulWeapons(final EntityPlayer player) {
        for (final ItemStack itemStack : player.inventory.mainInventory) {
            if (itemStack.getItem() instanceof ItemSoulWeapon) {
                player.inventory.deleteStack(itemStack);
            }
        }
    }
}
