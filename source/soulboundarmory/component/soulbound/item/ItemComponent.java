package soulboundarmory.component.soulbound.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.EnchantmentStorage;
import soulboundarmory.component.statistics.SkillStorage;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.component.statistics.Statistics;
import soulboundarmory.config.Configuration;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.serial.CompoundSerializable;
import soulboundarmory.skill.Skill;
import soulboundarmory.skill.SkillContainer;
import soulboundarmory.util.ItemUtil;
import soulboundarmory.util.Math2;

public abstract class ItemComponent<T extends ItemComponent<T>> implements CompoundSerializable {
    protected static final NumberFormat statisticFormat = DecimalFormat.getInstance();

    public final SoulboundComponent component;
    public final PlayerEntity player;
    public final EnchantmentStorage enchantments = new EnchantmentStorage();

    protected final Statistics statistics = new Statistics();
    protected final SkillStorage skills = new SkillStorage();
    protected ItemStack itemStack;
    protected boolean unlocked;
    protected int boundSlot;

    public ItemComponent(SoulboundComponent component) {
        this.component = component;
        this.player = component.player;
    }

    /**
     @return all item components attached to `entity`.
     */
    public static Stream<ItemComponent<?>> all(Entity entity) {
        return Components.soulbound(entity).flatMap(component -> component.items.values().stream());
    }

    /**
     @return the component attached to `entity` that matches `stack`.
     */
    public static Optional<ItemComponent<?>> get(Entity entity, ItemStack stack) {
        return Components.soulbound(entity).filter(component -> component.accepts(stack)).flatMap(component -> component.items.values().stream().filter(item -> item.accepts(stack))).findAny();
    }

    /**
     Find the first component that matches an item held by an entity.

     @return the component.
     */
    public static Optional<ItemComponent<?>> firstHeld(Entity entity) {
        if (entity == null) {
            return Optional.empty();
        }

        var components = Components.soulbound(entity).toList();

        for (var stack : entity.getItemsHand()) {
            for (var component : components) {
                if (component.accepts(stack)) {
                    return component.component(stack);
                }
            }
        }

        return Optional.empty();
    }

    /**
     @return the type of this item.
     */
    public abstract ItemComponentType<T> type();

    /**
     @return the item that corresponds to this component.
     */
    public abstract Item item();

    /**
     @return the item that may be consumed in order to unlock this item.
     */
    public abstract Item consumableItem();

    /**
     @return the name of this item without a "soulbound" prefix.
     */
    public abstract Text name();

    /**
     @return the increase in `statistic` per point.
     */
    public abstract double increase(StatisticType statistic);

    /**
     @param level a level
     @return the XP required in order to reach level `level` from the previous level.
     */
    public abstract int getLevelXP(int level);

