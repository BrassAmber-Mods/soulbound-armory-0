package net.auoeke.soulboundarmory.client.keyboard;

import net.auoeke.soulboundarmory.SoulboundArmoryClient;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.StorageType;
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
