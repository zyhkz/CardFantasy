package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.CardInfo;
import cfvbaibai.cardfantasy.engine.EntityInfo;
import cfvbaibai.cardfantasy.engine.HeroDieSignal;
import cfvbaibai.cardfantasy.engine.SkillResolver;

/**
 * Defensive CardSkill
 * Give 20*level damage to attacker.
 * Unavoidable.
 * 
 * Cannot be blocked by Immue or Dodge.
 */
public final class CounterAttackByEquipment {
    public static void apply(Skill cardSkill, SkillResolver resolver, CardInfo attacker, EntityInfo defender,
            int attackDamage) throws HeroDieSignal {
        if (attackDamage <= 0) {
            return;
        }
        if (attacker == null) {
            return;
        }
        int damage = cardSkill.getImpact3();
        GameUI ui = resolver.getStage().getUI();
        ui.useSkill(defender, attacker, cardSkill, true);

        ui.attackCard(defender, attacker, cardSkill, damage);
        resolver.resolveDeathSkills(defender, attacker, cardSkill, resolver.applyDamage(defender, attacker, cardSkill, damage));
    }
}
