package user11681.soulboundarmory.component.soulbound.player;

import net.minecraft.entity.player.PlayerEntity;
import user11681.soulboundarmory.component.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import user11681.soulboundarmory.registry.SoulboundItems;

public class WeaponSoulboundComponent extends SoulboundComponent<WeaponSoulboundComponent> {
    public WeaponSoulboundComponent(PlayerEntity player) {
        super(player);

        this.store(new DaggerStorage(this, SoulboundItems.dagger));
        this.store(new SwordStorage(this, SoulboundItems.sword));
        this.store(new GreatswordStorage(this, SoulboundItems.greatsword));
        this.store(new StaffStorage(this, SoulboundItems.staff));
    }
}
