package soulboundarmory.client.gui.bar;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import soulboundarmory.client.i18n.Translations;
import net.minecraft.util.text.ITextComponent;

public enum Style {
    EXPERIENCE(64, Translations.xpStyle),
    BOSS(74, Translations.bossStyle),
    HORSE(84, Translations.horseStyle);

    public static final List<Style> styles = ReferenceArrayList.wrap(values());
    public static final int count = styles.size();

    public final int v;

    private final ITextComponent text;

    Style(int v, ITextComponent text) {
        this.v = v;
        this.text = text;
    }

    public ITextComponent getText() {
        return this.text;
    }
}
