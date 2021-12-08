package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ItemComponentPacket;
import soulboundarmory.skill.Skill;

/**
 A client-to-server packet for learning or upgrading a skill.

 <ul>buffer:
 <li>Identifier (storage type)</li>
 <li>Identifier (skill's registry name)</li>
 </ul>
 */
public final class C2SSkill extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        storage.upgrade(storage.skill(Skill.registry.getValue(this.message.readIdentifier())));
    }
}
