package cfvbaibai.cardfantasy.engine.skill;

import java.util.List;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.CardInfo;
import cfvbaibai.cardfantasy.engine.CardStatusItem;
import cfvbaibai.cardfantasy.engine.EntityInfo;
import cfvbaibai.cardfantasy.engine.HeroDieSignal;
import cfvbaibai.cardfantasy.engine.OnAttackBlockingResult;
import cfvbaibai.cardfantasy.engine.OnDamagedResult;
import cfvbaibai.cardfantasy.engine.Player;
import cfvbaibai.cardfantasy.engine.SkillResolver;
import cfvbaibai.cardfantasy.engine.SkillUseInfo;

public final class IceMagic {
    public static void apply(SkillUseInfo skillUseInfo, SkillResolver resolver, EntityInfo attacker, Player defender,
            int victimCount, int rate, int extraDamage) throws HeroDieSignal {
        Skill skill = skillUseInfo.getSkill();
        List<CardInfo> victims = resolver.getStage().getRandomizer().pickRandom(
            defender.getField().toList(), victimCount, true, null);
        GameUI ui = resolver.getStage().getUI();

        for (CardInfo victim : victims) {
            if(victim ==null)
            {
                continue;
            }
            int damage = skill.getImpact() + extraDamage;
            ui.useSkill(attacker, victim, skill, true);
            OnAttackBlockingResult onAttackBlockingResult = resolver.resolveAttackBlockingSkills(attacker, victim, skill, damage);
            if (!onAttackBlockingResult.isAttackable()) {
                continue;
            }
            if (resolver.getStage().getRandomizer().roll100(rate)) {
                CardStatusItem status = CardStatusItem.frozen(skillUseInfo);
                if (!resolver.resolveBlockStatusSkills(attacker, victim, skillUseInfo, status).isBlocked()) {
                    ui.addCardStatus(attacker, victim, skill, status);
                    victim.addStatus(status);
                }
            }
            damage = onAttackBlockingResult.getDamage();
            ui.attackCard(attacker, victim, skill, damage);
            OnDamagedResult onDamagedResult = resolver.applyDamage(attacker, victim, skill, damage);
            if (attacker instanceof CardInfo) {
                resolver.resolveCounterAttackSkills((CardInfo)attacker, victim, skill, onAttackBlockingResult, null);
            }
            resolver.resolveDeathSkills(attacker, victim, skill, onDamagedResult);
        }
    }
}
