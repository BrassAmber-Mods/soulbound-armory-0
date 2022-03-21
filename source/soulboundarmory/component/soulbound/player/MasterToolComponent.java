package soulboundarmory.component.soulbound.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.tool.PickComponent;
import soulboundarmory.item.SoulboundToolItem;
import soulboundarmory.lib.component.EntityComponentKey;

public class MasterToolComponent extends MasterComponent<MasterToolComponent> {
    public MasterToolComponent(PlayerEntity player) {
        super(player);

        this.store(new PickComponent(this));
    }

    @Override
    public EntityComponentKey<MasterToolComponent> key() {
        return Components.tool;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return stack.getItem() instanceof SoulboundToolItem;
    }
}
