package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.List;

public final class ContinuousFireMult {
    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, EntityInfo attacker, Player defender)
            throws HeroDieSignal {
        GameUI ui = resolver.getStage().getUI();
        Skill skill = skillUseInfo.getSkill();

        int soulNumber = skillUseInfo.getSkillNumber();
        int impact = skillUseInfo.getSkill().getImpact();
        if(soulNumber == -1){
            soulNumber = 1;
            skillUseInfo.setSkillNumber(soulNumber);
        }else if(soulNumber<impact){
            soulNumber++;
            skillUseInfo.setSkillNumber(soulNumber);
        }

        for(int soul=0; soul< soulNumber;soul++) {
            List<CardInfo> victims = defender.getField().getAliveCards();
            List<CardInfo> attackVictims = attacker.getOwner().getField().getAliveCards();
            ui.useSkill(attacker, victims, skill, true);
            for (CardInfo victim : victims) {
                int damage = skill.getImpact() + skill.getImpact2() * victims.size();
                OnAttackBlockingResult result = resolver.resolveAttackBlockingSkills(attacker, victim, skill, damage);
                if (!result.isAttackable()) {
                    continue;
                }
                int magicEchoSkillResult = resolver.resolveMagicEchoSkill(attacker, victim, skill);
                if (magicEchoSkillResult == 1 || magicEchoSkillResult == 2) {
                    if (attacker instanceof CardInfo) {
                        CardInfo attackCard = (CardInfo) attacker;
                        if (attackCard.isDead()) {
                            if (magicEchoSkillResult == 1) {
                                continue;
                            }
                        } else {
                            int damage2 = skill.getImpact() + skill.getImpact2() * attackVictims.size();
                            OnAttackBlockingResult result2 = resolver.resolveAttackBlockingSkills(victim, attackCard, skill, damage2);
                            if (!result2.isAttackable()) {
                                if (magicEchoSkillResult == 1) {
                                    continue;
                                }
                            } else {
                                damage2 = result2.getDamage();
                                resolver.attackHero(defender, attackCard.getOwner(), skill, damage2);
                                ui.attackCard(victim, attackCard, skill, damage2);
                                resolver.resolveDeathSkills(victim, attackCard, skill, resolver.applyDamage(victim, attackCard, skill, damage2));
                            }
                        }
                    }
                    if (magicEchoSkillResult == 1) {
                        continue;
                    }
                }
                damage = result.getDamage();
                resolver.attackHero(attacker, defender, skill, damage);
                ui.attackCard(attacker, victim, skill, damage);
                resolver.resolveDeathSkills(attacker, victim, skill, resolver.applyDamage(attacker, victim, skill, damage));
            }
        }
    }

    public static void reset( SkillUseInfo skillUseInfo, CardInfo card) throws HeroDieSignal {
        skillUseInfo.setSkillNumber(0);
    }
}
