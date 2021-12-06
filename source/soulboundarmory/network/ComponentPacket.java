package soulboundarmory.network;

import soulboundarmory.component.ComponentRegistry;
import soulboundarmory.component.soulbound.player.SoulboundComponent;

public abstract class ComponentPacket extends BufferPacket {
    protected abstract void execute(SoulboundComponent component);

    @Override
    protected void execute() {
        this.execute((SoulboundComponent) ComponentRegistry.get(this.message.readIdentifier()).of(this.player()));
    }
}
