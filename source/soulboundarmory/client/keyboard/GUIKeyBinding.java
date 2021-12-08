package soulboundarmory.client.keyboard;

import cell.client.gui.CellElement;
import org.lwjgl.glfw.GLFW;
import soulboundarmory.client.gui.screen.SoulboundScreen;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;

/**
 Open {@linkplain SoulboundScreen the menu} if one of the held items is soulbound or {@linkplain ItemComponent#canConsume consumable}.
 */
public class GUIKeyBinding extends KeyBindingBase {
    public GUIKeyBinding() {
        super("menu", GLFW.GLFW_KEY_R);
    }

    @Override
    protected void press() {
        Components.soulbound(CellElement.minecraft.player).anyMatch(SoulboundComponent::tryOpenGUI);
    }
}
