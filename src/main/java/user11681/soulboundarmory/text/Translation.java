package user11681.soulboundarmory.text;

import net.minecraft.util.text.TranslationTextComponent;

public class Translation extends TranslationTextComponent {
    public Translation(String key) {
        super(key);
    }

    public Translation(String key, Object... args) {
        super(key, args);
    }

    public Translation format(Object... args) {
        return new Translation(this.getKey(), args);
    }

    @Override
    public String toString() {
        return this.getString();
    }
}
