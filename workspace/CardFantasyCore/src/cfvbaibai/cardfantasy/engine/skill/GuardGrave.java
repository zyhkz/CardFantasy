package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.data.SkillType;
import cfvbaibai.cardfantasy.engine.*;

public final class GuardGrave {
    public static boolean apply(SkillResolver resolver, CardInfo card) {
        for (SkillUseInfo cardSkillUserInfo : card.getAllUsableSkillsIgnoreSilence()) {
            if (cardSkillUserInfo.getType() == SkillType.守墓者) {
                resolver.getStage().getUI().useSkill(card,cardSkillUserInfo.getSkill(),true);
                return true;
            }
        }
        return false;
    }

    public static void reset(SkillUseInfo skillUseInfo) throws HeroDieSignal {
        int impact = skillUseInfo.getSkill().getImpact();
        skillUseInfo.setSkillNumber(impact);
    }

    public static void explode(SkillResolver resolver,CardInfo card,SkillUseInfo skillUseInfo) throws HeroDieSignal {
        int number = skillUseInfo.getSkillNumber();
        if(number <= 1){
            resolver.summonCard(card.getOwner(), card, card, false, skillUseInfo.getSkill(),0);//司命可以发动降临技能
            CardStatusItem item = CardStatusItem.weak(skillUseInfo);
            resolver.getStage().getUI().addCardStatus(card, card, skillUseInfo.getSkill(), item);
            card.addStatus(item);
            return;
        }
        skillUseInfo.setSkillNumber(number-1);
    }
}
