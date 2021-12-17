package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.network.ItemComponentPacket;
import soulboundarmory.skill.Skill;

/**
 A client-to-server packet for learning or upgrading a skill.
<br><br>
 buffer: <br>
 - Identifier (item component type) <br>
 - Identifier (skill's registry name) <br>
 */
public final class C2SSkill extends ItemComponentPacket {
    @Override
    public void execute(ItemComponent<?> component) {
        component.upgrade(component.skill(Skill.registry.getValue(this.message.readIdentifier())));
    }
}
