package soulboundarmory.component.soulbound.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.weapon.BigswordComponent;
import soulboundarmory.component.soulbound.item.weapon.DaggerComponent;
import soulboundarmory.component.soulbound.item.weapon.GreatswordComponent;
import soulboundarmory.component.soulbound.item.weapon.SwordComponent;
import soulboundarmory.component.soulbound.item.weapon.TridentComponent;
import soulboundarmory.item.SoulboundWeaponItem;
import soulboundarmory.module.component.EntityComponentKey;

public class MasterWeaponComponent extends MasterComponent<MasterWeaponComponent> {
    public MasterWeaponComponent(PlayerEntity player) {
        super(player);

        this.store(new DaggerComponent(this));
        this.store(new SwordComponent(this));
        this.store(new BigswordComponent(this));
        this.store(new GreatswordComponent(this));
        this.store(new TridentComponent(this));
    }

    @Override
    public EntityComponentKey<MasterWeaponComponent> key() {
        return Components.weapon;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return stack.getItem() instanceof SoulboundWeaponItem;
    }
}
