package soulboundarmory.component.soulbound.item;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import soulboundarmory.client.gui.screen.AttributeTab;
import soulboundarmory.client.gui.screen.EnchantmentTab;
import soulboundarmory.client.gui.screen.SelectionTab;
import soulboundarmory.client.gui.screen.SkillTab;
import soulboundarmory.client.gui.screen.SoulboundScreen;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.weapon.WeaponComponent;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.EnchantmentStorage;
import soulboundarmory.component.statistics.SkillMap;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.component.statistics.Statistics;
import soulboundarmory.config.Configuration;
import soulboundarmory.entity.Attributes;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.entity.SoulboundLightningEntity;
import soulboundarmory.item.SoulboundItems;
import soulboundarmory.module.gui.widget.Widget;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.serial.Serializable;
import soulboundarmory.skill.Skill;
import soulboundarmory.skill.SkillInstance;
import soulboundarmory.skill.Skills;
import soulboundarmory.util.AttributeModifierIdentifiers;
import soulboundarmory.util.ItemUtil;
import soulboundarmory.util.Math2;
import soulboundarmory.util.Sided;

public abstract class ItemComponent<T extends ItemComponent<T>> implements Serializable, Sided {
    protected static final NumberFormat statisticFormat = DecimalFormat.getInstance();

    public final MasterComponent<?> component;
    public final PlayerEntity player;
    public final EnchantmentStorage enchantments = new EnchantmentStorage(this);
    public final Statistics statistics = new Statistics(this);

    protected final SkillMap skills = new SkillMap(this);
    protected ItemStack itemStack;
    protected boolean unlocked;
    protected int animationTime;

    public ItemComponent(MasterComponent<?> component) {
        this.component = component;
        this.player = component.player;

        this.skills.add(Skills.enderPull);
    }

    /**
     @return all item components attached to `entity`
     */
    public static Stream<ItemComponent<?>> all(Entity entity) {
        return Components.soulbound(entity).flatMap(component -> component.items.values().stream());
    }

    /**
     @return the component attached to `entity` that matches `stack`
     */
    public static Optional<ItemComponent<?>> of(Entity entity, ItemStack stack) {
        return Components.soulbound(entity).filter(component -> component.accepts(stack)).flatMap(component -> component.items.values().stream().filter(item -> item.accepts(stack))).findAny();
    }

