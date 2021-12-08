package soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
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
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.AttributeModifierIdentifiers;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;

public class StaffComponent extends WeaponComponent<StaffComponent> {
    public int fireballCooldown;

    protected int spell;

    public StaffComponent(SoulboundComponent component, Item item) {
        super(component, item);
    }

    public static StaffComponent get(Entity entity) {
        return Components.weapon.of(entity).item(ItemComponentType.staff);
    }

    @Override
    public Text name() {
        return Translations.guiStaff;
    }

    @Override
    public ItemComponentType<StaffComponent> type() {
        return ItemComponentType.staff;
    }

    public void resetFireballCooldown() {
        this.fireballCooldown = (int) Math.round(20 / this.doubleValue(StatisticType.attackSpeed));
    }

    public int spell() {
        return this.spell;
    }

    public void spell(int spell) {
        this.spell = spell;
    }

    public void cycleSpells(int spells) {
        this.spell = Math.abs((this.spell + spells) % 2);

        if (this.client) {
            Packets.serverSpell.send(new ExtendedPacketBuffer().writeByte(this.spell));
        }
    }

    @Override
    public void attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, StatisticType.attackSpeed));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, StatisticType.attackDamage));
        }
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        return new ReferenceArrayList<>(List.of(
            new StatisticEntry(this.statistic(StatisticType.attackSpeed), Translations.guiAttackSpeed.format(this.formatStatistic(StatisticType.attackSpeed))),
            new StatisticEntry(this.statistic(StatisticType.attackDamage), Translations.guiAttackDamage.format(this.formatStatistic(StatisticType.attackDamage))),
            new StatisticEntry(this.statistic(StatisticType.criticalStrikeRate), Translations.guiCriticalStrikeRate.format(this.formatStatistic(StatisticType.criticalStrikeRate)))
        ));
    }

    @Override
    public List<Text> tooltip() {
        var format = DecimalFormat.getInstance();
        var tooltip = new ArrayList<>(List.of(
            Translations.tooltipAttackSpeed.format(format.format(this.doubleValue(StatisticType.attackSpeed))),
            Translations.tooltipAttackDamage.format(format.format(this.attributeTotal(StatisticType.attackDamage))),
            LiteralText.EMPTY,
            LiteralText.EMPTY
        ));

        if (this.doubleValue(StatisticType.criticalStrikeRate) > 0) {
            tooltip.add(Translations.tooltipCriticalStrikeRate.format(format.format(this.doubleValue(StatisticType.criticalStrikeRate) * 100)));
        }

        return tooltip;
    }

    @Override
    public Item consumableItem() {
        return SoulboundItems.staff;
    }

    @Override
    public double increase(StatisticType statistic) {
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

        tag.putByte("spell", (byte) this.spell);
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        super.deserializeNBT(tag);

        this.spell(tag.getByte("spell"));
    }

    @Override
    protected Statistics newStatistics() {
        return Statistics.builder()
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints, StatisticType.spentAttributePoints, StatisticType.spentEnchantmentPoints)
            .category(Category.attribute, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.criticalStrikeRate)
            .min(0.48, StatisticType.attackSpeed).min(8, StatisticType.attackDamage)
            .max(1, StatisticType.criticalStrikeRate).build();

    }

    @Override
    protected EnchantmentStorage newEnchantments() {
        return new EnchantmentStorage(enchantment -> {
            var name = enchantment.getTranslationKey().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack)
                && !enchantment.isCursed()
                && enchantment != UNBREAKING
                && (enchantment == SoulboundArmory.impact || !name.contains("soulbound"))
                && !name.contains("holding")
                && !name.contains("mending");
        });

    }

    @Override
    protected SkillStorage newSkills() {
        return new SkillStorage(Skills.healing, Skills.penetration, Skills.vulnerability, Skills.penetration, Skills.endermanacle);
    }
}
