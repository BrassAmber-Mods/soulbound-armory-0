package soulboundarmory.client.gui.screen;

import net.minecraft.text.Text;
import soulboundarmory.component.statistics.Statistic;

public class StatisticEntry {
	public final Statistic statistic;
	public final Text text;

	public StatisticEntry(Statistic statistic, Text text) {
		this.statistic = statistic;
		this.text = text;
	}
}
