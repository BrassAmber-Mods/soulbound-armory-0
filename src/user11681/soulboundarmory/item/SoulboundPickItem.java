package user11681.soulboundarmory.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.text.Translation;

import static user11681.soulboundarmory.capability.statistics.StatisticType.experience;
import static user11681.soulboundarmory.capability.statistics.StatisticType.level;
import static user11681.soulboundarmory.item.SoulboundToolMaterial.SOULBOUND;

public class SoulboundPickItem extends PickaxeItem implements SoulboundToolItem {
    public SoulboundPickItem() {
        super(SOULBOUND, 0, -2.4F, new Settings().group(ItemGroup.TOOLS));
    }

    @Override
    public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
        return this.harvestLevel(player, stack);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (miner instanceof PlayerEntity && this.canMine(state, world, pos, (PlayerEntity) miner)) {
            PickStorage component = StorageType.pick.get(miner);
            int xp = Math.min(5, (int) state.getHardness(world, pos)) + state.getHarvestLevel();

            if (!world.isClient && component.incrementStatistic(experience, xp) && Capabilities.config.get(miner).levelupNotifications) {
                ((PlayerEntity) miner).sendMessage(new Translation(Translations.messageLevelUp.getKey(), stack.getName(), component.datum(level)), true);
            }
        }

        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return HashMultimap.create();
    }
}
