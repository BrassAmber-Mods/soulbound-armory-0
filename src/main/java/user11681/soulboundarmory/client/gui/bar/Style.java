package user11681.soulboundarmory.client.gui.bar;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import net.minecraft.text.Text;
import user11681.soulboundarmory.client.i18n.Translations;

public enum Style {
    EXPERIENCE(64, Translations.xpStyle),
    BOSS(74, Translations.bossStyle),
    HORSE(84, Translations.horseStyle);

    public static final List<Style> styles = ReferenceArrayList.wrap(values());
    public static final int count = styles.size();

    public final int v;

    protected final Text text;

    Style(int v, Text text) {
        this.v = v;
        this.text = text;
    }

    public Text getText() {
        return this.text;
    }
}
