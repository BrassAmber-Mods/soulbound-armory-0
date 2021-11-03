package net.auoeke.soulboundarmory.capability.soulbound.item.weapon;

import net.auoeke.soulboundarmory.config.Configuration;
import net.auoeke.soulboundarmory.item.SoulboundItem;
import net.auoeke.soulboundarmory.item.SoulboundWeaponItem;
import net.minecraft.item.Item;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.soulbound.player.SoulboundCapability;

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
