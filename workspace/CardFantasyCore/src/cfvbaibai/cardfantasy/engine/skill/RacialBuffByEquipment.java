package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.CardFantasyRuntimeException;
import cfvbaibai.cardfantasy.data.Race;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

public final class RacialBuffByEquipment {
    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, EntityInfo entityInfo, Race race,
            SkillEffectType effectType) {
        Skill skill = skillUseInfo.getSkill();
        int impact = skill.getImpact3();
        Field field = entityInfo.getOwner().getField();
        for (CardInfo ally : field.getAliveCards()) {
            if (ally.getEffectsCausedBy(skillUseInfo).isEmpty()) {
                resolver.getStage().getUI().useSkill(entityInfo, skill, true);
                if (effectType == SkillEffectType.ATTACK_CHANGE) {
                    resolver.getStage().getUI().adjustAT(entityInfo, ally, impact, skill);
                } else if (effectType == SkillEffectType.MAXHP_CHANGE) {
                    resolver.getStage().getUI().adjustHP(entityInfo, ally, impact, skill);
                } else {
                    throw new CardFantasyRuntimeException("Invalid effect type: " + effectType.name());
                }
                ally.addEffect(new SkillEffect(effectType, skillUseInfo, impact, false));
            }
        }
    }
}