    /**
     Put attribute modifiers into the given map for a new stack of this item.

     @param modifiers the map into which to put the modifiers
     */
    public abstract void attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot);

    /**
     @return the tabs to display in the menu for this item.
     */
    public abstract List<SoulboundTab> tabs();

    /**
     @return a list of attributes to be displayed on the attribute tab for this item.
     */
    public abstract List<StatisticEntry> screenAttributes();

    /**
     @return the tooltip for stacks of this item.
     */
    public abstract List<Text> tooltip();

    /**
     Invoked every tick.
     */
    public void tick() {}

    public final boolean isClient() {
        return this.player.world.isClient;
    }

    /**
     Determine whether a given item stack matches this component.

     @param stack the item stack
     @return whether `stack` matches this component.
     */
    public boolean accepts(ItemStack stack) {
        return stack.getItem() == this.item();
    }

    /**
     @return whether the user has permanently unlocked this item.
     */
    public boolean isUnlocked() {
        return this.unlocked;
    }

    public void unlock() {
        this.unlocked = true;
    }

    public int size(Category category) {
        return this.statistics.size(category);
    }

    public Statistic statistic(StatisticType statistic) {
        return this.statistics.get(statistic);
    }

    public Statistic statistic(Category category, StatisticType statistic) {
        return this.statistics.get(category, statistic);
    }

    /**
     Calculate the total value of an attribute with all relevant enchantments applied.

     @param attribute the type of the attribute
     @return the total value of the attribute.
     */
    public double attributeTotal(StatisticType attribute) {
        if (attribute == StatisticType.attackDamage) {
            var attackDamage = this.doubleValue(StatisticType.attackDamage);

            for (var entry : this.enchantments.entrySet()) {
                var enchantment = entry.getKey();

                if (entry.getValue() > 0) {
                    attackDamage += enchantment.getAttackDamage(this.enchantment(enchantment), EntityGroup.DEFAULT);
                }
            }

            return attackDamage;
        }

        return this.doubleValue(attribute);
    }

    /**
     Calculate the value of an attribute relative to its base value.

     @param attribute the type of the attribute
     @return the relative value of the attribute.
     */
    public double attributeRelative(StatisticType attribute) {
        if (attribute == StatisticType.attackSpeed) return this.doubleValue(StatisticType.attackSpeed) - 4;
        if (attribute == StatisticType.attackDamage) return this.doubleValue(StatisticType.attackDamage) - 1;
        if (attribute == StatisticType.reach) return this.doubleValue(StatisticType.reach) - 3;

        return this.doubleValue(attribute);
    }

    public int intValue(StatisticType type) {
        return this.statistic(type).intValue();
    }

    public float floatValue(StatisticType type) {
        return this.statistic(type).floatValue();
    }

    public double doubleValue(StatisticType type) {
        return this.statistic(type).doubleValue();
    }

    public boolean incrementStatistic(StatisticType type, double amount) {
        var leveledUp = false;

        if (type == StatisticType.experience) {
            var statistic = this.statistic(StatisticType.experience);
            statistic.add(amount);
            var xp = statistic.intValue();

            if (xp >= this.nextLevelXP() && this.canLevelUp()) {
                var nextLevelXP = this.nextLevelXP();

                this.incrementStatistic(StatisticType.level, 1);
                this.incrementStatistic(StatisticType.experience, -nextLevelXP);

                leveledUp = true;
            }

            if (xp < 0) {
                this.incrementStatistic(StatisticType.level, -1);
                this.incrementStatistic(StatisticType.experience, this.getLevelXP(this.intValue(StatisticType.level) - 1));
            }
        } else if (type == StatisticType.level) {
            var sign = Math2.signum(amount);

            for (var i = 0; i < Math.abs(amount); i++) {
                this.levelUp(sign);
            }
        } else {
            this.statistics.add(type, amount);
        }

        this.updateItemStack();
        this.sync();

        return leveledUp;
    }

    public void incrementPoints(StatisticType type, int points) {
        if (points == 0) {
            return;
        }

        var statistic = this.statistic(type);
        BigDecimal change;

        if (points > 0) {
            change = BigDecimal.valueOf(statistic.max()).subtract(statistic.value());
            var newPoints = change.divide(BigDecimal.valueOf(this.increase(type)), RoundingMode.UP);
            var bigPoints = BigDecimal.valueOf(points);

            if (newPoints.compareTo(bigPoints) < 0) {
                points = newPoints.intValue();
            } else {
                change = BigDecimal.valueOf(this.increase(type)).multiply(bigPoints);
            }
        } else {
            change = BigDecimal.valueOf(statistic.min()).subtract(statistic.value());
            var newPoints = change.divide(BigDecimal.valueOf(this.increase(type)), RoundingMode.UP);
            var bigPoints = BigDecimal.valueOf(points);

            if (newPoints.compareTo(bigPoints) > 0) {
                points = newPoints.intValue();
            } else {
                change = BigDecimal.valueOf(this.increase(type)).multiply(bigPoints);
            }
        }

        var attributePoints = this.statistic(StatisticType.attributePoints);
        var spentAttributePoints = this.statistic(StatisticType.spentAttributePoints);

        attributePoints.add(-points);
        spentAttributePoints.add(points);
        statistic.add(change);
        statistic.incrementPoints(points);

        this.updateItemStack();
        this.sync();
    }

    public void set(StatisticType statistic, Number value) {
        this.statistics.put(statistic, value);
        this.sync();
    }

    public boolean canLevelUp() {
        var configuration = Configuration.instance();
        return this.intValue(StatisticType.level) < configuration.maxLevel || configuration.maxLevel < 0;
    }

    /**
     Increment the level and add attribute, enchantment and skill points.

     @param sign 1 if leveling up; -1 if leveling down
     */
    public void levelUp(int sign) {
        var level = this.statistic(StatisticType.level);
        level.add(sign);
        var configuration = Configuration.instance();

        if (level.intValue() % configuration.levelsPerEnchantment == 0) {
            this.incrementStatistic(StatisticType.enchantmentPoints, sign);
        }

        if (level.intValue() % configuration.levelsPerSkillPoint == 0) {
            this.incrementStatistic(StatisticType.skillPoints, sign);
        }

        this.incrementStatistic(StatisticType.attributePoints, sign);
    }

    public Collection<SkillContainer> skills() {
        return this.skills.values();
    }

    public SkillContainer skill(Skill skill) {
        return this.skills.get(skill);
    }

    public SkillContainer skill(Identifier identifier) {
        return this.skill(Skill.registry.getValue(identifier));
    }

    public boolean hasSkill(Identifier identifier) {
        return this.skill(identifier) != null;
    }

    public boolean hasSkill(Skill skill) {
        var container = this.skill(skill);
        return container != null && container.learned();
    }

    public boolean hasSkill(Skill skill, int level) {
        var container = this.skill(skill);
        return container != null && container.learned() && container.level() >= level;
    }

    /**
     Learn or upgrade a skill.
     */
    public void upgrade(SkillContainer skill) {
        if (this.isClient()) {
            Packets.serverSkill.send(new ExtendedPacketBuffer(this).writeIdentifier(skill.skill.getRegistryName()));
        } else {
            var points = this.intValue(StatisticType.skillPoints);
            var cost = skill.cost();

            // Synchronization is handled by incrementStatistic.
            if (skill.canLearn(points)) {
                skill.learn();
                this.incrementStatistic(StatisticType.skillPoints, -cost);
            } else if (skill.canUpgrade(points)) {
                skill.upgrade();
                this.incrementStatistic(StatisticType.skillPoints, -cost);
            }
        }
    }

    /**
     @return the XP required in order to reach the next level.
     */
    public int nextLevelXP() {
        return this.getLevelXP(this.intValue(StatisticType.level));
    }

    /**
     @return the current level of `enchantment`.
     */
    public int enchantment(Enchantment enchantment) {
        return this.enchantments.get(enchantment);
    }

    /**
     Add levels to an enchantment.

     @param enchantment the enchantment
     @param levels      the number of levels to add to `enchantment`
     */
    public void addEnchantment(Enchantment enchantment, int levels) {
        var current = this.enchantment(enchantment);
        var change = Math.max(0, current + levels) - current;

        this.statistics.add(StatisticType.enchantmentPoints, -change);
        this.statistics.add(StatisticType.spentEnchantmentPoints, change);

        this.enchantments.add(enchantment, change);
        this.updateItemStack();

        this.sync();
    }

    /**
     {@linkplain #reset(Category) reset} all statistic categories.
     */
    public void reset() {
        for (var category : Category.registry) {
            this.reset(category);
        }

        this.unlocked = false;
        this.boundSlot = -1;

        this.sync();
    }

    /**
     Reset all of this item's statistics in a category.

     @param category the category to reset
     */
    public void reset(Category category) {
        if (category == Category.datum) {
            this.statistics.reset(Category.datum);
        } else if (category == Category.attribute) {
            for (var type : this.statistics.get(Category.attribute).values()) {
                this.incrementStatistic(StatisticType.attributePoints, type.points());
                type.reset();
            }

            this.set(StatisticType.spentAttributePoints, 0);
            this.updateItemStack();
        } else if (category == Category.enchantment) {
            for (var enchantment : this.enchantments) {
                this.incrementStatistic(StatisticType.enchantmentPoints, this.enchantments.get(enchantment));
                this.enchantments.put(enchantment, 0);
            }

            this.statistic(StatisticType.spentEnchantmentPoints).setToMin();
            this.updateItemStack();
        } else if (category == Category.skill) {
            this.skills.reset();
            this.statistic(StatisticType.skillPoints).setToMin();
        }

        this.sync();
    }

    /**
     @return whether the given item stack may be consumed in order to unlock this item.
     */
    public boolean canConsume(ItemStack stack) {
        return this.consumableItem() == stack.getItem();
    }

    public int boundSlot() {
        return this.boundSlot;
    }

    public void bindSlot(int boundSlot) {
        this.boundSlot = boundSlot;
    }

    public boolean hasBoundSlot() {
        return this.boundSlot != -1;
    }

    public void unbindSlot() {
        this.boundSlot = -1;
    }

    /**
     @return the item stack in the bound slot.
     @throws IndexOutOfBoundsException if no slot is bound.
     */
    public final ItemStack stackInBoundSlot() {
        return this.player.inventory.getStack(this.boundSlot);
    }

    /**
     @return whether an item stack in any of the player's hands matches this component.
     */
    public boolean isItemEquipped() {
        return ItemUtil.handStacks(this.player).anyMatch(this::accepts);
    }

    /**
     Scan the inventory and clean it up.
     <br><br>
     If this component's player is not in creative mode,
     then remove their item stacks that do not correspond to this component or are not in the slot specified by `slot`.
     <br><br>
     If `slot` is -1, then set it to the bound slot if the item stack therein matches this component or the slot of the first item stack that matches this component.
     <br><br>
     If `slot` is still -1 and a matching item stack is encountered, then <br>
     - if the bound slot does not match `slot`, then bind that slot; <br>
     - if the item stack is not the current item stack, then replace it.
     <br><br>
     If a matching item stack is encountered and it does not equal {@link #itemStack}, then replace it by a copy thereof.

     @param slot the slot from which to not remove
     */
    public void updateInventory(int slot) {
        if (slot == -1 && this.hasBoundSlot() && this.accepts(this.stackInBoundSlot())) {
            slot = this.boundSlot;
        }

        var inventory = ItemUtil.inventory(this.player).toList().listIterator();

        while (inventory.hasNext()) {
            var stack = inventory.next();

            if (this.component.accepts(stack)) {
                if (this.accepts(stack)) {
                    if (slot == -1) {
                        slot = inventory.previousIndex();

                        if (this.hasBoundSlot()) {
                            this.bindSlot(slot);
                        }
                    } else if (inventory.previousIndex() != slot) {
                        this.player.inventory.removeOne(stack);

                        continue;
                    }

                    if (!stack.equals(this.itemStack, false)) {
                        this.player.inventory.setStack(slot, this.stack());
                    }
                } else if (!this.player.isCreative()) {
                    this.player.inventory.removeOne(stack);
                }
            }
        }
    }

    /**
     @return the attribute modifiers for new stacks of this item.
     */
    public final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(EquipmentSlot slot) {
        var modifiers = LinkedHashMultimap.<EntityAttribute, EntityAttributeModifier>create();
        this.attributeModifiers(modifiers, slot);

        return modifiers;
    }

    /**
     @return a copy of the current item stack.
     */
    public ItemStack stack() {
        return this.itemStack.copy();
    }

    /**
     Synchronize information to the client.
     */
    public void sync() {
        Packets.clientSync.sendIfServer(this.player, new ExtendedPacketBuffer(this).writeNbt(this.serialize()));
    }

    /**
     @return a new item stack with all statistics applied.
     */
    protected ItemStack newItemStack() {
        this.itemStack = this.item().getDefaultStack();
        Components.marker.of(this.itemStack).item = this;

        if (this.statistics != null && this.enchantments != null && this.skills != null) {
            for (var slot : EquipmentSlot.values()) {
                var attributeModifiers = this.attributeModifiers(slot);

                for (var attribute : attributeModifiers.keySet()) {
                    for (var modifier : attributeModifiers.get(attribute)) {
                        this.itemStack.addAttributeModifier(attribute, modifier, EquipmentSlot.MAINHAND);
                    }
                }
            }

            for (var entry : this.enchantments.entrySet()) {
                int level = entry.getValue();

                if (level > 0) {
                    this.itemStack.addEnchantment(entry.getKey(), level);
                }
            }
        }

        return this.itemStack;
    }

    /**
     Replace the current itme stack by a {@linkplain #newItemStack new item stack}.
     */
    protected void updateItemStack() {
        this.itemStack = this.newItemStack();
    }

    /**
     Format the value of a statistic of a given type.

     @param statistic the statistic type
     @return the formatted value.
     */
    protected String formatStatistic(StatisticType statistic) {
        var value = this.attributeTotal(statistic);
        return statisticFormat.format(statistic == StatisticType.criticalStrikeRate ? value * 100 : value);
    }

    /**
     @return an addition weapon attribute modifier with the given UUID and whose value is the relative value of the given statistic type.
     */
    protected final EntityAttributeModifier weaponModifier(UUID attribute, StatisticType statistic) {
        return new EntityAttributeModifier(attribute, "Weapon modifier", this.attributeRelative(statistic), EntityAttributeModifier.Operation.ADDITION);
    }

    /**
     @return an addition tool attribute modifier with the given UUID and whose value is the relative value of the given statistic type.
     */
    protected final EntityAttributeModifier toolModifier(UUID attribute, StatisticType statistic) {
        return new EntityAttributeModifier(attribute, "Tool modifier", this.attributeRelative(statistic), EntityAttributeModifier.Operation.ADDITION);
    }

    @Override
    public void serialize(NbtCompound tag) {
        tag.put("statistics", this.statistics.serialize());
        tag.put("enchantments", this.enchantments.serialize());
        tag.put("skills", this.skills.serialize());
        tag.putBoolean("unlocked", this.unlocked);
        tag.putInt("slot", this.boundSlot());
    }

    @Override
    public void deserialize(NbtCompound tag) {
        this.statistics.deserialize(tag.getCompound("statistics"));
        this.enchantments.deserialize(tag.getCompound("enchantments"));
        this.skills.deserialize(tag.getCompound("skills"));
        this.unlocked = tag.getBoolean("unlocked");
        this.bindSlot(tag.getInt("slot"));

        this.updateItemStack();
    }
}
