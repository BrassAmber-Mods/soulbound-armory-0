package user11681.soulboundarmory.capability.soulbound.player;

import net.minecraft.entity.player.PlayerEntity;
import user11681.soulboundarmory.capability.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.registry.SoulboundItems;

public class ToolCapability extends SoulboundCapability {
    public ToolCapability(PlayerEntity player) {
        super(player);

        this.store(new PickStorage(this, SoulboundItems.pick));
    }
}
