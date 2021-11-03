package net.auoeke.soulboundarmory.client.keyboard;

import org.lwjgl.glfw.GLFW;
import net.auoeke.soulboundarmory.SoulboundArmoryClient;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.StorageType;

public class GUIKeyBinding extends KeyBindingBase {
    public GUIKeyBinding() {
        super("menu", GLFW.GLFW_KEY_R);
    }

    @Override
    protected void press() {
        ItemStorage<?> component = StorageType.firstMenuStorage(SoulboundArmoryClient.player());

        if (component != null) {
            component.openGUI();
        }
    }
}
