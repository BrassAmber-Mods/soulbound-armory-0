package soulboundarmory.component.soulbound.item.tool;

import com.google.common.collect.Multimap;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeMod;
import soulboundarmory.client.gui.screen.AttributeTab;
import soulboundarmory.client.gui.screen.EnchantmentTab;
import soulboundarmory.client.gui.screen.SelectionTab;
import soulboundarmory.client.gui.screen.SkillTab;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.entity.Attributes;
import soulboundarmory.item.SoulboundToolMaterial;
import soulboundarmory.registry.Skills;

public abstract class ToolComponent<T extends ItemComponent<T>> extends ItemComponent<T> {
    public ToolComponent(SoulboundComponent<?> component) {
        super(component);

        this.skills.add(Skills.circumspection, Skills.enderPull);
    }

    public Text miningLevelName() {
        return this.miningLevelName(this.statistic(StatisticType.miningLevel).intValue());
    }

    public Text miningLevelName(int level) {
        return switch (level) {
            case 0 -> Translations.miningLevelCoal;
            case 1 -> Translations.miningLevelIron;
            case 2 -> Translations.miningLevelDiamond;
            case 3 -> Translations.miningLevelObsidian;
            default -> Text.of(String.valueOf(level));
        };
    }

    @Override
    public int getLevelXP(int level) {
        return this.canLevelUp()
            ? Configuration.instance().initialToolXP + (int) Math.round(4 * Math.pow(level, 1.25))
            : -1;
    }

    @Override
    public void mined(BlockState state, BlockPos position) {
        if (!this.isClient() && this.itemStack.isSuitableFor(state) && this.itemStack.getToolTypes().contains(state.getHarvestTool())) {
            var xp = Math.round(state.getHardness(this.player.world, position)) + state.getHarvestLevel();
            this.incrementStatistic(StatisticType.experience, state.calcBlockBreakingDelta(this.player, this.player.world, position) >= 1 ? Math.min(10, xp) : xp);
        }
    }

    @Override
    public double attributeTotal(StatisticType attribute) {
        var value = super.attributeTotal(attribute);
        return attribute == StatisticType.efficiency ? value + SoulboundToolMaterial.SOULBOUND.getMiningSpeedMultiplier() : value;
    }

    @Override
    public void attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), this.toolModifier(Attributes.reach, StatisticType.reach));
        }
    }

    @Override
    public List<SoulboundTab> tabs() {
        return List.of(new SelectionTab(Translations.guiToolSelection), new AttributeTab(), new EnchantmentTab(), new SkillTab());
    }
}
