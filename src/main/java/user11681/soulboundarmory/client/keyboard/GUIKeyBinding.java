package user11681.soulboundarmory.client.keyboard;

import org.lwjgl.glfw.GLFW;
import user11681.soulboundarmory.SoulboundArmoryClient;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;

public class GUIKeyBinding extends SoulboundArmoryKeyBinding {
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
