package user11681.soulboundarmory.text;

import net.minecraft.text.TranslatableText;

public class StringableText extends TranslatableText {
    public StringableText(String key) {
        super(key);
    }

    public StringableText(String key, Object... args) {
        super(key, args);
    }

    @Override
    public String toString() {
        return this.getString();
    }
}
