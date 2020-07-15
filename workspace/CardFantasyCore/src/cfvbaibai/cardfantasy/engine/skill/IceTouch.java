package cfvbaibai.cardfantasy.engine.skill;

import java.util.List;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.data.SkillType;
import cfvbaibai.cardfantasy.engine.CardInfo;
import cfvbaibai.cardfantasy.engine.CardStatusItem;
import cfvbaibai.cardfantasy.engine.EntityInfo;
import cfvbaibai.cardfantasy.engine.HeroDieSignal;
import cfvbaibai.cardfantasy.engine.Player;
import cfvbaibai.cardfantasy.engine.SkillResolver;
import cfvbaibai.cardfantasy.engine.SkillUseInfo;

public final class IceTouch {
    public static void apply(SkillUseInfo skillUseInfo, SkillResolver resolver, EntityInfo attacker, Player defender,
            int victimCount,int rate) throws HeroDieSignal {
        List<CardInfo> victims = resolver.getStage().getRandomizer().pickRandom(defender.getField().toList(),
                victimCount, true, null);
        GameUI ui = resolver.getStage().getUI();
        Skill skill = skillUseInfo.getSkill();
        ui.useSkill(attacker, victims, skill, true);
        int damage = skill.getImpact();
        int magnifier = skill.getImpact2();
        for (CardInfo victim : victims) {
            if (!resolver.resolveAttackBlockingSkills(attacker, victim, skillUseInfo.getSkill(), damage).isAttackable()) {
                continue;
            }
            int magicEchoSkillResult = resolver.resolveMagicEchoSkill(attacker, victim, skill);
            if (magicEchoSkillResult==1||magicEchoSkillResult==2) {
                if (attacker instanceof CardInfo) {
                    CardInfo attackCard = (CardInfo) attacker;
                    if (attackCard.isDead()) {
                        if (magicEchoSkillResult == 1) {
                            continue;
                        }
                    }
                    else {
                        int damage2 = damage;
                        if (!resolver.resolveAttackBlockingSkills(victim, attackCard, skillUseInfo.getSkill(), damage2).isAttackable()) {
                            if (magicEchoSkillResult == 1) {
                                continue;
                            }
                        }
                        else{
                            if (resolver.resolveIsImmune(attackCard,0)) {
                                damage2 *= magnifier;
                            } else {
                                if (resolver.getStage().getRandomizer().roll100(rate)) {
                                    CardStatusItem status = CardStatusItem.frozen(skillUseInfo);
                                    if (!resolver.resolveBlockStatusSkills(victim, attackCard, skillUseInfo, status).isBlocked()) {
                                        ui.addCardStatus(victim, attackCard, skill, status);
                                        attackCard.addStatus(status);
                                    }
                                }
                            }
                            ui.attackCard(victim, attackCard, skill, damage2);
                            resolver.resolveDeathSkills(victim, attackCard, skill,
                                    resolver.applyDamage(victim, attackCard, skill, damage2));
                        }
                    }
                }
                if (magicEchoSkillResult == 1) {
                    continue;
                }
            }
            int actualDamage = damage;
            if (resolver.resolveIsImmune(victim,0)) {
                actualDamage *= magnifier;
            } else {
                if (resolver.getStage().getRandomizer().roll100(rate)) {
                    CardStatusItem status = CardStatusItem.frozen(skillUseInfo);
                    if (!resolver.resolveBlockStatusSkills(attacker, victim, skillUseInfo, status).isBlocked()) {
                        ui.addCardStatus(attacker, victim, skill, status);
                        victim.addStatus(status);
                    }
                }
            }
            ui.attackCard(attacker, victim, skill, actualDamage);
            resolver.resolveDeathSkills(attacker, victim, skill,
                    resolver.applyDamage(attacker, victim, skill, actualDamage));
        }
    }
}
