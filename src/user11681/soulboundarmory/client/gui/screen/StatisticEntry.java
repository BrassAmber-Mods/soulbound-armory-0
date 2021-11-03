package user11681.soulboundarmory.client.gui.screen;

import net.minecraft.text.Text;
import user11681.soulboundarmory.capability.statistics.Statistic;

public class StatisticEntry {
    public final Statistic statistic;
    public final Text text;

    public StatisticEntry(Statistic statistic, Text text) {
        this.statistic = statistic;
        this.text = text;
    }
}
