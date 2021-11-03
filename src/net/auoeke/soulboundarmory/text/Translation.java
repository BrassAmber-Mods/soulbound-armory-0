package net.auoeke.soulboundarmory.text;

import net.minecraft.text.TranslatableText;

public class Translation extends TranslatableText {
    public Translation(String key) {
        super(key);
    }

    public Translation(String key, Object... args) {
        super(key, args);
    }

    public static Translation of(String key, Object... args) {
        return new Translation(String.format(key, args));
    }

    public Translation format(Object... args) {
        return new Translation(this.getKey(), args);
    }

    @Override
    public String toString() {
        return this.getString();
    }
}
