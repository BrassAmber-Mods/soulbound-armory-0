package soulboundarmory.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import net.auoeke.reflect.Accessor;
import soulboundarmory.client.gui.bar.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

// @Config(name = SoulboundArmory.ID)
// @Background("minecraft:textures/block/andesite.png")
public class Configuration {
    public static transient final String MULTIPLIER_CATEGORY = "multipliers";
    public static transient final String CLIENT_CATEGORY = "client";

    public static transient final Configuration instance = new Configuration();

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

    @Category(MULTIPLIER_CATEGORY)
    @Comment("1 + (armor multiplier) * armor")
    public double armorMultiplier = 0.2;

    @Category(MULTIPLIER_CATEGORY)
    @Comment("1 + (attack damage multiplier) * damage")
    public double attackDamageMultiplier = 0.35;

    @Category(MULTIPLIER_CATEGORY)
    @Comment({"(difficulty multiplier) * difficulty", "Difficulty ranges from 0 to 3.",})
    public double difficultyMultiplier = 0.5;

    @Category(MULTIPLIER_CATEGORY)
    @Comment("the multiplier for XPs gained by killing baby entities")
    public double babyMultiplier = 2;

    @Category(MULTIPLIER_CATEGORY)
    @Comment("the multiplier for XPs gained by killing bosses")
    public double bossMultiplier = 3;

    @Category(MULTIPLIER_CATEGORY)
    @Comment("the multiplier for XPs gained in hardcore mode")
    public double hardcoreMultiplier = 2;

    @Category(MULTIPLIER_CATEGORY)
    @Comment("the multiplier for XPs gained by killing passive entities")
    public double passiveMultiplier = 0;

    // @TransitiveObject
    @Category(CLIENT_CATEGORY)
    @OnlyIn(Dist.CLIENT)
    public Client client = new Client();

    public static Configuration instance() {
        return instance;
    }

    private static ForgeConfigSpec configuration(Class<?> type) {
        var builder = new ForgeConfigSpec.Builder();

        for (var field : type.getDeclaredFields()) {
            if ((field.getModifiers() & (Modifier.TRANSIENT | ~Modifier.STATIC)) != 0) {
                continue;
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
        }

        return builder.build();
    }

    public static void paths(String mod, Class<?> holder) {
        Map<Field, String> paths = new HashMap<>();

        for (var field : holder.getDeclaredFields()) {
            paths(paths, field, String.format("config.%s.%s", mod, field.getName()));
        }
    }

    public static void paths(Map<Field, String> paths, Field field, String path) {
        paths.put(field, path);

        for (var child : field.getType().getDeclaredFields()) {
            if (Accessor.get(child) != null) {
                paths(paths, child, path + "." + child.getName());
            }
        }
    }

    public static class Client {
        @Comment("receive levelup notifications above the hotbar")
        public boolean levelupNotifications = true;

        @Comment("display option button and sliders in the menu")
        public boolean displayOptions = true;

        @Comment("replace the default XP bar with an XP bar for the currently held soulbound item")
        public boolean overlayExperienceBar = true;

        // @EnumHandler(option = EnumDisplayOption.BUTTON)
        public Style style = Style.EXPERIENCE;

        @Comment("the colors of this mod's XP bar")
        // @CollapsibleObject(startExpanded = true)
        public Colors colors = new Colors();

        public void toggleOverlayExperienceBar() {
            this.overlayExperienceBar = !this.overlayExperienceBar;
        }

        public static class Colors {
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
                    default -> throw new IllegalArgumentException(String.format("invalid color component ID: %s", id));
                }
            }

            public int get(int id) {
                return switch (id) {
                    case 0 -> this.red;
                    case 1 -> this.green;
                    case 2 -> this.blue;
                    case 3 -> this.alpha;
                    default -> throw new IllegalArgumentException(String.format("invalid color component ID: %s", id));
                };
            }
        }
    }
}
