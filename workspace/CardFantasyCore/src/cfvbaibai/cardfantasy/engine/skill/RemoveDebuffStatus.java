package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.CardFantasyRuntimeException;
import cfvbaibai.cardfantasy.Randomizer;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.ArrayList;
import java.util.List;

public final class RemoveDebuffStatus {
    public static void apply(SkillUseInfo skillUseInfo, SkillResolver resolver, EntityInfo attacker,int number)
            throws HeroDieSignal {
        CardStatus status = attacker.getStatus();
        if (status.containsStatus(CardStatusType.迷惑) ||
                status.containsStatus(CardStatusType.冰冻) ||
                status.containsStatus(CardStatusType.锁定) ||
                status.containsStatus(CardStatusType.石化) ||
                status.containsStatus(CardStatusType.复活) ||
                status.containsStatus(CardStatusType.晕眩)) {
            return ;
        }
        List<CardInfo> cards = null;
        Skill skill = skillUseInfo.getSkill();
        int impact = skill.getImpact();
//        cards = attacker.getOwner().getField().getAliveCards();
        StageInfo stage = resolver.getStage();
        Randomizer random = stage.getRandomizer();

        cards = random.pickRandom(attacker.getOwner().getField().toList(), number, true, null);
        resolver.getStage().getUI().useSkill(attacker, cards, skillUseInfo.getSkill(), true);
        for (CardInfo card : cards) {
            CardStatus cardStatus = card.getStatus();
            List<CardStatusItem> deleteItems = new ArrayList<>();
            List<CardStatusItem> cardStatusItems = cardStatus.getAllItems();
            for (CardStatusItem cardStatusItem : cardStatusItems) {
                if (cardStatusItem.getType() == CardStatusType.死印 || cardStatusItem.getType() == CardStatusType.魔印
                        || cardStatusItem.getType() == CardStatusType.死咒 || cardStatusItem.getType() == CardStatusType.献祭
                        || cardStatusItem.getType() == CardStatusType.炼成 || cardStatusItem.getType() == CardStatusType.魂殇
                        || cardStatusItem.getType() == CardStatusType.黄天 || cardStatusItem.getType() == CardStatusType.祭奠
                        || cardStatusItem.getType() == CardStatusType.离魂 || cardStatusItem.getType() == CardStatusType.蛇影
                        || cardStatusItem.getType() == CardStatusType.链接 || cardStatusItem.getType() == CardStatusType.石化
                        || cardStatusItem.getType() == CardStatusType.远古 || cardStatusItem.getType() == CardStatusType.海啸
                        || cardStatusItem.getType() == CardStatusType.扩散 || cardStatusItem.getType() == CardStatusType.子嗣
                        || cardStatusItem.getType() == CardStatusType.庚子) {
                    deleteItems.add(cardStatusItem);
                } else if (cardStatusItem.getType() == CardStatusType.虚化) {
                    SkillUseInfo attackSkillUseInfo = cardStatusItem.getCause();
                    List<CardStatusItem> effectItems = cardStatus.getStatusOf(CardStatusType.麻痹);
                    for(CardStatusItem effectItem:effectItems)
                    {
                        if(effectItem.getCause() == attackSkillUseInfo)
                        {
                            deleteItems.add(effectItem);
                            break;
                        }
                    }
                    deleteItems.add(cardStatusItem);
                } else if (cardStatusItem.getType() == CardStatusType.咒怨) {
                    SkillUseInfo attackSkillUseInfo = cardStatusItem.getCause();
                    List<CardStatusItem> effectItems = cardStatus.getStatusOf(CardStatusType.沉默);
                    for(CardStatusItem effectItem:effectItems)
                    {
                        if(effectItem.getCause() == attackSkillUseInfo)
                        {
                            deleteItems.add(effectItem);
                            break;
                        }
                    }
                    deleteItems.add(cardStatusItem);
                }
            }
            for(CardStatusItem deleteItem:deleteItems)
            {
                if(deleteItem.getType() == CardStatusType.咒怨 || deleteItem.getType() == CardStatusType.虚化)
                {
                   cardStatus.removeItem(deleteItem);
                    resolver.getStage().getUI().removeCardStatus(card, CardStatusType.咒怨);
                } else if(deleteItem.getType() == CardStatusType.石化){
                    Petrifaction.reset(card,0);
                    cardStatus.removeItem(deleteItem);
                    resolver.getStage().getUI().removeCardStatus(card, CardStatusType.石化);
                } else{
                    cardStatus.removeItem(deleteItem);
                    resolver.attackHero(attacker,attacker.getOwner(),skill,-impact);
                }
            }
        }
    }
}
