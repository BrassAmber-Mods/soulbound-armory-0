package user11681.soulboundarmory.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.client.gui.ExperienceBarOverlay.Style;

@Config(name = SoulboundArmory.ID)
@Background(Configuration.BACKGROUND_TEXTURE)
public class Configuration implements ConfigData {
    @Excluded
    public static final String BACKGROUND_TEXTURE = "minecraft:textures/block/andesite.png";

    @Excluded
    public static final String MULTIPLIER_CATEGORY = "multipliers";
    @Excluded
    public static final String CLIENT_CATEGORY = "client";

    @Tooltip
    public int initialWeaponXP = 64;
    @Tooltip
    public int initialToolXP = 16;
    @Tooltip
    public int levelsPerEnchantment = 10;
    @Tooltip
    public int levelsPerSkillPoint = 5;
    @Tooltip(count = 2)
    public int maxLevel = -1;
    @Tooltip
    public int preservationLevel = 0;

    @Category(MULTIPLIER_CATEGORY)
    @Tooltip
    public double armorMultiplier = 0.2;
    @Category(MULTIPLIER_CATEGORY)
    @Tooltip
    public double attackDamageMultiplier = 0.35;
    @Category(MULTIPLIER_CATEGORY)
    @Tooltip(count = 2)
    public double difficultyMultiplier = 0.5;
    @Category(MULTIPLIER_CATEGORY)
    @Tooltip
    public double babyMultiplier = 2;
    @Category(MULTIPLIER_CATEGORY)
    @Tooltip
    public double bossMultiplier = 3;
    @Category(MULTIPLIER_CATEGORY)
    @Tooltip
    public double hardcoreMultiplier = 2;
    @Category(MULTIPLIER_CATEGORY)
    @Tooltip
    public double passiveMultiplier = 0;

    @TransitiveObject
    @Category(CLIENT_CATEGORY)
    @OnlyIn(Dist.CLIENT)
    public Client client = new Client();

    public static Configuration instance() {
        return AutoConfig.getConfigHolder(Configuration.class).getConfig();
    }

    public static class Client {
        @Tooltip
        public boolean addToOffhand = true;
        @Tooltip
        public boolean levelupNotifications = true;
        @Tooltip
        public boolean displayOptions = true;
        @Tooltip
        public boolean overlayExperienceBar = true;
        @EnumHandler(option = EnumDisplayOption.BUTTON)
        public Style style = Style.EXPERIENCE;
        @Tooltip
        @CollapsibleObject(startExpanded = true)
        public Colors colors = new Colors();

        public void toggleOverlayExperienceBar() {
            this.overlayExperienceBar = !this.overlayExperienceBar;
        }

        public static class Colors {
            @BoundedDiscrete(max = 255)
            public int red = 160;
            @BoundedDiscrete(max = 255)
            public int green = 255;
            @BoundedDiscrete(max = 255)
            public int blue = 160;
            @BoundedDiscrete(max = 255)
            public int alpha = 255;

            public void set(int id, final int value) {
                if (id == 0) {
                    this.red = value;
                } else if (id == 1) {
                    this.green = value;
                } else if (id == 2) {
                    this.blue = value;
                } else if (id == 3) {
                    this.alpha = value;
                } else {
                    throw new IllegalArgumentException(String.format("invalid color component ID: %s", id));
                }
            }

            public int get(int id) {
                switch (id) {
                    case 0:
                        return this.red;
                    case 1:
                        return this.green;
                    case 2:
                        return this.blue;
                    case 3:
                        return this.alpha;
                }

                throw new IllegalArgumentException(String.format("invalid color component ID: %s", id));
            }
        }
    }
}
