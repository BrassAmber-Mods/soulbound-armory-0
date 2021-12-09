package soulboundarmory.client.keyboard;

import soulboundarmory.config.Configuration;
import org.lwjgl.glfw.GLFW;

public class ExperienceBarKeyBinding extends KeyBindingBase {
    public ExperienceBarKeyBinding() {
        super("bar", GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    protected void press() {
        Configuration.instance().client.toggleOverlayExperienceBar();
    }
}
