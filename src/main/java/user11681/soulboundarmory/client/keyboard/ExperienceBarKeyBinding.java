package user11681.soulboundarmory.client.keyboard;

import org.lwjgl.glfw.GLFW;
import user11681.soulboundarmory.config.Configuration;

public class ExperienceBarKeyBinding extends SoulboundArmoryKeyBinding {
    public ExperienceBarKeyBinding() {
        super("bar", GLFW.GLFW_KEY_X);
    }

    @Override
    protected void press() {
        Configuration.Client client = Configuration.instance().client;

        client.toggleOverlayExperienceBar();
    }
}
