package user11681.soulboundarmory.capability.soulbound.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.CapabilityContainer;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.capability.statistics.Category;
import user11681.soulboundarmory.capability.statistics.EnchantmentStorage;
import user11681.soulboundarmory.capability.statistics.SkillStorage;
import user11681.soulboundarmory.capability.statistics.Statistic;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.capability.statistics.Statistics;
import user11681.soulboundarmory.client.gui.screen.tab.ScreenTab;
import user11681.soulboundarmory.client.gui.screen.tab.SoulboundTab;
import user11681.soulboundarmory.client.gui.screen.tab.StatisticEntry;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;
import user11681.soulboundarmory.serial.CompoundSerializable;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.SkillContainer;
import user11681.soulboundarmory.util.ItemUtil;

import static user11681.soulboundarmory.SoulboundArmoryClient.client;
import static user11681.soulboundarmory.capability.statistics.Category.attribute;
import static user11681.soulboundarmory.capability.statistics.Category.datum;
import static user11681.soulboundarmory.capability.statistics.Category.enchantment;
import static user11681.soulboundarmory.capability.statistics.Category.skill;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackDamage;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackSpeed;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.criticalStrikeProbability;
import static user11681.soulboundarmory.capability.statistics.StatisticType.enchantmentPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.experience;
import static user11681.soulboundarmory.capability.statistics.StatisticType.level;
import static user11681.soulboundarmory.capability.statistics.StatisticType.reach;
import static user11681.soulboundarmory.capability.statistics.StatisticType.skillPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentAttributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentEnchantmentPoints;

public abstract class ItemStorage<T extends ItemStorage<T>> implements CompoundSerializable {
    protected static final NumberFormat statisticFormat = DecimalFormat.getInstance();

    public EnchantmentStorage enchantments;

    protected final SoulboundCapability component;
    protected final PlayerEntity player;
    protected final Item item;
    protected final boolean isClientSide;

    protected SkillStorage skills;
    protected Statistics statistics;
    protected ItemStack itemStack;
    protected boolean unlocked;
    protected int boundSlot;
    protected int currentTab;

    public ItemStorage(SoulboundCapability component, Item item) {
        this.component = component;
        this.player = component.entity;
        this.isClientSide = component.entity.level.isClientSide;
        this.item = item;
        this.itemStack = this.newItemStack();
    }

    public static ItemStorage<?> get(Entity entity, Item item) {
        return Capabilities.get(entity).flatMap(component -> component.storages().values().stream()).filter(storage -> storage.player == entity && storage.getItem() == item).findAny().orElse(null);
    }

