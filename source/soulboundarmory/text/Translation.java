package soulboundarmory.text;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class Translation extends TranslatableText {
    public Translation(String key) {
        super(key);
    }

    public Translation(String key, Object... args) {
        super(key, args);
    }

    public static Translation of(String key, Object... args) {
        return new Translation(key.formatted(args));
    }

    public Translation format(Object... args) {
        return new Translation(this.getKey(), args);
    }

    public Text translate(Object... args) {
        return Text.of(I18n.translate(this.getKey(), args));
    }

    @Override
    public String toString() {
        return this.getString();
    }
}
