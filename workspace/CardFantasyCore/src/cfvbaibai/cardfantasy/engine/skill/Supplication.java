package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.CardFantasyRuntimeException;
import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.Randomizer;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2017/4/10.
 */
public final class Supplication {
    public static  void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, CardInfo card,Player defender) throws HeroDieSignal {
        if (card == null) {
            throw new CardFantasyRuntimeException("card should not be null");
        }
        Skill skill = skillUseInfo.getSkill();
        // Grave is a stack, find the last-in card and revive it.
        int count = skill.getImpact();
        Player player = card.getOwner();
        List<CardInfo> deck =  player.getDeck().toList();
        Hand hand = card.getOwner().getHand();
        if(deck.size()<=0 || hand.isFull()){
            return;
        }
        List<CardInfo> addHand = new ArrayList<CardInfo>();
        addHand = Randomizer.getRandomizer().pickRandom(deck, count, true,null);
        GameUI ui = resolver.getStage().getUI();
        ui.useSkill(card, card, skill, true);
        for(CardInfo addcard : addHand)
        {
            if(hand.isFull())
            {
                break;
            }
            ui.cardToHand(player, addcard);
            player.getDeck().removeCard(addcard);
            hand.addCard(addcard);
            resolver.resolvePrecastSkills(addcard,defender,false);
        }
        HellPrison.apply(resolver,defender,player);
    }
}
