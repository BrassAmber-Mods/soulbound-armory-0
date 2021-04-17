package user11681.soulboundarmory.client.keyboard;

import net.minecraft.client.option.KeyBinding;
import user11681.soulboundarmory.SoulboundArmory;

public class SoulboundArmoryKeyBinding extends KeyBinding {
    public SoulboundArmoryKeyBinding(String name, int key) {
        super(SoulboundArmory.key(name), key, String.format("key.categories.%s", SoulboundArmory.NAME));
    }

    protected void press() {}

    @Override
    public void setPressed(boolean pressed) {
        if (pressed && !super.isPressed()) {
            this.press();
        }

        super.setPressed(pressed);
    }
}
