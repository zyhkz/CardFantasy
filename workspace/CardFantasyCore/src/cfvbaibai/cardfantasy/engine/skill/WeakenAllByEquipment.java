package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.CardFantasyRuntimeException;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.List;

public final class WeakenAllByEquipment {

    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, EntityInfo attacker, Player defenderPlayer) throws HeroDieSignal {
        if (defenderPlayer == null) {
            throw new CardFantasyRuntimeException("defenderPlayer is null");
        }
        if (attacker == null) {
            return;
        }
        Skill skill = skillUseInfo.getSkill();
        List<CardInfo> defenders = defenderPlayer.getField().getAliveCards();
        resolver.getStage().getUI().useSkill(attacker, defenders, skill, true);
        Weaken.weakenCard(resolver, skillUseInfo, skillUseInfo.getSkill().getImpact3(), attacker, defenders,true);
    }
}
