package user11681.soulboundarmory.network.server;

import net.minecraftforge.fml.network.NetworkEvent;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.ItemComponentPacket;
import user11681.soulboundarmory.skill.Skill;

public class C2SSkill implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        storage.upgrade(storage.skill(Skill.registry.getValue(SoulboundArmory.id(buffer.readString()))));
        // this.component.sync();
    }
}
