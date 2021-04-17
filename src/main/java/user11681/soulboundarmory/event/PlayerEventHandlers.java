package user11681.soulboundarmory.event;

import java.util.Collection;
import java.util.Collections;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
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
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.soulbound.player.SoulboundItemUtil;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.entity.SoulboundArmoryAttributes;
import user11681.soulboundarmory.item.SoulboundWeaponItem;
import user11681.soulboundarmory.registry.SoulboundItems;

import static user11681.soulboundarmory.component.statistics.StatisticType.level;
import static user11681.soulboundarmory.registry.Skills.ENDER_PULL;

public class PlayerEventHandlers implements CommonListenerInitializer {
    @Listener
    public static void onPlayerDrops(final PlayerDropInventoryEvent event) {
        final PlayerEntity player = event.getPlayer();

        if (!player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            for (final ComponentType<? extends SoulboundComponent> type : Components.soulboundComponents) {
                final ItemStorage<?> storage = type.get(player).getStorage();

                if (storage != null && storage.getDatum(level) >= Configuration.instance().preservationLevel) {
                    for (final DefaultedList<ItemStack> inventory : event.getDrops()) {
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

    @Listener
    public static void onPlayerCopy(final PlayerCopyEvent event) {
        final PlayerEntity original = event.getOldPlayer();
        final PlayerEntity player = event.getPlayer();

        for (final ComponentType<? extends SoulboundComponent> type : Components.soulboundComponents) {
            final SoulboundComponent originalComponent = type.get(original);
            final SoulboundComponent currentComponent = type.get(player);

            currentComponent.fromTag(originalComponent.toTag(new NbtCompound()));

            if (!player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
                final ItemStorage<?> storage = currentComponent.getStorage();

                if (storage != null && storage.getDatum(level) >= Configuration.instance().preservationLevel) {
                    player.giveItemStack(storage.getItemStack());
                }
            }
        }
    }

    @Listener
    public static void onUseBlock(final UseBlockEvent event) {
        final PlayerEntity player = event.getPlayer();
        final ItemStack mainStack = player.getMainHandStack();

        if (mainStack.getItem() instanceof SoulboundWeaponItem && mainStack != event.getItemStack()) {
            event.setFail();
        }
    }

    @Listener
    public static void onBlockBreakSpeed(final BlockBreakSpeedEvent event) {
        final PlayerEntity player = event.getPlayer();

        if (player != null) {
            event.setSpeed(event.getSpeed() * (float) player.getAttributeValue(SoulboundArmoryAttributes.GENERIC_EFFICIENCY));
        }
    }

    @Listener
    public static void onBlockDrop(final BlockDropEvent event) {
        final Entity entity = event.getMiner();

        if (entity instanceof PlayerEntity && event.getTool().getItem() == SoulboundItems.pick && Components.toolComponent.get(entity).getStorage().hasSkill(ENDER_PULL)) {
            event.setPos(entity.getBlockPos());
        }
    }

    @Listener(priority = 20)
    public static void onEntityItemPickup(final ItemPickupEvent event) {
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
