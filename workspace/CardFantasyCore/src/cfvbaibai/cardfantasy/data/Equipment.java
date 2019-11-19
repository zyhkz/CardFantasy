package cfvbaibai.cardfantasy.data;

import java.util.ArrayList;
import java.util.List;

public class Equipment {
    private int addHp;
    private List<CardSkill> cardSkills;
    public Equipment(int addHp) {
        this.addHp = addHp;
        this.cardSkills=new ArrayList<>();//new add
    }

    public int getAddHp() {
        return addHp;
    }

    public List<CardSkill> getCardSkills() {
        return cardSkills;
    }

    public void insertSkill(CardSkill cardSkill){
        this.cardSkills.add(cardSkill);
    }
}
