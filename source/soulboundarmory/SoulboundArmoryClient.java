package soulboundarmory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import soulboundarmory.client.keyboard.GUIKeyBinding;

public final class SoulboundArmoryClient {
    public static final MinecraftClient client = MinecraftClient.getInstance();

    public static final KeyBinding guiKeyBinding = new GUIKeyBinding();
    // public static final KeyBinding toggleXPBarKeyBinding = new ExperienceBarKeyBinding();
}
