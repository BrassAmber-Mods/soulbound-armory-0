package soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.EnchantmentStorage;
import soulboundarmory.component.statistics.SkillStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.component.statistics.Statistics;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.text.Translation;
import soulboundarmory.util.AttributeModifierIdentifiers;
import soulboundarmory.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;

public class StaffStorage extends WeaponStorage<StaffStorage> {
    protected int fireballCooldown;
    protected int spell;

    public StaffStorage(SoulboundComponent component, Item item) {
        super(component, item);

        this.statistics = Statistics.create()
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints, StatisticType.spentAttributePoints, StatisticType.spentEnchantmentPoints)
            .category(Category.attribute, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.criticalStrikeRate)
            .min(0.48, StatisticType.attackSpeed).min(8, StatisticType.attackDamage)
            .max(1, StatisticType.criticalStrikeRate).build();

        this.enchantments = new EnchantmentStorage(enchantment -> {
            var name = enchantment.getTranslationKey().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack) && !Util.contains(enchantment, UNBREAKING, VANISHING_CURSE)
                && (enchantment == SoulboundArmory.impact || !name.contains("soulbound")) && !name.contains("holding")
                && !name.contains("mending");
        });

        this.skills = new SkillStorage(Skills.healing, Skills.penetration, Skills.vulnerability, Skills.penetration, Skills.endermanacle);
    }

    public static StaffStorage get(Entity entity) {
        return Components.weapon.of(entity).storage(StorageType.staff);
    }

    @Override
    public Text name() {
        return Translations.soulboundStaff;
    }

    @Override
    public StorageType<StaffStorage> type() {
        return StorageType.staff;
    }

    public int getFireballCooldown() {
        return this.fireballCooldown;
    }

    public void setFireballCooldown(int ticks) {
        this.fireballCooldown = ticks;
    }

    public void resetFireballCooldown() {
        this.fireballCooldown = (int) Math.round(20 / this.attribute(StatisticType.attackSpeed));
    }

    public int spell() {
        return this.spell;
    }

    public void setSpell(int spell) {
        this.spell = spell;
    }

    public void cycleSpells(int spells) {
        this.spell = Math.abs((this.spell + spells) % 2);

        this.sync();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, "Weapon modifier", this.attributeRelative(StatisticType.attackSpeed), EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, "Weapon modifier", this.attributeRelative(StatisticType.attackDamage), EntityAttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        return new ReferenceArrayList<>(List.of(
            new StatisticEntry(this.statistic(StatisticType.attackSpeed), new Translation("%s%s: %s", Translations.attackSpeedFormat, Translations.attackSpeedName, this.formatStatistic(StatisticType.attackSpeed))),
            new StatisticEntry(this.statistic(StatisticType.attackDamage), new Translation("%s%s: %s", Translations.attackDamageFormat, Translations.attackDamageName, this.formatStatistic(StatisticType.attackDamage))),
            new StatisticEntry(this.statistic(StatisticType.criticalStrikeRate), new Translation("%s%s: %s%%", Translations.criticalStrikeRateFormat, Translations.criticalStrikeRateName, this.formatStatistic(StatisticType.criticalStrikeRate)))
        ));
    }

    @Override
    public List<Text> tooltip() {
        var format = DecimalFormat.getInstance();
        var tooltip = new ArrayList<>(List.of(
            new Translation(" %s%s %s", Translations.attackSpeedFormat, format.format(this.attribute(StatisticType.attackSpeed)), Translations.attackSpeedName),
            new Translation(" %s%s %s", Translations.attackDamageFormat, format.format(this.attributeTotal(StatisticType.attackDamage)), Translations.attackDamageName),
            LiteralText.EMPTY,
            LiteralText.EMPTY
        ));

        if (this.attribute(StatisticType.criticalStrikeRate) > 0) {
            tooltip.add(Text.of(String.format(" %s%s%% %s", Translations.criticalStrikeRateFormat, format.format(this.attribute(StatisticType.criticalStrikeRate) * 100), Translations.criticalStrikeRateName)));
        }

        return tooltip;
    }

    @Override
    public Item consumableItem() {
        return SoulboundItems.staff;
    }

    @Override
    public double increase(StatisticType statistic, int points) {
        if (statistic == StatisticType.attackSpeed) return 0.08;
        if (statistic == StatisticType.attackDamage) return 0.2;
        if (statistic == StatisticType.criticalStrikeRate) return 0.04;

        return 0;
    }

    @Override
    public void tick() {
        if (this.fireballCooldown > 0) {
            this.fireballCooldown--;
        }
    }

    @Override
    public void serializeNBT(NbtCompound tag) {
        super.serializeNBT(tag);

        tag.putInt("spell", this.spell);
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        super.deserializeNBT(tag);

        this.setSpell(tag.getInt("spell"));
    }

    @Override
    public NbtCompound clientTag() {
        var tag = super.clientTag();
        tag.putInt("spell", this.spell);

        return tag;
    }
}
