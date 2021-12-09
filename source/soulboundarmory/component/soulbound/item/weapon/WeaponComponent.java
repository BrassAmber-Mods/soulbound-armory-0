package soulboundarmory.component.soulbound.item.weapon;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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

public abstract class WeaponComponent<T extends ItemComponent<T>> extends ItemComponent<T> {
    protected double criticalStrikeProgress;

    public WeaponComponent(SoulboundComponent component) {
        super(component);
    }

    @Override
    public Item consumableItem() {
        return Items.WOODEN_SWORD;
    }

    @Override
    public int getLevelXP(int level) {
        return this.canLevelUp()
            ? Configuration.instance().initialWeaponXP + 3 * (int) Math.round(Math.pow(level, 1.65))
            : -1;
    }

    /**
     @return true if the hit is critical.
     */
    public boolean hit() {
        this.criticalStrikeProgress += this.doubleValue(StatisticType.criticalStrikeRate);

        if (this.criticalStrikeProgress >= 1) {
            this.criticalStrikeProgress--;

            return true;
        }

        return false;
    }

    @Override
    public List<SoulboundTab> tabs() {
        return List.of(new SelectionTab(Translations.guiWeaponSelection), new AttributeTab(), new EnchantmentTab(), new SkillTab());
    }
}
