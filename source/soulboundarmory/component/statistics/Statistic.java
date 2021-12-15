package soulboundarmory.component.statistics;

import java.math.BigDecimal;
import net.minecraft.nbt.NbtCompound;
import soulboundarmory.serial.Serializable;

public class Statistic extends Number implements Comparable<Number>, Serializable {
    public final Category category;
    public final StatisticType type;

    protected BigDecimal value = BigDecimal.ZERO;
    protected double min = 0;
    protected double max = Double.MAX_VALUE;
    protected double defaultMax = this.max;

    public Statistic(Category category, StatisticType statistic) {
        this.type = statistic;
        this.category = category;
    }

    @Override
    public String toString() {
        return "Statistic {name: %s, type: %s, min: %.3f, max: %.3f, value: %s}".formatted(this.type.id(), this.category.id(), this.min, this.max, this.value);
    }

    public boolean aboveMin() {
        return this.greaterThan(this.min);
    }

    public double min() {
        return this.min;
    }

    public void min(double min) {
        this.min = min;

        if (this.doubleValue() < min) {
            this.setToMin();
        }
    }

    public void setToMin() {
        this.value(this.min);
    }

    public boolean belowMax() {
        return this.lessThan(this.max);
    }

    public double max() {
        return this.max;
    }

    public void max(double max) {
        this.max = max;
    }

    public void defaultMax(double max) {
        this.max = max;
        this.defaultMax = max;

        if (this.doubleValue() > max) {
            this.setToMax();
        }
    }

    public void setToMax() {
        this.value(this.max);
    }

    public BigDecimal value() {
        return this.value;
    }

    public void value(BigDecimal value) {
        this.value = value;
    }

    public void value(Number value) {
        this.value = BigDecimal.valueOf(value.doubleValue());
    }

    @Override
    public int intValue() {
        return this.value.intValue();
    }

    @Override
    public long longValue() {
        return this.value.longValue();
    }

    @Override
    public float floatValue() {
        return this.value.floatValue();
    }

    @Override
    public double doubleValue() {
        return this.value.doubleValue();
    }

    public boolean lessThan(Number number) {
        return this.compareTo(number) < 0;
    }

    public boolean greaterThan(Number number) {
        return this.compareTo(number) > 0;
    }

    public void addUnchecked(Number number) {
        this.value = this.value.add(number instanceof BigDecimal big ? big : BigDecimal.valueOf(number.doubleValue()));
    }

    public void add(Number number) {
        var currentValue = this.value.doubleValue();
        var addition = number.doubleValue();

        if (currentValue + addition < this.min) {
            this.setToMin();
        } else if (currentValue + addition > this.max) {
            this.setToMax();
        } else {
            this.addUnchecked(number);
        }
    }

    public void reset() {
        this.setToMin();
        // this.max = this.defaultMax;
    }

    @Override
    public int compareTo(Number number) {
        return number instanceof BigDecimal big ? this.value.compareTo(big) : Double.compare(this.value.doubleValue(), number.doubleValue());
    }

    @Override
    public void serialize(NbtCompound tag) {
        tag.putDouble("min", this.min);
        tag.putDouble("max", this.max);
        tag.putString("value", this.value.toString());
    }

    @Override
    public void deserialize(NbtCompound tag) {
        this.value = new BigDecimal(tag.getString("value"));
        var dMin = this.min - tag.getDouble("min");

        if (dMin != 0) {
            this.addUnchecked(dMin);
        }

        this.max = Math.max(this.defaultMax, tag.getDouble("max"));
    }
}
