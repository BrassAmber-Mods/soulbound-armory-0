package soulboundarmory.registry;

import soulboundarmory.SoulboundArmory;
import soulboundarmory.skill.Skill;
import soulboundarmory.skill.tool.common.EnderPullSkill;
import soulboundarmory.skill.weapon.common.NourishmentSkill;
import soulboundarmory.skill.weapon.dagger.ReturnSkill;
import soulboundarmory.skill.weapon.dagger.ShadowCloneSkill;
import soulboundarmory.skill.weapon.dagger.SneakReturnSkill;
import soulboundarmory.skill.weapon.dagger.ThrowingSkill;
import soulboundarmory.skill.weapon.greatsword.FreezingSkill;
import soulboundarmory.skill.weapon.greatsword.LeapingSkill;
import soulboundarmory.skill.weapon.staff.EndermanacleSkill;
import soulboundarmory.skill.weapon.staff.FireballSkill;
import soulboundarmory.skill.weapon.staff.HealingSkill;
import soulboundarmory.skill.weapon.staff.PenetrationSkill;
import soulboundarmory.skill.weapon.staff.VulnerabilitySkill;
import soulboundarmory.skill.weapon.sword.SummonLightningSkill;

public class Skills {
    // Should not be visible in the game if not implemented.
    // public static final Skill ambidexterity = new AmbidexteritySkill(SoulboundArmory.id("ambidexterity"));
    public static final Skill endermanacle = new EndermanacleSkill(SoulboundArmory.id("endermanacle"));
    public static final Skill enderPull = new EnderPullSkill(SoulboundArmory.id("ender_pull"));
    public static final Skill fireball = new FireballSkill(SoulboundArmory.id("fireball"));
    public static final Skill freezing = new FreezingSkill(SoulboundArmory.id("freezing"));
    public static final Skill healing = new HealingSkill(SoulboundArmory.id("healing"));
    public static final Skill leaping = new LeapingSkill(SoulboundArmory.id("leaping"));
    public static final Skill nourishment = new NourishmentSkill(SoulboundArmory.id("nourishment"));
    public static final Skill penetration = new PenetrationSkill(SoulboundArmory.id("penetration"));
    public static final Skill returning = new ReturnSkill(SoulboundArmory.id("return"));
    public static final Skill shadowClone = new ShadowCloneSkill(SoulboundArmory.id("shadow_clone"));
    public static final Skill sneakReturn = new SneakReturnSkill(SoulboundArmory.id("sneak_return"));
    public static final Skill summonLightning = new SummonLightningSkill(SoulboundArmory.id("summon_lightning"));
    public static final Skill throwing = new ThrowingSkill(SoulboundArmory.id("throwing"));
    public static final Skill vulnerability = new VulnerabilitySkill(SoulboundArmory.id("vulnerability"));
}
