package user11681.soulboundarmory.component.statistics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import nerdhub.cardinal.components.api.util.NbtSerializable;
import net.minecraft.nbt.NbtCompound;

public class Statistics extends HashMap<Category, Map<StatisticType, Statistic>> implements NbtSerializable, Iterable<Statistic> {
    protected Statistics() {
    }

    public static Builder create() {
        return new Builder(new Statistics());
    }

    public Statistic get(final Category category, final StatisticType statistic) {
        return this.get(category).get(statistic);
    }

    public Statistic get(final StatisticType type) {
        for (final Map<StatisticType, Statistic> category : this.values()) {
            final Statistic statistic = category.get(type);

            if (statistic != null) {
                return statistic;
            }
        }

        return null;
    }

    public void put(final StatisticType type, final Number value) {
        this.get(type).setValue(value);
    }

    public void add(final StatisticType type, final Number value) {
        final Statistic statistic = this.get(type);

        if (statistic != null) {
            statistic.add(value);
        }
    }

    public int size(final Category category) {
        return this.get(category).size();
    }

    public void reset() {
        for (final Category category : this.keySet()) {
            this.reset(category);
        }
    }

    public void reset(final Category category) {
        for (final Statistic statistic : this.get(category).values()) {
            statistic.reset();
        }
    }

        @Override
    public Iterator<Statistic> iterator() {
        final Set<Statistic> statistics = new HashSet<>();

        for (final Map<StatisticType, Statistic> category : this.values()) {
            statistics.addAll(category.values());
        }

        return statistics.iterator();
    }

        @Override
    public NbtCompound toTag(final NbtCompound tag) {
        for (final Category category : this.keySet()) {
            tag.put(category.asString(), this.toTag(category));
        }

        return tag;
    }

    public NbtCompound toTag(final Category category) {
        final NbtCompound tag = new NbtCompound();

        for (final Statistic statistic : this.get(category).values()) {
            tag.put(statistic.type().asString(), statistic.toTag(new NbtCompound()));
        }

        return tag;
    }

    @Override
    public void fromTag(final NbtCompound tag) {
        for (final String key : tag.getKeys()) {
            this.fromTag(tag.getCompound(key), Category.category.get(key));
        }
    }

    public void fromTag(final NbtCompound tag, final Category category) {
        if (category != null) {
            for (final String identifier : tag.getKeys()) {
                final Statistic statistic = this.get(StatisticType.statistic.get(identifier));

                if (statistic != null) {
                    statistic.fromTag(tag.getCompound(identifier));
                }
            }
        }
    }

    public static class Builder {
        protected final Statistics statistics;

        protected Builder(final Statistics statistics) {
            this.statistics = statistics;
        }

        public Builder category(final Category categoryType, final StatisticType... statisticTypes) {
            final Map<StatisticType, Statistic> category = new HashMap<>();
            final Statistics statistics = this.statistics;

            for (final StatisticType statisticType : statisticTypes) {
                category.put(statisticType, new Statistic(categoryType, statisticType));
                statistics.put(categoryType, category);
            }

            return this;
        }

        public Builder min(final double min, final Category category) {
            for (final StatisticType statistic : this.statistics.get(category).keySet()) {
                this.min(min, statistic);
            }

            return this;
        }

        public Builder min(final double min, final StatisticType... types) {
            final Statistics statistics = this.statistics;

            for (final StatisticType type : types) {
                statistics.get(type).setMin(min);
            }

            return this;
        }

        public Builder max(final double max, final Category category) {
            for (final StatisticType type : this.statistics.get(category).keySet()) {
                this.max(max, type);
            }

            return this;
        }

        public Builder max(final double max, final StatisticType... types) {
            final Statistics statistics = this.statistics;

            for (final StatisticType type : types) {
                statistics.get(type).setMax(max);
            }

            return this;
        }

        public Statistics build() {
            return this.statistics;
        }
    }
}
