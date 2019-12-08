package cfvbaibai.cardfantasy.engine.skill;

import cfvbaibai.cardfantasy.GameUI;
import cfvbaibai.cardfantasy.Randomizer;
import cfvbaibai.cardfantasy.data.Skill;
import cfvbaibai.cardfantasy.engine.*;

import java.util.ArrayList;
import java.util.List;

public final class HealByEquipment {
    public static void apply(Skill cardSkill, SkillResolver resolver, EntityInfo healer,int victimCount) throws HeroDieSignal {
        if (healer == null) {
            return;
        }
        StageInfo stage = resolver.getStage();
        Randomizer random = stage.getRandomizer();
        GameUI ui = stage.getUI();
        Field field = healer.getOwner().getField();
        int healCount = cardSkill.getImpact3();
        List<CardInfo> healeeCandidates = random.pickRandom(field.toList(), victimCount, true, null);
        resolver.getStage().getUI().useSkill(healer, healeeCandidates, cardSkill, true);
        for (CardInfo healee : healeeCandidates) {
            int healHP = healCount;
            if (healHP + healee.getHP() > healee.getMaxHP()) {
                healHP = healee.getMaxHP() - healee.getHP();
            }

            if (healHP == 0) {
                continue;
            }
            OnAttackBlockingResult result = resolver.resolveHealBlockingSkills(healer, healee, cardSkill);
            if (!result.isAttackable()) {
                continue;
            }
            resolver.getStage().getUI().healCard(healer, healee, cardSkill, healHP);
            resolver.applyDamage(healer, healee, cardSkill, -healHP);
        }
    }
}
