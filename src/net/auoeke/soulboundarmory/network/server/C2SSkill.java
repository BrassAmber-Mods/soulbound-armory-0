package net.auoeke.soulboundarmory.network.server;

import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.network.ItemComponentPacket;
import net.minecraftforge.fml.network.NetworkEvent;
import net.auoeke.soulboundarmory.SoulboundArmory;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.skill.Skill;

public class C2SSkill implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        storage.upgrade(storage.skill(Skill.registry.getValue(SoulboundArmory.id(buffer.readUtf()))));
        // this.component.sync();
    }
}
