package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.engine.*;

import java.util.List;

public final class AllDelayDouble {
    public static void apply(SkillUseInfo skillUseInfo, SkillResolver resolver, CardInfo attacker, Player defender)
            throws HeroDieSignal {
        int summonDelayOffset = skillUseInfo.getSkill().getImpact();
        List<CardInfo> allHandCards = defender.getHand().toList();
        List<CardInfo> selfHandCards = attacker.getOwner().getHand().toList();
        resolver.getStage().getUI().useSkill(attacker, allHandCards, skillUseInfo.getSkill(), true);
        resolver.getStage().getUI().useSkill(attacker, selfHandCards, skillUseInfo.getSkill(), true);
        if (resolver.resolveStopDelay(defender)) {
            for (CardInfo card : allHandCards) {
                if (resolver.resolveStopCardDelay(card)) {
                    continue;
                }
                int summonDelay = card.getSummonDelay();
                resolver.getStage().getUI().increaseSummonDelay(card, summonDelayOffset);
                card.setSummonDelay(summonDelay + summonDelayOffset);
            }
        }
        if (resolver.resolveStopDelay(attacker.getOwner())) {
            for (CardInfo card : selfHandCards) {
                if (resolver.resolveStopCardDelay(card)) {
                    continue;
                }
                int summonDelay = card.getSummonDelay();
                resolver.getStage().getUI().increaseSummonDelay(card, summonDelayOffset);
                card.setSummonDelay(summonDelay + summonDelayOffset);
            }
        }
    }
}
