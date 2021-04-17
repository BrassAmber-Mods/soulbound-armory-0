package user11681.soulboundarmory.component.soulbound.player;

import net.minecraft.entity.player.PlayerEntity;
import user11681.soulboundarmory.component.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.registry.SoulboundItems;

public class ToolSoulboundComponent extends SoulboundComponent<ToolSoulboundComponent> {
    public ToolSoulboundComponent(final PlayerEntity player) {
        super(player);

        this.store(new PickStorage(this, SoulboundItems.pick));
    }
}
