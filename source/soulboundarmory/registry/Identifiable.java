package soulboundarmory.registry;

import net.minecraft.util.Identifier;

public abstract class Identifiable {
	private Identifier id;

	public Identifier id() {
		return this.id;
	}

	public String string() {
		return this.id().toString();
	}
}
