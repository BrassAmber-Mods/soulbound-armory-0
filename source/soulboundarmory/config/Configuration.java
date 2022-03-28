package soulboundarmory.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.client.gui.bar.BarStyle;
import soulboundarmory.module.config.Background;
import soulboundarmory.module.config.Category;
import soulboundarmory.module.config.Comment;
import soulboundarmory.module.config.ConfigurationFile;
import soulboundarmory.module.config.Flat;
import soulboundarmory.module.config.Interval;
import soulboundarmory.util.Math2;

// @Config(name = SoulboundArmory.ID)
public final class Configuration implements ConfigurationFile {
    @Comment("experience points required to reach the first level for tools")
    public static int initialToolXP = 16;

    @Comment("experience points required to reach the first level for weapons")
    public static int initialWeaponXP = 64;

    @Comment("levels required in order to gain an enchantment point")
    public static int levelsPerEnchantment = 10;

    @Comment("levels required in order to gain a skill point")
    public static int levelsPerSkillPoint = 5;

    @Comment({"maximum level", "maximum level < 0 => no limit"})
    public static int maxLevel = -1;

    @Comment("minimum level for items to be preserved after death")
    public static int preservationLevel = 0;

    @Comment("free point restoration")
    public static boolean freeRestoration = true;

    @Flat
    @Category("multipliers")
    public static class Multipliers {
        @Comment("1 + (armor multiplier) * armor")
        public static double armor = 0.2;

        @Comment("1 + (attack damage multiplier) * damage")
        public static double attackDamage = 0.35;

        @Comment("1 + (attack speed multiplier) * damage")
        public static double attackSpeed = 0.5;

        @Comment({"(difficulty multiplier) * difficulty (peaceful = 0; hard = 3)"})
        public static double difficulty = 0.5;

        @Comment("peaceful mode multiplier")
        public static double peaceful = 0;

        @Comment("hostile baby kill XP multiplier")
        public static double baby = 2;

        @Comment("boss kill XP multiplier")
        public static double boss = 3;

        @Comment("hardcore mode XP multiplier")
        public static double hardcore = 2;

        @Comment("passive entity kill XP multiplier")
        public static double passive = 0;
    }

    @OnlyIn(Dist.CLIENT)
    @Category("client")
    public static class Client {
        @Comment("receive levelup notifications above the hotbar")
        public static boolean levelupNotifications = false;

        @Comment("display option button and sliders in the menu")
        public static boolean displayOptions = false;

        @Comment("use a custom experience bar for the currently held soulbound item")
        public static boolean overlayExperienceBar = true;

        @Comment("enable enchantment glint for enchanted items")
        public static boolean enchantmentGlint = false;

        @Comment("display attributes in tooltips")
        public static boolean tooltipAttributes = true;

        @Comment("experience bar style")
        public static BarStyle style = BarStyle.EXPERIENCE;

        public void toggleOverlayExperienceBar() {
            overlayExperienceBar ^= true;
        }

        @Comment("XP bar color")
        public static class Color {
            @Interval(max = 255)
            public static int red = 160;

            @Interval(max = 255)
            public static int green = 255;

            @Interval(max = 255)
            public static int blue = 160;

            @Interval(max = 255)
            public static int alpha = 255;

            public static void set(int id, int value) {
                switch (id) {
                    case 0 -> red = value;
                    case 1 -> green = value;
                    case 2 -> blue = value;
                    case 3 -> alpha = value;
                    default -> throw new IllegalArgumentException("color component ID: " + id);
                }
            }

            public static int get(int id) {
                return switch (id) {
                    case 0 -> red;
                    case 1 -> green;
                    case 2 -> blue;
                    case 3 -> alpha;
                    default -> throw new IllegalArgumentException("color component ID: " + id);
                };
            }

            public static float getf(int id) {
                return get(id) / 255F;
            }

            public static int argb() {
                return Math2.pack(red, green, blue, alpha);
            }
        }
    }
}
