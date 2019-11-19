package cfvbaibai.cardfantasy.engine;

import cfvbaibai.cardfantasy.NonSerializable;
import cfvbaibai.cardfantasy.data.*;

import java.util.ArrayList;
import java.util.List;

public class EquipmentInfo extends EntityInfo {
    @NonSerializable
    private Player owner;
    private Equipment equipment;
    @NonSerializable
    private int addHp;
    private List<SkillUseInfo> skillUseInfoList;

    public EquipmentInfo(Equipment equipment, Player owner) {
        this.equipment = equipment;
        this.owner = owner;
        this.skillUseInfoList = setSkillUserInfo(equipment);
    }

    private List<SkillUseInfo> setSkillUserInfo(Equipment equipment){
        List<SkillUseInfo> skillUseInfoList = new ArrayList<>();
        for(CardSkill cardSkill:equipment.getCardSkills()){
            SkillUseInfo skillUseInfo = new SkillUseInfo(this,cardSkill);
            skillUseInfoList.add(skillUseInfo);
        }
        return skillUseInfoList;
    }
    
    public Player getOwner() {
        return this.owner;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public int getAddHp() {
        return addHp;
    }

    public List<SkillUseInfo> getSkillUseInfoList() {
        return skillUseInfoList;
    }

    public void setSkillUseInfoList(List<SkillUseInfo> skillUseInfoList) {
        this.skillUseInfoList = skillUseInfoList;
    }

    public String getShortDesc() {
//        return String.format("【%s%d-%s%d-%s%d】", this.indenture.getName(), this.indenture.getLevel(), this.cardInfo.getName(),
//                this.cardInfo.getLevel(), this.cardInfo.getExtraSkill().getName(),this.cardInfo.getExtraSkill().getLevel());
        String skillName = "";
        if(skillUseInfoList.size()>0){
            skillName = skillUseInfoList.get(0).getSkill().getName();
        }
        return String.format("【%s%d-%s】", "装备-HP", this.addHp,skillName);
    }

    @Override
    public CardStatus getStatus() {
        return new CardStatus();
    }
}