    /**
     Find the first component that matches an item held by an entity.

     @return the component
     */
    public static Optional<ItemComponent<?>> fromHands(Entity entity) {
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

    public static Optional<ItemComponent<?>> fromMainHand(LivingEntity entity) {
        return of(entity, entity.getMainHandStack());
    }

    /**
     Find the item component corresponding to the weapon that an attacker used.

     @param source the source of the damage that the attacker inflicted
     @return an {@link Optional} containing the item component if it has been found
     */
    public static Optional<? extends ItemComponent<?>> fromAttacker(LivingEntity target, DamageSource source) {
        var entity = source.getSource();

        if (entity instanceof PlayerEntity) {
            return fromHands(entity).filter(WeaponComponent.class::isInstance);
        }

        var attacker = source.getAttacker();

        if (attacker == null) {
            attacker = target.getDamageTracker().getBiggestAttacker();
        }

        return entity instanceof SoulboundDaggerEntity ? ItemComponentType.dagger.nullable(attacker)
            : entity instanceof SoulboundLightningEntity ? ItemComponentType.sword.nullable(attacker)
                : Optional.empty();
    }

    /**
     @return the type of this item
     */
    public abstract ItemComponentType<T> type();

    /**
     @return the item that corresponds to this component
     */
    public abstract Item item();

    /**
     @return the item that may be consumed in order to unlock this item
     */
    public abstract Item consumableItem();

    /**
     @return the name of this item without a "soulbound" prefix
     */
    public abstract Text name();

    /**
     @return the increase in `statistic` per point
     @param type
     */
    public abstract double increase(StatisticType type);

    /**
     @param level a level
     @return the XP required in order to reach level `level` from the previous level
     */
    public abstract int levelXP(int level);

    /**
     @return a list of attributes for display on the attribute tab for this item
     */
    public abstract List<StatisticType> screenAttributes();

    /**
     @return the tooltip for stacks of this item
     */
    public abstract List<Text> tooltip();

    public void killed(LivingEntity entity) {}

    public void mined(BlockState state, BlockPos position) {}

    @Override
    public final boolean isClient() {
        return this.player.world.isClient;
    }

    public final int level() {
        return this.intValue(StatisticType.level);
    }

    public final double experience() {
        return this.doubleValue(StatisticType.experience);
    }

    public final int attributePoints() {
        return this.intValue(StatisticType.attributePoints);
    }

    public final int enchantmentPoints() {
        return this.intValue(StatisticType.enchantmentPoints);
    }

    public final int skillPoints() {
        return this.intValue(StatisticType.skillPoints);
    }

    public final double attackDamage() {
        return this.doubleValue(StatisticType.attackDamage);
    }

    public final double attackSpeed() {
        return this.doubleValue(StatisticType.attackSpeed);
    }

    public final double criticalHitRate() {
        return this.doubleValue(StatisticType.criticalHitRate);
    }

    public final double efficiency() {
        return this.doubleValue(StatisticType.efficiency);
    }

    public final int animationTime() {
        return this.animationTime;
    }

    public int maxLevel() {
        return Configuration.instance().maxLevel;
    }

    /**
     @return this item's current tool material
     */
    public ToolMaterial material() {
        return SoulboundItems.baseMaterial;
    }

    public int totalXP(int level) {
        var total = 0;

        for (; level > 0; level--) {
            total += this.levelXP(level);
        }

        return total;
    }

    /**
     Determine whether a given item stack matches this component.

     @param stack the item stack
     @return whether `stack` matches this component
     */
    public boolean accepts(ItemStack stack) {
        return stack.getItem() == this.item();
    }

    /**
     @return the tabs to display in the menu for this item
     */
    @OnlyIn(Dist.CLIENT)
    public List<SoulboundTab> tabs() {
        return ReferenceList.of(new SelectionTab(), new AttributeTab(), new EnchantmentTab(), new SkillTab());
    }

    /**
     @return whether the user has permanently unlocked this item
     */
    public boolean isUnlocked() {
        return this.unlocked;
    }

    public void unlock() {
        if (!this.unlocked) {
            this.unlocked = true;

            if (this.isClient()) {
                if (Widget.cellScreen() instanceof SoulboundScreen screen) {
                    screen.close();
                    this.component.tab(1);
                }
            } else {
                Packets.clientUnlock.sendNearby(this.player, new ExtendedPacketBuffer().writeEntity(this.player).writeItemStack(this.itemStack));
            }
        }
    }

    public void select(int slot) {
        if (this.isClient()) {
            Packets.serverSelectItem.send(new ExtendedPacketBuffer(this).writeInt(slot));
        }

        if (this.isUnlocked() || this.canConsume(this.player.getInventory().getStack(slot))) {
            this.player.getInventory().setStack(slot, this.stack());
            this.component.select(this);
            this.updateInventory(slot);
            this.synchronize();
            this.component.refresh();
        }
    }

    /**
     @return this component's instance of a statistic type if it is present or null
     */
    public Statistic statistic(StatisticType statistic) {
        return this.statistics.get(statistic);
    }

    /**
     Calculate the total value of an attribute with all relevant enchantments applied.

     @param attribute the type of the attribute
     @return the total value of the attribute
     */
    public double attributeTotal(StatisticType attribute) {
        var doubleValue = this.doubleValue(attribute);

        if (attribute == StatisticType.attackDamage) {
            for (var entry : this.enchantments.reference2IntEntrySet()) {
                var enchantment = entry.getKey();

                if (entry.getIntValue() > 0) {
                    doubleValue += enchantment.getAttackDamage(this.enchantment(enchantment), EntityGroup.DEFAULT);
                }
            }
        } else if (attribute == StatisticType.efficiency) {
            var efficiency = this.enchantment(Enchantments.EFFICIENCY);
            doubleValue += this.material().getMiningSpeedMultiplier() + Math2.square(efficiency);

            if (efficiency > 0) {
                doubleValue++;
            }
        }

        return doubleValue;
    }

    /**
     Calculate the value of an attribute relative to its base value.

     @param attribute the type of the attribute
     @return the relative value of the attribute
     */
    public double attributeRelative(StatisticType attribute) {
        if (attribute == StatisticType.attackSpeed) return this.attackSpeed() - 4;
        if (attribute == StatisticType.attackDamage) return this.attackDamage() - 1;
        if (attribute == StatisticType.reach) return this.doubleValue(StatisticType.reach) - 3;
        if (attribute == StatisticType.efficiency) return this.efficiency() - this.material().getMiningSpeedMultiplier();

        return this.doubleValue(attribute);
    }

    /**
     @return the integral value of a statistic if it is present or 0
     */
    public int intValue(StatisticType type) {
        var statistic = this.statistic(type);
        return statistic == null ? 0 : statistic.intValue();
    }

    /**
     @return the float value of a statistic if it is present or 0
     */
    public float floatValue(StatisticType type) {
        var statistic = this.statistic(type);
        return statistic == null ? 0 : statistic.floatValue();
    }

    /**
     @return the double value of a statistic if it is present or 0
     */
    public double doubleValue(StatisticType type) {
        var statistic = this.statistic(type);
        return statistic == null ? 0 : statistic.doubleValue();
    }

    /**
     Increase a statistic with special handling for experience points and level and synchronize.
     If the item leveled up and levelup messages are enabled, then send a message.

     @param type   the type of the statistic
     @param amount the amount to add
     */
    public void add(StatisticType type, double amount) {
        var leveledUp = false;

        if (type == StatisticType.experience) {
            var statistic = this.statistic(StatisticType.experience);
            statistic.add(amount);
            var xp = statistic.intValue();

            if (xp >= this.nextLevelXP() && this.canLevelUp()) {
                var nextLevelXP = this.nextLevelXP();

                this.add(StatisticType.level, 1);
                this.add(StatisticType.experience, -nextLevelXP);

                leveledUp = true;
            }

            if (xp < 0) {
                this.add(StatisticType.level, -1);
                this.add(StatisticType.experience, this.levelXP(this.level() - 1));
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
        this.synchronize();

        if (leveledUp && Components.config.of(this.player).levelupNotifications) {
            this.player.sendMessage(Translations.levelupMessage.format(this.itemStack.getName(), this.level()), true);
        }
    }

    /**
     Add points to an attribute.

     @param type   the type of the attribute.
     @param points the number of points to add; will be clamped in order to not exceed the attribute's bounds and available attribute points.
     */
    public void addAttribute(StatisticType type, int points) {
        var attributePoints = this.statistic(StatisticType.attributePoints);
        var attribute = this.statistic(type);
        var bigPoints = BigDecimal.valueOf(points);
        var bigIncrease = BigDecimal.valueOf(this.increase(type));
        var change = bigIncrease.multiply(bigPoints);

        if (points > 0) {
            var maxChange = BigDecimal.valueOf(attribute.max()).subtract(attribute.value());

            if (maxChange.compareTo(change) < 0) {
                change = maxChange;
                points = change.divide(bigIncrease, RoundingMode.UP).intValue();
            }

            if (points > attributePoints.intValue()) {
                change = bigIncrease.multiply(attributePoints.value());
                points = attributePoints.intValue();
            }
        } else {
            var maxChange = BigDecimal.valueOf(attribute.min()).subtract(attribute.value());

            if (maxChange.compareTo(change) > 0) {
                change = maxChange;
                points = change.divide(bigIncrease, RoundingMode.UP).intValue();
            }
        }

        if (change.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        if (points > 0 || Configuration.instance().freeRestoration) {
            attributePoints.add(-points);

            if (points > 0) {
                this.statistics.history.record(type, points);
            }
        } else {
            this.add(StatisticType.level, points);
        }

        attribute.add(change);

        this.updateItemStack();
        this.synchronize();
    }

    /**
     Set the value of a statistic and synchronize.
     */
    public void set(StatisticType statistic, Number value) {
        this.statistics.put(statistic, value);
        this.synchronize();
    }

    /**
     @return whether this item can level up further, taking the configuration into account
     */
    public boolean canLevelUp() {
        var configuration = Configuration.instance();
        return this.level() < configuration.maxLevel || configuration.maxLevel < 0;
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
            this.add(StatisticType.enchantmentPoints, sign);
        }

        if (level.intValue() % configuration.levelsPerSkillPoint == 0) {
            this.add(StatisticType.skillPoints, sign);
        }

        this.add(StatisticType.attributePoints, sign);
    }

    /**
     @return the XP required in order to reach the next level
     */
    public int nextLevelXP() {
        return this.levelXP(this.level());
    }

    public Collection<SkillInstance> skills() {
        return this.skills.values();
    }

    public SkillInstance skill(Skill skill) {
        return this.skills.get(skill);
    }

    public SkillInstance skill(Identifier identifier) {
        return this.skill(Skills.registry().getValue(identifier));
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
    public void upgrade(SkillInstance skill) {
        if (this.isClient()) {
            Packets.serverSkill.send(new ExtendedPacketBuffer(this).writeIdentifier(skill.skill.id()));
        } else if (skill.canUpgrade(this.skillPoints())) {
            this.add(StatisticType.skillPoints, -skill.cost());
            skill.upgrade();
            this.synchronize();
        }
    }

    /**
     @return the current level of `enchantment`
     */
    public int enchantment(Enchantment enchantment) {
        return this.enchantments.get(enchantment);
    }

    /**
     Add levels to an enchantment.

     @param enchantment the enchantment
     @param levels      the number of levels to add
     */
    public void addEnchantment(Enchantment enchantment, int levels) {
        var enchantmentPoints = this.statistic(StatisticType.enchantmentPoints);
        var current = this.enchantment(enchantment);

        if (levels > 0) {
            levels = Math.min(levels, enchantmentPoints.intValue());
        } else if (levels < 0) {
            levels = Math.max(levels, -current);
        }

        var change = Math.max(0, current + levels) - current;
        this.enchantments.add(enchantment, change);

        if (change > 0 || Configuration.instance().freeRestoration) {
            enchantmentPoints.add(-change);
        } else {
            this.add(StatisticType.level, Configuration.instance().levelsPerEnchantment * change);
        }

        this.updateItemStack();

        Packets.clientEnchant.sendIfServer(this.player, new ExtendedPacketBuffer(this)
            .writeRegistryEntry(enchantment)
            .writeInt(current + change)
            .writeInt(enchantmentPoints.intValue())
        );
    }

    /**
     Reset all of this item's statistics in a category.

     @param category the category to reset
     */
    public void reset(Category category) {
        if (category == Category.datum) {
            this.statistics.reset(Category.datum);
        } else if (category == Category.attribute) {
            for (var attribute : this.statistics.get(Category.attribute).values()) {
                this.addAttribute(attribute.type, Integer.MIN_VALUE);
                attribute.reset();
            }
        } else if (category == Category.enchantment) {
            for (var enchantment : this.enchantments) {
                this.addEnchantment(enchantment, Integer.MIN_VALUE);
            }
        } else if (category == Category.skill) {
            this.skills.values().forEach(skill -> {
                if (Configuration.instance().freeRestoration) {
                    this.statistic(StatisticType.skillPoints).add(skill.spentPoints());
                } else {
                    this.add(StatisticType.level, Configuration.instance().levelsPerSkillPoint * -skill.spentPoints());
                }

                skill.reset();
            });
        }

        this.synchronize();
    }

    /**
     {@linkplain #reset(Category) Reset} all statistic categories and lock this item.
     */
    public void reset() {
        var level = this.level();

        for (var category : Category.registry()) {
            this.reset(category);
        }

        if (Configuration.instance().freeRestoration) {
            this.set(StatisticType.attributePoints, level);
        }

        this.unlocked = false;
        this.synchronize();
    }

    /**
     @return whether the given item stack may be consumed in order to unlock this item
     */
    public boolean canConsume(ItemStack stack) {
        return this.consumableItem() == stack.getItem();
    }

    /**
     @return whether an item stack in any of the player's hands matches this component
     */
    public boolean isItemEquipped() {
        return ItemUtil.handStacks(this.player).anyMatch(this::accepts);
    }

    /**
     Scan the inventory and clean it up.
     <br>
     - If the player is not in creative mode,
     then remove their item stacks that do not correspond to this component or are not in the slot specified by `slot`.
     <br>
     - If `slot` is -1, a matching item stack is encountered and the bound slot does not match `slot`, then bind that slot.
     <br>
     - If a matching item stack is encountered and it does not equal {@link #itemStack}, then replace it by a copy thereof.

     @param slot the slot from which to not remove
     */
    public void updateInventory(int slot) {
        var inventory = ItemUtil.inventory(this.player).toList().listIterator();

        while (inventory.hasNext()) {
            var stack = inventory.next();

            if (this.component.accepts(stack)) {
                if (this.accepts(stack)) {
                    if (slot == -1) {
                        slot = inventory.previousIndex();

                        if (this.component.hasBoundSlot()) {
                            this.component.bindSlot(slot);
                        }
                    } else if (inventory.previousIndex() != slot) {
                        this.player.getInventory().removeOne(stack);

                        continue;
                    }

                    if (!stack.equals(this.itemStack, false)) {
                        this.player.getInventory().setStack(slot, this.stack());
                    }
                } else if (!this.player.isCreative()) {
                    this.player.getInventory().removeOne(stack);
                }
            }
        }
    }

    /**
     Put attribute modifiers into the given map for a new stack of this item.

     @param modifiers the map into which to put the modifiers
     */
    public void attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, StatisticType.attackSpeed));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, StatisticType.attackDamage));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), this.weaponModifier(Attributes.reach, StatisticType.reach));
        }
    }

    /**
     @return the attribute modifiers for new stacks of this item
     */
    public final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(EquipmentSlot slot) {
        var modifiers = LinkedHashMultimap.<EntityAttribute, EntityAttributeModifier>create();
        this.attributeModifiers(modifiers, slot);

        return modifiers;
    }

    public Text format(StatisticType statistic) {
        if (statistic == StatisticType.attackDamage) return Translations.guiAttackDamage.format(this.formatValue(statistic));
        if (statistic == StatisticType.attackSpeed) return Translations.guiAttackSpeed.format(this.formatValue(statistic));
        if (statistic == StatisticType.criticalHitRate) return Translations.guiCriticalHitRate.format(this.formatValue(statistic));
        if (statistic == StatisticType.efficiency) return Translations.guiEfficiency.format(this.formatValue(statistic));
        if (statistic == StatisticType.reach) return Translations.guiReach.format(this.formatValue(statistic));

        return Text.of("%s: %s".formatted(statistic.id().getPath(), this.formatValue(statistic)));
    }

    /**
     Ensure that the client's component is up to date with the server.

     @see #serialize(NbtCompound)
     @see #deserialize(NbtCompound)
     */
    public void synchronize() {
        Packets.clientSyncItem.sendIfServer(this.player, new ExtendedPacketBuffer(this).writeNbt(this.serialize()));
    }

    /**
     @return a copy of the current item stack
     */
    public ItemStack stack() {
        return this.itemStack.copy();
    }

    /**
     Replace the current itme stack by a {@linkplain #newItemStack new item stack}.
     */
    public void updateItemStack() {
        this.itemStack = this.newItemStack();
    }

    /**
     Invoked every tick.
     */
    public void tick() {
        this.animationTime--;
    }

    /**
     @return a new item stack with all statistics applied
     */
    protected ItemStack newItemStack() {
        this.itemStack = this.item().getDefaultStack();
        Components.marker.of(this.itemStack).item(this);

        for (var slot : EquipmentSlot.values()) {
            var attributeModifiers = this.attributeModifiers(slot);

            for (var attribute : attributeModifiers.keySet()) {
                for (var modifier : attributeModifiers.get(attribute)) {
                    this.itemStack.addAttributeModifier(attribute, modifier, EquipmentSlot.MAINHAND);
                }
            }
        }

        this.enchantments.forEach((enchantment, level) -> {
            if (level > 0) {
                this.itemStack.addEnchantment(enchantment, level);
            }
        });

        return this.itemStack;
    }

    /**
     Format the value of a statistic of a given type.

     @param statistic the statistic type
     @return the formatted value
     */
    protected String formatValue(StatisticType statistic) {
        if (statistic == StatisticType.upgradeProgress || statistic == StatisticType.criticalHitRate) {
            return this.formatPercentage(statistic);
        }

        return statisticFormat.format(this.attributeTotal(statistic));
    }

    protected String formatPercentage(StatisticType statistic) {
        return Integer.toString((int) (this.floatValue(statistic) * 100));
    }

    /**
     @return an addition weapon attribute modifier with the given UUID and whose value is the relative value of the given statistic type
     */
    protected final EntityAttributeModifier weaponModifier(UUID attribute, StatisticType statistic) {
        return new EntityAttributeModifier(attribute, "Weapon modifier", this.attributeRelative(statistic), EntityAttributeModifier.Operation.ADDITION);
    }

    /**
     @return an addition tool attribute modifier with the given UUID and whose value is the relative value of the given statistic type
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
    }

    @Override
    public void deserialize(NbtCompound tag) {
        this.statistics.deserialize(tag.getCompound("statistics"));
        this.enchantments.deserialize(tag.getCompound("enchantments"));
        this.skills.deserialize(tag.getCompound("skills"));
        this.unlocked = tag.getBoolean("unlocked");

        this.updateItemStack();
    }
}
