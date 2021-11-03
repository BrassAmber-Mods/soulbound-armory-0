package user11681.soulboundarmory.capability.soulbound.item.tool;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraftforge.common.ForgeMod;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.entity.SAAttributes;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.item.SoulboundToolItem;

import static user11681.soulboundarmory.capability.statistics.StatisticType.efficiency;
import static user11681.soulboundarmory.capability.statistics.StatisticType.miningLevel;
import static user11681.soulboundarmory.capability.statistics.StatisticType.reach;

public abstract class ToolStorage<T extends ItemStorage<T>> extends ItemStorage<T> {
    public ToolStorage(SoulboundCapability component, Item item) {
        super(component, item);
    }

    @Override
    public int getLevelXP(int level) {
        return this.canLevelUp()
            ? Configuration.instance().initialToolXP + (int) Math.round(4 * Math.pow(level, 1.25))
            : -1;
    }

    public Text miningLevelName() {
        return this.miningLevelName(this.statistic(miningLevel).intValue());
    }

    public Text miningLevelName(int level) {
        return switch (level) {
            case 0 -> Translations.miningLevelCoal;
            case 1 -> Translations.miningLevelIron;
            case 2 -> Translations.miningLevelDiamond;
            case 3 -> Translations.miningLevelObsidian;
            default -> new LiteralText("unknown");
        };

    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(SAAttributes.efficiency, new EntityAttributeModifier(SAAttributes.efficiencyUUID, "Tool modifier", this.attribute(efficiency), EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new EntityAttributeModifier(SAAttributes.reachUUID, "Tool modifier", this.attribute(reach), EntityAttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }

    @Override
    public Class<? extends SoulboundItem> itemClass() {
        return SoulboundToolItem.class;
    }
}
