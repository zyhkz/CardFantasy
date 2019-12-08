package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.Randomizer;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.List;

public class MagicMarkByEquipment {
    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, EntityInfo caster, Player enemyPlayer, int victimCount) throws HeroDieSignal {
        GameUI ui = resolver.getStage().getUI();
        Skill skill = skillUseInfo.getSkill();
        Randomizer random = Randomizer.getRandomizer();
        List<CardInfo> victims = random.pickRandom(enemyPlayer.getField().toList(), victimCount, true, null);
        ui.useSkill(caster, victims, skill, true);
        for (CardInfo victim : victims) {
            CardStatusItem statusItem = CardStatusItem.magicMark(skillUseInfo);
            if (!resolver.resolveAttackBlockingSkills(caster, victim, skill, 1).isAttackable()) {
                continue;
            }
            int magicEchoSkillResult = resolver.resolveMagicEchoSkill(caster, victim, skill);
            if (magicEchoSkillResult == 1 || magicEchoSkillResult == 2) {
                if (caster instanceof CardInfo) {
                    CardInfo attackCard = (CardInfo) caster;
                    if (attackCard.isDead()) {
                        if (magicEchoSkillResult == 1) {
                            continue;
                        }
                    }
                    else{
                        if (!resolver.resolveAttackBlockingSkills(victim, attackCard, skill, 1).isAttackable()) {
                            if (magicEchoSkillResult == 1) {
                                continue;
                            }
                        }
                        else{
                            ui.addCardStatus(victim, attackCard, skill, statusItem);
                            attackCard.addStatus(statusItem);
                        }
                    }
                }
                if (magicEchoSkillResult == 1) {
                    continue;
                }
            }
            ui.addCardStatus(caster, victim, skill, statusItem);
            victim.addStatus(statusItem);
        }
    }
}
