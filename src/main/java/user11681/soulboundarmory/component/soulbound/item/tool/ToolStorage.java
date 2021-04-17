package user11681.soulboundarmory.component.soulbound.item.tool;

import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import user11681.soulboundarmory.client.gui.screen.tab.AttributeTab;
import user11681.soulboundarmory.client.gui.screen.tab.EnchantmentTab;
import user11681.soulboundarmory.client.gui.screen.tab.SelectionTab;
import user11681.soulboundarmory.client.gui.screen.tab.SkillTab;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.entity.SoulboundArmoryAttributes;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.item.SoulboundToolItem;
import user11681.usersmanual.client.gui.screen.ScreenTab;

import static user11681.soulboundarmory.component.statistics.StatisticType.efficiency;
import static user11681.soulboundarmory.component.statistics.StatisticType.miningLevel;
import static user11681.soulboundarmory.component.statistics.StatisticType.reach;

public abstract class ToolStorage<T extends ItemStorage<T>> extends ItemStorage<T> {
    public ToolStorage(final SoulboundComponent component, final Item item) {
        super(component, item);
    }

    @Override
    public int getLevelXP(final int level) {
        return this.canLevelUp()
                ? Configuration.instance().initialToolXP + (int) Math.round(4 * Math.pow(level, 1.25))
                : -1;
    }

    public Text getMiningLevelName() {
        return this.getMiningLevelName((int) this.getAttribute(miningLevel));
    }

    public Text getMiningLevelName(final int level) {
        switch (level) {
            case 0:
                return Translations.miningLevelCoal;
            case 1:
                return Translations.miningLevelIron;
            case 2:
                return Translations.miningLevelDiamond;
            case 3:
                return Translations.miningLevelObsidian;
        }

        return new LiteralText("unknown");
    }

    @Environment(EnvType.CLIENT)
    public List<ScreenTab> getTabs() {
        final List<ScreenTab> tabs = new ArrayList<>();

        tabs.add(new SelectionTab(Translations.menuToolSelection, this.component, tabs));
        tabs.add(new AttributeTab(this.component, tabs));
        tabs.add(new EnchantmentTab(this.component, tabs));
        tabs.add(new SkillTab(this.component, tabs));

        return tabs;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(final Multimap<EntityAttribute, EntityAttributeModifier> modifiers, final EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(SoulboundArmoryAttributes.GENERIC_EFFICIENCY, new EntityAttributeModifier(SoulboundArmoryAttributes.EFFICIENCY_MODIFIER_ID, "Tool modifier", this.getAttribute(efficiency), EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(ReachEntityAttributes.REACH, new EntityAttributeModifier(SoulboundArmoryAttributes.REACH_MODIFIER_UUID, "Tool modifier", this.getAttribute(reach), EntityAttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }

    @Override
    public Class<? extends SoulboundItem> getBaseItemClass() {
        return SoulboundToolItem.class;
    }
}
