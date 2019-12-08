package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.ArrayList;
import java.util.List;

public final class EnergyDrainByEquipment {
    public static int apply(SkillUseInfo skillUseInfo, SkillResolver resolver, CardInfo attacker, EntityInfo defender,int damage) throws HeroDieSignal {
        Skill skill = skillUseInfo.getSkill();
        int adjAT = attacker.getLevel1AT() * skill.getImpact3() / 100;

        List<CardInfo> victims = new ArrayList<CardInfo>();
        victims.add(attacker);
        int totalAttackWeakened = Weaken.weakenCardOfEnergyDrain(resolver, skillUseInfo, adjAT, defender, victims);

        damage = -totalAttackWeakened;
        return damage;
    }
}
