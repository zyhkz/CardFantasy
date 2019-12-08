package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.List;

public final class UnderworldCallByEquipment {
    public static void apply(SkillResolver resolver, Skill cardSkill, EntityInfo attacker, Player defenderHero,
                              int victimCount) throws HeroDieSignal {
        GameUI ui = resolver.getStage().getUI();
        int threshold=cardSkill.getImpact();
        int damageInit=cardSkill.getImpact2();
        List<CardInfo> victims = resolver.getStage().getRandomizer().pickRandom(
                defenderHero.getField().toList(), victimCount, true, null);
        ui.useSkill(attacker, victims, cardSkill, true);
        for (CardInfo victim : victims) {
            int damage = damageInit;
            OnAttackBlockingResult result = resolver.resolveAttackBlockingSkills(attacker, victim, cardSkill, damage);
            if (!result.isAttackable()) {
                continue;
            }
            else if(victim.isBoss())
            {
                continue;
            }
            else{
                int magicEchoSkillResult = resolver.resolveMagicEchoSkill(attacker, victim, cardSkill);
                if (magicEchoSkillResult==1||magicEchoSkillResult==2) {
                    if (magicEchoSkillResult == 1) {
                        continue;
                    }

                }
                if (victim.getHP() >= victim.getMaxHP() * threshold / 100) {
                    damage = result.getDamage();
                    ui.attackCard(attacker, victim, cardSkill, damage);
                    resolver.resolveDeathSkills(attacker, victim, cardSkill,  resolver.applyDamage(attacker, victim, cardSkill,damage));
                }
                else{
                    damage = victim.getMaxHP();
                    ui.attackCard(attacker, victim, cardSkill, damage);
                    resolver.resolveDeathSkills(attacker, victim, cardSkill,  resolver.applyDamage(attacker, victim, cardSkill,damage));
                }
            }

        }
    }
}
