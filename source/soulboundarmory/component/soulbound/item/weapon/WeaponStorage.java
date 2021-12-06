package soulboundarmory.component.soulbound.item.weapon;

import java.util.List;
import net.minecraft.item.Item;
import soulboundarmory.client.gui.screen.AttributeTab;
import soulboundarmory.client.gui.screen.EnchantmentTab;
import soulboundarmory.client.gui.screen.SelectionTab;
import soulboundarmory.client.gui.screen.SkillTab;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.item.SoulboundItem;
import soulboundarmory.item.SoulboundWeaponItem;

public abstract class WeaponStorage<T extends ItemStorage<T>> extends ItemStorage<T> {
    protected double criticalStrikeProgress;

    public WeaponStorage(SoulboundComponent component, Item item) {
        super(component, item);
    }

    @Override
    public int getLevelXP(int level) {
        return this.canLevelUp()
            ? Configuration.instance().initialWeaponXP + 3 * (int) Math.round(Math.pow(level, 1.65))
            : -1;
    }

    @Override
    public Class<? extends SoulboundItem> itemClass() {
        return SoulboundWeaponItem.class;
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
    protected List<SoulboundTab> tabs() {
        return List.of(new SelectionTab(Translations.guiWeaponSelection), new AttributeTab(), new EnchantmentTab(), new SkillTab());
    }
}
