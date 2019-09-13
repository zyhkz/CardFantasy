package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

public class MonkeyStick {
    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, CardInfo attacker, CardInfo defender) throws HeroDieSignal {
        if (defender == null || defender.isDead() ) {
            return;
        }
        if (defender.getStatus().containsStatus(CardStatusType.晕眩)) {
            return;
        }
        Skill skill = skillUseInfo.getSkill();
        int impact = skill.getImpact();
        int rate = skill.getImpact2();
        GameUI ui = resolver.getStage().getUI();
        ui.useSkill(attacker, defender, skill, true);
        if (!resolver.resolveAttackBlockingSkills(attacker, defender, skill, 1).isAttackable()) {
            return;
        }
        if (resolver.getStage().getRandomizer().roll100(rate)) {
            CardStatusItem status = CardStatusItem.faint(skillUseInfo);
            ui.addCardStatus(attacker, defender, skill, status);
            defender.addStatus(status);
            ui.attackCard(attacker, defender, skill, impact);
        }
        OnDamagedResult onDamagedResult = resolver.applyDamage(attacker, defender, skill, impact);
        resolver.resolveDeathSkills(attacker, defender, skill, onDamagedResult);
    }
}
