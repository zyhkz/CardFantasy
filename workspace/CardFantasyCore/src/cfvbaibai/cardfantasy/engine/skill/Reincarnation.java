package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.CardInfo;
import cfvbaibai.cardfantasy.engine.CardStatusType;
import cfvbaibai.cardfantasy.engine.Grave;
import cfvbaibai.cardfantasy.engine.Hand;
import cfvbaibai.cardfantasy.engine.SkillResolver;

public final class Reincarnation {
    public static boolean apply(SkillResolver resolver, Skill cardSkill, CardInfo card) {
        if (!card.isDead()) {
            return false; // The card is unbending!
        }
        if (card.getStatus().containsStatus(CardStatusType.召唤)) {
            // Summoned card cannot be reincarnated.
            return false;
        }
        int rate = cardSkill.getImpact();
        GameUI ui = resolver.getStage().getUI();
        boolean bingo = resolver.getStage().getRandomizer().roll100(rate);
        ui.useSkill(card, card, cardSkill, bingo);
        if (bingo) {
            Grave grave = card.getOwner().getGrave();
            grave.removeCard(card);
            Hand hand = card.getOwner().getHand();
            if (hand.isFull()) {
                ui.cardToDeck(card.getOwner(), card);
                card.getOwner().getDeck().addCard(card);
            } else {
                ui.cardToHand(card.getOwner(), card);
                hand.addCard(card);
            }
            return true;
        }
        return false;
    }
}