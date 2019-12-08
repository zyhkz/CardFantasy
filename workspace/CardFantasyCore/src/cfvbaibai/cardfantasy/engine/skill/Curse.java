package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.CardFantasyRuntimeException;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

public final class Curse {
    public static void apply(SkillResolver resolver, Skill cardSkill, EntityInfo attacker, Player defenderHero) throws HeroDieSignal {
        if (attacker == null) {
            throw new CardFantasyRuntimeException("attacker is null");
        }
        int damage = cardSkill.getImpact();
        resolver.attackHero(attacker, defenderHero, cardSkill, damage);
    }
}
