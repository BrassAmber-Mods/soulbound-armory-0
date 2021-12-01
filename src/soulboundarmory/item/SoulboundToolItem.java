package soulboundarmory.item;

import soulboundarmory.component.statistics.StatisticType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import soulboundarmory.component.soulbound.item.StorageType;

public interface SoulboundToolItem extends SoulboundItem {
    default int harvestLevel(LivingEntity user, ItemStack stack) {
        return user instanceof PlayerEntity ? StorageType.get(user, stack.getItem()).get().statistic(StatisticType.miningLevel).intValue() : 0;
    }
}