    public static ItemStorage<?> get(Entity entity, StorageType<?> type) {
        for (CapabilityContainer<? extends SoulboundCapability> component : Capabilities.soulboundCapabilities) {
            for (ItemStorage<?> storage : component.get(entity).storages().values()) {
                if (storage.type() == type) {
                    return storage;
                }
            }
        }

        return null;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    public SoulboundCapability getComponent() {
        return this.component;
    }

    public Item getItem() {
        return this.item;
    }

    public ItemStack getMenuEquippedStack() {
        for (ItemStack itemStack : this.player.getHandSlots()) {
             Item item = itemStack.getItem();

            if (item == this.getItem() || item == this.getConsumableItem()) {
                return itemStack;
            }
        }

        return null;
    }

    public boolean isUnlocked() {
        return this.unlocked;
    }

    public void unlocked(boolean unlocked) {
        this.unlocked = unlocked;
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

    public int datum(StatisticType statistic) {
        return this.statistics.get(statistic).intValue();
    }

    public double attributeTotal(StatisticType statistic) {
        if (statistic == attackDamage) {
            double attackDamage = this.attribute(StatisticType.attackDamage);

            for (Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
                 Enchantment enchantment = entry.getKey();

                if (entry.getValue() > 0) {
                    attackDamage += enchantment.getDamageBonus(this.enchantment(enchantment), CreatureAttribute.UNDEFINED);
                }
            }

            return attackDamage;
        }

        return this.attribute(statistic);
    }

    public double attributeRelative(StatisticType attribute) {
        if (attribute == attackSpeed) {
            return this.attribute(attackSpeed) - 4;
        }

        if (attribute == attackDamage) {
            return this.attribute(attackDamage) - 1;
        }

        if (attribute == reach) {
            return this.attribute(reach) - 3;
        }

        return this.attribute(attribute);
    }

    public double attribute(StatisticType type) {
        return this.statistics.get(type).doubleValue();
    }

    public boolean incrementStatistic(StatisticType type, double amount) {
        boolean leveledUp = false;

        if (type == experience) {
             Statistic statistic = this.statistic(experience);
            statistic.add(amount);
             int xp = statistic.intValue();

            if (xp >= this.nextLevelXP() && this.canLevelUp()) {
                 int nextLevelXP = this.nextLevelXP();

                this.incrementStatistic(level, 1);
                this.incrementStatistic(experience, -nextLevelXP);

                leveledUp = true;
            }

            if (xp < 0) {
                 int currentLevelXP = this.getLevelXP(this.datum(level) - 1);

                this.incrementStatistic(level, -1);
                this.incrementStatistic(experience, currentLevelXP);
            }
        } else if (type == level) {
             int sign = (int) Math.signum(amount);

            for (int i = 0; i < Math.abs(amount); i++) {
                this.onLevelup(sign);
            }
        } else {
            this.statistics.add(type, amount);
        }

        this.updateItemStack();
        this.sync();

        return leveledUp;
    }

    public void incrementPoints(StatisticType type, int points) {
         int sign = (int) Math.signum(points);
         Statistic statistic = this.statistic(type);

        for (int i = 0; i < Math.abs(points); i++) {
            if (sign > 0 && this.datum(attributePoints) > 0 || sign < 0 && statistic.isAboveMin()) {
                this.statistic(attributePoints).add(-sign);
                this.statistic(spentAttributePoints).add(sign);

                 double change = sign * this.getIncrease(type);

                statistic.add(change);
                statistic.incrementPoints();
            }
        }

        this.updateItemStack();
        this.sync();
    }

    public double getIncrease(StatisticType statisticType) {
         Statistic statistic = this.statistic(statisticType);

        return statistic == null ? 0 : this.getIncrease(statisticType, statistic.getPoints());
    }

    public void set(StatisticType statistic, Number value) {
        this.statistics.put(statistic, value);

        this.sync();
    }

    public boolean canLevelUp() {
         Configuration configuration = Configuration.instance();

        return this.datum(level) < configuration.maxLevel || configuration.maxLevel < 0;
    }

    public void onLevelup(int sign) {
         Statistic statistic = this.statistic(level);
        statistic.add(sign);
         int level = statistic.intValue();
         Configuration configuration = Configuration.instance();

        if (level % configuration.levelsPerEnchantment == 0) {
            this.incrementStatistic(enchantmentPoints, sign);
        }

        if (level % configuration.levelsPerSkillPoint == 0) {
            this.incrementStatistic(skillPoints, sign);
        }

        this.incrementStatistic(attributePoints, sign);
    }

    public List<SkillContainer> skills() {
        List<SkillContainer> skills = new ObjectArrayList<>(this.skills.values());

        skills.sort(Comparator.comparingInt(SkillContainer::tier));

        return skills;
    }

    public SkillContainer skill(ResourceLocation identifier) {
        return this.skill(Skill.registry.getValue(identifier));
    }

    public SkillContainer skill(Skill skill) {
        return this.skills.get(skill);
    }

    public boolean hasSkill(ResourceLocation identifier) {
        return this.hasSkill(Skill.registry.getValue(identifier));
    }

    public boolean hasSkill(Skill skill) {
        return this.skills.contains(skill);
    }

    public boolean hasSkill(Skill skill, int level) {
        return this.skills.contains(skill, level);
    }

    public void upgrade(SkillContainer skill) {
//        if (this.isClientSide) {
//            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_SKILL, new ExtendedPacketBuffer(this, item).writeString(skill.toString()));
//        } else {
        int points = this.datum(skillPoints);
        int cost = skill.cost();

        if (skill.canLearn(points)) {
            skill.learn();

            this.incrementStatistic(skillPoints, -cost);
        } else if (skill.canUpgrade(points)) {
            skill.upgrade();

            this.incrementStatistic(skillPoints, -cost);
        }
//        }
    }

    public int nextLevelXP() {
        return this.getLevelXP(this.datum(level));
    }

    public int enchantment(Enchantment enchantment) {
        return this.enchantments.get(enchantment);
    }

    public void addEnchantment(Enchantment enchantment, int value) {
        int current = this.enchantment(enchantment);
        int change = Math.max(0, current + value) - current;

        this.statistics.add(enchantmentPoints, -change);
        this.statistics.add(spentEnchantmentPoints, change);

        this.enchantments.add(enchantment, change);
        this.updateItemStack();

        this.sync();
    }

    public void reset() {
        for (Category category : Category.registry) {
            this.reset(category);
        }

        this.unlocked = false;
        this.boundSlot = -1;
        this.currentTab = 0;

        this.sync();
    }

    public void reset(Category category) {
        if (category == datum) {
            this.statistics.reset(datum);
        } else if (category == attribute) {
            for (Statistic type : this.statistics.get(attribute).values()) {
                this.incrementStatistic(attributePoints, type.getPoints());
                type.reset();
            }

            this.set(spentAttributePoints, 0);
            this.updateItemStack();
        } else if (category == enchantment) {
            for (Enchantment enchantment : this.enchantments) {
                this.incrementStatistic(enchantmentPoints, this.enchantments.get(enchantment));
                this.enchantments.put(enchantment, 0);
            }

            this.statistic(spentEnchantmentPoints).setToMin();
            this.updateItemStack();
        } else if (category == skill) {
            this.skills.reset();

            this.statistic(skillPoints).setToMin();
        }

        this.sync();
    }

    public boolean canUnlock() {
        return !this.unlocked && ItemUtil.isEquipped(this.player, this.getConsumableItem());
    }

    public boolean canConsume(Item item) {
        return this.getConsumableItem() == item;
    }

    public int boundSlot() {
        return this.boundSlot;
    }

    public void bindSlot(int boundSlot) {
        this.boundSlot = boundSlot;
    }

    public void unbindSlot() {
        this.boundSlot = -1;
    }

    public int currentTab() {
        return this.currentTab;
    }

    public void currentTab(int tab) {
        this.currentTab = tab;

        if (this.isClientSide) {
            this.sync();
        }
    }

    @SuppressWarnings("VariableUseSideOnly")
    public void refresh() {
        if (this.isClientSide) {
            if (client.screen instanceof SoulboundTab) {
                List<Item> handItems = ItemUtil.handItems(this.player);

                if (handItems.contains(this.getItem())) {
                    this.openGUI();
                } else if (handItems.contains(this.getConsumableItem())) {
                    this.openGUI(0);
                }
            }
        } else {
            Packets.clientRefresh.send(this.player, new ExtendedPacketBuffer(this));
        }
    }

    public void openGUI() {
        this.openGUI(ItemUtil.equippedStack(this.player.inventory, this.getBaseItemClass()) == null ? 0 : this.currentTab);
    }

    @SuppressWarnings({"LocalVariableDeclarationSideOnly", "VariableUseSideOnly", "MethodCallSideOnly"})
    public void openGUI(int tab) {
        if (this.isClientSide) {
            Screen currentScreen = client.screen;

            if (currentScreen instanceof SoulboundTab && this.currentTab == tab) {
                ((SoulboundTab) currentScreen).refresh();
            } else {
                List<ScreenTab> tabs = this.tabs();

                client.setScreen(tabs.get(MathHelper.clamp(tab, 0, tabs.size() - 1)));
            }
        } else {
            Packets.clientOpenGUI.send(this.player, new ExtendedPacketBuffer(this).writeInt(tab));
        }
    }

    public boolean itemEquipped() {
        return ItemUtil.isEquipped(this.player, this.getItem());
    }

    public boolean anyItemEquipped() {
        return this.getMenuEquippedStack() != null;
    }

    public void removeOtherItems() {
        for (ItemStorage<?> storage : this.component.storages().values()) {
            if (storage != this) {
                PlayerEntity player = this.player;

                for (ItemStack itemStack : ItemUtil.inventory(player)) {
                    if (itemStack.getItem() == storage.getItem()) {
                        player.inventory.removeItem(itemStack);
                    }
                }
            }
        }
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    protected void updateItemStack() {
        ItemStack itemStack = this.itemStack = this.newItemStack();

        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            Multimap<Attribute, AttributeModifier> attributeModifiers = this.getAttributeModifiers(slot);

            for (Attribute attribute : attributeModifiers.keySet()) {
                for (AttributeModifier modifier : attributeModifiers.get(attribute)) {
                    itemStack.addAttributeModifier(attribute, modifier, EquipmentSlotType.MAINHAND);
                }
            }
        }

        for (Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
            int level = entry.getValue();

            if (level > 0) {
                itemStack.enchant(entry.getKey(), level);
            }
        }
    }

    protected ItemStack newItemStack() {
        ItemStack itemStack = new ItemStack(this.item);
//        Capabilities.itemData.get(itemStack).storage = this;

        return itemStack;
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot) {
        return this.getAttributeModifiers(LinkedHashMultimap.create(), slot);
    }

    protected String formatStatistic(StatisticType statistic) {
        double value = this.attributeTotal(statistic);

        return statisticFormat.format(statistic == criticalStrikeProbability ? value * 100 : value);
    }

    @Override
    public void serializeNBT(CompoundNBT tag) {
        tag.put("statistics", this.statistics.serializeNBT());
        tag.put("enchantments", this.enchantments.serializeNBT());
        tag.put("skills", this.skills.serializeNBT());
        tag.putBoolean("unlocked", this.unlocked);
        tag.putInt("slot", this.boundSlot());
        tag.putInt("tab", this.currentTab());
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        this.statistics.deserializeNBT(tag.getCompound("statistics"));
        this.enchantments.deserializeNBT(tag.getCompound("enchantments"));
        this.skills.deserializeNBT(tag.getCompound("skills"));
        this.unlocked(tag.getBoolean("unlocked"));
        this.bindSlot(tag.getInt("slot"));
        this.currentTab(tag.getInt("tab"));

        this.updateItemStack();
    }

    public CompoundNBT toClientTag() {
         CompoundNBT tag = new CompoundNBT();

        tag.putInt("tab", this.currentTab);

        return tag;
    }

    @SuppressWarnings("VariableUseSideOnly")
    public void sync() {
        if (!this.isClientSide) {
            Packets.clientSync.send(this.player, new ExtendedPacketBuffer(this).writeNbt(this.tag()));
        } else {
            Packets.serverSync.send(new ExtendedPacketBuffer(this).writeNbt(this.toClientTag()));
        }
    }

    public void tick() {}

    public abstract ITextComponent getName();

    public abstract List<StatisticEntry> getScreenAttributes();

    public abstract List<ITextComponent> getTooltip();

    public abstract Item getConsumableItem();

    public abstract double getIncrease(StatisticType statistic, int points);

    public abstract int getLevelXP(int level);

    @OnlyIn(Dist.CLIENT)
    public abstract List<ScreenTab> tabs();

    public abstract StorageType<T> type();

    public abstract Class<? extends SoulboundItem> getBaseItemClass();

    public abstract Multimap<Attribute, AttributeModifier> getAttributeModifiers(Multimap<Attribute, AttributeModifier> modifiers, EquipmentSlotType slot);
}
