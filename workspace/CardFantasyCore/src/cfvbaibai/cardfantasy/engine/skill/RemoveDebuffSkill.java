package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.Randomizer;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.data.SkillType;
import cfvbaibai.cardfantasy.engine.*;

import java.util.ArrayList;
import java.util.List;

public final class RemoveDebuffSkill {
    public static void apply(SkillUseInfo skillUseInfo, SkillResolver resolver, EntityInfo attacker,int number,Player defender)
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
                        || cardStatusItem.getType() == CardStatusType.庚子 || cardStatusItem.getType() == CardStatusType.傀儡) {
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
                } else if (cardStatusItem.getType() == CardStatusType.咒怨 || cardStatusItem.getType() == CardStatusType.咒恨 || cardStatusItem.getType() == CardStatusType.咒皿) {
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
                if(deleteItem.getType() == CardStatusType.咒怨) {
                   cardStatus.removeItem(deleteItem);
                    resolver.getStage().getUI().removeCardStatus(card, CardStatusType.咒怨);
                }else if(deleteItem.getType() == CardStatusType.咒恨){
                    cardStatus.removeItem(deleteItem);
                    resolver.getStage().getUI().removeCardStatus(card, CardStatusType.咒恨);
                } else if(deleteItem.getType() == CardStatusType.咒皿){
                    cardStatus.removeItem(deleteItem);
                    resolver.getStage().getUI().removeCardStatus(card, CardStatusType.咒皿);
                } else if(deleteItem.getType() == CardStatusType.虚化){
                    cardStatus.removeItem(deleteItem);
                    resolver.getStage().getUI().removeCardStatus(card, CardStatusType.虚化);
                } else{
                    cardStatus.removeItem(deleteItem);
                    if(skillUseInfo.getType() == SkillType.以德服人) {
                        RegressionSoul.apply(resolver, skillUseInfo, attacker, defender);
                    } else if(skillUseInfo.getType() == SkillType.圣灵之力) {
                        CardInfo cardInfo = (CardInfo) attacker;
                        Revive.apply(resolver, skillUseInfo.getAttachedUseInfo1(), cardInfo);
                    } else if(skillUseInfo.getType() == SkillType.平沙落雁){
                        Spread.apply(resolver, skillUseInfo, attacker, defender, 3, 6);
                    } else if(skillUseInfo.getType() == SkillType.清水甘露){
                        ManaErode.apply(skillUseInfo.getSkill(), resolver, attacker, defender, 1);
                    }
                }
            }
        }
    }
}
