package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.CardFantasyRuntimeException;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

public final class SelfDoubleBuff {
    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, CardInfo card,
                             int attackNumber, int attackMaxNumber) {
        if (card == null) {
            throw new CardFantasyRuntimeException("card cannot be null");
        }
        Skill skill = skillUseInfo.getSkill();
        int impact = skill.getImpact();
        int impact2 = skill.getImpact();
        int number = impact;
        if(skillUseInfo.getSkillNumber()==-1) {
            skillUseInfo.setSkillNumber(1);
        }else {
            int count = skillUseInfo.getSkillNumber()+1;
            if(number*count >=impact2){
                number = impact2;
            }else{
                number = number*count;
            }
            if(attackNumber*count >=attackMaxNumber){
                attackNumber = attackMaxNumber;
                count--;
            }else{
                attackNumber = attackNumber*count;
            }
            skillUseInfo.setSkillNumber(count);
        }
        if (card.getEffectsCausedBy(skillUseInfo).isEmpty()) {
            resolver.getStage().getUI().useSkill(card, skill, true);
            resolver.getStage().getUI().adjustHP(card, card, number, skill);
            resolver.getStage().getUI().adjustAT(card, card, number, skill);
            card.addEffect(new SkillEffect(SkillEffectType.MAXHP_CHANGE, skillUseInfo, number, false));
            card.addEffect(new SkillEffect(SkillEffectType.ATTACK_CHANGE, skillUseInfo, attackMaxNumber, false));
        }

    }
}
