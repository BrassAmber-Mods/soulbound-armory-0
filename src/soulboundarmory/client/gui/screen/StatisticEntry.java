package soulboundarmory.client.gui.screen;

import soulboundarmory.component.statistics.Statistic;
import net.minecraft.util.text.ITextComponent;

public class StatisticEntry {
    public final Statistic statistic;
    public final ITextComponent text;

    public StatisticEntry(Statistic statistic, ITextComponent text) {
        this.statistic = statistic;
        this.text = text;
    }
}
