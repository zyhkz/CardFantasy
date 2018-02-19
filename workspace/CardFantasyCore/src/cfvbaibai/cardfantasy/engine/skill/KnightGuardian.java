package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.CardInfo;
import cfvbaibai.cardfantasy.engine.EntityInfo;
import cfvbaibai.cardfantasy.engine.SkillResolver;

public final class KnightGuardian {
    public static int apply(SkillResolver resolver, Skill skill, EntityInfo attacker, CardInfo defender,
            Skill attackSkill, int originalDamage) {
        if (!resolver.isPhysicalAttackSkill(attackSkill) && !resolver.isMagicalSkill(attackSkill)) {
            return originalDamage;
        }
        if(attacker instanceof  CardInfo){
            CardInfo attack = (CardInfo) attacker;
            if(resolver.resolveStopBlockSkill(skill, attack, defender) &&!resolver.isMagicalSkill(attackSkill)) {
                return originalDamage;
            }
        }
        int actualDamage = originalDamage / 2;
        if (resolver.isMagicalSkill(attackSkill) && defender.getHP() < originalDamage) {
            actualDamage = defender.getHP();
        }
        GameUI ui = resolver.getStage().getUI();
        ui.useSkill(defender, attacker, skill, true);
        ui.blockDamage(defender, attacker, defender, skill, originalDamage, actualDamage);
        return actualDamage;
    }
}
