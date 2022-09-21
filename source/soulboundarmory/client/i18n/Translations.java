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
    public static final Text empty = Text.empty();

    public static final Translation tooltipEfficiency = attribute("efficiency");
    public static final Translation tooltipReach = attribute("reach");
    public static final Translation tooltipUpgradeProgress = attribute("upgrade_progress");
    public static final Translation tooltipAttackSpeed = attribute("attack_speed");
    public static final Translation tooltipAttackDamage = attribute("attack_damage");
    public static final Translation tooltipCriticalHitRate = attribute("critical_hit_rate");

    public static final Text guiDagger = gui("dagger").text();
    public static final Text guiSword = gui("sword").text();
    public static final Text guiGreatsword = gui("greatsword").text();
    public static final Text guiBigsword = gui("bigsword").text();
    public static final Text guiTrident = gui("trident").text();
    public static final Text guiPick = gui("pick").text();
    public static final Text guiToolSelection = gui("selection").text();
    public static final Text guiButtonAttributes = gui("attributes").text();
    public static final Text guiButtonEnchantments = gui("enchantments").text();
    public static final Text guiSkills = gui("skills").text();
    public static final Text guiButtonReset = gui("reset").text();
    public static final Text guiButtonBind = gui("bind").text();
    public static final Text guiButtonUnbind = gui("unbind").text();
    public static final PluralizableTranslation guiUnspentPoints = new PluralizableTranslation(gui("unspent_points"), gui("unspent_point"));
    public static final PluralizableTranslation guiPoints = new PluralizableTranslation(gui("points"), gui("point"));
    public static final PluralizableTranslation guiSkillUpgradeCost = new PluralizableTranslation(gui("upgrade_cost_singular"), gui("upgrade_cost_plural"));
    public static final PluralizableTranslation guiSkillLearnCost = new PluralizableTranslation(gui("learn_cost_singular"), gui("learn_cost_plural"));
    public static final Translation guiLevel = gui("level");
    public static final Translation guiLevelFinite = gui("level_finite");
    public static final Text red = gui("red").text();
    public static final Text green = gui("green").text();
    public static final Text blue = gui("blue").text();
    public static final Text alpha = gui("alpha").text();
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
    public static final Text xpStyle = gui("style.experience").text();
    public static final Text bossStyle = gui("style.boss").text();
    public static final Text horseStyle = gui("style.horse").text();
    public static final Text configure = gui("configure").text();

    public static final Translation levelupMessage = message("levelup");
    public static final Text cannotAbsorbDamaged = message("cannot_absorb_damaged").text();
    public static final Text cannotAbsorbWeaker = message("cannot_absorb_weaker").text();

    public static final Translation guiKey = of("key", "gui");

    public static final Translation tier = of("tier");

    public static final Translation commandNoItem = of("command", "no_item");

    public static Text skillName(Skill skill) {
        return Text.of(Util.capitalize(I18n.translate("skill.%s.%s.name".formatted(skill.id().getNamespace(), skill.id().getPath()))));
    }

    public static Text toolMaterial(ToolMaterial material) {
        return of("tool_material", TierSortingRegistry.getName(material).getPath()).text();
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
