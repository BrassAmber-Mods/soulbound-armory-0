package soulboundarmory.network.server;

import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.ItemComponentPacket;
import net.minecraftforge.fml.network.NetworkEvent;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.skill.Skill;

public class C2SSkill implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        storage.upgrade(storage.skill(Skill.registry.getValue(SoulboundArmory.id(buffer.readUtf()))));
        // this.component.sync();
    }
}
