package user11681.soulboundarmory.client.i18n;

import user11681.soulboundarmory.client.gui.bar.Style;
import user11681.soulboundarmory.text.Translation;

public class Translations {
    public static final Translation soulboundDagger = new Translation("gui.soulboundarmory.dagger");
    public static final Translation soulboundSword = new Translation("gui.soulboundarmory.sword");
    public static final Translation soulboundGreatsword = new Translation("gui.soulboundarmory.greatsword");
    public static final Translation soulboundStaff = new Translation("gui.soulboundarmory.staff");
    public static final Translation soulboundPick = new Translation("gui.soulboundarmory.pick");

    public static final Translation menuKey = new Translation("key.soulboundarmory.gui");
    public static final Translation toggleXPBar = new Translation("key.soulboundarmory.bar");

    public static final Translation menuToolSelection = new Translation("gui.soulboundarmory.confirmation");
    public static final Translation menuWeaponSelection = new Translation("gui.soulboundarmory.selection");
    public static final Translation menuButtonAttributes = new Translation("gui.soulboundarmory.attributes");
    public static final Translation menuButtonEnchantments = new Translation("gui.soulboundarmory.enchantments");
    public static final Translation menuSkills = new Translation("gui.soulboundarmory.skills");
    public static final Translation menuButtonReset = new Translation("gui.soulboundarmory.reset");
    public static final Translation menuButtonBind = new Translation("gui.soulboundarmory.bind");
    public static final Translation menuButtonUnbind = new Translation("gui.soulboundarmory.unbind");
    public static final Translation menuUnspentPoints = new Translation("gui.soulboundarmory.unspent_points");
    public static final Translation menuUnspentPoint = new Translation("gui.soulboundarmory.unspent_point");
    public static final Translation menuPoints = new Translation("gui.soulboundarmory.points");
    public static final Translation menuPoint = new Translation("gui.soulboundarmory.point");
    public static final Translation menuSkillLearnCost = new Translation("gui.soulboundarmory.learn_cost");
    public static final Translation menuSkillUpgradeCost = new Translation("gui.soulboundarmory.upgrade_cost");
    public static final Translation menuLevel = new Translation("gui.soulboundarmory.world");
    public static final Translation red = new Translation("gui.soulboundarmory.red");
    public static final Translation green = new Translation("gui.soulboundarmory.green");
    public static final Translation blue = new Translation("gui.soulboundarmory.blue");
    public static final Translation alpha = new Translation("gui.soulboundarmory.alpha");

    public static final Translation barLevel = new Translation("gui.soulboundarmory.bar.world");
    public static final Translation barXP = new Translation("gui.soulboundarmory.bar.xp");
    public static final Translation barFullXP = new Translation("gui.soulboundarmory.bar.full_xp");

    public static final Translation style = new Translation("gui.soulboundarmory.style");
    public static final Translation xpStyle = new Translation("gui.soulboundarmory.style.experience");
    public static final Translation bossStyle = new Translation("gui.soulboundarmory.style.boss");
    public static final Translation horseStyle = new Translation("gui.soulboundarmory.style.horse");

    public static final Translation experienceFormat = new Translation("datum.soulboundarmory.experience.format");
    public static final Translation levelFormat = new Translation("datum.soulboundarmory.world.format");
    public static final Translation skillPointsFormat = new Translation("datum.soulboundarmory.skill_points.format");
    public static final Translation attributePointsFormat = new Translation("datum.soulboundarmory.attribute_points.format");
    public static final Translation enchantmentPointsFormat = new Translation("datum.soulboundarmory.enchantment_points.format");
    public static final Translation spentAttributePointsFormat = new Translation("datum.soulboundarmory.spent_attribute_points.format");
    public static final Translation spentEnchantmentPointsFormat = new Translation("datum.soulboundarmory.spent_enchantment_points.format");
    public static final Translation toolEfficiencyFormat = new Translation("attribute.soulboundarmory.efficiency.tool.format");
    public static final Translation weaponEfficiencyFormat = new Translation("attribute.soulboundarmory.efficiency.weapon.format");
    public static final Translation reachFormat = new Translation("attribute.soulboundarmory.reach.format");
    public static final Translation miningLevelFormat = new Translation("attribute.soulboundarmory.mining_level.format");
    public static final Translation attackSpeedFormat = new Translation("attribute.soulboundarmory.attack_speed.format");
    public static final Translation attackDamageFormat = new Translation("attribute.soulboundarmory.attack_damage.format");
    public static final Translation criticalStrikeRateFormat = new Translation("attribute.soulboundarmory.critical_strike_rate.format");
    public static final Translation attackRangeFormat = new Translation("attribute.soulboundarmory.attack_range.format");

    public static final Translation experienceName = new Translation("attribute.soulboundarmory.experience");
    public static final Translation levelName = new Translation("attribute.soulboundarmory.world");
    public static final Translation skillPointsName = new Translation("attribute.soulboundarmory.skill_points");
    public static final Translation attributePointsName = new Translation("attribute.soulboundarmory.attribute_points");
    public static final Translation enchantmentPointsName = new Translation("attribute.soulboundarmory.enchantment_points");
    public static final Translation spentAttributePointsName = new Translation("attribute.soulboundarmory.spent_attribute_points");
    public static final Translation spentEnchantmentPointsName = new Translation("attribute.soulboundarmory.spent_enchantment_points");
    public static final Translation attackSpeedName = new Translation("attribute.soulboundarmory.attack_speed");
    public static final Translation attackDamageName = new Translation("attribute.soulboundarmory.attack_damage");
    public static final Translation criticalStrikeRateName = new Translation("attribute.soulboundarmory.critical_strike_rate");
    public static final Translation toolEfficiencyName = new Translation("attribute.soulboundarmory.efficiency.tool");
    public static final Translation weaponEfficiencyName = new Translation("attribute.soulboundarmory.efficiency.weapon");
    public static final Translation miningLevelName = new Translation("attribute.soulboundarmory.mining_level");
    public static final Translation reachName = new Translation("attribute.soulboundarmory.reach");
    public static final Translation attackRangeName = new Translation("attribute.soulboundarmory.attack_range");

    public static final Translation messageLevelUp = new Translation("message.soulboundarmory.levelup");

    public static final Translation miningLevelCoal = new Translation("mining_level.soulboundarmory.coal");
    public static final Translation miningLevelIron = new Translation("mining_level.soulboundarmory.iron");
    public static final Translation miningLevelDiamond = new Translation("mining_level.soulboundarmory.diamond");
    public static final Translation miningLevelObsidian = new Translation("mining_level.soulboundarmory.obsidian");

    public static final Translation commandUsage0 = new Translation("command.soulboundarmory.client_usage0");
    public static final Translation commandUsage1 = new Translation("command.soulboundarmory.client_usage1");
    public static final Translation commandNoItem = new Translation("command.soulboundarmory.no_item");

    public static Translation style(Style style) {
        return switch (style) {
            case EXPERIENCE -> xpStyle;
            case BOSS -> bossStyle;
            case HORSE -> horseStyle;
        };
    }
}
