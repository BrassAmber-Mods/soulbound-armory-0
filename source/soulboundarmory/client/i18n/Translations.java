package soulboundarmory.client.i18n;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraftforge.common.TierSortingRegistry;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.skill.Skill;
import soulboundarmory.text.PluralizableTranslation;
import soulboundarmory.text.Translation;
import soulboundarmory.util.Util;

public class Translations {
    public static final Translation tooltipEfficiency = attribute("efficiency");
    public static final Translation tooltipReach = attribute("reach");
    public static final Translation tooltipUpgradeProgress = attribute("upgrade_progress");
    public static final Translation tooltipAttackSpeed = attribute("attack_speed");
    public static final Translation tooltipAttackDamage = attribute("attack_damage");
    public static final Translation tooltipCriticalHitRate = attribute("critical_hit_rate");

    public static final Translation guiDagger = gui("dagger");
    public static final Translation guiSword = gui("sword");
    public static final Translation guiGreatsword = gui("greatsword");
    public static final Translation guiBigsword = gui("bigsword");
    public static final Translation guiTrident = gui("trident");
    public static final Translation guiPick = gui("pick");
    public static final Translation guiToolSelection = gui("selection");
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
    public static final PluralizableTranslation guiSkillUpgradeCost = new PluralizableTranslation(gui("upgrade_cost_singular"), gui("upgrade_cost_plural"));
    public static final PluralizableTranslation guiSkillLearnCost = new PluralizableTranslation(gui("learn_cost_singular"), gui("learn_cost_plural"));
    public static final Translation guiLevel = gui("level");
    public static final Translation guiLevelFinite = gui("level_finite");
    public static final Translation red = gui("red");
    public static final Translation green = gui("green");
    public static final Translation blue = gui("blue");
    public static final Translation alpha = gui("alpha");
    public static final Translation guiEfficiency = gui("efficiency");
    public static final Translation guiReach = gui("reach");
    public static final Translation guiUpgradeProgress = gui("upgrade_progress");
    public static final Translation guiAttackSpeed = gui("attack_speed");
    public static final Translation guiAttackDamage = gui("attack_damage");
    public static final Translation guiCriticalHitRate = gui("critical_hit_rate");
    public static final Translation toggleBar = gui("bar.toggle");
    public static final Translation barLevel = gui("bar.level");
    public static final Translation barXP = gui("bar.xp");
    public static final Translation barFullXP = gui("bar.full_xp");
    public static final Translation style = gui("style");
    public static final Translation xpStyle = gui("style.experience");
    public static final Translation bossStyle = gui("style.boss");
    public static final Translation horseStyle = gui("style.horse");

    public static final Translation levelupMessage = message("levelup");
    public static final Translation cannotAbsorbDamaged = message("cannot_absorb_damaged");
    public static final Translation cannotAbsorbWeaker = message("cannot_absorb_weaker");

    public static final Translation guiKey = of("key", "gui");

    public static final Translation tier = of("tier");

    public static final Translation commandNoItem = of("command", "no_item");

    public static Text skillName(Skill skill) {
        return Text.of(Util.capitalize(I18n.translate("skill.%s.%s.name".formatted(skill.id().getNamespace(), skill.id().getPath()))));
    }

    public static Translation toolMaterial(ToolMaterial material) {
        return of("tool_material", TierSortingRegistry.getName(material).getPath());
    }

    public static List<Text> skillDescription(Skill skill) {
        return Stream.of(I18n.translate("skill.%s.%s.desc".formatted(skill.id().getNamespace(), skill.id().getPath())).split("\n")).map(Text::of).toList();
    }

    private static Translation of(String path) {
        return Translation.of("%s.%s", SoulboundArmory.ID, path);
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

    private static Translation message(String path) {
        return of("message", path);
    }
}
