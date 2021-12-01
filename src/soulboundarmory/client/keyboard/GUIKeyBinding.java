package soulboundarmory.client.keyboard;

import soulboundarmory.SoulboundArmoryClient;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.StorageType;
import org.lwjgl.glfw.GLFW;

public class GUIKeyBinding extends KeyBindingBase {
    public GUIKeyBinding() {
        super("menu", GLFW.GLFW_KEY_R);
    }

    @Override
    protected void press() {
        StorageType.firstMenuStorage(SoulboundArmoryClient.player()).ifPresent(ItemStorage::openGUI);
    }
}
