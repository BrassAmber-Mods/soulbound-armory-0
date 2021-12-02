package soulboundarmory.client.keyboard;

import soulboundarmory.SoulboundArmory;
import net.minecraft.client.settings.KeyBinding;

public abstract class KeyBindingBase extends KeyBinding {
    public KeyBindingBase(String name, int key) {
        super("key.%s.%s".formatted(SoulboundArmory.ID, name), key, "key.categories.%s".formatted(SoulboundArmory.ID));
    }

    @Override
    public void setPressed(boolean pressed) {
        if (pressed && !super.isPressed()) {
            this.press();
        }

        super.setPressed(pressed);
    }

    protected abstract void press();
}
