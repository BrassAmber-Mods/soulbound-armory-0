package user11681.soulboundarmory.network.server;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.ItemComponentPacket;
import user11681.soulboundarmory.skill.Skill;

public class C2SSkill implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        storage.upgrade(storage.skill(Skill.registry.getValue(buffer.readString())));
        // this.component.sync();
    }
}
