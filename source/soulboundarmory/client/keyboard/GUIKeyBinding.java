package soulboundarmory.client.keyboard;

import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.gui.screen.SoulboundScreen;
import soulboundarmory.component.soulbound.item.ItemComponent;

/**
 Open {@linkplain SoulboundScreen the menu} if one of the held items is soulbound or {@linkplain ItemComponent#canConsume consumable}.
 */
public final class GUIKeyBinding extends KeyBinding {
    public static final GUIKeyBinding instance = new GUIKeyBinding();

    public boolean wasPressed;

    private GUIKeyBinding() {
        super("key.%s.%s".formatted(SoulboundArmory.ID, "menu"), GLFW.GLFW_KEY_R, "key.categories.%s".formatted(SoulboundArmory.ID));
    }

    @Override
    public void setPressed(boolean pressed) {
        this.wasPressed = this.isPressed();

        if (pressed && !this.wasPressed) {
            this.press();
        }

        super.setPressed(pressed);
    }

    private void press() {}
}
