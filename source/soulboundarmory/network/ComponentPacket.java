package soulboundarmory.network;

import soulboundarmory.module.component.ComponentRegistry;
import soulboundarmory.component.soulbound.player.MasterComponent;

public abstract class ComponentPacket extends BufferPacket {
    protected abstract void execute(MasterComponent<?> component);

    @Override
    protected void execute() {
        this.execute((MasterComponent<?>) ComponentRegistry.findEntity(this.message.readIdentifier()).of(this.player()));
    }
}
