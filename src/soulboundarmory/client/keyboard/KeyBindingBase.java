package soulboundarmory.client.keyboard;

import soulboundarmory.SoulboundArmory;
import net.minecraft.client.settings.KeyBinding;

public abstract class KeyBindingBase extends KeyBinding {
    public KeyBindingBase(String name, int key) {
        super("key.%s.%s".formatted(SoulboundArmory.ID, name), key, String.format("key.categories.%s", SoulboundArmory.ID));
    }

    @Override
    public void setDown(boolean pressed) {
        if (pressed && !super.isDown()) {
            this.press();
        }

        super.setDown(pressed);
    }

    protected abstract void press();
}
