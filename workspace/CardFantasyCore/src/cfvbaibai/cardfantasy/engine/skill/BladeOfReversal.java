package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.Randomizer;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.data.SkillTag;
import cfvbaibai.cardfantasy.engine.*;

import java.util.ArrayList;
import java.util.List;

public class BladeOfReversal {
    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, CardInfo attackCard, CardInfo defenderCard,int victimCount,int effectNumber) throws HeroDieSignal {

        StageInfo stage = resolver.getStage();
        Randomizer random = stage.getRandomizer();
        Player attackHero = attackCard.getOwner();
        Player defenderHero = defenderCard.getOwner();
        GameUI ui = resolver.getStage().getUI();
        Skill skill = skillUseInfo.getSkill();
        List<CardInfo> victims = random.pickRandom(attackHero.getField().toList(), victimCount, true, null);
        if (victims.size() > 0) {
            ui.useSkill(attackCard, victims, skill, true);
            CardStatusItem statusItem = CardStatusItem.sheep(skillUseInfo);
            CardStatusItem statusItemSlience = CardStatusItem.slience(skillUseInfo);
            statusItem.setEffectNumber(effectNumber);
            for (CardInfo victim : victims) {
                if (!resolver.resolveAttackBlockingSkills(attackCard, victim, skill, 1).isAttackable()) {
                    continue;
                }
                if(effectNumber>0)
                {
                    if(!victim.getStatus().getStatusOf(CardStatusType.变羊).isEmpty()){
                        continue;
                    }
                }
                ui.addCardStatus(attackCard, victim, skill, statusItem);
                victim.addStatus(statusItem);
                ui.addCardStatus(attackCard, victim, skill, statusItemSlience);
                victim.addStatus(statusItemSlience);
                int impactAdd = victim.getInitAT()-skill.getImpact();
                resolver.getStage().getUI().adjustAT(attackCard, victim, -impactAdd, skill);
                victim.addCoefficientEffect(new SkillEffect(SkillEffectType.ATTACK_CHANGE, skillUseInfo, -impactAdd, false));
                int impactHpAdd = victim.getHP()-skill.getImpact2();
                resolver.getStage().getUI().adjustHP(attackCard, victim, -impactHpAdd, skill);
                victim.addCoefficientEffect(new SkillEffect(SkillEffectType.MAXHP_CHANGE, skillUseInfo, -impactHpAdd, false));
            }

        }
        if (defenderCard.isAlive()) {
            int healHP = 999999;
            if (healHP + defenderCard.getHP() > defenderCard.getMaxHP()) {
                healHP = defenderCard.getMaxHP() - defenderCard.getHP();
            }
            if (healHP == 0) {
                return;
            }
            OnAttackBlockingResult result = resolver.resolveHealBlockingSkills(defenderCard, defenderCard, skill);
            if (!result.isAttackable()) {
                return;
            }
            resolver.getStage().getUI().useSkill(defenderCard, skill, true);
            resolver.getStage().getUI().healCard(defenderCard, defenderCard, skill, healHP);
            resolver.applyDamage(defenderCard, defenderCard, skill, -healHP);
        }
        if(defenderHero.getGrave().size() >=2){
            Grave grave = defenderHero.getGrave();
            List<CardInfo> revivableCards = new ArrayList<CardInfo>();
            for (CardInfo deadCard : grave.toList()) {
                if (deadCard != null &&!deadCard.getIsDeathNow()&& !deadCard.containsAllUsableSkillsWithTag(SkillTag.复活)) {
                    revivableCards.add(deadCard);
                }
            }
            if (revivableCards.isEmpty()) {
                return;
            }
            CardInfo cardToRevive =null;
            if(revivableCards.size()==1)
            {
                cardToRevive =revivableCards.get(0);
            }
            else {
                cardToRevive = resolver.getStage().getRandomizer().pickRandom(
                        revivableCards, 1, true, null).get(0);
            }
            resolver.getStage().getUI().useSkill(defenderCard, cardToRevive, skill, true);
            if (SoulSeal.soulSealed(resolver, defenderCard)) {
                return;
            }
            defenderHero.getGrave().removeCard(cardToRevive);
            resolver.summonCard(defenderHero, cardToRevive, defenderCard, false, skill,0);
            CardStatusItem item = CardStatusItem.weak(skillUseInfo);
            resolver.getStage().getUI().addCardStatus(defenderCard, cardToRevive, skill, item);
            cardToRevive.addStatus(item);
        } else{
            List<CardInfo> allDeckCards = defenderHero.getDeck().toList();
            CardInfo target = null;
            for (CardInfo card : allDeckCards) {
                if (target == defenderCard) {
                    continue;
                }
                if (target == null || card.getSummonDelay() > target.getSummonDelay()) {
                    target = card;
                }
            }
            if (target == null) {
                // No card in deck.
                return;
            }
            resolver.getStage().getUI().useSkill(defenderCard, target, skill, true);
            resolver.summonCard(defenderCard.getOwner(), target, null, false, skill,0);
            CardStatusItem item = CardStatusItem.weak(skillUseInfo);
            target.addStatus(item);
        }

    }
}
