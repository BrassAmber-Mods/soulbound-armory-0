package soulboundarmory.client.i18n;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.skill.Skill;
import soulboundarmory.text.Translation;
import soulboundarmory.util.Util;

public class Translations {
    public static final Translation tooltipToolEfficiency = attribute("tool_efficiency");
    public static final Translation tooltipWeaponEfficiency = attribute("weapon_efficiency");
    public static final Translation tooltipReach = attribute("reach");
    public static final Translation tooltipMiningLevel = attribute("harvest_level");
    public static final Translation tooltipAttackSpeed = attribute("attack_speed");
    public static final Translation tooltipAttackDamage = attribute("attack_damage");
    public static final Translation tooltipCriticalStrikeRate = attribute("critical_strike_rate");
    public static final Translation tooltipAttackRange = attribute("attack_range");

    public static final Translation guiDagger = gui("dagger");
    public static final Translation guiSword = gui("sword");
    public static final Translation guiGreatsword = gui("greatsword");
    public static final Translation guiStaff = gui("staff");
    public static final Translation guiPick = gui("pick");
    public static final Translation guiToolSelection = gui("tool_selection");
    public static final Translation guiWeaponSelection = gui("weapon_selection");
    public static final Translation guiButtonAttributes = gui("attributes");
    public static final Translation guiButtonEnchantments = gui("enchantments");
    public static final Translation guiSkills = gui("skills");
    public static final Translation guiButtonReset = gui("reset");
    public static final Translation guiButtonBind = gui("bind");
    public static final Translation guiButtonUnbind = gui("unbind");
    public static final Translation guiUnspentPoints = gui("unspent_points");
    public static final Translation guiUnspentPoint = gui("unspent_point");
    public static final Translation guiPoints = gui("points");
    public static final Translation guiPoint = gui("point");
    public static final Translation guiSkillLearnCostSingular = gui("learn_cost_singular");
    public static final Translation guiSkillLearnCostPlural = gui("learn_cost_plural");
    public static final Translation guiSkillUpgradeCostSingular = gui("upgrade_cost_singular");
    public static final Translation guiSkillUpgradeCostPlural = gui("upgrade_cost_plural");
    public static final Translation guiLevel = gui("level");
    public static final Translation red = gui("red");
    public static final Translation green = gui("green");
    public static final Translation blue = gui("blue");
    public static final Translation alpha = gui("alpha");
    public static final Translation guiToolEfficiency = gui("tool_efficiency");
    public static final Translation guiWeaponEfficiency = gui("weapon_efficiency");
    public static final Translation guiReach = gui("reach");
    public static final Translation guiMiningLevel = gui("harvest_level");
    public static final Translation guiAttackSpeed = gui("attack_speed");
    public static final Translation guiAttackDamage = gui("attack_damage");
    public static final Translation guiCriticalStrikeRate = gui("critical_strike_rate");
    public static final Translation guiAttackRange = gui("attack_range");
    public static final Translation barLevel = gui("bar.level");
    public static final Translation barXP = gui("bar.xp");
    public static final Translation barFullXP = gui("bar.full_xp");
    public static final Translation style = gui("style");
    public static final Translation xpStyle = gui("style.experience");
    public static final Translation bossStyle = gui("style.boss");
    public static final Translation horseStyle = gui("style.horse");

    public static final Translation miningLevelWood = harvestLevel("wood");
    public static final Translation miningLevelStone = harvestLevel("stone");
    public static final Translation miningLevelIron = harvestLevel("iron");
    public static final Translation miningLevelDiamond = harvestLevel("diamond");
    public static final Translation miningLevelNetherite = harvestLevel("netherite");

    public static final Translation hudSpell = of("hud", "spell");

    public static final Translation guiKey = of("key", "gui");

    public static final Translation levelupMessage = of("message", "levelup");

    public static final Translation commandNoItem = of("command", "no_item");

    public static Text skillName(Skill skill) {
        return Text.of(Util.capitalize(I18n.translate("skill.%s.%s.name".formatted(skill.getRegistryName().getNamespace(), skill.getRegistryName().getPath()))));
    }

    public static List<Text> skillDescription(Skill skill) {
        return Stream.of(Util.capitalize(I18n.translate("skill.%s.%s.desc".formatted(skill.getRegistryName().getNamespace(), skill.getRegistryName().getPath()))).split("\n")).map(Text::of).toList();
    }

    private static Translation of(String category, String path) {
        return Translation.of("%s.%s.%s", category, SoulboundArmory.ID, path);
    }

    private static Translation attribute(String path) {
        return of("attribute", path);
    }

    private static Translation gui(String path) {
        return of("gui", path);
    }

    private static Translation harvestLevel(String path) {
        return of("harvest_level", path);
    }
}
