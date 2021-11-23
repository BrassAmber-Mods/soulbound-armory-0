package net.auoeke.soulboundarmory.client.gui.screen;

import net.auoeke.soulboundarmory.capability.statistics.Statistic;
import net.minecraft.util.text.ITextComponent;

public class StatisticEntry {
    public final Statistic statistic;
    public final ITextComponent text;

    public StatisticEntry(Statistic statistic, ITextComponent text) {
        this.statistic = statistic;
        this.text = text;
    }
}
