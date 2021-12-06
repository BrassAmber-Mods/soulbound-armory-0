package soulboundarmory.component.soulbound.player;

import soulboundarmory.component.ComponentKey;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.weapon.DaggerStorage;
import soulboundarmory.component.soulbound.item.weapon.GreatswordStorage;
import soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import soulboundarmory.item.SoulboundWeaponItem;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;

public class WeaponComponent extends SoulboundComponent {
    public WeaponComponent(PlayerEntity player) {
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

    @Override
    protected ComponentKey<PlayerEntity, ? extends SoulboundComponent> key() {
        return Components.weapon;
    }
}
