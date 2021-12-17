package soulboundarmory.component.statistics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.history.AttributeHistory;
import soulboundarmory.serial.Serializable;

public class Statistics extends HashMap<Category, Map<StatisticType, Statistic>> implements Serializable, Iterable<Statistic> {
    public final AttributeHistory history;

    public Statistics(ItemComponent<?> component) {
        this.history = new AttributeHistory(component);
    }

    public Statistic get(Category category, StatisticType statistic) {
        return this.get(category).get(statistic);
    }

    public Statistic get(StatisticType type) {
        for (var category : this.values()) {
            var statistic = category.get(type);

            if (statistic != null) {
                return statistic;
            }
        }

        return null;
    }

    public void put(StatisticType type, Number value) {
        this.get(type).value(value);
    }

    public void add(StatisticType type, Number value) {
        var statistic = this.get(type);

        if (statistic != null) {
            statistic.add(value);
        }
    }

    public int size(Category category) {
        return this.get(category).size();
    }

    public void reset() {
        for (var category : this.keySet()) {
            this.reset(category);
        }
    }

    public void reset(Category category) {
        for (var statistic : this.get(category).values()) {
            statistic.reset();
        }
    }

    public Statistics category(Category categoryType, StatisticType... statisticTypes) {
        var category = new HashMap<StatisticType, Statistic>();

        for (var statisticType : statisticTypes) {
            category.put(statisticType, new Statistic(categoryType, statisticType));
            this.put(categoryType, category);
        }

        return this;
    }

    public Statistics min(double min, Category category) {
        for (var statistic : this.get(category).keySet()) {
            this.min(min, statistic);
        }

        return this;
    }

    public Statistics min(double min, StatisticType... types) {
        for (var type : types) {
            this.get(type).min(min);
        }

        return this;
    }

    public Statistics max(double max, Category category) {
        for (var type : this.get(category).keySet()) {
            this.max(max, type);
        }

        return this;
    }

    public Statistics max(double max, StatisticType... types) {
        for (var type : types) {
            this.get(type).defaultMax(max);
        }

        return this;
    }

    public NbtCompound serialize(Category category) {
        var tag = new NbtCompound();

        for (var statistic : this.get(category).values()) {
            tag.put(statistic.type.id().toString(), statistic.serialize());
        }

        return tag;
    }

    @Override
    public void serialize(NbtCompound tag) {
        for (var category : this.keySet()) {
            tag.put(category.id().toString(), this.serialize(category));
        }
    }

    @Override
    public void deserialize(NbtCompound tag) {
        for (var key : tag.getKeys()) {
            this.deserialize(tag.getCompound(key), Category.registry.getValue(new Identifier(key)));
        }
    }

    @Override
    public Iterator<Statistic> iterator() {
        var statistics = new HashSet<Statistic>();

        for (var category : this.values()) {
            statistics.addAll(category.values());
        }

        return statistics.iterator();
    }

    public void deserialize(NbtCompound tag, Category category) {
        if (category != null) {
            for (var identifier : tag.getKeys()) {
                var statistic = this.get(StatisticType.registry.getValue(new Identifier(identifier)));

                if (statistic != null) {
                    statistic.deserialize(tag.getCompound(identifier));
                }
            }
        }
    }
}
