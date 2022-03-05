package soulboundarmory.network.client;

import soulboundarmory.lib.gui.widget.Widget;
import java.util.Optional;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.component.Components;
import soulboundarmory.network.BufferPacket;

/**
 A server-to-client packet that unlocks an item that was selected for the player's first time.
 <br><br>
 buffer: <br>
 - int (player) <br>
 - ItemStack (unlocked item) <br>
 */
public final class S2CUnlock extends BufferPacket {
    @Override
    protected void execute() {
        this.message.<AbstractClientPlayerEntity>readEntity().ifPresent(player -> {
            var marker = Components.marker.of(this.message.readItemStack());
            Components.entityData.of(player).unlockedStack = Optional.of(marker);
            marker.unlock();

            Widget.client.particleManager.addEmitter(player, SoulboundArmory.unlockParticle, 30);
            player.world.playSound(player.getX(), player.getY(), player.getZ(), SoulboundArmory.unlockAnimationSound, player.getSoundCategory(), 1, 1, false);

            if (player == Widget.player()) {
                Widget.gameRenderer.showFloatingItem(marker.stack);
            }
        });
    }
}
