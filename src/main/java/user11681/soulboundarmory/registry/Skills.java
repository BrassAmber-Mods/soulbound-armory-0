package user11681.soulboundarmory.registry;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.weapon.common.NourishmentSkill;
import user11681.soulboundarmory.skill.weapon.dagger.ReturnSkill;
import user11681.soulboundarmory.skill.weapon.dagger.ShadowCloneSkill;
import user11681.soulboundarmory.skill.weapon.dagger.SneakReturnSkill;
import user11681.soulboundarmory.skill.weapon.dagger.ThrowingSkill;
import user11681.soulboundarmory.skill.weapon.greatsword.FreezingSkill;
import user11681.soulboundarmory.skill.weapon.greatsword.LeapingSkill;
import user11681.soulboundarmory.skill.tool.common.AmbidexteritySkill;
import user11681.soulboundarmory.skill.tool.common.EnderPullSkill;
import user11681.soulboundarmory.skill.weapon.staff.EndermanacleSkill;
import user11681.soulboundarmory.skill.weapon.staff.FireballSkill;
import user11681.soulboundarmory.skill.weapon.staff.HealingSkill;
import user11681.soulboundarmory.skill.weapon.staff.PenetrationSkill;
import user11681.soulboundarmory.skill.weapon.staff.VulnerabilitySkill;
import user11681.soulboundarmory.skill.weapon.sword.SummonLightningSkill;

public class Skills {
    public static final Skill AMBIDEXTERITY = Skill.skill.register(new AmbidexteritySkill(new Identifier(SoulboundArmory.ID, "ambidexterity")));
    public static final Skill ENDERMANACLE = Skill.skill.register(new EndermanacleSkill(new Identifier(SoulboundArmory.ID, "endermanacle")));
    public static final Skill ENDER_PULL = Skill.skill.register(new EnderPullSkill(new Identifier(SoulboundArmory.ID, "ender_pull")));
    public static final Skill FIREBALL = Skill.skill.register(new FireballSkill(new Identifier(SoulboundArmory.ID, "fireball")));
    public static final Skill FREEZING = Skill.skill.register(new FreezingSkill(new Identifier(SoulboundArmory.ID, "freezing")));
    public static final Skill HEALING = Skill.skill.register(new HealingSkill(new Identifier(SoulboundArmory.ID, "healing")));
    public static final Skill LEAPING = Skill.skill.register(new LeapingSkill(new Identifier(SoulboundArmory.ID, "leaping")));
    public static final Skill NOURISHMENT = Skill.skill.register(new NourishmentSkill(new Identifier(SoulboundArmory.ID, "nourishment")));
    public static final Skill PENETRATION = Skill.skill.register(new PenetrationSkill(new Identifier(SoulboundArmory.ID, "penetration")));
    public static final Skill RETURN = Skill.skill.register(new ReturnSkill(new Identifier(SoulboundArmory.ID, "return")));
    public static final Skill SHADOW_CLONE = Skill.skill.register(new ShadowCloneSkill(new Identifier(SoulboundArmory.ID, "shadow_clone")));
    public static final Skill SNEAK_RETURN = Skill.skill.register(new SneakReturnSkill(new Identifier(SoulboundArmory.ID, "sneak_return")));
    public static final Skill SUMMON_LIGHTNING = Skill.skill.register(new SummonLightningSkill(new Identifier(SoulboundArmory.ID, "summon_lightning")));
    public static final Skill THROWING = Skill.skill.register(new ThrowingSkill(new Identifier(SoulboundArmory.ID, "throwing")));
    public static final Skill VULNERABILITY = Skill.skill.register(new VulnerabilitySkill(new Identifier(SoulboundArmory.ID, "vulnerability")));

    static {
        for (final Skill skill : Skill.skill) {
            if (skill.getIdentifier().getNamespace().equals(SoulboundArmory.ID)) {
                skill.initDependencies();
            }
        }
    }
}
