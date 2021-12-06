package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ItemComponentPacket;
import soulboundarmory.skill.Skill;

public final class C2SSkill extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        storage.upgrade(storage.skill(Skill.registry.getValue(this.message.readIdentifier())));
    }
}
