package soulboundarmory.client.keyboard;

import soulboundarmory.SoulboundArmory;
import net.minecraft.client.settings.KeyBinding;

public abstract class KeyBindingBase extends KeyBinding {
    public KeyBindingBase(String name, int key) {
        super(String.format("key.%s.%s", SoulboundArmory.ID, name), key, String.format("key.categories.%s", SoulboundArmory.NAME));
    }

    protected abstract void press();

    @Override
    public void setDown(boolean pressed) {
        if (pressed && !super.isDown()) {
            this.press();
        }

        super.setDown(pressed);
    }
}
