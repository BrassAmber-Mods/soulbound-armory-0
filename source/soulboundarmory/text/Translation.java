package soulboundarmory.text;

import java.util.stream.Stream;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class Translation extends TranslatableTextContent {
    public Translation(String key) {
        super(key);
    }

    public Translation(String key, Object... args) {
        super(key, args);
    }

    public static Translation of(String key, Object... args) {
        return new Translation(key.formatted(args));
    }

    public Text translate(Object... args) {
        return Text.of(I18n.translate(this.getKey(), Stream.of(args).map(arg -> arg instanceof StringVisitable text ? text.getString() : arg).toArray()));
    }

    public MutableText text(Object... args) {
        return Text.translatable(this.getKey(), args);
    }

    public MutableText text() {
        return MutableText.of(this);
    }

    @Override
    public String toString() {
        return this.text().getString();
    }
}
