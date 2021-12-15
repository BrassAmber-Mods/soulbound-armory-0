package soulboundarmory.component.soulbound.item.weapon;

import java.util.List;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import soulboundarmory.client.gui.screen.AttributeTab;
import soulboundarmory.client.gui.screen.EnchantmentTab;
import soulboundarmory.client.gui.screen.SelectionTab;
import soulboundarmory.client.gui.screen.SkillTab;
import soulboundarmory.client.gui.screen.SoulboundTab;
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
    }

    @Override
    public Item consumableItem() {
        return Items.WOODEN_SWORD;
    }

    @Override
    public int getLevelXP(int level) {
        return this.canLevelUp()
            ? Configuration.instance().initialWeaponXP + 3 * (int) Math.round(Math.pow(level, 1.65))
            : -1;
    }

    @Override
    public void killed(LivingEntity entity) {
        if (!this.isClient()) {
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

            this.incrementStatistic(StatisticType.experience, Math.round(xp));
        }
    }

    @Override
    public double attributeTotal(StatisticType attribute) {
        if (attribute == StatisticType.efficiency && this.doubleValue(attribute) == this.statistic(attribute).min()) {
            return super.attributeTotal(attribute) - this.material().getMiningSpeedMultiplier();
        }

        return super.attributeTotal(attribute);
    }

    /**
     @return true if the hit is critical.
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
    public List<SoulboundTab> tabs() {
        return List.of(new SelectionTab(Translations.guiWeaponSelection), new AttributeTab(), new EnchantmentTab(), new SkillTab());
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
