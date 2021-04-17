package user11681.soulboundarmory.component.soulbound.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import nerdhub.cardinal.components.api.util.NbtSerializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import user11681.cell.client.gui.screen.ScreenTab;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.SoulboundArmoryClient;
import user11681.soulboundarmory.client.gui.screen.tab.SoulboundTab;
import user11681.soulboundarmory.client.gui.screen.tab.StatisticEntry;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.statistics.Category;
import user11681.soulboundarmory.component.statistics.EnchantmentStorage;
import user11681.soulboundarmory.component.statistics.SkillStorage;
import user11681.soulboundarmory.component.statistics.Statistic;
import user11681.soulboundarmory.component.statistics.StatisticType;
import user11681.soulboundarmory.component.statistics.Statistics;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.SkillContainer;
import user11681.soulboundarmory.util.ItemUtil;

import static user11681.soulboundarmory.SoulboundArmoryClient.client;
import static user11681.soulboundarmory.component.statistics.Category.attribute;
import static user11681.soulboundarmory.component.statistics.Category.datum;
import static user11681.soulboundarmory.component.statistics.Category.enchantment;
import static user11681.soulboundarmory.component.statistics.Category.skill;
import static user11681.soulboundarmory.component.statistics.StatisticType.attackDamage;
import static user11681.soulboundarmory.component.statistics.StatisticType.attackSpeed;
import static user11681.soulboundarmory.component.statistics.StatisticType.attributePoints;
import static user11681.soulboundarmory.component.statistics.StatisticType.criticalStrikeProbability;
import static user11681.soulboundarmory.component.statistics.StatisticType.enchantmentPoints;
import static user11681.soulboundarmory.component.statistics.StatisticType.experience;
import static user11681.soulboundarmory.component.statistics.StatisticType.level;
import static user11681.soulboundarmory.component.statistics.StatisticType.reach;
import static user11681.soulboundarmory.component.statistics.StatisticType.skillPoints;
import static user11681.soulboundarmory.component.statistics.StatisticType.spentAttributePoints;
import static user11681.soulboundarmory.component.statistics.StatisticType.spentEnchantmentPoints;

public abstract class ItemStorage<T extends ItemStorage<T>> implements NbtSerializable {
    protected static final NumberFormat format = DecimalFormat.getInstance();

    protected final SoulboundComponent<?> component;
    protected final PlayerEntity player;
    protected final Item item;
    protected final boolean isClient;

    protected EnchantmentStorage enchantments;
    protected SkillStorage skills;
    protected Statistics statistics;
    protected ItemStack itemStack;
    protected boolean unlocked;
    protected int boundSlot;
    protected int currentTab;

    public ItemStorage(SoulboundComponent<?> component, final Item item) {
        this.component = component;
        this.player = component.player;
        this.isClient = component.player.world.isClient;
        this.item = item;
        this.itemStack = this.newItemStack();
    }

    public static ItemStorage<?> get(final Entity entity, final Item item) {
        for (SoulboundComponent<?> component : Components.getComponents(entity)) {
            for (ItemStorage<?> storage : component.getStorages().values()) {
                if (storage.player == entity && storage.getItem() == item) {
                    return storage;
                }
            }
        }

        return null;
    }

