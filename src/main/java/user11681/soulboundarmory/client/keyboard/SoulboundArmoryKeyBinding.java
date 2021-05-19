package user11681.soulboundarmory.client.keyboard;

import net.minecraft.client.settings.KeyBinding;
import user11681.soulboundarmory.SoulboundArmory;

public abstract class SoulboundArmoryKeyBinding extends KeyBinding {
    public SoulboundArmoryKeyBinding(String name, int key) {
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
