package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.data.SkillType;
import cfvbaibai.cardfantasy.engine.*;

public final class WaterArmorByEquipment {
    public static int apply(Skill cardSkill, SkillResolver resolver, CardInfo attacker, Player defender, int originalDamage) throws HeroDieSignal {
        GameUI ui = resolver.getStage().getUI();
        ui.useSkill(defender, attacker, cardSkill, true);
        int maxDamage = cardSkill.getImpact();
        int maxHealHeroDamage = cardSkill.getImpact2();
        int actualDamage = originalDamage;
        int healHero = 0;
        if (actualDamage > maxDamage) {
            healHero = actualDamage - maxDamage;
            actualDamage = maxDamage;
        }
        if (healHero > maxHealHeroDamage) {
            healHero = maxHealHeroDamage;
        }

        resolver.attackHero(defender, defender.getOwner(), cardSkill, -healHero);

        ui.blockDamage(defender, attacker, defender, cardSkill, originalDamage, actualDamage);
        return actualDamage;
    }
}
