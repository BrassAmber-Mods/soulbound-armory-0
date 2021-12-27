package soulboundarmory.component.soulbound.item.weapon;

import java.util.List;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.util.EntityUtil;
import soulboundarmory.util.Util;

public abstract class WeaponComponent<T extends ItemComponent<T>> extends ItemComponent<T> {
    protected double criticalHitProgress;

    public WeaponComponent(SoulboundComponent<?> component) {
        super(component);

        this.statistics.statistics(StatisticType.criticalHitRate);
    }

    /**
     @return true if the hit is critical
     */
    public boolean hit() {
        this.criticalHitProgress += this.doubleValue(StatisticType.criticalHitRate);

        if (this.criticalHitProgress >= 1) {
            this.criticalHitProgress--;

            return true;
        }

        return false;
    }

    @Override
    public Item consumableItem() {
        return Items.WOODEN_SWORD;
    }

    @Override
    public int levelXP(int level) {
        return this.canLevelUp()
            ? Configuration.instance().initialWeaponXP + 3 * (int) Math.round(Math.pow(level, 1.65))
            : -1;
    }

    @Override
    public void killed(LivingEntity entity) {
        if (this.isServer()) {
            var damage = EntityUtil.attribute(entity, EntityAttributes.GENERIC_ATTACK_DAMAGE);
            var speed = EntityUtil.attribute(entity, EntityAttributes.GENERIC_ATTACK_SPEED);
            var configuration = Configuration.instance();
            var difficulty = this.player.world.getDifficulty().getId();

            var xp = entity.getMaxHealth()
                * (difficulty == 0 ? configuration.peacefulMultiplier : difficulty) * configuration.difficultyMultiplier
                * (1 + EntityUtil.attribute(entity, EntityAttributes.GENERIC_ARMOR) * configuration.armorMultiplier)
                * (damage <= 0 ? configuration.passiveMultiplier : 1 + damage * configuration.attackDamageMultiplier)
                * (1 + speed * configuration.attackSpeedMultiplier);

            if (EntityUtil.isBoss(entity)) {
                xp *= configuration.bossMultiplier;
            }

            if (this.player.world.getServer().isHardcore()) {
                xp *= configuration.hardcoreMultiplier;
            }

            if (damage > 0 && entity.isBaby()) {
                xp *= configuration.babyMultiplier;
            }

            this.add(StatisticType.experience, Math.round(xp));
        }
    }

    @Override
    public double attributeTotal(StatisticType attribute) {
        if (attribute == StatisticType.efficiency && this.statistic(StatisticType.efficiency).min() == 0) {
            return this.doubleValue(attribute) == 0 ? 0 : super.attributeTotal(attribute) - this.increase(attribute);
        }

        return super.attributeTotal(attribute);
    }

    @Override
    public Map<Statistic, Text> screenAttributes() {
        return Util.map(
            this.statisticEntry(StatisticType.attackDamage, Translations.guiAttackDamage),
            this.statisticEntry(StatisticType.attackSpeed, Translations.guiAttackSpeed),
            this.statisticEntry(StatisticType.criticalHitRate, Translations.guiCriticalHitRate)
        );
    }

    @Override
    public List<Text> tooltip() {
        var tooltip = Util.list(
            Translations.tooltipAttackDamage.translate(this.format(StatisticType.attackDamage)),
            Translations.tooltipAttackSpeed.translate(this.format(StatisticType.attackSpeed))
        );

        if (this.criticalHitRate() > 0) tooltip.add(Translations.tooltipCriticalHitRate.translate(this.format(StatisticType.criticalHitRate)));
        if (this.efficiency() > 0) tooltip.add(Translations.tooltipEfficiency.translate(this.format(StatisticType.efficiency)));

        return tooltip;
    }

    @Override
    public void serialize(NbtCompound tag) {
        super.serialize(tag);

        tag.putDouble("criticalHitProgress", this.criticalHitProgress);
    }

    @Override
    public void deserialize(NbtCompound tag) {
        super.deserialize(tag);

        this.criticalHitProgress = tag.getDouble("criticalHitProgress");
    }
}
