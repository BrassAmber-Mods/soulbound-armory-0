package user11681.usersmanual.text;

import net.minecraft.text.TranslatableText;

public class StringifiedText extends TranslatableText {
    public StringifiedText(final String key, final Object... args) {
        super(key, args);
    }

    @Override
    public String toString() {
        return this.getString();
    }
}
