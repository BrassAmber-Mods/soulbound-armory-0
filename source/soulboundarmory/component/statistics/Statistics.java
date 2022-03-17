package soulboundarmory.component.statistics;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.history.AttributeHistory;
import soulboundarmory.serial.Serializable;

public class Statistics extends Reference2ReferenceOpenHashMap<Category, Map<StatisticType, Statistic>> implements Serializable, Iterable<Statistic> {
    public final AttributeHistory history;

    public Statistics(ItemComponent<?> component) {
        this.history = new AttributeHistory(component);
    }

    public Statistic get(StatisticType type) {
        return this.get(type.category).get(type);
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
        this.get(category).values().forEach(Statistic::reset);
    }

    public Statistics statistics(StatisticType... types) {
        Stream.of(types).forEach(this::obtain);

        return this;
    }

    public Statistics statistic(StatisticType type, Consumer<Statistic> initialize) {
        initialize.accept(this.obtain(type));

        return this;
    }

    public Statistics min(double min, StatisticType... types) {
        Stream.of(types).forEach(type -> this.obtain(type).min(min));

        return this;
    }

    public Statistics max(double max, StatisticType... types) {
        Stream.of(types).forEach(type -> this.obtain(type).defaultMax(max));

        return this;
    }

    public Statistics constant(double value, StatisticType... types) {
        return this.min(value, types).max(value, types);
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
            this.deserialize(tag.getCompound(key), Category.registry.get(new Identifier(key)));
        }
    }

    @Override
    public Iterator<Statistic> iterator() {
        return this.values().stream().flatMap(category -> category.values().stream()).iterator();
    }

    private Statistic obtain(StatisticType type) {
        return this.computeIfAbsent(type.category, category -> new Reference2ReferenceOpenHashMap<>()).computeIfAbsent(type, StatisticType::instantiate);
    }

    private void deserialize(NbtCompound tag, Category category) {
        if (category != null) {
            for (var identifier : tag.getKeys()) {
                var statistic = this.get(StatisticType.registry.get(new Identifier(identifier)));

                if (statistic != null) {
                    statistic.deserialize(tag.getCompound(identifier));
                }
            }
        }
    }
}
