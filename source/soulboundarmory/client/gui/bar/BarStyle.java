package soulboundarmory.client.gui.bar;

import java.util.List;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;

public enum BarStyle {
	EXPERIENCE(64, Translations.xpStyle),
	BOSS(74, Translations.bossStyle),
	HORSE(84, Translations.horseStyle);

	public static final List<BarStyle> styles = ReferenceArrayList.wrap(values());
	public static final int count = styles.size();

	public final int v;

	public final Text text;

	BarStyle(int v, Text text) {
		this.v = v;
		this.text = text;
	}
}
