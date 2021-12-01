package soulboundarmory.component.soulbound.player;

import soulboundarmory.item.SoulboundToolItem;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;
import soulboundarmory.component.soulbound.item.tool.PickStorage;

public class ToolComponent extends SoulboundComponent {
    public ToolComponent(PlayerEntity player) {
        super(player);

        this.store(new PickStorage(this, SoulboundItems.pick));
    }

    @Override
    public boolean hasSoulboundItem() {
        return ItemUtil.has(this.entity, SoulboundToolItem.class);
    }
}
