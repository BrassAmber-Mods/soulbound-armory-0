package soulboundarmory.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javax.annotation.Nullable;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.text.Translation;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class SoulboundPickItem extends PickaxeItem implements SoulboundToolItem {
    public SoulboundPickItem() {
        super(SoulboundToolMaterial.SOULBOUND, 0, -2.4F, new Properties().group(ItemGroup.TOOLS));
    }

    @Override
    public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
        return this.harvestLevel(player, stack);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (miner instanceof PlayerEntity player && this.canPlayerBreakBlockWhileHolding(state, world, pos, (PlayerEntity) miner)) {
            var component = StorageType.pick.get(miner);
            var xp = Math.min(5, (int) state.getBlockHardness(world, pos)) + state.getHarvestLevel();

            if (!world.isRemote && component.incrementStatistic(StatisticType.experience, xp) && Components.config.of(miner).levelupNotifications) {
                player.sendStatusMessage(new Translation(Translations.messageLevelUp.getKey(), stack.getDisplayName(), component.datum(StatisticType.level)), true);
            }
        }

        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot) {
        return HashMultimap.create();
    }
}
