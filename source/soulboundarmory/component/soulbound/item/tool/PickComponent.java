package soulboundarmory.component.soulbound.item.tool;

import com.google.common.collect.Multimap;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolActions;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.entity.Attributes;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.AttributeModifierIdentifiers;

public class PickComponent extends ToolComponent<PickComponent> {
    public PickComponent(SoulboundComponent<?> component) {
        super(component);

        this.statistics
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
            .category(Category.attribute, StatisticType.efficiency, StatisticType.reach, StatisticType.upgradeProgress)
            .min(2, StatisticType.reach)
            .max(0, StatisticType.upgradeProgress);

        this.enchantments.initialize(enchantment -> Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains));
    }

    @Override
    public ItemComponentType<PickComponent> type() {
        return ItemComponentType.pick;
    }

    @Override
    public Item item() {
        return SoulboundItems.pick;
    }

    @Override
    public Item consumableItem() {
        return Items.WOODEN_PICKAXE;
    }

    @Override
    public Text name() {
        return Translations.guiPick;
    }

    @Override
    public double increase(StatisticType type) {
        if (type == StatisticType.efficiency) return 0.5;
        if (type == StatisticType.reach) return 0.1;
        if (type == StatisticType.upgradeProgress) return 0.2;

        return 0;
    }

    /**
     Put attribute modifiers into the given map for a new stack of this item.@param modifiers the map into which to put the modifiers

     @param slot
     */
    @Override
    public void attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, StatisticType.attackSpeed));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, StatisticType.attackDamage));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), this.weaponModifier(Attributes.reach, StatisticType.reach));
        }
    }

    @Override
    protected Tag<Block> tag() {
        return BlockTags.PICKAXE_MINEABLE;
    }

    @Override
    protected boolean canAbsorb(ItemStack stack) {
        return stack.canPerformAction(ToolActions.PICKAXE_DIG);
    }
}
