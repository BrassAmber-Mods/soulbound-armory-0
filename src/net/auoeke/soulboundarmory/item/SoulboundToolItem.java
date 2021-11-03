package net.auoeke.soulboundarmory.item;

import net.auoeke.soulboundarmory.capability.statistics.StatisticType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.auoeke.soulboundarmory.capability.soulbound.item.StorageType;

public interface SoulboundToolItem extends SoulboundItem {
    default int harvestLevel(LivingEntity user, ItemStack stack) {
        return user instanceof PlayerEntity ? StorageType.get(user, stack.getItem()).statistic(StatisticType.miningLevel).intValue() : 0;
    }
}
