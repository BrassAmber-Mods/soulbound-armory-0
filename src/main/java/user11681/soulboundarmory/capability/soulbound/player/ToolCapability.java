package user11681.soulboundarmory.capability.soulbound.player;

import net.minecraft.entity.player.PlayerEntity;
import user11681.soulboundarmory.capability.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.item.SoulboundToolItem;
import user11681.soulboundarmory.registry.SoulboundItems;
import user11681.soulboundarmory.util.ItemUtil;

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
