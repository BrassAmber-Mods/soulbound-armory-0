package user11681.soulboundarmory.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;

import static user11681.soulboundarmory.capability.statistics.StatisticType.miningLevel;

public interface SoulboundToolItem extends SoulboundItem {
    default int harvestLevel(LivingEntity user, ItemStack stack) {
        return user instanceof PlayerEntity ? StorageType.get(user, stack.getItem()).statistic(miningLevel).intValue() : 0;
    }
}
