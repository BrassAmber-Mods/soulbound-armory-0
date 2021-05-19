package user11681.soulboundarmory.capability.statistics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import nerdhub.cardinal.components.api.util.INBTSerializable;
import net.minecraft.nbt.CompoundNBT;

public class Statistics extends HashMap<Category, Map<StatisticType, Statistic>> implements INBTSerializable, Iterable<Statistic> {
    protected Statistics() {
    }

    public static Builder create() {
        return new Builder(new Statistics());
    }

    public Statistic get(Category category, final StatisticType statistic) {
        return this.get(category).get(statistic);
    }

    public Statistic get(StatisticType type) {
        for (Map<StatisticType, Statistic> category : this.values()) {
            final Statistic statistic = category.get(type);

            if (statistic != null) {
                return statistic;
            }
        }

        return null;
    }

    public void put(StatisticType type, final Number value) {
        this.get(type).setValue(value);
    }

    public void add(StatisticType type, final Number value) {
        final Statistic statistic = this.get(type);

        if (statistic != null) {
            statistic.add(value);
        }
    }

    public int size(Category category) {
        return this.get(category).size();
    }

    public void reset() {
        for (Category category : this.keySet()) {
            this.reset(category);
        }
    }

    public void reset(Category category) {
        for (Statistic statistic : this.get(category).values()) {
            statistic.reset();
        }
    }

        @Override
    public Iterator<Statistic> iterator() {
        final Set<Statistic> statistics = new HashSet<>();

        for (Map<StatisticType, Statistic> category : this.values()) {
            statistics.addAll(category.values());
        }

        return statistics.iterator();
    }

        @Override
    public CompoundNBT toTag(CompoundNBT tag) {
        for (Category category : this.keySet()) {
            tag.put(category.asString(), this.toTag(category));
        }

        return tag;
    }

    public CompoundNBT toTag(Category category) {
        final CompoundNBT tag = new CompoundNBT();

        for (Statistic statistic : this.get(category).values()) {
            tag.put(statistic.type().asString(), statistic.toTag(new CompoundNBT()));
        }

        return tag;
    }

    @Override
    public void fromTag(CompoundNBT tag) {
        for (String key : tag.getKeys()) {
            this.fromTag(tag.getCompound(key), Category.registry.get(key));
        }
    }

    public void fromTag(CompoundNBT tag, final Category category) {
        if (category != null) {
            for (String identifier : tag.getKeys()) {
                final Statistic statistic = this.get(StatisticType.registry.get(identifier));

                if (statistic != null) {
                    statistic.fromTag(tag.getCompound(identifier));
                }
            }
        }
    }

    public static class Builder {
        protected final Statistics statistics;

        protected Builder(Statistics statistics) {
            this.statistics = statistics;
        }

        public Builder category(Category categoryType, final StatisticType... statisticTypes) {
            final Map<StatisticType, Statistic> category = new HashMap<>();
            final Statistics statistics = this.statistics;

            for (StatisticType statisticType : statisticTypes) {
                category.put(statisticType, new Statistic(categoryType, statisticType));
                statistics.put(categoryType, category);
            }

            return this;
        }

        public Builder min(double min, final Category category) {
            for (StatisticType statistic : this.statistics.get(category).keySet()) {
                this.min(min, statistic);
            }

            return this;
        }

        public Builder min(double min, final StatisticType... types) {
            final Statistics statistics = this.statistics;

            for (StatisticType type : types) {
                statistics.get(type).setMin(min);
            }

            return this;
        }

        public Builder max(double max, final Category category) {
            for (StatisticType type : this.statistics.get(category).keySet()) {
                this.max(max, type);
            }

            return this;
        }

        public Builder max(double max, final StatisticType... types) {
            final Statistics statistics = this.statistics;

            for (StatisticType type : types) {
                statistics.get(type).setMax(max);
            }

            return this;
        }

        public Statistics build() {
            return this.statistics;
        }
    }
}
