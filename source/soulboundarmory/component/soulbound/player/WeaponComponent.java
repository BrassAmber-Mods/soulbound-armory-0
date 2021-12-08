package soulboundarmory.component.soulbound.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import soulboundarmory.client.gui.screen.SelectionTab;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.ComponentKey;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.weapon.DaggerComponent;
import soulboundarmory.component.soulbound.item.weapon.GreatswordComponent;
import soulboundarmory.component.soulbound.item.weapon.StaffComponent;
import soulboundarmory.component.soulbound.item.weapon.SwordComponent;
import soulboundarmory.item.SoulboundWeaponItem;
import soulboundarmory.registry.SoulboundItems;

public class WeaponComponent extends SoulboundComponent {
    public WeaponComponent(PlayerEntity player) {
        super(player);

        this.store(new DaggerComponent(this, SoulboundItems.dagger));
        this.store(new SwordComponent(this, SoulboundItems.sword));
        this.store(new GreatswordComponent(this, SoulboundItems.greatsword));
        this.store(new StaffComponent(this, SoulboundItems.staff));
    }

    @Override
    public ComponentKey<PlayerEntity, ? extends SoulboundComponent> key() {
        return Components.weapon;
    }

    @Override
    protected boolean isAcceptable(ItemStack stack) {
        return stack.getItem() instanceof SoulboundWeaponItem;
    }

    @Override
    public SoulboundTab selectionTab() {
        return new SelectionTab(Translations.guiWeaponSelection);
    }
}
