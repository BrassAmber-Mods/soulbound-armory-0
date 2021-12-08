package soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.EnchantmentStorage;
import soulboundarmory.component.statistics.SkillStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.component.statistics.Statistics;
import soulboundarmory.registry.Skills;
import soulboundarmory.util.AttributeModifierIdentifiers;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;

public class SwordComponent extends WeaponComponent<SwordComponent> {
    protected int lightningCooldown;

    public SwordComponent(SoulboundComponent component, Item item) {
        super(component, item);
    }

    public static SwordComponent get(Entity entity) {
        return Components.weapon.of(entity).item(ItemComponentType.sword);
    }

    @Override
    public Text name() {
        return Translations.guiSword;
    }

    @Override
    public ItemComponentType<SwordComponent> type() {
        return ItemComponentType.sword;
    }

    public int lightningCooldown() {
        return this.lightningCooldown;
    }

    public void resetLightningCooldown() {
        if (!this.player.isCreative()) {
            this.lightningCooldown = (int) Math.round(96 / this.doubleValue(StatisticType.attackSpeed));
        }
    }

    @Override
    public void attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, StatisticType.attackDamage));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, StatisticType.attackSpeed));
        }
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        return Arrays.asList(
            new StatisticEntry(this.statistic(StatisticType.attackSpeed), Translations.guiAttackSpeed.format(this.formatStatistic(StatisticType.attackSpeed))),
            new StatisticEntry(this.statistic(StatisticType.attackDamage), Translations.guiAttackDamage.format(this.formatStatistic(StatisticType.attackDamage))),
            new StatisticEntry(this.statistic(StatisticType.criticalStrikeRate), Translations.guiCriticalStrikeRate.format(this.formatStatistic(StatisticType.criticalStrikeRate))),
            new StatisticEntry(this.statistic(StatisticType.efficiency), Translations.guiWeaponEfficiency.format(this.formatStatistic(StatisticType.efficiency)))
        );
    }

    @Override
    public List<Text> tooltip() {
        var format = DecimalFormat.getInstance();

        return new ReferenceArrayList<>(List.of(
            Translations.tooltipAttackSpeed.format(format.format(this.doubleValue(StatisticType.attackSpeed))),
            Translations.tooltipAttackDamage.format(format.format(this.attributeTotal(StatisticType.attackDamage))),
            LiteralText.EMPTY,
            LiteralText.EMPTY,
            Translations.tooltipCriticalStrikeRate.format(format.format(this.doubleValue(StatisticType.criticalStrikeRate) * 100)),
            Translations.tooltipToolEfficiency.format(format.format(this.doubleValue(StatisticType.efficiency)))
        ));
    }

    @Override
    public Item consumableItem() {
        return Items.WOODEN_SWORD;
    }

    @Override
    public double increase(StatisticType statistic) {
        if (statistic == StatisticType.attackSpeed) return 0.03;
        if (statistic == StatisticType.attackDamage) return 0.07;
        if (statistic == StatisticType.criticalStrikeRate) return 0.015;
        if (statistic == StatisticType.efficiency) return 0.04;

        return 0;
    }

    @Override
    public void tick() {
        if (!this.client) {
            if (this.lightningCooldown > 0) {
                this.lightningCooldown--;
            }
        }
    }

    @Override
    public void serializeNBT(NbtCompound tag) {
        super.serializeNBT(tag);

        tag.putInt("lightningCooldown", this.lightningCooldown());
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        super.deserializeNBT(tag);

        this.lightningCooldown = tag.getInt("lightningCooldown");
    }

    @Override
    protected Statistics newStatistics() {
        return Statistics.builder()
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints, StatisticType.spentAttributePoints, StatisticType.spentEnchantmentPoints)
            .category(Category.attribute, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.criticalStrikeRate, StatisticType.efficiency, StatisticType.reach)
            .min(1.6, StatisticType.attackSpeed).min(4, StatisticType.attackDamage).min(3, StatisticType.reach)
            .max(1, StatisticType.criticalStrikeRate)
            .build();
    }

    @Override
    protected EnchantmentStorage newEnchantments() {
        return new EnchantmentStorage(enchantment -> {
            var name = enchantment.getTranslationKey().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack) && !Arrays.asList(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                && (enchantment == SoulboundArmory.impact || !name.contains("soulbound")) && !name.contains("holding")
                && !name.contains("mending");
        });
    }

    @Override
    protected SkillStorage newSkills() {
        return new SkillStorage(Skills.nourishment, Skills.summonLightning);
    }
}
