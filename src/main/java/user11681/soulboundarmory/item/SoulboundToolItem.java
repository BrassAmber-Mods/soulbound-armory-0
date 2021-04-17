package user11681.soulboundarmory.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import user11681.soulboundarmory.component.soulbound.item.StorageType;

import static user11681.soulboundarmory.component.statistics.StatisticType.miningLevel;

public interface SoulboundToolItem extends SoulboundItem {
    @Override
    default int getMiningLevel(final Tag<Item> tag, final BlockState state, final ItemStack stack, final LivingEntity user) {
        if (user instanceof PlayerEntity) {
            return StorageType.get(user, stack.getItem()).getStatistic(miningLevel).intValue();
        }

        return 0;
    }
}
