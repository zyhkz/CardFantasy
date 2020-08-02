package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.Randomizer;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.List;

public final class HotSummerFire {
    public static void apply(SkillUseInfo skillUseInfo, SkillResolver resolver, EntityInfo attacker, Player defender,
            int victimCount) throws HeroDieSignal {
        StageInfo stage = resolver.getStage();
        Randomizer random = stage.getRandomizer();
        GameUI ui = stage.getUI();

        List<CardInfo> victims = random.pickRandom(defender.getField().toList(), victimCount, true, null);

        //火球类技能
        Skill oneSkill = skillUseInfo.getAttachedUseInfo1().getSkill();
        //燃烧类技能
        Skill twoSkill = skillUseInfo.getAttachedUseInfo2().getSkill();
        ui.useSkill(attacker, victims, skillUseInfo.getSkill(), true);
        int burningDamage = twoSkill.getImpact();
        CardStatusItem newBurningStatus = CardStatusItem.burning(burningDamage, skillUseInfo);
        for (CardInfo victim : victims) {
            int damage = random.next(oneSkill.getImpact(), oneSkill.getImpact2()  + 1);
            OnAttackBlockingResult result = resolver.resolveAttackBlockingSkills(attacker, victim, oneSkill, damage);
            if (!result.isAttackable()) {
                continue;
            }
            int magicEchoSkillResult = resolver.resolveMagicEchoSkill(attacker, victim, oneSkill);
            if (magicEchoSkillResult==1||magicEchoSkillResult==2) {
                if (attacker instanceof CardInfo) {
                    CardInfo attackCard =  (CardInfo)attacker;
                    if(attackCard.isDead())
                    {
                        if (magicEchoSkillResult == 1) {
                            continue;
                        }
                    }
                    else {
                        OnAttackBlockingResult result2 = resolver.resolveAttackBlockingSkills(victim, attackCard, oneSkill, damage);
                        int damage2 = result2.getDamage();
                        if (!result2.isAttackable()) {
                            if (magicEchoSkillResult == 1) {
                                continue;
                            }
                        }
                       else {
                            ui.attackCard(victim, attackCard, oneSkill, damage2);
                            OnDamagedResult resultDamage2 = resolver.applyDamage(victim, attackCard, oneSkill, damage2);
                            if (!resultDamage2.cardDead && !resultDamage2.unbending) {
                                boolean skipped = false;
                                for (CardStatusItem existingBurningStatus : attackCard.getStatus().getStatusOf(CardStatusType.燃烧)) {
                                    if (existingBurningStatus.getEffect() == newBurningStatus.getEffect()) {
                                        skipped = true;
                                        break;
                                    }
                                }
                                if(!skipped){
                                    OnAttackBlockingResult resultBurning = resolver.resolveAttackBlockingSkills(victim, attackCard, twoSkill, burningDamage);
                                    if (resultBurning.isAttackable()) {
                                        ui.addCardStatus(victim, attackCard, twoSkill, newBurningStatus);
                                        attackCard.addStatus(newBurningStatus);
                                    }
                                }
                            }
                            resolver.resolveDeathSkills(victim, attackCard, oneSkill, resultDamage2);
                        }
                    }
                }
                if (magicEchoSkillResult == 1) {
                    continue;
                }
            }
            damage = result.getDamage();
            ui.attackCard(attacker, victim, oneSkill, damage);
            OnDamagedResult resultDamage=resolver.applyDamage(attacker, victim, oneSkill, damage);
            if (!resultDamage.cardDead && !resultDamage.unbending) {
                boolean skipped = false;
                for (CardStatusItem existingBurningStatus : victim.getStatus().getStatusOf(CardStatusType.燃烧)) {
                    if (existingBurningStatus.getEffect() == newBurningStatus.getEffect()) {
                        skipped = true;
                        break;
                    }
                }
                if(!skipped){
                    OnAttackBlockingResult resultBurning = resolver.resolveAttackBlockingSkills(attacker, victim, twoSkill, burningDamage);
                    if (resultBurning.isAttackable()) {
                        ui.addCardStatus(attacker, victim, twoSkill, newBurningStatus);
                        victim.addStatus(newBurningStatus);
                    }
                }
            }
            resolver.resolveDeathSkills(attacker, victim, oneSkill, resultDamage);
        }
    }
}
