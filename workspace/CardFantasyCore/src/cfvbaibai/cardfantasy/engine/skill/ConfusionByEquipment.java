package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.Randomizer;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.List;

public final class ConfusionByEquipment {
    public static void apply(SkillUseInfo skillUseInfo, SkillResolver resolver, EquipmentInfo attacker, Player defender,
            int victimCount) throws HeroDieSignal {
        Skill skill = skillUseInfo.getSkill();
        StageInfo stage = resolver.getStage();
        Randomizer random = stage.getRandomizer();
        GameUI ui = stage.getUI();

        int rate = skill.getImpact();
        List<CardInfo> victims = random.pickRandom(defender.getField().toList(), victimCount, true, null);
        ui.useSkill(attacker, victims, skill, true);
        for (CardInfo victim : victims) {
            if (!resolver.resolveAttackBlockingSkills(attacker, victim, skill, 1).isAttackable()) {
                continue;
            }
            if (victim.getStatus().containsStatus(CardStatusType.迷惑)) {
                continue;
            }
            int magicEchoSkillResult = resolver.resolveMagicEchoSkill(attacker, victim, skill);
            if (magicEchoSkillResult==1||magicEchoSkillResult==2) {
                if (magicEchoSkillResult == 1) {
                    continue;
                }
            }
            if (random.roll100(rate)) {
                CardStatusItem status = CardStatusItem.confused(skillUseInfo);
                ui.addCardStatus(attacker, victim, skill, status);
                victim.addStatus(status);
            }
        }
    }
}
