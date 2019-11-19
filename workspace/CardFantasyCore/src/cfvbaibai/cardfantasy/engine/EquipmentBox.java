package cfvbaibai.cardfantasy.engine;

import cfvbaibai.cardfantasy.NonSerializable;
import cfvbaibai.cardfantasy.data.Equipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EquipmentBox {
    private List<EquipmentInfo> equipmentInfos;
    private int addHps;
    @NonSerializable
    private Player owner;

    public EquipmentBox(Player owner, Collection<Equipment> Equipments) {
        this.owner = owner;
        this.equipmentInfos = new ArrayList<EquipmentInfo>();
        this.addHps = 0;
        for (Equipment equipment : Equipments) {
            this.equipmentInfos.add(new EquipmentInfo(equipment, owner));
            this.addHps = this.addHps + equipment.getAddHp();
        }
    }
    
    public Player getOwner() {
        return this.owner;
    }

    public EquipmentInfo addEquipmentInfo(EquipmentInfo equipmentInfo) {
        this.equipmentInfos.add(equipmentInfo);
        return equipmentInfo;
    }

    public List<EquipmentInfo> getEquipmentInfos() {
        return new ArrayList<EquipmentInfo>(this.equipmentInfos);
    }

    public int getAddHps() {
        return addHps;
    }

    public List<SkillUseInfo> getSkillUseInfos() {
        List<SkillUseInfo> skillUseInfoList = new ArrayList<>();
        for(EquipmentInfo equipmentInfo:this.equipmentInfos){
            skillUseInfoList.addAll(equipmentInfo.getSkillUseInfoList());
        }
        return  skillUseInfoList;
    }
}
