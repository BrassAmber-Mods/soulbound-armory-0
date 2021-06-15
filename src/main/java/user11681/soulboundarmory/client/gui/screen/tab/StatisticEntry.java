package user11681.soulboundarmory.client.gui.screen.tab;

import net.minecraft.util.text.ITextComponent;
import user11681.soulboundarmory.capability.statistics.Statistic;

public class StatisticEntry {
    public final Statistic statistic;
    public final ITextComponent text;

    public StatisticEntry(Statistic statistic, ITextComponent text) {
        this.statistic = statistic;
        this.text = text;
    }
}
