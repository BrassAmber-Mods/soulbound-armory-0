package net.auoeke.soulboundarmory.client.keyboard;

import net.auoeke.soulboundarmory.config.Configuration;
import org.lwjgl.glfw.GLFW;

public class ExperienceBarKeyBinding extends KeyBindingBase {
    public ExperienceBarKeyBinding() {
        super("bar", GLFW.GLFW_KEY_X);
    }

    @Override
    protected void press() {
        Configuration.Client client = Configuration.instance().client;

        client.toggleOverlayExperienceBar();
    }
}
