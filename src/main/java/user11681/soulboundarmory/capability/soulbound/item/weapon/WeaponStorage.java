package user11681.soulboundarmory.capability.soulbound.item.weapon;

import net.minecraft.item.Item;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.item.SoulboundWeaponItem;

public abstract class WeaponStorage<T extends ItemStorage<T>> extends ItemStorage<T> {
    public WeaponStorage(SoulboundCapability component, Item item) {
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
}
