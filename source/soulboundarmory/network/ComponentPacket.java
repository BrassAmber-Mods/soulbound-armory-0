package soulboundarmory.network;

import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.module.component.ComponentRegistry;

public abstract class ComponentPacket extends BufferPacket {
	protected abstract void execute(MasterComponent<?> component);

	@Override
	protected void execute() {
		this.execute((MasterComponent<?>) ComponentRegistry.findEntity(this.message.readIdentifier()).of(this.player()));
	}
}
