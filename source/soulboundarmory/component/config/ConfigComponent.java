package soulboundarmory.component.config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import soulboundarmory.config.Configuration;
import soulboundarmory.module.component.EntityComponent;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

public final class ConfigComponent implements EntityComponent<ConfigComponent> {
	public final PlayerEntity player;
	public boolean levelupNotifications;
	public boolean glint;

	public ConfigComponent(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public void spawn() {
		if (this.player.world.isClient) {
			Packets.serverConfig.send(new ExtendedPacketBuffer().writeBoolean(Configuration.Client.levelupNotifications).writeBoolean(Configuration.Client.enchantmentGlint));
		}
	}

	@Override
	public void serialize(NbtCompound tag) {}

	@Override
	public void deserialize(NbtCompound tag) {}
}
