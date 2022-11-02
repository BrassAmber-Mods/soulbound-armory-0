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

public interface Translations {
	Text empty = Text.empty();

	Translation tooltipEfficiency = attribute("efficiency");
	Translation tooltipReach = attribute("reach");
	Translation tooltipUpgradeProgress = attribute("upgrade_progress");
	Translation tooltipAttackSpeed = attribute("attack_speed");
	Translation tooltipAttackDamage = attribute("attack_damage");
	Translation tooltipCriticalHitRate = attribute("critical_hit_rate");

	Text guiDagger = gui("dagger").text();
	Text guiSword = gui("sword").text();
	Text guiGreatsword = gui("greatsword").text();
	Text guiBigsword = gui("bigsword").text();
	Text guiTrident = gui("trident").text();
	Text guiPickaxe = gui("pickaxe").text();
	Text guiToolSelection = gui("selection").text();
	Text guiButtonAttributes = gui("attributes").text();
	Text guiButtonEnchantments = gui("enchantments").text();
	Text guiSkills = gui("skills").text();
	Text guiButtonReset = gui("reset").text();
	Text guiButtonBind = gui("bind").text();
	Text guiButtonUnbind = gui("unbind").text();
	PluralizableTranslation guiUnspentPoints = pluralizable(gui("unspent_point"), gui("unspent_points"));
	PluralizableTranslation guiPoints = pluralizable(gui("point"), gui("points"));
	PluralizableTranslation guiSkillUpgradeCost = pluralizable(gui("upgrade_cost_singular"), gui("upgrade_cost_plural"));
	PluralizableTranslation guiSkillLearnCost = pluralizable(gui("learn_cost_singular"), gui("learn_cost_plural"));
	Translation guiLevel = gui("level");
	Translation guiLevelFinite = gui("level_finite");
	Text red = gui("red").text();
	Text green = gui("green").text();
	Text blue = gui("blue").text();
	Text alpha = gui("alpha").text();
	Translation guiEfficiency = gui("efficiency");
	Translation guiReach = gui("reach");
	Translation guiUpgradeProgress = gui("upgrade_progress");
	Translation guiAttackSpeed = gui("attack_speed");
	Translation guiAttackDamage = gui("attack_damage");
	Translation guiCriticalHitRate = gui("critical_hit_rate");
	Translation toggleBar = gui("bar.toggle");
	Translation barLevel = gui("bar.level");
	Translation barXP = gui("bar.xp");
	Translation barFullXP = gui("bar.full_xp");
	Translation style = gui("style");
	Text xpStyle = gui("style.experience").text();
	Text bossStyle = gui("style.boss").text();
	Text horseStyle = gui("style.horse").text();
	Text configure = gui("configure").text();

	Translation levelupMessage = message("levelup");
	Text cannotAbsorbDamaged = message("cannot_absorb_damaged").text();
	Text cannotAbsorbWeaker = message("cannot_absorb_weaker").text();

	Translation guiKey = of("key", "gui");

	Translation tier = of("tier");

	Translation commandNoItem = of("command", "no_item");

	static PluralizableTranslation pluralizable(Translation singular, Translation plural) {
		return new PluralizableTranslation(singular, plural);
	}

	static Text skillName(Skill skill) {
		return Text.of(Util.capitalize(I18n.translate("skill.%s.%s.name".formatted(skill.id().getNamespace(), skill.id().getPath()))));
	}

	static Text toolMaterial(ToolMaterial material) {
		return of("tool_material", TierSortingRegistry.getName(material).getPath()).text();
	}

	static List<Text> skillDescription(Skill skill) {
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
