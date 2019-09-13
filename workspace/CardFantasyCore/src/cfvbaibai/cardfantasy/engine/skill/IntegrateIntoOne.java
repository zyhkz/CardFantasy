package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.data.CardSkill;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.data.SkillType;
import cfvbaibai.cardfantasy.engine.*;
import cfvbaibai.cardfantasy.game.DeckBuilder;

import java.util.List;

public final class IntegrateIntoOne {
    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, CardInfo resonantCard, String deleteCardName) throws HeroDieSignal{
        if(resonantCard ==null || resonantCard.isDead())
        {
            return;
        }
        Player player =  resonantCard.getOwner();
        List<CardInfo> livingCards = null;
        livingCards = player.getField().getAliveCards();
        for (CardInfo fieldCard : livingCards) {
            if(fieldCard.getName().equals(deleteCardName)) {
                player.getField().expelCard(fieldCard.getPosition());
                player.getField().expelCard(resonantCard.getPosition());

                resolver.resolveLeaveSkills(resonantCard);
                resolver.resolveLeaveSkills(fieldCard);

                resolver.getStage().getUI().useSkill(resonantCard, skillUseInfo.getSkill(), true);
                resolver.getStage().getUI().cardDead(resonantCard);
                resolver.getStage().getUI().cardDead(fieldCard);
                resolver.summonCard(resonantCard.getOwner(), resonantCard, null, false, skillUseInfo.getSkill(),0);
                break;
            }
        }

    }
}
