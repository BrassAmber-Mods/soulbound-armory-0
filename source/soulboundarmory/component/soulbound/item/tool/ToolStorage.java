package soulboundarmory.component.soulbound.item.tool;

import com.google.common.collect.Multimap;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.config.Configuration;
import soulboundarmory.entity.SAAttributes;
import soulboundarmory.item.SoulboundItem;
import soulboundarmory.item.SoulboundToolItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraftforge.common.ForgeMod;

public abstract class ToolStorage<T extends ItemStorage<T>> extends ItemStorage<T> {
    public ToolStorage(SoulboundComponent component, Item item) {
        super(component, item);
    }

    @Override
    public int getLevelXP(int level) {
        return this.canLevelUp()
            ? Configuration.instance().initialToolXP + (int) Math.round(4 * Math.pow(level, 1.25))
            : -1;
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
            default -> Text.of("unknown");
        };
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(SAAttributes.efficiency, new EntityAttributeModifier(SAAttributes.efficiencyUUID, "Tool modifier", this.doubleValue(StatisticType.efficiency), EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new EntityAttributeModifier(SAAttributes.reachUUID, "Tool modifier", this.doubleValue(StatisticType.reach), EntityAttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }

    @Override
    public Class<? extends SoulboundItem> itemClass() {
        return SoulboundToolItem.class;
    }
}
