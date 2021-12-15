package soulboundarmory.network.client;

import cell.client.gui.widget.Widget;
import soulboundarmory.component.Components;
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
    protected void execute() {
        Components.entityData.nullable(this.message.readEntity()).ifPresent(component -> {
            var frozen = this.message.readBoolean();
            component.freeze(this.player(), 0, frozen ? 1 : 0, 0);

            if (frozen) {
                component.tickDelta = Widget.tickDelta();
                component.animationProgress = -1;
            }
        });
    }
}
