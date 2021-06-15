package user11681.soulboundarmory.capability.soulbound.item.tool;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.client.gui.screen.tab.AttributeTab;
import user11681.soulboundarmory.client.gui.screen.tab.EnchantmentTab;
import user11681.soulboundarmory.client.gui.screen.tab.ScreenTab;
import user11681.soulboundarmory.client.gui.screen.tab.SelectionTab;
import user11681.soulboundarmory.client.gui.screen.tab.SkillTab;
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

    public ITextComponent getMiningLevelName() {
        return this.getMiningLevelName((int) this.attribute(miningLevel));
    }

    public ITextComponent getMiningLevelName(int level) {
        return switch (level) {
            case 0 -> Translations.miningLevelCoal;
            case 1 -> Translations.miningLevelIron;
            case 2 -> Translations.miningLevelDiamond;
            case 3 -> Translations.miningLevelObsidian;
            default -> new StringTextComponent("unknown");
        };

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<ScreenTab> tabs() {
        List<ScreenTab> tabs = new ReferenceArrayList<>();

        tabs.add(new SelectionTab(Translations.menuToolSelection, this.component, tabs));
        tabs.add(new AttributeTab(this.component, tabs));
        tabs.add(new EnchantmentTab(this.component, tabs));
        tabs.add(new SkillTab(this.component, tabs));

        return tabs;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(Multimap<Attribute, AttributeModifier> modifiers, EquipmentSlotType slot) {
        if (slot == EquipmentSlotType.MAINHAND) {
            modifiers.put(SAAttributes.efficiency, new AttributeModifier(SAAttributes.efficiencyUUID, "Tool modifier", this.attribute(efficiency), AttributeModifier.Operation.ADDITION));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(SAAttributes.reachUUID, "Tool modifier", this.attribute(reach), AttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }

    @Override
    public Class<? extends SoulboundItem> getBaseItemClass() {
        return SoulboundToolItem.class;
    }
}
