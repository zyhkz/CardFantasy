package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.engine.*;

import java.util.List;

public class SpeedUpOpponent {
    public static void apply(SkillUseInfo skillUseInfo, SkillResolver resolver, EntityInfo attacker, Player defender) {
        int summonDelayOffset = skillUseInfo.getSkill().getImpact();
        List<CardInfo> allHandCards = defender.getOwner().getHand().toList();
        CardInfo victim = null;
        for (CardInfo card : allHandCards) {
            if (victim == null || card.getSummonDelay() > victim.getSummonDelay()) {
                victim = card;
            }
        }
        if (victim == null) {
            // No card at hand.
            return;
        }
        int summonDelay = victim.getSummonDelay();
        if (summonDelay < summonDelayOffset) {
            summonDelayOffset = summonDelay;
        }
        if (summonDelayOffset == 0) {
            return;
        }
        resolver.getStage().getUI().useSkill(attacker, victim, skillUseInfo.getSkill(), true);
        resolver.getStage().getUI().increaseSummonDelay(victim, -summonDelayOffset);
        victim.setSummonDelay(summonDelay - summonDelayOffset);
    }
}
