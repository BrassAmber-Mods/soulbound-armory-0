package user11681.soulboundarmory.capability.soulbound.player;

import net.minecraft.entity.player.PlayerEntity;
import user11681.soulboundarmory.capability.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.SwordStorage;
import user11681.soulboundarmory.item.SoulboundWeaponItem;
import user11681.soulboundarmory.registry.SoulboundItems;
import user11681.soulboundarmory.util.ItemUtil;

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
