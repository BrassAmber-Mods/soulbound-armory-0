package soulboundarmory.item;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.StatisticType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface SoulboundToolItem extends SoulboundItem {
    default int harvestLevel(LivingEntity user, ItemStack stack) {
        return user instanceof PlayerEntity ? ItemComponent.get(user, stack).get().statistic(StatisticType.miningLevel).intValue() : 0;
    }
}
