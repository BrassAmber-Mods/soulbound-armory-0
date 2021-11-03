package net.auoeke.soulboundarmory.capability.soulbound.player;

import net.auoeke.soulboundarmory.item.SoulboundToolItem;
import net.auoeke.soulboundarmory.registry.SoulboundItems;
import net.auoeke.soulboundarmory.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.auoeke.soulboundarmory.capability.soulbound.item.tool.PickStorage;

public class ToolCapability extends SoulboundCapability {
    public ToolCapability(PlayerEntity player) {
        super(player);

        this.store(new PickStorage(this, SoulboundItems.pick));
    }

    @Override
    public boolean hasSoulboundItem() {
        return ItemUtil.has(this.entity, SoulboundToolItem.class);
    }
}
