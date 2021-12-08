package soulboundarmory.component.soulbound.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import soulboundarmory.client.gui.screen.SelectionTab;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.ComponentKey;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.tool.PickStorage;
import soulboundarmory.item.SoulboundToolItem;
import soulboundarmory.registry.SoulboundItems;

public class ToolComponent extends SoulboundComponent {
    public ToolComponent(PlayerEntity player) {
        super(player);

        this.store(new PickStorage(this, SoulboundItems.pick));
    }

    @Override
    public ComponentKey<PlayerEntity, ? extends SoulboundComponent> key() {
        return Components.tool;
    }

    @Override
    protected boolean isAcceptable(ItemStack stack) {
        return stack.getItem() instanceof SoulboundToolItem;
    }

    @Override
    public SoulboundTab selectionTab() {
        return new SelectionTab(Translations.guiToolSelection);
    }
}
