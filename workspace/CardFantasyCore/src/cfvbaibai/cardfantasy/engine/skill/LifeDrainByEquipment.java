package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

public class LifeDrainByEquipment {
    public static void apply(SkillUseInfo skillUseInfo, SkillResolver resolver, CardInfo attacker, EquipmentInfo defender) throws HeroDieSignal {
        Skill skill = skillUseInfo.getSkill();
        int damage = attacker.getBasicMaxHP() * skill.getImpact3() / 100;

        if (!attacker.isDead()) {
            // 反射装甲+恶灵汲取的场合，攻击卡牌可能先被送还了，然后再发动恶灵汲取
            OnAttackBlockingResult onAttackBlockingResult = resolver.resolveAttackBlockingSkills(defender, attacker, skill, damage);
            if (!onAttackBlockingResult.isAttackable()) {
                return;
            }
            damage = onAttackBlockingResult.getDamage();
            resolver.getStage().getUI().attackCard(defender, attacker, skill, damage);
            resolver.resolveDeathSkills(defender, attacker, skill, resolver.applyDamage(defender, attacker, skill, damage));
        }
    }
}
