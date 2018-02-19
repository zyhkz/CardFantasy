package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.CardFantasyRuntimeException;
import cfvbaibai.cardfantasy.Randomizer;
import cfvbaibai.cardfantasy.data.CardSkill;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.SkillUseInfo;
import cfvbaibai.cardfantasy.engine.SkillResolver;
import cfvbaibai.cardfantasy.engine.CardInfo;

import java.util.ArrayList;
import java.util.List;
public class HandCardAddSkillNormal {
    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, CardInfo card, Skill addSkill,int number) {
        if (card == null || card.isDead()) {
            return;
        }
        Skill skill = skillUseInfo.getSkill();
        CardSkill cardSkill = new CardSkill(addSkill.getType(), addSkill.getLevel(), 0, false, false, false, false);
        resolver.getStage().getUI().useSkill(card, skill, true);
        List<CardInfo> allHandCards = card.getOwner().getHand().toList();
        List<CardInfo> addCard=new ArrayList<CardInfo>();
        List<CardInfo> revivableCards = new ArrayList<CardInfo>();
        SkillUseInfo thisSkillUserInfo= null;
        boolean flag = true;
        for (CardInfo handCard : allHandCards) {
            for(SkillUseInfo skillInfo:handCard.getSkillUserInfos())
            {
                if(skillInfo.getGiveSkill()==2)
                {
                    flag=false;
                    break;
                }
            }
            if(!flag)
            {
                flag =true;
                continue;
            }
            if (handCard != null && !handCard.containsAllSkill(addSkill.getType())) {
                revivableCards.add(handCard);
            }
        }
        if (revivableCards.isEmpty()) {
            return;
        }
        addCard = Randomizer.getRandomizer().pickRandom(
                revivableCards, number, true, null);


        for (CardInfo once : addCard) {
            if(once.containsAllSkill(addSkill.getType()))
            {
                continue;
            }
            thisSkillUserInfo = new SkillUseInfo(once,cardSkill);
            thisSkillUserInfo.setGiveSkill(2);
            once.addSkill(thisSkillUserInfo);
        }
    }
}
