package soulboundarmory.event;

import cell.client.gui.widget.Widget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.util.ItemUtil;

@EventBusSubscriber(value = Dist.CLIENT, modid = SoulboundArmory.ID)
public final class ClientEvents {
    @SubscribeEvent
    public static void scroll(InputEvent.MouseScrollEvent event) {
        if (Screen.hasAltDown()) {
            var player = Widget.player();

            if (player != null && player.world != null) {
                var staff = ItemComponentType.staff.of(player);

                if (ItemUtil.handStacks(player).anyMatch(staff::accepts)) {
                    var dy = (int) event.getMouseY();

                    if (dy != 0) {
                        staff.cycleSpells(-dy);
                        Widget.hud.setOverlayMessage(Translations.hudSpell.format(staff.spell()), false);

                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
