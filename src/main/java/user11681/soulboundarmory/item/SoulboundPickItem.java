package user11681.soulboundarmory.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlotType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.Attribute;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.text.Translation;
import user11681.soulboundarmory.util.MathUtil;

import static user11681.soulboundarmory.capability.statistics.StatisticType.experience;
import static user11681.soulboundarmory.capability.statistics.StatisticType.level;
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

            if (!world.isClientSide && component.incrementStatistic(experience, xp) && Capabilities.config.get(miner).getLevelupNotifications()) {
                ((PlayerEntity) miner).sendMessage(new Translation(Translations.messageLevelUp.getKey(), stack.getName(), component.getDatum(level)), true);
            }
        }

        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot) {
        return HashMultimap.create();
    }
}
