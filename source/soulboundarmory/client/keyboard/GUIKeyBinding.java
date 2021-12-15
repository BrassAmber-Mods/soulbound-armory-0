package soulboundarmory.client.keyboard;

import cell.client.gui.widget.Widget;
import java.util.stream.Stream;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import soulboundarmory.client.gui.screen.SoulboundScreen;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponent;

/**
 Open {@linkplain SoulboundScreen the menu} if one of the held items is soulbound or {@linkplain ItemComponent#canConsume consumable}.
 */
public class GUIKeyBinding extends KeyBindingBase {
    public GUIKeyBinding() {
        super("menu", GLFW.GLFW_KEY_R);
    }

    @Override
    protected void press() {
        Stream.of(Hand.values()).anyMatch(hand -> Components.soulbound(Widget.minecraft.player).anyMatch(component -> component.tryOpenGUI(hand)));
    }
}