    public static ItemStorage<?> get(final Entity entity, final StorageType<?> type) {
        for (ComponentKey<? extends SoulboundComponent<?>> component : Components.soulboundComponents) {
            for (ItemStorage<?> storage : component.get(entity).getStorages().values()) {
                if (storage.getType() == type) {
                    return storage;
                }
            }
        }

        return null;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    public SoulboundComponent<?> getComponent() {
        return this.component;
    }

    public Item getItem() {
        return this.item;
    }

    public ItemStack getMenuEquippedStack() {
        for (final ItemStack itemStack : this.player.getItemsHand()) {
            final Item item = itemStack.getItem();

            if (item == this.getItem() || item == this.getConsumableItem()) {
                return itemStack;
            }
        }

        return null;
    }

    public boolean isUnlocked() {
        return this.unlocked;
    }

    public void setUnlocked(final boolean unlocked) {
        this.unlocked = unlocked;
    }

    public int size(final Category category) {
        return this.statistics.size(category);
    }

    public Statistic getStatistic(final StatisticType statistic) {
        return this.statistics.get(statistic);
    }

    public Statistic getStatistic(final Category category, final StatisticType statistic) {
        return this.statistics.get(category, statistic);
    }

    public int getDatum(final StatisticType statistic) {
        return this.statistics.get(statistic).intValue();
    }

    public double getAttributeTotal(StatisticType statistic) {
        if (statistic == attackDamage) {
            double attackDamage = this.getAttribute(StatisticType.attackDamage);

            for (final Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
                final Enchantment enchantment = entry.getKey();

                if (entry.getValue() > 0) {
                    attackDamage += enchantment.getAttackDamage(this.getEnchantment(enchantment), EntityGroup.DEFAULT);
                }
            }

            return attackDamage;
        }

        return this.getAttribute(statistic);
    }

    public double getAttributeRelative(final StatisticType attribute) {
        if (attribute == attackSpeed) {
            return this.getAttribute(attackSpeed) - 4;
        }

        if (attribute == attackDamage) {
            return this.getAttribute(attackDamage) - 1;
        }

        if (attribute == reach) {
            return this.getAttribute(reach) - 3;
        }

        return this.getAttribute(attribute);
    }

    public double getAttribute(final StatisticType type) {
        return this.statistics.get(type).doubleValue();
    }

    public boolean incrementStatistic(final StatisticType type, final double amount) {
        boolean leveledUp = false;

        if (type == experience) {
            final Statistic statistic = this.getStatistic(experience);
            statistic.add(amount);
            final int xp = statistic.intValue();

            if (xp >= this.getNextLevelXP() && this.canLevelUp()) {
                final int nextLevelXP = this.getNextLevelXP();

                this.incrementStatistic(level, 1);
                this.incrementStatistic(experience, -nextLevelXP);

                leveledUp = true;
            }

            if (xp < 0) {
                final int currentLevelXP = this.getLevelXP(this.getDatum(level) - 1);

                this.incrementStatistic(level, -1);
                this.incrementStatistic(experience, currentLevelXP);
            }
        } else if (type == level) {
            final int sign = (int) Math.signum(amount);

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

    public void incrementPoints(final StatisticType type, final int points) {
        final int sign = (int) Math.signum(points);
        final Statistic statistic = this.getStatistic(type);

        for (int i = 0; i < Math.abs(points); i++) {
            if (sign > 0 && this.getDatum(attributePoints) > 0 || sign < 0 && statistic.isAboveMin()) {
                this.getStatistic(attributePoints).add(-sign);
                this.getStatistic(spentAttributePoints).add(sign);

                final double change = sign * this.getIncrease(type);

                statistic.add(change);
                statistic.incrementPoints();
            }
        }

        this.updateItemStack();
        this.sync();
    }

    public double getIncrease(final StatisticType statisticType) {
        final Statistic statistic = this.getStatistic(statisticType);

        return statistic == null ? 0 : this.getIncrease(statisticType, statistic.getPoints());
    }

    public void setStatistic(final StatisticType statistic, final Number value) {
        this.statistics.put(statistic, value);

        this.sync();
    }

    public boolean canLevelUp() {
        final Configuration configuration = Configuration.instance();

        return this.getDatum(level) < configuration.maxLevel || configuration.maxLevel < 0;
    }

    public void onLevelup(int sign) {
        final Statistic statistic = this.getStatistic(level);
        statistic.add(sign);
        final int level = statistic.intValue();
        final Configuration configuration = Configuration.instance();

        if (level % configuration.levelsPerEnchantment == 0) {
            this.incrementStatistic(enchantmentPoints, sign);
        }

        if (level % configuration.levelsPerSkillPoint == 0) {
            this.incrementStatistic(skillPoints, sign);
        }

        this.incrementStatistic(attributePoints, sign);
    }

    public List<SkillContainer> getSkills() {
        List<SkillContainer> skills = new ObjectArrayList<>(this.skills.values());

        skills.sort(Comparator.comparingInt(SkillContainer::getTier));

        return skills;
    }

    public SkillContainer getSkill(final Identifier identifier) {
        return this.getSkill(Skill.skill.get(identifier));
    }

    public SkillContainer getSkill(final Skill skill) {
        return this.skills.get(skill);
    }

    public boolean hasSkill(final Identifier identifier) {
        return this.hasSkill(Skill.skill.get(identifier));
    }

    public boolean hasSkill(final Skill skill) {
        return this.skills.contains(skill);
    }

    public boolean hasSkill(final Skill skill, final int level) {
        return this.skills.contains(skill, level);
    }

    public void upgradeSkill(final SkillContainer skill) {
//        if (this.isClient) {
//            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_SKILL, new ExtendedPacketBuffer(this, item).writeString(skill.toString()));
//        } else {
        int points = this.getDatum(skillPoints);
        int cost = skill.getCost();

        if (skill.canBeLearned(points)) {
            skill.learn();

            this.incrementStatistic(skillPoints, -cost);
        } else if (skill.canBeUpgraded(points)) {
            skill.upgrade();

            this.incrementStatistic(skillPoints, -cost);
        }
//        }
    }

    public int getNextLevelXP() {
        return this.getLevelXP(this.getDatum(level));
    }

    public int getEnchantment(Enchantment enchantment) {
        return this.enchantments.get(enchantment);
    }

    public EnchantmentStorage getEnchantments() {
        return this.enchantments;
    }

    public void addEnchantment(Enchantment enchantment, int value) {
        int current = this.getEnchantment(enchantment);
        int change = Math.max(0, current + value) - current;

        this.statistics.add(enchantmentPoints, -change);
        this.statistics.add(spentEnchantmentPoints, change);

        this.enchantments.add(enchantment, change);
        this.updateItemStack();

        this.sync();
    }

    public void reset() {
        for (Category category : Category.category) {
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
            for (final Statistic type : this.statistics.get(attribute).values()) {
                this.incrementStatistic(attributePoints, type.getPoints());
                type.reset();
            }

            this.setStatistic(spentAttributePoints, 0);
            this.updateItemStack();
        } else if (category == enchantment) {
            for (final Enchantment enchantment : this.enchantments) {
                this.incrementStatistic(enchantmentPoints, this.enchantments.get(enchantment));
                this.enchantments.put(enchantment, 0);
            }

            this.getStatistic(spentEnchantmentPoints).setToMin();
            this.updateItemStack();
        } else if (category == skill) {
            this.skills.reset();

            this.getStatistic(skillPoints).setToMin();
        }

        this.sync();
    }

    public boolean canUnlock() {
        return !this.unlocked && ItemUtil.isItemEquipped(this.player, this.getConsumableItem());
    }

    public boolean canConsume(final Item item) {
        return this.getConsumableItem() == item;
    }

    public int getBoundSlot() {
        return this.boundSlot;
    }

    public void bindSlot(final int boundSlot) {
        this.boundSlot = boundSlot;
    }

    public void unbindSlot() {
        this.boundSlot = -1;
    }

    public int getCurrentTab() {
        return this.currentTab;
    }

    public void setCurrentTab(final int tab) {
        this.currentTab = tab;

        if (this.isClient) {
            this.sync();
        }
    }

    @SuppressWarnings("VariableUseSideOnly")
    public void refresh() {
        if (this.isClient) {
            if (client.currentScreen instanceof SoulboundTab) {
                List<Item> handItems = ItemUtil.handItems(this.player);

                if (handItems.contains(this.getItem())) {
                    this.openGUI();
                } else if (handItems.contains(this.getConsumableItem())) {
                    this.openGUI(0);
                }
            }
        } else {
            ServerPlayNetworking.send((ServerPlayerEntity) this.player, Packets.clientRefresh, new ExtendedPacketBuffer(this));
        }
    }

    public void openGUI() {
        this.openGUI(ItemUtil.getEquippedItemStack(this.player.getInventory(), this.getBaseItemClass()) == null ? 0 : this.currentTab);
    }

    @SuppressWarnings({"LocalVariableDeclarationSideOnly", "VariableUseSideOnly", "MethodCallSideOnly"})
    public void openGUI(int tab) {
        if (this.isClient) {
            Screen currentScreen = client.currentScreen;

            if (currentScreen instanceof SoulboundTab && this.currentTab == tab) {
                ((SoulboundTab) currentScreen).refresh();
            } else {
                List<ScreenTab> tabs = this.getTabs();

                client.openScreen(tabs.get(MathHelper.clamp(tab, 0, tabs.size() - 1)));
            }
        } else {
            SoulboundArmory.PACKET_REGISTRY.sendToPlayer(this.player, Packets.clientOpenGUI, new ExtendedPacketBuffer(this).writeInt(tab));
        }
    }

    public boolean isItemEquipped() {
        return ItemUtil.isItemEquipped(this.player, this.getItem());
    }

    public boolean isAnyItemEquipped() {
        return this.getMenuEquippedStack() != null;
    }

    public void removeOtherItems() {
        for (ItemStorage<?> storage : this.component.getStorages().values()) {
            if (storage != this) {
                PlayerEntity player = this.player;

                for (ItemStack itemStack : ItemUtil.inventory(player)) {
                    if (itemStack.getItem() == storage.getItem()) {
                        player.getInventory().removeOne(itemStack);
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

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = this.getAttributeModifiers(slot);

            for (EntityAttribute attribute : attributeModifiers.keySet()) {
                for (EntityAttributeModifier modifier : attributeModifiers.get(attribute)) {
                    itemStack.addAttributeModifier(attribute, modifier, EquipmentSlot.MAINHAND);
                }
            }
        }

        for (Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
            int level = entry.getValue();

            if (level > 0) {
                itemStack.addEnchantment(entry.getKey(), level);
            }
        }
    }

    protected ItemStack newItemStack() {
        ItemStack itemStack = new ItemStack(this.item);

        Components.itemData.get(itemStack).storage = this;

        return itemStack;
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return this.getAttributeModifiers(LinkedHashMultimap.create(), slot);
    }

    protected String formatStatistic(StatisticType statistic) {
        double value = this.getAttributeTotal(statistic);

        return format.format(statistic == criticalStrikeProbability ? value * 100 : value);
    }


    @Override
    public void fromTag(NbtCompound tag) {
        this.statistics.fromTag(tag.getCompound("statistics"));
        this.enchantments.fromTag(tag.getCompound("enchantments"));
        this.skills.fromTag(tag.getCompound("skills"));
        this.setUnlocked(tag.getBoolean("unlocked"));
        this.bindSlot(tag.getInt("slot"));
        this.setCurrentTab(tag.getInt("tab"));

        this.updateItemStack();
    }

    @Override
    public @NotNull NbtCompound toTag(NbtCompound tag) {
        tag.put("statistics", this.statistics.toTag(new NbtCompound()));
        tag.put("enchantments", this.enchantments.toTag(new NbtCompound()));
        tag.put("skills", this.skills.toTag(new NbtCompound()));
        tag.putBoolean("unlocked", this.unlocked);
        tag.putInt("slot", this.getBoundSlot());
        tag.putInt("tab", this.getCurrentTab());

        return tag;
    }

    public NbtCompound toClientTag() {
        final NbtCompound tag = new NbtCompound();

        tag.putInt("tab", this.currentTab);

        return tag;
    }

    @SuppressWarnings("VariableUseSideOnly")
    public void sync() {
        if (!this.isClient) {
            SoulboundArmory.PACKET_REGISTRY.sendToPlayer(this.player, Packets.clientSync, new ExtendedPacketBuffer(this).writeNbt(this.toTag(new NbtCompound())));
        } else {
            SoulboundArmoryClient.packetRegistry.sendToServer(Packets.serverSync, new ExtendedPacketBuffer(this).writeNbt(this.toClientTag()));
        }
    }

    public void tick() {}

    public abstract Text getName();

    public abstract List<StatisticEntry> getScreenAttributes();

    public abstract List<Text> getTooltip();

    public abstract Item getConsumableItem();

    public abstract double getIncrease(StatisticType statistic, int points);

    public abstract int getLevelXP(int level);

    @Environment(EnvType.CLIENT)
    public abstract List<ScreenTab> getTabs();

    public abstract StorageType<T> getType();

    public abstract Class<? extends SoulboundItem> getBaseItemClass();

    public abstract Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot);
}
