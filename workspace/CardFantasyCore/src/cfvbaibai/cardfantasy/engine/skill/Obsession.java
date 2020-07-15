package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.Randomizer;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;
import cfvbaibai.cardfantasy.game.DeckBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Obsession {
    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, CardInfo attackCard, int victimCount,int effectNumber) throws HeroDieSignal {

        StageInfo stage = resolver.getStage();
        Randomizer random = stage.getRandomizer();
        List<CardInfo> victims = random.pickRandom(attackCard.getOwner().getField().toList(), victimCount, true, null);
        if (victims.size() == 0) {
            return;
        }
        GameUI ui = resolver.getStage().getUI();
        Skill skill = skillUseInfo.getSkill();
        ui.useSkill(attackCard, victims, skill, true);
        CardStatusItem statusItem = CardStatusItem.obsession(skillUseInfo);
        statusItem.setEffectNumber(effectNumber);
        for (CardInfo victim : victims) {
            if(effectNumber>0) {
                continue;
            }
            ui.addCardStatus(attackCard, victim, skill, statusItem);
            victim.addStatus(statusItem);
        }
    }

    public static boolean explode(SkillResolver resolver, CardInfo defender) {
        boolean unbendingResult = false;
        List<CardStatusItem> cardStatusItemList = defender.getStatus().getAllItems();
        for (CardStatusItem item : cardStatusItemList) {
            if (item.getType() == CardStatusType.执念) {
                GameUI ui = resolver.getStage().getUI();
                Skill skill = item.getCause().getSkill();
                ui.useSkill(defender, skill, true);
                if (defender.getHP() == 0) {
                    ui.adjustHP(defender, defender, 1, skill);
                    defender.setBasicHP(1);
                }
                CardStatusItem statusItem = CardStatusItem.unbending(item.getCause());
                ui.addCardStatus(defender ,defender, skill, statusItem);
                defender.addStatus(statusItem);
                unbendingResult = true;
                break;
            }
        }
        return unbendingResult;
    }

    public static void remove(SkillResolver resolver,CardInfo attacker){
        CardStatus cardStatus = attacker.getStatus();
        List<CardStatusItem> deleteItems = new ArrayList<>();
        List<CardStatusItem> cardStatusItems = cardStatus.getAllItems();
        for (CardStatusItem cardStatusItem : cardStatusItems) {
            if(cardStatusItem.getType() == CardStatusType.执念){
                deleteItems.add(cardStatusItem);
                break;
            }
        }
        for(CardStatusItem deleteItem:deleteItems) {
            cardStatus.removeItem(deleteItem);
            resolver.getStage().getUI().removeCardStatus(attacker, CardStatusType.执念);
        }
    }
}
