package user11681.usersmanual.client.keyboard;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.KeyBinding;

@Environment(EnvType.CLIENT)
public abstract class ModKeyBinding extends KeyBinding {
    public ModKeyBinding(final String translationKey, final int code, final String category) {
        super(translationKey, code, category);
    }

    @Override
    public void setPressed(final boolean pressed) {
        if (!this.isPressed() && pressed) {
            this.onPress();
        } else {
            this.onHold();
        }

        super.setPressed(pressed);
    }

    protected void onPress() {}

    protected void onHold() {}
}
