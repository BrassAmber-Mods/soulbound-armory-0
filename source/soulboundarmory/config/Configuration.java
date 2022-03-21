package soulboundarmory.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.auoeke.reflect.Accessor;
import net.auoeke.reflect.Fields;
import net.auoeke.reflect.Flags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import soulboundarmory.client.gui.bar.BarStyle;
import soulboundarmory.lib.config.Category;
import soulboundarmory.lib.config.Comment;
import soulboundarmory.lib.config.Interval;
import soulboundarmory.util.Math2;

// @Config(name = SoulboundArmory.ID)
// @Background("minecraft:textures/block/andesite.png")
public final class Configuration {
    private static final String MULTIPLIERS = "multipliers";
    private static final String CLIENT = "client";

    public static final Configuration instance = new Configuration();

    @Comment("the amount of experience points required to reach the first level for tools")
    public int initialToolXP = 16;

    @Comment("the amount of experience points required to reach the first level for weapons")
    public int initialWeaponXP = 64;

    @Comment("the number of levels required in order to gain an enchantment point")
    public int levelsPerEnchantment = 10;

    @Comment("the number of levels required in order to gain a skill point")
    public int levelsPerSkillPoint = 5;

    @Comment({"the maximum soulbound item level", "< 0 => no limit"})
    public int maxLevel = -1;

    @Comment("the minimum level for soul weapons to be preserved after death")
    public int preservationLevel = 0;

    @Comment("whether point restoration is free")
    public boolean freeRestoration = true;

    @Category(MULTIPLIERS)
    @Comment("1 + (armor multiplier) * armor")
    public double armorMultiplier = 0.2;

    @Category(MULTIPLIERS)
    @Comment("1 + (attack damage multiplier) * damage")
    public double attackDamageMultiplier = 0.35;

    @Category(MULTIPLIERS)
    @Comment("1 + (attack speed multiplier) * damage")
    public double attackSpeedMultiplier = 0.5;

    @Category(MULTIPLIERS)
    @Comment({"(difficulty multiplier) * difficulty", "Difficulty ranges from 0 to 3."})
    public double difficultyMultiplier = 0.5;

    @Category(MULTIPLIERS)
    @Comment("peaceful mode multiplier")
    public double peacefulMultiplier = 0;

    @Category(MULTIPLIERS)
    @Comment("the multiplier for XPs gained by killing baby entities")
    public double babyMultiplier = 2;

    @Category(MULTIPLIERS)
    @Comment("the multiplier for XPs gained by killing bosses")
    public double bossMultiplier = 3;

    @Category(MULTIPLIERS)
    @Comment("the multiplier for XPs gained in hardcore mode")
    public double hardcoreMultiplier = 2;

    @Category(MULTIPLIERS)
    @Comment("the multiplier for XPs gained by killing passive entities")
    public double passiveMultiplier = 0;

    // @TransitiveObject
    @Category(CLIENT)
    @OnlyIn(Dist.CLIENT)
    public Client client = new Client();

    public static Configuration instance() {
        return instance;
    }

    private static ForgeConfigSpec configuration(Class<?> type) {
        var builder = new ForgeConfigSpec.Builder();

        Fields.of(type).forEach(field -> {
            if (Flags.isTransient(field) || Flags.isInstance(field)) {
                return;
            }

            var category = field.getAnnotation(Category.class);

            if (category != null) {
                builder.push(category.value());
            }

            var comment = field.getAnnotation(Comment.class);

            if (comment != null) {
                builder.comment(comment.value());
            }

            var defaultValue = Accessor.get(field);
            var interval = field.getAnnotation(Interval.class);

            if (interval == null) {
                builder.define(field.getName(), defaultValue);
            } else if (field.getType() == int.class) {
                builder.defineInRange(field.getName(), (int) defaultValue, interval.min(), interval.max());
            } else {
                throw new ClassCastException("Interval-annotated field %s must be of type int");
            }

            if (category != null) {
                builder.pop();
            }
        });

        return builder.build();
    }

    public static void paths(String mod, Class<?> holder) {
        var paths = new HashMap<Field, String>();
        Fields.of(holder).forEach(field -> paths(paths, field, String.format("config.%s.%s", mod, field.getName())));
    }

    public static void paths(Map<Field, String> paths, Field field, String path) {
        paths.put(field, path);
        Fields.of(field.getType()).filter(child -> Accessor.get(child) != null).forEach(child -> paths(paths, child, path + "." + child.getName()));
    }

    public static class Client {
        @Comment("receive levelup notifications above the hotbar")
        public boolean levelupNotifications = false;

        @Comment("display option button and sliders in the menu")
        public boolean displayOptions = false;

        @Comment("replace the default XP bar with an XP bar for the currently held soulbound item")
        public boolean overlayExperienceBar = true;

        @Comment("enable enchantment glint for enchanted items")
        public boolean enchantmentGlint = false;

        @Comment("display attributes in tooltips")
        public boolean tooltipAttributes = true;

        // @EnumHandler(option = EnumDisplayOption.BUTTON)
        public BarStyle style = BarStyle.EXPERIENCE;

        @Comment("the colors of this mod's XP bar")
        // @CollapsibleObject(startExpanded = true)
        public Color color = new Color();

        public void toggleOverlayExperienceBar() {
            this.overlayExperienceBar = !this.overlayExperienceBar;
        }

        public static class Color {
            @Interval(max = 255)
            public int red = 160;

            @Interval(max = 255)
            public int green = 255;

            @Interval(max = 255)
            public int blue = 160;

            @Interval(max = 255)
            public int alpha = 255;

            public void set(int id, int value) {
                switch (id) {
                    case 0 -> this.red = value;
                    case 1 -> this.green = value;
                    case 2 -> this.blue = value;
                    case 3 -> this.alpha = value;
                    default -> throw new IllegalArgumentException("invalid color component ID: " + id);
                }
            }

            public int get(int id) {
                return switch (id) {
                    case 0 -> this.red;
                    case 1 -> this.green;
                    case 2 -> this.blue;
                    case 3 -> this.alpha;
                    default -> throw new IllegalArgumentException("invalid color component ID: " + id);
                };
            }

            public float getf(int id) {
                return this.get(id) / 255F;
            }

            public int argb() {
                return Math2.pack(this.red, this.green, this.blue, this.alpha);
            }
        }
    }
}
