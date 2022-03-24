package soulboundarmory.network.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.component.Components;
import soulboundarmory.module.gui.AbstractNode;
import soulboundarmory.network.BufferPacket;

/**
 A server-to-client packet that is sent to update the frozen state of an entity.
 <br><br>
 buffer: <br>
 - int (entity ID) <br>
 - boolean (frozen) <br>
 */
public class S2CFreeze extends BufferPacket {
    @Override
    @OnlyIn(Dist.CLIENT)
    protected void execute() {
        this.message.readEntity().map(Components.entityData::of).ifPresent(component -> {
            var frozen = this.message.readBoolean();
            component.freeze(this.player(), 0, frozen ? 1 : 0, 0);

            if (frozen) {
                component.tickDelta = AbstractNode.tickDelta();
                component.animationProgress = -1;
            }
        });
    }
}
