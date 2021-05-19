package user11681.soulboundarmory.capability.soulbound.player;

import user11681.soulboundarmory.capability.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.SwordStorage;
import user11681.soulboundarmory.registry.SoulboundItems;

public class WeaponCapability extends SoulboundCapability {
    public WeaponCapability() {
        this.store(new DaggerStorage(this, SoulboundItems.dagger));
        this.store(new SwordStorage(this, SoulboundItems.sword));
        this.store(new GreatswordStorage(this, SoulboundItems.greatsword));
        this.store(new StaffStorage(this, SoulboundItems.staff));
    }
}
