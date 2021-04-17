package user11681.soulboundarmory.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.text.StringableText;
import user11681.usersmanual.math.MathUtil;

import static user11681.soulboundarmory.component.statistics.StatisticType.experience;
import static user11681.soulboundarmory.component.statistics.StatisticType.level;
import static user11681.soulboundarmory.item.ModToolMaterials.SOULBOUND;

;

public class SoulboundPickItem extends PickaxeItem implements SoulboundToolItem {
    public SoulboundPickItem() {
        super(SOULBOUND, 0, -2.4F, new Settings());
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (miner instanceof PlayerEntity && this.canMine(state, world, pos, (PlayerEntity) miner)) {
            PickStorage component = StorageType.pick.get(miner);
            ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());
            int xp = MathUtil.roundRandomly(Math.min(state.getHardness(world, pos), 5), world.random);

            if (entry != null) {
                xp += entry.getMiningLevel(FabricToolTags.PICKAXES);
            }

            if (!world.isClient && component.incrementStatistic(experience, xp) && Components.config.get(miner).getLevelupNotifications()) {
                ((PlayerEntity) miner).sendMessage(new StringableText(Translations.messageLevelUp.getKey(), stack.getName(), component.getDatum(level)), true);
            }
        }

        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return HashMultimap.create();
    }
}
