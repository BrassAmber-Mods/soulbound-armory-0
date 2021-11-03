package net.auoeke.soulboundarmory.client.keyboard;

import net.minecraft.client.option.KeyBinding;
import net.auoeke.soulboundarmory.SoulboundArmory;

public abstract class KeyBindingBase extends KeyBinding {
    public KeyBindingBase(String name, int key) {
        super(String.format("key.%s.%s", SoulboundArmory.ID, name), key, String.format("key.categories.%s", SoulboundArmory.NAME));
    }

    protected abstract void press();

    @Override
    public void setPressed(boolean pressed) {
        if (pressed && !super.isPressed()) {
            this.press();
        }

        super.setPressed(pressed);
    }
}
