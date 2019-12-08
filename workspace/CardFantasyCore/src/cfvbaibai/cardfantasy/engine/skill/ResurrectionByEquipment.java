package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.CardFantasyRuntimeException;
import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.data.Equipment;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.ArrayList;
import java.util.List;

public final class ResurrectionByEquipment {
    public static void apply(SkillResolver resolver, SkillUseInfo skillUseInfo, EquipmentInfo resurrector) {
        if (resurrector == null) {
            throw new CardFantasyRuntimeException("resurrector should not be null");
        }
        Skill skill = skillUseInfo.getSkill();
        // Grave is a stack, find the last-in card and revive it.
        int resurrectionCount = skill.getImpact();
        Player player = resurrector.getOwner();
        List<CardInfo> exclusions = null;
        List<CardInfo> deadCards = player.getGrave().toList();
        if (skill.isDeathSkill()) {
            exclusions = new ArrayList<CardInfo>();
            if(resurrectionCount==deadCards.size())
            {
                resurrectionCount=resurrectionCount-1;
            }
        }
        GameUI ui = resolver.getStage().getUI();
        if (SoulSeal.soulSealed(resolver, resurrector)) {
            return;
        }
        for (int i=0;i<=deadCards.size()-1&&i<=resurrectionCount-1;i++) {
            ui.useSkill(resurrector, deadCards.get(i), skill, true);
            ui.cardToDeck(player, deadCards.get(i));
            player.getGrave().removeCard(deadCards.get(i));
            if (player.getDeck().size() > 0) {
       //     int position = Randomizer.getRandomizer().next(0, player.getDeck().size());
         // 回魂是有顺序的。
                int position = 0;
                player.getDeck().insertCardToPosition(deadCards.get(i), position);
            } else {
                player.getDeck().addCard(deadCards.get(i));
            }
        }
        skillUseInfo.setIsUsed(true);
    }

    public static void reset( SkillUseInfo skillUseInfo) throws HeroDieSignal {
        skillUseInfo.setIsUsed(false);
    }
}
