package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.data.Race;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.data.SkillTag;
import cfvbaibai.cardfantasy.engine.*;

import java.util.ArrayList;
import java.util.List;

public class SoulControlAll {
    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, CardInfo attacker, Player defenderHero) throws HeroDieSignal {
//        if (attacker.hasUsed(skillUseInfo)) {
//            return;
//        }
        if (SoulSeal.soulSealed(resolver, attacker)) {
            return;
        }
        Skill skill = skillUseInfo.getSkill();
        List<CardInfo> candidates = new ArrayList<CardInfo>();
        for (CardInfo deadCard : defenderHero.getGrave().toList()) {
            if (!deadCard.getIsDeathNow() && !deadCard.containsUsableSkillsWithTag(SkillTag.召唤) &&
                !deadCard.containsUsableSkillsWithTag(SkillTag.复活) &&
                !deadCard.containsUsableSkillsWithTag(SkillTag.守护) &&
                !deadCard.containsUsableSkillsWithTag(SkillTag.抗夺魂) &&
                deadCard.getRace() != Race.BOSS &&
                deadCard.getRace() != Race.DEMON) {
                candidates.add(deadCard);
            }
        }
        resolver.getStage().getUI().useSkill(attacker, candidates, skill, true);
        for(CardInfo victim:candidates) {
            victim.switchOwner(attacker.getOwner());
            resolver.summonCard(victim.getOwner(), victim, attacker, false, skill, 0);
            CardStatusItem weakStatusItem = CardStatusItem.weak(skillUseInfo);
            resolver.getStage().getUI().addCardStatus(attacker, victim, skill, weakStatusItem);
            victim.addStatus(weakStatusItem);
        }
//        attacker.setUsed(skillUseInfo);
    }
}
