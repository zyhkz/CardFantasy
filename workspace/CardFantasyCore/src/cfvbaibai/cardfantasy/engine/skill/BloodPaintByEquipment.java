package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.List;

public final class BloodPaintByEquipment {
    public static void apply(Skill cardSkill, SkillResolver resolver, EquipmentInfo attacker, Player defender,Player attackerPlayer) throws HeroDieSignal {
        int damage = cardSkill.getImpact3();
        int count = cardSkill.getImpact();
        if(count == 1){
            count = 2;
        }else if(count == 2){
            count = 3;
        }else if(count == 3){
            count = 5;
        }
        List <CardInfo> victims = resolver.getStage().getRandomizer().pickRandom(
            defender.getField().toList(), count, true, null);
        GameUI ui = resolver.getStage().getUI();
        ui.useSkill(attacker, victims, cardSkill, true);
        for (CardInfo victim : victims) {
            OnAttackBlockingResult onAttackBlockingResult = resolver.resolveAttackBlockingSkills(attacker, victim, cardSkill, damage);
            if (!onAttackBlockingResult.isAttackable()) {
                continue;
            }
            int actualDamage = onAttackBlockingResult.getDamage(); 
            ui.attackCard(attacker, victim, cardSkill, actualDamage);
            OnDamagedResult onDamagedResult = resolver.applyDamage(attacker, victim, cardSkill, actualDamage);
            resolver.resolveDeathSkills(attacker, victim, cardSkill, onDamagedResult);
            resolver.attackHero(attackerPlayer, attackerPlayer, cardSkill, -onDamagedResult.actualDamage);
        }
    }
}
