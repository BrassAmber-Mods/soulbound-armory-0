package soulboundarmory.component.soulbound.item.weapon;

import net.minecraft.item.Item;
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
}
