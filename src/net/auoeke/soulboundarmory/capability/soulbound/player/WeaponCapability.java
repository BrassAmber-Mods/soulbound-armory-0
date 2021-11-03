package net.auoeke.soulboundarmory.capability.soulbound.player;

import net.auoeke.soulboundarmory.capability.soulbound.item.weapon.DaggerStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.weapon.GreatswordStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.weapon.StaffStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.weapon.SwordStorage;
import net.auoeke.soulboundarmory.item.SoulboundWeaponItem;
import net.auoeke.soulboundarmory.registry.SoulboundItems;
import net.auoeke.soulboundarmory.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;

public class WeaponCapability extends SoulboundCapability {
    public WeaponCapability(PlayerEntity player) {
        super(player);

        this.store(new DaggerStorage(this, SoulboundItems.dagger));
        this.store(new SwordStorage(this, SoulboundItems.sword));
        this.store(new GreatswordStorage(this, SoulboundItems.greatsword));
        this.store(new StaffStorage(this, SoulboundItems.staff));
    }

    @Override
    public boolean hasSoulboundItem() {
        return ItemUtil.has(this.entity, SoulboundWeaponItem.class);
    }
}
