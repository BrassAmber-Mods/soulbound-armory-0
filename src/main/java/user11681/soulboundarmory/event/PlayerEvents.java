package user11681.soulboundarmory.event;

import java.util.Collection;
import java.util.Collections;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.collection.NonNullList;
import net.minecraft.world.GameRules;
import user11681.anvil.entrypoint.CommonListenerInitializer;
import user11681.anvil.event.Listener;
import user11681.anvilevents.event.block.BlockDropEvent;
import user11681.anvilevents.event.entity.player.BlockBreakSpeedEvent;
import user11681.anvilevents.event.entity.player.ItemPickupEvent;
import user11681.anvilevents.event.entity.player.PlayerCopyEvent;
import user11681.anvilevents.event.entity.player.PlayerDropInventoryEvent;
import user11681.anvilevents.event.entity.player.PlayerTickEvent;
import user11681.anvilevents.event.entity.player.UseBlockEvent;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundItemUtil;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.entity.SAAttributes;
import user11681.soulboundarmory.item.SoulboundWeaponItem;
import user11681.soulboundarmory.registry.SoulboundItems;

import static user11681.soulboundarmory.capability.statistics.StatisticType.level;
import static user11681.soulboundarmory.registry.Skills.enderPull;

public class PlayerEvents implements CommonListenerInitializer {
    @SubscribeEvent
    public static void onPlayerDrops(PlayerDropInventoryEvent event) {
        final PlayerEntity player = event.getPlayer();

        if (!player.level.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            for (ComponentType<? extends SoulboundCapability> type : Capabilities.soulboundCapabilities) {
                final ItemStorage<?> storage = type.get(player).getStorage();

                if (storage != null && storage.getDatum(level) >= Configuration.instance().preservationLevel) {
                    for (NonNullList<ItemStack> inventory : event.getDrops()) {
                        for (int slot = 0, inventorySize = inventory.size(); slot < inventorySize; slot++) {
                            final ItemStack itemStack = inventory.get(slot);

                            if (storage.getBaseItemClass().isInstance(itemStack.getItem()) && SoulboundItemUtil.addItemStack(itemStack, player)) {
                                inventory.set(slot, ItemStack.EMPTY);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCopy(PlayerCopyEvent event) {
        final PlayerEntity original = event.getOldPlayer();
        final PlayerEntity player = event.getPlayer();

        for (ComponentType<? extends SoulboundCapability> type : Capabilities.soulboundCapabilities) {
            final SoulboundCapability originalComponent = type.get(original);
            final SoulboundCapability currentComponent = type.get(player);

            currentComponent.fromTag(originalComponent.toTag(new CompoundNBT()));

            if (!player.level.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
                final ItemStorage<?> storage = currentComponent.storage();

                if (storage != null && storage.getDatum(level) >= Configuration.instance().preservationLevel) {
                    player.giveItemStack(storage.getItemStack());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onUseBlock(UseBlockEvent event) {
        final PlayerEntity player = event.getPlayer();
        final ItemStack mainStack = player.getMainHandStack();

        if (mainStack.getItem() instanceof SoulboundWeaponItem && mainStack != event.getItemStack()) {
            event.setFail();
        }
    }

    @SubscribeEvent
    public static void onBlockBreakSpeed(BlockBreakSpeedEvent event) {
        final PlayerEntity player = event.getPlayer();

        if (player != null) {
            event.setSpeed(event.getSpeed() * (float) player.getAttributeValue(SAAttributes.efficiency));
        }
    }

    @SubscribeEvent
    public static void onBlockDrop(BlockDropEvent event) {
        final Entity entity = event.getMiner();

        if (entity instanceof PlayerEntity && event.getTool().getItem() == SoulboundItems.pick && Capabilities.tool.get(entity).getStorage().hasSkill(enderPull)) {
            event.setPos(entity.getBlockPos());
        }
    }

    @SubscribeEvent(priority = 20)
    public static void onEntityItemPickup(ItemPickupEvent event) {
/*
        final ItemEntity entity = event.getItemEntity();
        final ItemStack stack = entity.getStack();
        final PlayerEntity player = event.getPlayer();

        player.sendPickup(entity, stack.getCount());
        SoulboundItemUtil.addItemStack(stack, player);

        event.setFail();
*/
    }

    @Override
    public Collection<Class<?>> get() {
        return Collections.singleton(this.getClass());
    }
}
