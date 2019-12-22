package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.Randomizer;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.ArrayList;
import java.util.List;

public class DeathMarkMult {
    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, CardInfo caster, Player defenderHero,int victimCount,int effectNumber) throws HeroDieSignal {

        StageInfo stage = resolver.getStage();
        Randomizer random = stage.getRandomizer();
        GameUI ui = stage.getUI();

        List<CardInfo> victims = random.pickRandom(defenderHero.getField().toList(), victimCount, true, null);
        Skill skill = skillUseInfo.getSkill();
        ui.useSkill(caster, victims, skill, true);
        for(CardInfo victim:victims) {
            CardStatusItem statusItem = CardStatusItem.deathMark(skillUseInfo);
            if (!resolver.resolveAttackBlockingSkills(caster, victim, skill, 1).isAttackable()) {
                continue;
            }
            statusItem.setEffectNumber(effectNumber);
            if (effectNumber > 0) {
                if (!victim.getStatus().getStatusOf(CardStatusType.死印).isEmpty()) {
                    continue;
                }
            }
            int magicEchoSkillResult = resolver.resolveMagicEchoSkill(caster, victim, skill);
            if (magicEchoSkillResult == 1 || magicEchoSkillResult == 2) {
                if (caster.isDead()) {
                    if (magicEchoSkillResult == 1) {
                        continue;
                    }
                } else if (!resolver.resolveAttackBlockingSkills(victim, caster, skill, 1).isAttackable()) {
                    if (magicEchoSkillResult == 1) {
                        continue;
                    }
                } else if (effectNumber > 0) {
                    if (!caster.getStatus().getStatusOf(CardStatusType.死印).isEmpty()) {
                        if (magicEchoSkillResult == 1) {
                            continue;
                        }
                    }
                } else {
                    ui.addCardStatus(victim, caster, skill, statusItem);
                    caster.addStatus(statusItem);
                    if (magicEchoSkillResult == 1) {
                        continue;
                    }
                }
            }
            ui.addCardStatus(caster, victim, skill, statusItem);
            victim.addStatus(statusItem);
        }
    }
}
