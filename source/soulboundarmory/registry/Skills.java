package soulboundarmory.registry;

import soulboundarmory.skill.Skill;
import soulboundarmory.skill.tool.common.AbsorptionSkill;
import soulboundarmory.skill.tool.common.CircumspectionSkill;
import soulboundarmory.skill.tool.common.EnderPullSkill;
import soulboundarmory.skill.weapon.common.NourishmentSkill;
import soulboundarmory.skill.weapon.common.PrecisionSkill;
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
    public static final Skill absorption = new AbsorptionSkill();
    public static final Skill circumspection = new CircumspectionSkill();
    public static final Skill endermanacle = new EndermanacleSkill();
    public static final Skill enderPull = new EnderPullSkill();
    public static final Skill fireball = new FireballSkill();
    public static final Skill freezing = new FreezingSkill();
    public static final Skill healing = new HealingSkill();
    public static final Skill leaping = new LeapingSkill();
    public static final Skill nourishment = new NourishmentSkill();
    public static final Skill penetration = new PenetrationSkill();
    public static final Skill precision = new PrecisionSkill();
    public static final Skill returning = new ReturnSkill();
    public static final Skill shadowClone = new ShadowCloneSkill();
    public static final Skill sneakReturn = new SneakReturnSkill();
    public static final Skill summonLightning = new SummonLightningSkill();
    public static final Skill throwing = new ThrowingSkill();
    public static final Skill vulnerability = new VulnerabilitySkill();
}
