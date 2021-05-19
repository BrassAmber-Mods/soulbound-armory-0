package user11681.soulboundarmory.registry;

import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.tool.common.AmbidexteritySkill;
import user11681.soulboundarmory.skill.tool.common.EnderPullSkill;
import user11681.soulboundarmory.skill.weapon.common.NourishmentSkill;
import user11681.soulboundarmory.skill.weapon.dagger.ReturnSkill;
import user11681.soulboundarmory.skill.weapon.dagger.ShadowCloneSkill;
import user11681.soulboundarmory.skill.weapon.dagger.SneakReturnSkill;
import user11681.soulboundarmory.skill.weapon.dagger.ThrowingSkill;
import user11681.soulboundarmory.skill.weapon.greatsword.FreezingSkill;
import user11681.soulboundarmory.skill.weapon.greatsword.LeapingSkill;
import user11681.soulboundarmory.skill.weapon.staff.EndermanacleSkill;
import user11681.soulboundarmory.skill.weapon.staff.FireballSkill;
import user11681.soulboundarmory.skill.weapon.staff.HealingSkill;
import user11681.soulboundarmory.skill.weapon.staff.PenetrationSkill;
import user11681.soulboundarmory.skill.weapon.staff.VulnerabilitySkill;
import user11681.soulboundarmory.skill.weapon.sword.SummonLightningSkill;

public class Skills {
    public static final Skill ambidexterity = register(new AmbidexteritySkill(SoulboundArmory.id("ambidexterity")));
    public static final Skill endermanacle = register(new EndermanacleSkill(SoulboundArmory.id("endermanacle")));
    public static final Skill enderPull = register(new EnderPullSkill(SoulboundArmory.id("ender_pull")));
    public static final Skill fireball = register(new FireballSkill(SoulboundArmory.id("fireball")));
    public static final Skill freezing = register(new FreezingSkill(SoulboundArmory.id("freezing")));
    public static final Skill healing = register(new HealingSkill(SoulboundArmory.id("healing")));
    public static final Skill leaping = register(new LeapingSkill(SoulboundArmory.id("leaping")));
    public static final Skill nourishment = register(new NourishmentSkill(SoulboundArmory.id("nourishment")));
    public static final Skill penetration = register(new PenetrationSkill(SoulboundArmory.id("penetration")));
    public static final Skill returning = register(new ReturnSkill(SoulboundArmory.id("return")));
    public static final Skill shadowClone = register(new ShadowCloneSkill(SoulboundArmory.id("shadow_clone")));
    public static final Skill sneakReturn = register(new SneakReturnSkill(SoulboundArmory.id("sneak_return")));
    public static final Skill summonLightning = register(new SummonLightningSkill(SoulboundArmory.id("summon_lightning")));
    public static final Skill throwing = register(new ThrowingSkill(SoulboundArmory.id("throwing")));
    public static final Skill vulnerability = register(new VulnerabilitySkill(SoulboundArmory.id("vulnerability")));

    private static Skill register(Skill skill) {
        Skill.registry.register(skill);

        return skill;
    }

    static {
        for (Skill skill : Skill.registry) {
            if (skill.getRegistryName().getNamespace().equals(SoulboundArmory.ID)) {
                skill.initDependencies();
            }
        }
    }
}
