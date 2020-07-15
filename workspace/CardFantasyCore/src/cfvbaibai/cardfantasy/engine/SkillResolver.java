package cfvbaibai.cardfantasy.engine;

import cfvbaibai.cardfantasy.CardFantasyRuntimeException;
import cfvbaibai.cardfantasy.data.*;
import cfvbaibai.cardfantasy.engine.skill.*;

import java.util.ArrayList;
import java.util.List;


public class SkillResolver {
    private StageInfo stage;

    public SkillResolver(StageInfo stage) {
        this.stage = stage;
    }

    public StageInfo getStage() {
        return this.stage;
    }

    //添加阶段为方便技能处理
    public void setStagePhase(Phase phase) {
        this.stage.setPhase(phase);
    }

    public boolean isPhysicalAttackSkill(Skill skill) {
        return skill == null || skill.getType().containsTag(SkillTag.物理攻击);
    }

    public boolean isMagicalSkill(Skill skill) {
        return skill != null && skill.getType().containsTag(SkillTag.魔法);
    }

    public void removeStatus(CardInfo card, CardStatusType statusType) {
        if (card == null) {
            return;
        }
        if (card.removeStatus(statusType)) {
            this.stage.getUI().removeCardStatus(card, statusType);
        }
    }

    public List<CardInfo> getAdjacentCards(Field field, int position) {
        List<CardInfo> cards = this.getCardsOnSides(field, position);
        CardInfo card = field.getCard(position);
        if (card != null) {
            cards.add(card);
        }
        return cards;
    }

    public List<CardInfo> getCardsOnSides(Field field, int position) {
        List<CardInfo> cards = new ArrayList<CardInfo>();
        if (position > 0) {
            CardInfo leftSide = field.getCard(position - 1);
            if (leftSide != null) {
                cards.add(leftSide);
            }
        }
        CardInfo rightSide = field.getCard(position + 1);
        if (rightSide != null) {
            cards.add(rightSide);
        }
        return cards;
    }

    public List<CardInfo> getFrontCards(Field field, int position) {
        List<CardInfo> cards = this.getCardsOnFront(field, position);
        //前置卡不包含本身
//        CardInfo card = field.getCard(position);
//        if (card != null) {
//            cards.add(card);
//        }
        return cards;
    }

    public List<CardInfo> getCardsOnFront(Field field, int position) {
        List<CardInfo> cards = new ArrayList<CardInfo>();
        CardInfo frontCard = null;
        if (position > 0) {
            for (int i = 0; i < position; i++) {
                frontCard = field.getCard(i);
                if (frontCard != null) {
                    cards.add(frontCard);
                }
            }
        }
        return cards;
    }

    public void resolveEquipmentSkills(Player attacker, Player defender) throws HeroDieSignal {
        List<EquipmentInfo> equipmentInfos = attacker.getEquipmentBox().getEquipmentInfos();
        for (EquipmentInfo equipmentInfo : equipmentInfos) {
            for (SkillUseInfo equipmentSkillUserInfo : equipmentInfo.getSkillUseInfoList()) {
                if (equipmentSkillUserInfo.getType() == SkillType.装备噬魂) {
                    BloodPaintByEquipment.apply(equipmentSkillUserInfo.getSkill(), this, equipmentInfo, defender, attacker);
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备裂魂 || equipmentSkillUserInfo.getType() == SkillType.装备死咒
                        || equipmentSkillUserInfo.getType() == SkillType.装备箭雨 || equipmentSkillUserInfo.getType() == SkillType.装备威压
                        || equipmentSkillUserInfo.getType() == SkillType.装备星辉 || equipmentSkillUserInfo.getType() == SkillType.装备绝杀
                        || equipmentSkillUserInfo.getType() == SkillType.装备震击 || equipmentSkillUserInfo.getType() == SkillType.装备饮魂) {
                    equipmentSkillUserInfo.setSkillNumber(0);
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备操魂) {
                    ResurrectionByEquipment.reset(equipmentSkillUserInfo);
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备祈祷) {
                    PrayByEquipment.apply(equipmentSkillUserInfo.getSkill(), this, equipmentInfo);
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备强攻 || equipmentSkillUserInfo.getType() == SkillType.装备鼓舞) {
                    int impact = equipmentSkillUserInfo.getSkill().getImpact();
                    if (impact == 1) {
                        FiledAddSkillByEquipment.apply(this, equipmentSkillUserInfo, equipmentInfo, attacker, equipmentSkillUserInfo.getAttachedUseInfo1().getAttachedUseInfo1().getSkill(), 3);
                    } else if (impact == 2) {
                        FiledAddSkillByEquipment.apply(this, equipmentSkillUserInfo, equipmentInfo, attacker, equipmentSkillUserInfo.getAttachedUseInfo1().getAttachedUseInfo2().getSkill(), 5);
                    } else if (impact == 3) {
                        FiledAddSkillByEquipment.apply(this, equipmentSkillUserInfo, equipmentInfo, attacker, equipmentSkillUserInfo.getAttachedUseInfo2().getSkill(), -1);
                    }
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备呓语) {
                    int impact = equipmentSkillUserInfo.getSkill().getImpact();
                    if (impact == 1) {
                        ConfusionByEquipment.apply(equipmentSkillUserInfo, this, equipmentInfo, defender, 3);
                    } else if (impact == 2) {
                        ConfusionByEquipment.apply(equipmentSkillUserInfo, this, equipmentInfo, defender, 5);
                    } else if (impact == 3) {
                        ConfusionByEquipment.apply(equipmentSkillUserInfo, this, equipmentInfo, defender, -1);
                    }
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备烙印) {
                    int impact2 = equipmentSkillUserInfo.getSkill().getImpact2();
                    if (impact2 == 1) {
                        MagicMarkByEquipment.apply(this, equipmentSkillUserInfo.getAttachedUseInfo1().getAttachedUseInfo1(), equipmentInfo, defender, 3);
                    } else if (impact2 == 2) {
                        MagicMarkByEquipment.apply(this, equipmentSkillUserInfo.getAttachedUseInfo1(), equipmentInfo, defender, 5);
                    } else if (impact2 == 3) {
                        MagicMarkByEquipment.apply(this, equipmentSkillUserInfo, equipmentInfo, defender, -1);
                    }
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备缚魂) {
                    int impact = equipmentSkillUserInfo.getSkill().getImpact();
                    if (impact == 1) {
                        SoulChains.apply(this, equipmentSkillUserInfo, equipmentInfo, defender, 1, 2);
                    } else if (impact == 2) {
                        SoulChains.apply(this, equipmentSkillUserInfo, equipmentInfo, defender, 1, 4);
                    } else if (impact == 3) {
                        SoulChains.apply(this, equipmentSkillUserInfo, equipmentInfo, defender, 2, 6);
                    }
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备纷争) {
                    int impact = equipmentSkillUserInfo.getSkill().getImpact();
                    if (impact == 1) {
                        Insane.apply(equipmentSkillUserInfo, this, equipmentInfo, defender, 1, 100);
                    } else if (impact == 2) {
                        Insane.apply(equipmentSkillUserInfo, this, equipmentInfo, defender, 2, 140);
                    } else if (impact == 3) {
                        Insane.apply(equipmentSkillUserInfo, this, equipmentInfo, defender, 3, 200);
                    }
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备祝福) {
                    RacialBuffByEquipment.apply(this, equipmentSkillUserInfo, equipmentInfo, null, SkillEffectType.ATTACK_CHANGE);
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备护佑) {
                    RacialBuffByEquipment.apply(this, equipmentSkillUserInfo, equipmentInfo, null, SkillEffectType.MAXHP_CHANGE);
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备月华) {
                    int impact = equipmentSkillUserInfo.getSkill().getImpact();
                    if (impact == 1) {
                        HealByEquipment.apply(equipmentSkillUserInfo.getSkill(), this, attacker, 1);
                    } else if (impact == 2) {
                        HealByEquipment.apply(equipmentSkillUserInfo.getSkill(), this, attacker, 3);
                    } else if (impact == 3) {
                        HealByEquipment.apply(equipmentSkillUserInfo.getSkill(), this, attacker, -1);
                    }
                }
            }
        }
    }

    public void resolvePreAttackSkills(Player attacker, Player defender) throws HeroDieSignal {
        List<CardInfo> cards = attacker.getField().getAliveCards();
        for (CardInfo card : cards) {
            for (SkillUseInfo skillUseInfo : card.getUsableNormalSkills()) {
                if (!FailureSkillUseInfoList.explode(this, card, defender)) {
                    if (skillUseInfo.getType() == SkillType.神性祈求 || skillUseInfo.getType() == SkillType.神性祈祷 || skillUseInfo.getType() == SkillType.阳式阴式
                            || skillUseInfo.getType() == SkillType.荼蘼盛放 || skillUseInfo.getType() == SkillType.灵魂净化) {
                        Purify.apply(skillUseInfo, this, card, -1);
                    } else if (skillUseInfo.getType() == SkillType.净化领域) {
                        Purify.apply(skillUseInfo, this, card, -1);
                    } else if (skillUseInfo.getType() == SkillType.驱魔) {
                        RemoveDebuffStatus.apply(skillUseInfo, this, card, -1);
                    } else if (skillUseInfo.getType() == SkillType.妖力驱散) {
                        RemoveDebuffStatus.apply(skillUseInfo, this, card, 4);
                    } else if (skillUseInfo.getType() == SkillType.以德服人 || skillUseInfo.getType() == SkillType.平沙落雁) {
                        RemoveDebuffSkill.apply(skillUseInfo, this, card, -1, defender);
                    } else if (skillUseInfo.getType() == SkillType.圣灵之力) {
                        RemoveDebuffSkill.apply(skillUseInfo, this, card, 3, defender);
                    } else if (skillUseInfo.getType() == SkillType.净魂领域 || skillUseInfo.getType() == SkillType.星座能量清醒) {
                        Purify.apply(skillUseInfo, this, card, -2);
                    } else if (skillUseInfo.getType() == SkillType.幻音) {
                        Purify.apply(skillUseInfo.getAttachedUseInfo2(), this, card, -2);
                    } else if (skillUseInfo.getType() == SkillType.西凉铁骑 || skillUseInfo.getType() == SkillType.零度领域 || skillUseInfo.getType() == SkillType.冰肌雪骨
                            || skillUseInfo.getType() == SkillType.共生 || skillUseInfo.getType() == SkillType.名将之风 || skillUseInfo.getType() == SkillType.圣剑
                            || skillUseInfo.getType() == SkillType.仁厚 || skillUseInfo.getType() == SkillType.御魔长袍 || skillUseInfo.getType() == SkillType.弑神之剑
                            || skillUseInfo.getType() == SkillType.星阵) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.剧毒领域) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo2().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.袈裟斩) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.厄运枪) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.顽石契约) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.碎冰成雪) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.致命打击) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.爱心料理) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.仁德之君) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.质能展开) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.神圣光环) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.链式防守 || skillUseInfo.getType() == SkillType.潘帕斯雄鹰) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.剑舞) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.陨星) {
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.月神的恩泽 || skillUseInfo.getType() == SkillType.临风傲雪 || skillUseInfo.getType() == SkillType.淬毒之刃) {
                        MoonBounty.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.水榭桃盈) {
                        AddSidesSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.铁舞天衣) {
                        AddSidesSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.不死战神 || skillUseInfo.getType() == SkillType.页游不凋花) {
                        AddSkillOneself.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.荆棘守护) {
                        AddSidesSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.隐遁之术 || skillUseInfo.getType() == SkillType.神兵召唤光环
                            || skillUseInfo.getType() == SkillType.天降神兵 || skillUseInfo.getType() == SkillType.聚能立场
                            || skillUseInfo.getType() == SkillType.圣盾大光环 || skillUseInfo.getType() == SkillType.勤学苦练光环
                            || skillUseInfo.getType() == SkillType.战争狂热) {
                        AllFiledExceptSelf.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.集训) {
                        AllFiledExceptSelf.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getAttachedUseInfo1().getSkill());
                        AllFiledExceptSelf.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo2().getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.致命晶莹) {
                        AllFiledAddSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.乐不思蜀) {
                        AllFiledAddSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.御魔屏障) {
                        AllFiledAddSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.邪恶光环) {
                        AllFiledAddSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.荆棘刺盾) {
                        AllFiledAddSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.共生链接) {
                        AllFiledAddSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.圣战之歌) {
                        AllFiledAddSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.圣域屏障 || skillUseInfo.getType() == SkillType.足球风暴 || skillUseInfo.getType() == SkillType.破阵之势
                            || skillUseInfo.getType() == SkillType.蛇蜕之术 || skillUseInfo.getType() == SkillType.破釜沉舟 || skillUseInfo.getType() == SkillType.行军补给
                            || skillUseInfo.getType() == SkillType.徐如林 || skillUseInfo.getType() == SkillType.晶石铠甲 || skillUseInfo.getType() == SkillType.奥术之源
                            || skillUseInfo.getType() == SkillType.灵王震怒 || skillUseInfo.getType() == SkillType.偷袭 || skillUseInfo.getType() == SkillType.免疫光环
                            || skillUseInfo.getType() == SkillType.无畏军旗 || skillUseInfo.getType() == SkillType.异界神兵 || skillUseInfo.getType() == SkillType.魔力护甲) {
                        AllFiledAddSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.出云蛇势 || skillUseInfo.getType() == SkillType.燃烧铠甲) {
                        AllFiledAddSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                        AllFiledAddSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo2().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.庇护光环 || skillUseInfo.getType() == SkillType.武形破剑光环 || skillUseInfo.getType() == SkillType.镜面光环
                            || skillUseInfo.getType() == SkillType.秘纹领域 || skillUseInfo.getType() == SkillType.圣光奏鸣曲 || skillUseInfo.getType() == SkillType.不可侵犯
                            || skillUseInfo.getType() == SkillType.正义庇所 || skillUseInfo.getType() == SkillType.严阵以待 || skillUseInfo.getType() == SkillType.荆棘护体
                            || skillUseInfo.getType() == SkillType.寒霜护佑) {
                        AddSidesSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.神圣领域 || skillUseInfo.getType() == SkillType.反击阵列) {
                        AddSidesSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                        AddSidesSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo2().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.骑士庇护) {
                        AddSidesSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                        Purify.apply(skillUseInfo, this, card, -1);
                    } else if (skillUseInfo.getType() == SkillType.流光回梦) {
                        Cooperation.apply(this, skillUseInfo, card, "雪舞霓裳", true);
                    } else if (skillUseInfo.getType() == SkillType.暗红魔导阵) {
                        Cooperation.apply(this, skillUseInfo, card, "幻镜魔导镜像", false);
                        Cooperation.apply(this, skillUseInfo, card, "幻镜魔导", false);
                        Cooperation.apply(this, skillUseInfo, card, "真幻镜魔导", false);
                    } else if (skillUseInfo.getType() == SkillType.魏国英魂) {
                        Cooperation.apply(this, skillUseInfo, card, "三国英魂·孟德", true);
                    } else if (skillUseInfo.getType() == SkillType.卡组保护) {
                        CooperationExceptSelf.apply(this, skillUseInfo, card, "魔卡策划X", false);
                    } else if (skillUseInfo.getType() == SkillType.冥狱牢囚) {
                        Cooperation.apply(this, skillUseInfo, card, "樱蝶仙子", true);
                    } else if (skillUseInfo.getType() == SkillType.重整 || skillUseInfo.getType() == SkillType.不朽岿岩 || skillUseInfo.getType() == SkillType.不息神盾
                            || skillUseInfo.getType() == SkillType.再生金蝉 || skillUseInfo.getType() == SkillType.天道无常 || skillUseInfo.getType() == SkillType.卷土重来
                            || skillUseInfo.getType() == SkillType.摄魂之力 || skillUseInfo.getType() == SkillType.情况紧急 || skillUseInfo.getType() == SkillType.复仇者
                            || skillUseInfo.getType() == SkillType.柳暗花明 || skillUseInfo.getType() == SkillType.无限剑制 || skillUseInfo.getType() == SkillType.金蝉脱壳
                            || skillUseInfo.getType() == SkillType.凤凰于飞) {
                        Reforming.reset(skillUseInfo, card);
                    } else if (skillUseInfo.getType() == SkillType.烈火炙魂 || skillUseInfo.getType() == SkillType.据守) {
                        ReformingMult.reset(skillUseInfo, card);
                    } else if (skillUseInfo.getType() == SkillType.代罪 || skillUseInfo.getType() == SkillType.紊乱) {
                        Reforming.reset(skillUseInfo, card);
                    } else if (skillUseInfo.getType() == SkillType.风暴雷云) {
                        ReformingAwaken.reset(skillUseInfo, card);
                    } else if (skillUseInfo.getType() == SkillType.元素分离) {
                        Cooperation.apply(this, skillUseInfo.getAttachedUseInfo1().getAttachedUseInfo1(), card, "土熊猫", false);
                        Cooperation.apply(this, skillUseInfo.getAttachedUseInfo1().getAttachedUseInfo2(), card, "火熊猫", false);
                        Cooperation.apply(this, skillUseInfo.getAttachedUseInfo2(), card, "风熊猫", false);
                    } else if (skillUseInfo.getType() == SkillType.无刀取 || skillUseInfo.getType() == SkillType.神圣领域) {
                        HolyShield.resetApply(skillUseInfo, this, card);
                    } else if (skillUseInfo.getType() == SkillType.霜之领域) {
                        AllFiledAddSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill());
                        GiveSideSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo2().getSkill());
                    } else if (skillUseInfo.getType() == SkillType.风暴涌动) {
                        Cooperation.apply(this, skillUseInfo, card, "风暴主宰", false);
                    } else if (skillUseInfo.getType() == SkillType.天下英主) {
                        Cooperation.applyVague(this, skillUseInfo, card, "五子良将", false);
                    } else if (skillUseInfo.getType() == SkillType.神鬼之医 || skillUseInfo.getType() == SkillType.逆转之矢 || skillUseInfo.getType() == SkillType.风雨无阻) {
                        DisorderMult.reset(skillUseInfo, card);
                    } else if (skillUseInfo.getType() == SkillType.一夫当关) {
                        Cooperation.apply(this, skillUseInfo.getAttachedUseInfo2(), card, "旷世绝恋·貂蝉", false);
                    } else if (skillUseInfo.getType() == SkillType.魔王降临 || skillUseInfo.getType() == SkillType.魔王之怒) {
                        RaceChangeSelf.apply(this, skillUseInfo, card);
                    } else if (skillUseInfo.getType() == SkillType.神谕 || skillUseInfo.getType() == SkillType.冰与火之歌 || skillUseInfo.getType() == SkillType.沉默之境
                            || skillUseInfo.getType() == SkillType.禁语) {
                        SummonStopSkillUseInfoList.apply(this, skillUseInfo, card);
                    } else if (skillUseInfo.getType() == SkillType.棋布星罗 || skillUseInfo.getType() == SkillType.同调 || skillUseInfo.getType() == SkillType.轮换
                            || skillUseInfo.getType() == SkillType.戍边 || skillUseInfo.getType() == SkillType.替身术 || skillUseInfo.getType() == SkillType.顽疾) {
                        ScatterHereAndThere.reset(skillUseInfo, card);
                    }
                } else {
                    if (skillUseInfo.getType() == SkillType.重整 || skillUseInfo.getType() == SkillType.不朽岿岩 || skillUseInfo.getType() == SkillType.不息神盾
                            || skillUseInfo.getType() == SkillType.再生金蝉 || skillUseInfo.getType() == SkillType.天道无常 || skillUseInfo.getType() == SkillType.卷土重来
                            || skillUseInfo.getType() == SkillType.摄魂之力 || skillUseInfo.getType() == SkillType.情况紧急 || skillUseInfo.getType() == SkillType.复仇者
                            || skillUseInfo.getType() == SkillType.柳暗花明 || skillUseInfo.getType() == SkillType.无限剑制 || skillUseInfo.getType() == SkillType.金蝉脱壳
                            || skillUseInfo.getType() == SkillType.凤凰于飞) {
                        Reforming.reset(skillUseInfo, card);
                    } else if (skillUseInfo.getType() == SkillType.烈火炙魂 || skillUseInfo.getType() == SkillType.据守) {
                        ReformingMult.reset(skillUseInfo, card);
                    } else if (skillUseInfo.getType() == SkillType.代罪 || skillUseInfo.getType() == SkillType.紊乱) {
                        Reforming.reset(skillUseInfo, card);
                    } else if (skillUseInfo.getType() == SkillType.风暴雷云) {
                        ReformingAwaken.reset(skillUseInfo, card);
                    } else if (skillUseInfo.getType() == SkillType.无刀取 || skillUseInfo.getType() == SkillType.神圣领域) {
                        HolyShield.resetApply(skillUseInfo, this, card);
                    } else if (skillUseInfo.getType() == SkillType.神鬼之医 || skillUseInfo.getType() == SkillType.逆转之矢 || skillUseInfo.getType() == SkillType.风雨无阻) {
                        DisorderMult.reset(skillUseInfo, card);
                    } else if (skillUseInfo.getType() == SkillType.棋布星罗 || skillUseInfo.getType() == SkillType.轮换 || skillUseInfo.getType() == SkillType.同调
                            || skillUseInfo.getType() == SkillType.戍边 || skillUseInfo.getType() == SkillType.替身术 || skillUseInfo.getType() == SkillType.顽疾) {
                        ScatterHereAndThere.reset(skillUseInfo, card);
                    }
                }
            }
        }
    }

    public void resolvePreAttackSkills(CardInfo attacker, Player defender, int status) throws HeroDieSignal {

        //厄运类技能计算
        boolean doomFlag = false;
        CardStatus cardStatusList = attacker.getStatus();
        SkillUseInfo doomSkillUseInfo = null;
        List<CardStatusItem> doomStatus = cardStatusList.getStatusOf(CardStatusType.厄运);
        int doomImpact = 0;
        String message = "";
        if (doomStatus.size() > 0) {
            doomFlag = true;
            doomSkillUseInfo = doomStatus.get(0).getCause();
            doomImpact = doomSkillUseInfo.getSkill().getImpact2();
            message = doomSkillUseInfo.getOwner().getShortDesc() + "使用了技能" + doomSkillUseInfo.getSkill().getName();
        }

        for (SkillUseInfo skillUseInfo : attacker.getUsableNormalSkills()) {
            if (attacker.isDead() || attacker.getStatus().containsStatus(CardStatusType.不屈)) {
                continue;
            }
            if (doomFlag) {
                if (doomSkillMessgae(message, skillUseInfo, doomImpact)) {
                    continue;
                }
            }
            if (skillUseInfo.getType() == SkillType.透支 || skillUseInfo.getType() == SkillType.过载 || skillUseInfo.getType() == SkillType.勤学苦练
                    || skillUseInfo.getType() == SkillType.三界行者 || skillUseInfo.getType() == SkillType.勤能补拙 || skillUseInfo.getType() == SkillType.舍命一击) {
                Overdraw.apply(this, skillUseInfo, attacker);
            } else if (skillUseInfo.getType() == SkillType.力竭) {
                Exhausted.apply(this, skillUseInfo, attacker);
            } else if (skillUseInfo.getType() == SkillType.未知) {
                // JUST A PLACEHOLDER
            } else if (skillUseInfo.getType() == SkillType.送还 || skillUseInfo.getType() == SkillType.突袭 || skillUseInfo.getType() == SkillType.食梦 || skillUseInfo.getType() == SkillType.幻影斩) {
                Return.apply(this, skillUseInfo.getSkill(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.全体送还) {
                ReturnNumber.apply(this, skillUseInfo.getSkill(), attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.流光回梦) {
                ReturnToHandAndDelay.apply(this, skillUseInfo.getSkill(), attacker, defender, 2, 1);
            } else if (skillUseInfo.getType() == SkillType.退散) {
                ReturnCard.apply(this, skillUseInfo.getSkill(), attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.弱者溃散) {
                ReturnCardAndDelay.apply(this, skillUseInfo.getSkill(), attacker, defender, 2, -1);
            } else if (skillUseInfo.getType() == SkillType.LETITGO || skillUseInfo.getType() == SkillType.击溃 || skillUseInfo.getType() == SkillType.高位逼抢) {
                Return.apply(this, skillUseInfo.getSkill().getAttachedSkill1(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.沉默 || skillUseInfo.getType() == SkillType.沉默领域 ||
                    skillUseInfo.getType() == SkillType.觉醒沉默 && attacker.isAwaken(skillUseInfo, Race.KINGDOM, 1) ||
                    skillUseInfo.getType() == SkillType.觉醒沉默A && attacker.isAwaken(skillUseInfo, Race.FOREST, 2)) {
                Silence.apply(this, skillUseInfo, attacker, defender, false, false);
            } else if (skillUseInfo.getType() == SkillType.全体沉默) {
                Silence.apply(this, skillUseInfo, attacker, defender, true, false);
            } else if (skillUseInfo.getType() == SkillType.死亡印记 || skillUseInfo.getType() == SkillType.武形印记) {
                DeathMark.apply(this, skillUseInfo, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.反间) {
                DeathMark.apply(this, skillUseInfo, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.霜火炸弹 || skillUseInfo.getType() == SkillType.破片手雷) {
                DeathMark.apply(this, skillUseInfo, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.爆裂巫术) {
                DeathMark.apply(this, skillUseInfo, attacker, defender, 2);
            } else if (skillUseInfo.getType() == SkillType.煮豆燃萁) {
                DeathMark.apply(this, skillUseInfo, attacker, defender, 7);
            } else if (skillUseInfo.getType() == SkillType.关小黑屋) {
                Enprison.apply(this, skillUseInfo.getSkill(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.吐槽) {
                Tsukomi.apply(this, skillUseInfo.getSkill(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.火球) {
                FireMagic.apply(skillUseInfo.getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.八重红彼岸 || skillUseInfo.getType() == SkillType.浩劫
                    || skillUseInfo.getType() == SkillType.谜之水枪
                    || skillUseInfo.getType() == SkillType.最终判决 || skillUseInfo.getType() == SkillType.剧毒剑刃) {
                GreatFireMagic.apply(skillUseInfo.getSkill(), this, attacker, defender, 1, false);
            } else if (skillUseInfo.getType() == SkillType.灭世之力 || skillUseInfo.getType() == SkillType.法术灭杀 || skillUseInfo.getType() == SkillType.毁灭之翼) {
                GreatFireMagic.apply(skillUseInfo.getSkill(), this, attacker, defender, -1, false);
            } else if (skillUseInfo.getType() == SkillType.奥术湮灭) {
                GreatFireMagic.apply(skillUseInfo.getSkill(), this, attacker, defender, 2, false);
            } else if (skillUseInfo.getType() == SkillType.火墙) {
                FireMagic.apply(skillUseInfo.getSkill(), this, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.烈焰风暴) {
                FireMagic.apply(skillUseInfo.getSkill(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.落雷) {
                LighteningMagic.apply(skillUseInfo, this, attacker, defender, 1, 50);
            } else if (skillUseInfo.getType() == SkillType.连环闪电) {
                LighteningMagic.apply(skillUseInfo, this, attacker, defender, 3, 40);
            } else if (skillUseInfo.getType() == SkillType.雷暴) {
                LighteningMagic.apply(skillUseInfo, this, attacker, defender, -1, 35);
            } else if (skillUseInfo.getType() == SkillType.雷神降临 ||
                    skillUseInfo.getType() == SkillType.觉醒雷神降临 && attacker.isAwaken(skillUseInfo, Race.HELL, 1)) {
                LighteningMagic.apply(skillUseInfo, this, attacker, defender, -1, 75);
            } else if (skillUseInfo.getType() == SkillType.冰弹) {
                IceMagic.apply(skillUseInfo, this, attacker, defender, 1, 45, 0);
            } else if (skillUseInfo.getType() == SkillType.圣诞雪球) {
                IceMagic.apply(skillUseInfo, this, attacker, defender, 1, 90, 0);
            } else if (skillUseInfo.getType() == SkillType.霜冻新星) {
                IceMagic.apply(skillUseInfo, this, attacker, defender, 3, 35, 0);
            } else if (skillUseInfo.getType() == SkillType.暴风雪) {
                IceMagic.apply(skillUseInfo, this, attacker, defender, -1, 30, 0);
            } else if (skillUseInfo.getType() == SkillType.冰封禁制) {
                IceMagic.apply(skillUseInfo, this, attacker, defender, -1, 90, 0);
            } else if (skillUseInfo.getType() == SkillType.寒霜冲击) {
                IceMagic.apply(skillUseInfo, this, attacker, defender, -1, 50, (5 + skillUseInfo.getSkill().getLevel() * 5) * defender.getField().getAliveCards().size());
            } else if (skillUseInfo.getType() == SkillType.毒液) {
                PoisonMagic.apply(skillUseInfo, this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.毒雾) {
                PoisonMagic.apply(skillUseInfo, this, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.毒云) {
                PoisonMagic.apply(skillUseInfo, this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.陷阱) {
                Trap.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.治疗) {
                Heal.apply(skillUseInfo.getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.甘霖 || skillUseInfo.getType() == SkillType.甘露) {
                Rainfall.apply(skillUseInfo.getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.月神的护佑 || skillUseInfo.getType() == SkillType.月之守护 || skillUseInfo.getType() == SkillType.月之守望 || skillUseInfo.getType() == SkillType.紫气东来) {
                LunaBless.apply(skillUseInfo.getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.月神的触碰 || skillUseInfo.getType() == SkillType.月神的恩赐 || skillUseInfo.getType() == SkillType.救死扶伤) {
                LunaTouch.apply(skillUseInfo.getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.治疗之雾) {
                HealingMist.apply(skillUseInfo.getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.祈祷 || skillUseInfo.getType() == SkillType.补给) {
                Pray.apply(skillUseInfo.getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.复活 || skillUseInfo.getType() == SkillType.乐善好施) {
                Revive.apply(this, skillUseInfo, attacker);
            } else if (skillUseInfo.getType() == SkillType.新生) {
                NewBorn.apply(this, skillUseInfo, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.圣灵之泉) {
                NewBorn.apply(this, skillUseInfo, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.夺魂 || skillUseInfo.getType() == SkillType.灵魂支配 || skillUseInfo.getType() == SkillType.归心) {
                SoulControl.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.鬼才) {
                SoulControl.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.背刺 || skillUseInfo.getType() == SkillType.大背刺 || skillUseInfo.getType() == SkillType.突击
                    || skillUseInfo.getType() == SkillType.闪耀突击 || skillUseInfo.getType() == SkillType.深海之力 || skillUseInfo.getType() == SkillType.潜伏之力) {
                BackStab.apply(this, skillUseInfo, attacker);
            } else if (skillUseInfo.getType() == SkillType.群体削弱 || skillUseInfo.getType() == SkillType.霸王之姿 || skillUseInfo.getType() == SkillType.喋喋不休
                    || skillUseInfo.getType() == SkillType.威吓) {
                WeakenAll.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.诅咒铠甲) {
                WeakenAll.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.回魂) {
                Resurrection.apply(this, skillUseInfo, attacker);
            } else if (skillUseInfo.getType() == SkillType.祈愿 || skillUseInfo.getType() == SkillType.神恩 || skillUseInfo.getType() == SkillType.萦梦) {
                Supplication.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.号角 || skillUseInfo.getType() == SkillType.集结旗帜) {
                Horn.apply(skillUseInfo, this, attacker);
            } else if (skillUseInfo.getType() == SkillType.疾如风) {
                Supplication.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
                Horn.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.觉醒风之祈愿) {
                if (skillUseInfo.getOwner().getOwner().getHand().isFull()) {
                    Horn.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker);
                }
                if (!skillUseInfo.getOwner().getOwner().getHand().isFull()) {
                    Supplication.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, defender);
                }
            } else if (skillUseInfo.getType() == SkillType.归魂) {
                RegressionSoul.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.太平要术) {
                RegressionSoul.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
                LunaBless.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.狙击 || skillUseInfo.getType() == SkillType.射门) {
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.魔神之刃) {
                Snipe.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.武形秘箭 || skillUseInfo.getType() == SkillType.骤雨) {
                Snipe.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.星座能量思考) {
                Snipe.apply(skillUseInfo.getAttachedUseInfo2(), skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.寒心恨雪) {
                Snipe.apply(skillUseInfo.getAttachedUseInfo2(), skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.二重狙击 || skillUseInfo.getType() == SkillType.处罚) {
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 2);
            } else if (skillUseInfo.getType() == SkillType.神箭三重奏) {
                Snipe.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.寒莹触碰 || skillUseInfo.getType() == SkillType.猎杀时刻) {
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.武形神箭) {
                Snipe.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, -1);
                Snipe.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.穿云箭 || skillUseInfo.getType() == SkillType.完美狙击 || skillUseInfo.getType() == SkillType.精准狙击) {
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.弹无虚发) {
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.破阵弧光) {
                Snipe.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 1);
                Snipe.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 1);
                Snipe.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.三连狙击) {
                SnipeOneNumber.apply(skillUseInfo.getAttachedUseInfo1(), skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, 1);
                SnipeOneNumber.apply(skillUseInfo.getAttachedUseInfo1(), skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, 1);
                if (defender.getField().getAliveCards().size() >= 3) {
                    SnipeOneNumber.apply(skillUseInfo.getAttachedUseInfo1(), skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, 1);
                } else {
                    SnipeOneNumber.apply(skillUseInfo.getAttachedUseInfo2(), skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker, defender, 1);
                }
                Snipe.apply(skillUseInfo.getAttachedUseInfo1(), skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.魔力飞刃) {
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 1);
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 1);
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 1);
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.绯弹) {
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.左轮射击) {
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.百步穿杨) {
                Snipe.apply(skillUseInfo, skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, -1);
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.枪林弹雨) {
                Snipe.apply(skillUseInfo, skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, -1);
                Snipe.apply(skillUseInfo, skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.迷魂) {
                Confusion.apply(skillUseInfo, this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.混乱领域 || skillUseInfo.getType() == SkillType.连奏 || skillUseInfo.getType() == SkillType.迷幻之境) {
                Confusion.apply(skillUseInfo, this, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.国色 || skillUseInfo.getType() == SkillType.千娇百媚 || skillUseInfo.getType() == SkillType.精神侵蚀) {
                Confusion.apply(skillUseInfo, this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.魅惑之舞) {
                Confusion.apply(skillUseInfo, this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.蝶语 || skillUseInfo.getType() == SkillType.倾城之舞) {
                Confusion.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.幻音) {
                Confusion.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.无我境界) {
                Confusion.apply(skillUseInfo, this, attacker, defender, 3);
                Insane.apply(skillUseInfo, this, attacker, defender, 1, 100);
            } else if (skillUseInfo.getType() == SkillType.烈火焚神 || skillUseInfo.getType() == SkillType.天火 || skillUseInfo.getType() == SkillType.全体灼烧) {
                BurningFlame.apply(skillUseInfo, this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.诅咒) {
                Curse.apply(this, skillUseInfo.getSkill(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.魔神之咒) {
                Curse.apply(this, skillUseInfo.getSkill(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.摧毁) {
                Destroy.apply(this, skillUseInfo.getSkill(), attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.冥府之召) {
                UnderworldCall.apply(this, skillUseInfo.getSkill(), attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.万剑归宗) {
                UnderworldCall.apply(this, skillUseInfo.getSkill(), attacker, defender, 5);
                UnderworldCall.apply(this, skillUseInfo.getSkill(), attacker, defender, 5);
            } else if (skillUseInfo.getType() == SkillType.死亡威慑) {
                UnderworldCall.apply(this, skillUseInfo.getSkill(), attacker, defender, 2);
            } else if (skillUseInfo.getType() == SkillType.药桶爆弹) {
                UnderworldCall.apply(this, skillUseInfo.getSkill(), attacker, defender, 5);
            } else if (skillUseInfo.getType() == SkillType.死亡宣告) {
                UnderworldCall.apply(this, skillUseInfo.getSkill(), attacker, defender, 2);
            } else if (skillUseInfo.getType() == SkillType.烈焰审判) {
                UnderworldCall.apply(this, skillUseInfo.getSkill(), attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.炼金失败 || skillUseInfo.getType() == SkillType.凤凰涅盘 || skillUseInfo.getType() == SkillType.海滨危机
                    || skillUseInfo.getType() == SkillType.战术性撤退 || skillUseInfo.getType() == SkillType.自毁 || skillUseInfo.getType() == SkillType.巧变
                    || skillUseInfo.getType() == SkillType.进退自如) {
                AlchemyFailure.apply(this, skillUseInfo, skillUseInfo.getSkill(), attacker);
            } else if (skillUseInfo.getType() == SkillType.瘟疫) {
                Plague.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.凋零真言 || skillUseInfo.getType() == SkillType.暗之凋零) {
                WitheringWord.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.血炼 || skillUseInfo.getType() == SkillType.生命吸取) {
                BloodPaint.apply(skillUseInfo.getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.鲜血盛宴 || skillUseInfo.getType() == SkillType.歃血魔咒 ||
                    skillUseInfo.getType() == SkillType.猎杀之夜 || skillUseInfo.getType() == SkillType.武之圣域 || skillUseInfo.getType() == SkillType.赤血刀) {
                BloodPaint.apply(skillUseInfo.getSkill(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.熔岩护甲) {
                BloodPaint.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.天谴 || skillUseInfo.getType() == SkillType.末世术 || skillUseInfo.getType() == SkillType.以逸待劳
                    || skillUseInfo.getType() == SkillType.柔光) {
                HeavenWrath.apply(this, skillUseInfo.getSkill(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.莫测) {
                HeavenWrath.apply(this, skillUseInfo.getAttachedUseInfo1().getSkill(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.封印 || skillUseInfo.getType() == SkillType.封锁) {
                Seal.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.圣炎 || skillUseInfo.getType() == SkillType.弑魂夺魄 || skillUseInfo.getType() == SkillType.冰与火之歌 || skillUseInfo.getType() == SkillType.云隐时现) {
                HolyFire.apply(skillUseInfo.getSkill(), this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.法力侵蚀 || skillUseInfo.getType() == SkillType.灵王的轰击 || skillUseInfo.getType() == SkillType.灵能冲击 ||
                    skillUseInfo.getType() == SkillType.觉醒灵王的轰击 && attacker.isAwaken(skillUseInfo, Race.FOREST, 2)) {

                ManaErode.apply(skillUseInfo.getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.破魔手) {
                ManaErode.apply(skillUseInfo.getSkill(), this, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.法力风暴 || skillUseInfo.getType() == SkillType.魔法毁灭 || skillUseInfo.getType() == SkillType.片翼天使) {
                ManaErode.apply(skillUseInfo.getSkill(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.趁胜追击) {
                WinningPursuit.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.复仇) {
                Revenge.apply(this, skillUseInfo, attacker);
            } else if (skillUseInfo.getType() == SkillType.奋战 || skillUseInfo.getType() == SkillType.英勇打击) {
                BraveFight.apply(this, skillUseInfo, attacker);
            } else if (skillUseInfo.getType() == SkillType.樱魂) {
                BraveFight.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker);
            } else if (skillUseInfo.getType() == SkillType.振奋 || skillUseInfo.getType() == SkillType.会心一击) {
                Arouse.apply(this, skillUseInfo, attacker);
            } else if (skillUseInfo.getType() == SkillType.全体阻碍) {
                AllDelay.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.全体加速) {
                AllSpeedUp.apply(skillUseInfo, this, attacker);
            } else if (skillUseInfo.getType() == SkillType.阻碍) {
                OneDelay.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.加速) {
                SpeedUp.apply(skillUseInfo, this, attacker);
            } else if (skillUseInfo.getType() == SkillType.净化) {
                Purify.apply(skillUseInfo, this, attacker, -1);
            } else if (skillUseInfo.getType() == SkillType.虚弱) {
                Soften.apply(skillUseInfo, this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.圣光洗礼 || skillUseInfo.getType() == SkillType.森林沐浴 ||
                    skillUseInfo.getType() == SkillType.蛮荒威压 || skillUseInfo.getType() == SkillType.地狱同化) {
                RaceChange.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.战争怒吼 || skillUseInfo.getType() == SkillType.常夏日光 || skillUseInfo.getType() == SkillType.碎裂怒吼 || skillUseInfo.getType() == SkillType.阳式阴式) {
                Soften.apply(skillUseInfo, this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.镜像 || skillUseInfo.getType() == SkillType.毅重) {
                // 镜像召唤的单位可以被连锁攻击
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 1, attacker.getName());
            } else if (skillUseInfo.getType() == SkillType.虚梦) {
                //镜像召唤的单位可以被连锁攻击
                Summon.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, SummonType.Normal, 1, attacker.getName());
            } else if (skillUseInfo.getType() == SkillType.召唤王国战士) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "圣骑士", "魔剑士");
            } else if (skillUseInfo.getType() == SkillType.召唤骷髅战士) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "骷髅战士", "骷髅战士");
            } else if (skillUseInfo.getType() == SkillType.召唤噩梦护卫) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "时光女神", "金属巨龙");
            } else if (skillUseInfo.getType() == SkillType.召唤邪龙护卫) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "亡灵守护神", "光明之龙");
            } else if (skillUseInfo.getType() == SkillType.召唤复仇护卫) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "雷兽", "末日预言师");
            } else if (skillUseInfo.getType() == SkillType.召唤花仙子) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "花仙子", "花仙子");
            } else if (skillUseInfo.getType() == SkillType.召唤火焰乌鸦) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "火焰乌鸦", "火焰乌鸦");
            } else if (skillUseInfo.getType() == SkillType.召唤人马巡逻者) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "人马巡逻者", "人马巡逻者");
            } else if (skillUseInfo.getType() == SkillType.召唤女神侍者) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "女神侍者", "女神侍者");
            } else if (skillUseInfo.getType() == SkillType.召唤树人守护者) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "霜雪树人", "树人祭司");
            } else if (skillUseInfo.getType() == SkillType.召唤炎魔) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 1, "炎魔");
            } else if (skillUseInfo.getType() == SkillType.双子之身) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 1, "双子座·幻影");
            } else if (skillUseInfo.getType() == SkillType.召唤北海神兽) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "北海神兽", "北海神兽");
            } else if (skillUseInfo.getType() == SkillType.召唤梦境女神) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "梦境女神", "梦境女神");
            } else if (skillUseInfo.getType() == SkillType.酋长号令) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "战意斗神", "战意斗神");
            } else if (skillUseInfo.getType() == SkillType.原召唤花族守卫) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "黄金金属巨龙", "原处女座");
            } else if (skillUseInfo.getType() == SkillType.召唤花族守卫) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "黄金金属巨龙", "处女座");
            } else if (skillUseInfo.getType() == SkillType.召唤花族侍卫) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "时光女神", "雷雕之魂");
            } else if (skillUseInfo.getType() == SkillType.原召唤花族侍卫) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "原时光女神", "雷雕之魂");
            } else if (skillUseInfo.getType() == SkillType.七十二变) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "齐天美猴王", "齐天美猴王");
            } else if (skillUseInfo.getType() == SkillType.龙魂召唤) {
                SummonWhenAttack.apply(this, skillUseInfo, attacker, 2, false, "徒壁幼龙", "徒壁幼龙");
            } else if (skillUseInfo.getType() == SkillType.仙子召唤) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "蝶语仙子", "蝶语仙子");
            } else if (skillUseInfo.getType() == SkillType.召唤炮灰) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "炮灰", "炮灰");
            } else if (skillUseInfo.getType() == SkillType.召唤孔明) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 1, "三国英魂孔明");
            } else if (skillUseInfo.getType() == SkillType.英灵降临) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 1,
                        "圣剑持有者", "银河圣剑使", "精灵游骑兵", "爱神", "蝗虫公爵", "战场女武神", "龙角将军", "断罪之镰");
            } else if (skillUseInfo.getType() == SkillType.禁术召唤) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 1,
                        "网页版禁术无尽华尔兹", "网页版禁术全领域沉默", "网页版禁术救赎", "网页版禁术末世降临", "网页版禁术全体阻碍");
            } else if (skillUseInfo.getType() == SkillType.进攻号令) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 2,
                        "网页版原素曜灵", "网页版原素猎手", "网页版原素狂暴者");
            } else if (skillUseInfo.getType() == SkillType.进攻号令) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 2,
                        "网页版原素侍卫", "网页版原素将军");
            } else if (skillUseInfo.getType() == SkillType.页游纯洁) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2,
                        "降临天使", "大主教");
            } else if (skillUseInfo.getType() == SkillType.万华镜) {
                Summon.apply(this, skillUseInfo.getAttachedUseInfo1().getAttachedUseInfo1(), attacker, SummonType.Normal, 1,
                        "幻镜魔导镜像");
                Summon.apply(this, skillUseInfo.getAttachedUseInfo1().getAttachedUseInfo2(), attacker, SummonType.Normal, 1,
                        "幻镜魔导镜像");
                Summon.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, SummonType.Normal, 1,
                        "幻镜魔导镜像");
            } else if (skillUseInfo.getType() == SkillType.页游万华镜) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 1,
                        "网页版幻镜魔导·屠戮", "网页版幻镜魔导·归源", "网页版幻镜魔导·暗炎");
            } else if (skillUseInfo.getType() == SkillType.圣诞大狂欢) {
                SummonOpponent.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, SummonType.Normal, 4, "圣诞雪人", "圣诞雪人", "圣诞雪人", "圣诞雪人");
                Summon.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, SummonType.Normal, 4, "圣诞老人", "圣诞树人", "圣诞麋鹿", "圣诞麋鹿");
            } else if (skillUseInfo.getType() == SkillType.大地召唤) {
                SummonOpponent.apply(this, skillUseInfo, attacker, SummonType.Normal, 1, "网页版大地之影");
            } else if (skillUseInfo.getType() == SkillType.樱色轮舞) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 1,
                        "月樱公主", "风之樱女", "春樱斗魂");
            } else if (skillUseInfo.getType() == SkillType.魏国英魂) {
                AddCard.apply(this, skillUseInfo, attacker, SummonType.Summoning, 1,
                        "三国英魂孟德", "三国英魂仲达", "三国樱魂文远", "三国英魂元让", "三国英魂甄姬", "三国英魂文若");
            } else if (skillUseInfo.getType() == SkillType.蜀国英魂) {
                AddCard.apply(this, skillUseInfo, attacker, SummonType.Summoning, 1,
                        "三国英魂子龙", "三国英魂翼德", "三国英魂卧龙", "三国英魂孔明", "三国英魂孟起", "三国英魂云长", "三国英魂汉升", "三国英魂玄德", "三国英魂星彩");
            } else if (skillUseInfo.getType() == SkillType.吴国英魂) {
                AddCard.apply(this, skillUseInfo, attacker, SummonType.Summoning, 1,
                        "三国英魂大乔", "三国英魂仲谋", "三国英魂子敬", "三国英魂伯言", "三国英魂子义");
            } else if (skillUseInfo.getType() == SkillType.繁星) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 1,
                        "月亮女神", "复活节兔女郎", "银河圣剑使", "世界树之灵");
            } else if (skillUseInfo.getType() == SkillType.育龙者) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 1,
                        "光明之龙", "金属巨龙", "黄金金属巨龙", "元素灵龙", "暴怒霸龙", "毁灭之龙", "幽灵巨龙",
                        "水晶巨龙", "毒雾羽龙", "黄金毒龙", "地魔龙", "邪狱魔龙", "混沌之龙", "地狱雷龙");
            } else if (skillUseInfo.getType() == SkillType.寒霜召唤) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 1,
                        "陨星魔法使", "怒雪咆哮", "圣诞老人", "寒霜冰灵使", "白羊座", "霜狼酋长", "雪月花", "梦魇猎手·霜");
            } else if (skillUseInfo.getType() == SkillType.原寒霜召唤) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 1,
                        "陨星魔法使", "原怒雪咆哮", "圣诞老人", "寒霜冰灵使", "原白羊座", "霜狼酋长", "雪月花", "梦魇猎手·霜");
            } else if (skillUseInfo.getType() == SkillType.无尽梦魇) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 1,
                        "梦魇猎手·岚", "梦魇猎手·霜", "梦魇猎手·胧");
            } else if (skillUseInfo.getType() == SkillType.百鬼夜行) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 1,
                        "大江三鬼·银", "大江三鬼·红", "大江三鬼·金");
            } else if (skillUseInfo.getType() == SkillType.武形降临) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 1,
                        "武形火焰尊者", "武形神射尊者", "武形破拳尊者", "武形剑圣", "武形斗圣");
            } else if (skillUseInfo.getType() == SkillType.爱之召唤) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 2,
                        "爱之使者", "森林丘比特", "占卜少女", "爱神");
            } else if (skillUseInfo.getType() == SkillType.召唤伍长) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 1, "巅峰伍长");
            } else if (skillUseInfo.getType() == SkillType.召唤兵长) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 1, "巅峰兵长");
            } else if (skillUseInfo.getType() == SkillType.突击军势) {
                Summon.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, SummonType.Normal, 1, "巅峰伍长");
                Summon.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, SummonType.Normal, 1, "巅峰兵长");
            } else if (skillUseInfo.getType() == SkillType.突击军阵) {
                Summon.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, SummonType.Normal, 1, "巅峰伍长·合金");
                Summon.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, SummonType.Normal, 1, "巅峰兵长·合金");
            } else if (skillUseInfo.getType() == SkillType.灵龙轰咆) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 2,
                        "光明之龙", "金属巨龙", "黄金金属巨龙", "元素灵龙", "暴怒霸龙", "毁灭之龙", "幽灵巨龙",
                        "水晶巨龙", "毒雾羽龙", "黄金毒龙", "地魔龙", "邪狱魔龙", "混沌之龙");
            } else if (skillUseInfo.getType() == SkillType.法师契约) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 2,
                        "魔导师", "暴雪召唤士");
            } else if (skillUseInfo.getType() == SkillType.圣堂召唤) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2,
                        "圣堂刑律官", "圣堂执政官");
            } else if (skillUseInfo.getType() == SkillType.圣德同伴) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2,
                        "大主教", "幻术舞姬");
            } else if (skillUseInfo.getType() == SkillType.乱世红颜) {
                SummonMultiple.apply(this, skillUseInfo, attacker, 5,
                        "肉林");
            } else if (skillUseInfo.getType() == SkillType.召唤小黑) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 1,
                        "暗黑游侠");
            } else if (skillUseInfo.getType() == SkillType.连营) {
                Summon.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, SummonType.Normal, 2, "炮灰", "炮灰");
                MagicMark.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.魔力法阵) {
                MagicMark.apply(this, skillUseInfo, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.荣耀降临) {
                MagicMark.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.魔力印记 || skillUseInfo.getType() == SkillType.恶魔印记) {
                MagicMark.apply(this, skillUseInfo, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.东风 || skillUseInfo.getType() == SkillType.酩酊 || skillUseInfo.getType() == SkillType.灵力魔阵) {
                MagicMark.apply(this, skillUseInfo, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.腐蚀术) {
                MagicMark.apply(this, skillUseInfo, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.致盲) {
                Blind.apply(this, skillUseInfo, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.闪光弹) {
                Blind.apply(this, skillUseInfo, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.爆震弹) {
                FireMagic.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, -1);
                Blind.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.动能追加 || skillUseInfo.getType() == SkillType.彼岸冥灵) {
                EnergyIncrement.apply(skillUseInfo, this, attacker);
            } else if (skillUseInfo.getType() == SkillType.祈福 || skillUseInfo.getType() == SkillType.王母挥袂
                    || skillUseInfo.getType() == SkillType.真理导言 || skillUseInfo.getType() == SkillType.神性祈祷
                    || skillUseInfo.getType() == SkillType.柳暗花明 || skillUseInfo.getType() == SkillType.光耀晨星) {
                Bless.apply(skillUseInfo.getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.闭月) {
                Bless.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.修罗地火攻 || skillUseInfo.getType() == SkillType.火攻 || skillUseInfo.getType() == SkillType.地狱烈火) {
                SuraFire.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.炎敷) {
                SuraFire.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
                Trap.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.天照) {
                SuraFire.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
                BurningFlame.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.淬毒手里剑) {
                HandSword.apply(this, skillUseInfo, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.望月杀阵) {
                HandSword.apply(this, skillUseInfo, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.熔魂之刃) {
                HandSword.apply(this, skillUseInfo, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.火烧连营 || skillUseInfo.getType() == SkillType.彻骨霜火 || skillUseInfo.getType() == SkillType.妖狐火焰) {
                ContinuousFire.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.病情加重) {
                ContinuousFireMult.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.邪龙之怒) {
                ContinuousFire.applyNumber(this, skillUseInfo, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.侵略如火) {
                MagicMark.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender, -1);
                ContinuousFire.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.化学风暴 || skillUseInfo.getType() == SkillType.生化风暴) {
                ChemicalRage.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.精神狂乱) {
                Insane.apply(skillUseInfo, this, attacker, defender, 1, 100);
            } else if (skillUseInfo.getType() == SkillType.离间) {
                Insane.apply(skillUseInfo, this, attacker, defender, 3, 100);
            } else if (skillUseInfo.getType() == SkillType.铁骑破阵) {
                Insane.apply(skillUseInfo, this, attacker, defender, 3, 200);
            } else if (skillUseInfo.getType() == SkillType.骚乱 || skillUseInfo.getType() == SkillType.横冲直撞) {
                Insane.apply(skillUseInfo, this, attacker, defender, 3, 150);
            } else if (skillUseInfo.getType() == SkillType.癫狂之舞) {
                Insane.apply(skillUseInfo, this, attacker, defender, 1, 150);
            } else if (skillUseInfo.getType() == SkillType.怨魂附身) {
                Insane.apply(skillUseInfo, this, attacker, defender, 2, 200);
            } else if (skillUseInfo.getType() == SkillType.精神污染) {
                Insane.apply(skillUseInfo, this, attacker, defender, 3, 0);
            } else if (skillUseInfo.getType() == SkillType.原素之舞) {
                Insane.apply(skillUseInfo, this, attacker, defender, 5, 70);
            } else if (skillUseInfo.getType() == SkillType.醉酒狂暴) {
                Insane.apply(skillUseInfo, this, attacker, defender, 5, 200);
            } else if (skillUseInfo.getType() == SkillType.无尽华尔兹) {
                Insane.apply(skillUseInfo, this, attacker, defender, -1, 100);
            } else if (skillUseInfo.getType() == SkillType.破阵) {
                Insane.apply(skillUseInfo, this, attacker, defender, -1, 100);
                SoulControl.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.圣洁魅惑) {
                Insane.apply(skillUseInfo, this, attacker, defender, 3, 50);
            } else if (skillUseInfo.getType() == SkillType.霓裳羽衣舞) {
                Insane.apply(skillUseInfo, this, attacker, defender, 7, 150);
            } else if (skillUseInfo.getType() == SkillType.乱战) {
                Insane.apply(skillUseInfo, this, attacker, defender, 5, 150);
            } else if (skillUseInfo.getType() == SkillType.天怒) {
                FireMagic.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, -1);
                BurningFlame.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.纯质流火 || skillUseInfo.getType() == SkillType.凤凰业火 || skillUseInfo.getType() == SkillType.烈火冲击
                    || skillUseInfo.getType() == SkillType.火焰呼吸 || skillUseInfo.getType() == SkillType.妖火降世) {
                GreatFireMagic.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, -1, true);
                BurningFlame.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.浴火) {
                GreatFireMagic.apply(skillUseInfo.getAttachedUseInfo1().getAttachedUseInfo1().getSkill(), this, attacker, defender, -1, true);
                BurningFlame.apply(skillUseInfo.getAttachedUseInfo1().getAttachedUseInfo2(), this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.业火) {
                HellFire.apply(skillUseInfo, this, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.魔龙吐息) {
                HellFire.apply(skillUseInfo, this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.流星火球) {
                HellFire.apply(skillUseInfo, this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.传送) {
                Transport.apply(this, skillUseInfo.getSkill(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.灵魂消散 || skillUseInfo.getType() == SkillType.灵魂汲取 || skillUseInfo.getType() == SkillType.净世破魔
                    || skillUseInfo.getType() == SkillType.灵魂净化) {
                SoulCrash.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.天启导言) {
                HeavenWrath.apply(this, skillUseInfo.getSkill(), attacker, defender);
                SoulCrash.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.全体裂伤) {
                Wound.applyToAll(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.凋零陷阱) {
                WitheringWord.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender);
                Trap.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.觉醒白虎守护) {
                if (attacker.getOwner().getHP() >= attacker.getOwner().getMaxHP() * 0.7) {
                    LunaBless.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker);
                } else {
                    Bless.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker);
                }
            } else if (skillUseInfo.getType() == SkillType.觉醒神之祈福) {
                if (attacker.getOwner().getHP() >= attacker.getOwner().getMaxHP() * 0.5) {
                    Bless.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker);
                } else {
                    Bless.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker);
                }
            } else if (skillUseInfo.getType() == SkillType.觉醒星之意志) {
                if (defender.getField().getAliveCards().size() >= 5) {
                    SoulCrash.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender);
                }
                if (defender.getField().getAliveCards().size() < 5) {
                    ManaErode.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker, defender, 1);
                }
            } else if (skillUseInfo.getType() == SkillType.觉醒极寒) {
                if (defender.getField().getAliveCards().size() >= 3) {
                    IceMagic.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, -1, 50, 45 * defender.getField().getAliveCards().size());
                }
                if (defender.getField().getAliveCards().size() < 3) {
                    IceTouch.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender, 3,100);
                }
            } else if (skillUseInfo.getType() == SkillType.页游极寒冲击) {
                IceTouch.apply(skillUseInfo, this, attacker, defender, 3,100);
            } else if (skillUseInfo.getType() == SkillType.王牌狙击) {
                if (attacker.getOwner().getHP() >= attacker.getOwner().getMaxHP() * 0.7) {
                    Snipe.apply(skillUseInfo.getAttachedUseInfo1(), skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, 1);
                }
                if (attacker.getOwner().getHP() < attacker.getOwner().getMaxHP() * 0.7) {
                    Snipe.apply(skillUseInfo.getAttachedUseInfo2(), skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker, defender, 1);
                }
            } else if (skillUseInfo.getType() == SkillType.觉醒继志) {
                if (defender.getField().getAliveCards().size() >= 5) {
                    Destroy.apply(this, skillUseInfo.getAttachedUseInfo1().getSkill(), attacker, defender, 1);
                }
                if (defender.getField().getAliveCards().size() < 5) {
                    Soften.apply(skillUseInfo, this, attacker, defender, -1);
                }
            } else if (skillUseInfo.getType() == SkillType.觉醒狼顾 || skillUseInfo.getType() == SkillType.觉醒雷狱) {
                if (defender.getField().getAliveCards().size() >= 5) {
                    LighteningMagic.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, -1, 75);
                }
                if (defender.getField().getAliveCards().size() < 5) {
                    ThunderStrike.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender, 3,75);
                }
            } else if (skillUseInfo.getType() == SkillType.觉醒雷神之咒) {
                if (defender.getField().getAliveCards().size() >= 3) {
                    RedGun.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, 3);
                }
                if (defender.getField().getAliveCards().size() < 3) {
                    RedGun.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender, 1);
                }
            } else if (skillUseInfo.getType() == SkillType.觉醒圣光惩戒) {
                if (defender.getField().getAliveCards().size() >= 5) {
                    LighteningMagic.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, -1, 75);
                }
                if (defender.getField().getAliveCards().size() < 5) {
                    Bless.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker);
                }
            } else if (skillUseInfo.getType() == SkillType.觉醒异端审判) {
                if (defender.getField().getAliveCards().size() >= 5) {
                    SoulCrash.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender);
                }
                if (defender.getField().getAliveCards().size() < 5) {
                    SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker, defender, 1);
                }
            } else if (skillUseInfo.getType() == SkillType.觉醒原素之舞) {
                if (defender.getField().getAliveCards().size() >= 5) {
                    ThunderStrike.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, -1,75);
                }
                if (defender.getField().getAliveCards().size() < 5) {
                    Snipe.apply(skillUseInfo, skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker, defender, 3);
                }
            } else if (skillUseInfo.getType() == SkillType.原素共鸣) {
                ResonantElements.apply(this, skillUseInfo, attacker, "原素曜灵");
            } else if (skillUseInfo.getType() == SkillType.生物进化) {
                Evolution.apply(this, skillUseInfo, attacker, "进化材料", "科学家变异");
            } else if (skillUseInfo.getType() == SkillType.精神补完) {
                Evolution.apply(this, skillUseInfo, attacker, "进化材料", "科学家进化");
            } else if (skillUseInfo.getType() == SkillType.暗之献祭) {
                Deformation.apply(this, skillUseInfo, attacker, "真幻镜魔导");
            } else if (skillUseInfo.getType() == SkillType.晦月) {
                Deformation.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, "晦月");
            } else if (skillUseInfo.getType() == SkillType.蚀月) {
                Deformation.apply(this, skillUseInfo, attacker, "月蚀兽");
            } else if (skillUseInfo.getType() == SkillType.暗之归还) {
                Deformation.apply(this, skillUseInfo, attacker, "幻镜魔导");
            } else if (skillUseInfo.getType() == SkillType.银色之棘毁灭) {
                Deformation.apply(this, skillUseInfo, attacker, "银色之棘");
            } else if (skillUseInfo.getType() == SkillType.银色之棘守护) {
                Deformation.apply(this, skillUseInfo, attacker, "毁灭之棘");
            } else if (skillUseInfo.getType() == SkillType.回归) {
                Deformation.apply(this, skillUseInfo, attacker, "时空守护者");
            } else if (skillUseInfo.getType() == SkillType.神依) {
                Deformation.apply(this, skillUseInfo, attacker, "时空女神");
            } else if (skillUseInfo.getType() == SkillType.魔性变身) {
                Deformation.apply(this, skillUseInfo, attacker, "大毒汁之王");
            } else if (skillUseInfo.getType() == SkillType.地裂) {
                GiantEarthquakesLandslides.apply(this, skillUseInfo.getSkill(), attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.觉醒天崩地裂) {
                GiantEarthquakesLandslides.apply(this, skillUseInfo.getAttachedUseInfo1().getSkill(), attacker, defender, 3);
                ManaErode.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.寒冰触碰) {
                IceTouch.apply(skillUseInfo, this, attacker, defender, 3,50);
            } else if (skillUseInfo.getType() == SkillType.寒霜之指) {
                IceTouch.apply(skillUseInfo, this, attacker, defender, 1,50);
            } else if (skillUseInfo.getType() == SkillType.风暴汇集) {
                IceTouch.apply(skillUseInfo, this, attacker, defender, -1,80);
            } else if (skillUseInfo.getType() == SkillType.冰之魔枪) {
                IceTouch.apply(skillUseInfo, this, attacker, defender, 5,50);
            } else if (skillUseInfo.getType() == SkillType.寒霜拳) {
                IceTouch.apply(skillUseInfo, this, attacker, defender, 1,50);
            } else if (skillUseInfo.getType() == SkillType.魔力碎片) {
                IceTouch.apply(skillUseInfo, this, attacker, defender, 3,75);
            } else if (skillUseInfo.getType() == SkillType.漫天风雪) {
                IceMagic.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, -1, 50, 45 * defender.getField().getAliveCards().size());
                IceTouch.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender, 3,50);
            } else if (skillUseInfo.getType() == SkillType.雷霆一击) {
                ThunderStrike.apply(skillUseInfo, this, attacker, defender, 3,75);
            } else if (skillUseInfo.getType() == SkillType.正义裁决) {
                ThunderStrike.apply(skillUseInfo, this, attacker, defender, 4,75);
                HolyFire.apply(skillUseInfo.getSkill(), this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.妖力侵蚀) {
                ThunderStrike.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, 3,75);
            } else if (skillUseInfo.getType() == SkillType.雷公助我) {
                ThunderStrike.apply(skillUseInfo, this, attacker, defender, 3,75);
            } else if (skillUseInfo.getType() == SkillType.薜荔之怒) {
                ThunderStrike.apply(skillUseInfo, this, attacker, defender, -1,75);
            } else if (skillUseInfo.getType() == SkillType.雷霆之怒) {
                ThunderStrike.apply(skillUseInfo, this, attacker, defender, -1,75);
            } else if (skillUseInfo.getType() == SkillType.赤之魔枪 || skillUseInfo.getType() == SkillType.灵能启迪 || skillUseInfo.getType() == SkillType.狂怒
                    || skillUseInfo.getType() == SkillType.魔力汲取) {
                RedGun.apply(skillUseInfo, this, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.灰飞烟灭) {
                RedGun.apply(skillUseInfo, this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.天道无常) {
                if (defender.getField().getAliveCards().size() < 4) {
                    RedGun.apply(skillUseInfo, this, attacker, defender, 3);
                }
            } else if (skillUseInfo.getType() == SkillType.五雷轰顶 || skillUseInfo.getType() == SkillType.雷神附体) {
                RedGun.apply(skillUseInfo, this, attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.灵能爆发) {
                RedGun.apply(skillUseInfo, this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.全垒打) {
                RedGun.apply(skillUseInfo, this, attacker, defender, 4);
            } else if (skillUseInfo.getType() == SkillType.雷切) {
                ThunderStrike.apply(skillUseInfo, this, attacker, defender, 3,75);
            } else if (skillUseInfo.getType() == SkillType.麻痹药剂) {
                ThunderStrike.apply(skillUseInfo, this, attacker, defender, 2,75);
            } else if (skillUseInfo.getType() == SkillType.王佐之才) {
                HandCardAddTwoSkill.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill());
            } else if (skillUseInfo.getType() == SkillType.你们来啊 || skillUseInfo.getType() == SkillType.你们上啊 || skillUseInfo.getType() == SkillType.还有谁) {
                HandCardAddTwoSkillOpponent.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill(), defender);
            } else if (skillUseInfo.getType() == SkillType.抗魔石肤) {
                HandCardAddOneSkill.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill());
            } else if (skillUseInfo.getType() == SkillType.肾上腺素) {
                HandCardAddOneSkill.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill());
            } else if (skillUseInfo.getType() == SkillType.愈音) {
                HandCardAddSkillNormal.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill(), 1);
            } else if (skillUseInfo.getType() == SkillType.敏助) {
                HandCardAddSkillNormal.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill(), 1);
            } else if (skillUseInfo.getType() == SkillType.亚平宁之蓝 || skillUseInfo.getType() == SkillType.荣誉之地 || skillUseInfo.getType() == SkillType.花酿
                    || skillUseInfo.getType() == SkillType.页游生命之杯 || skillUseInfo.getType() == SkillType.复苏药剂 || skillUseInfo.getType() == SkillType.无上荣耀) {
                HandCardAddSkillNormal.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill(), 1);
            } else if (skillUseInfo.getType() == SkillType.偷偷削弱) {
                HandCardBuff.apply(this, skillUseInfo, attacker, SkillEffectType.MAXHP_CHANGE, 1);
            } else if (skillUseInfo.getType() == SkillType.新卡作成 || skillUseInfo.getType() == SkillType.卡牌作废) {
                AddSkillOpponent.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill(), 1, defender, 0);
            } else if (skillUseInfo.getType() == SkillType.枯萎) {
                AddSkillOpponent.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill(), 2, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.冰巨人吞噬) {
                Erode.apply(this, skillUseInfo, attacker, defender, null, false);
            } else if (skillUseInfo.getType() == SkillType.天召) {
                DivineSummon.apply(skillUseInfo, this, attacker);
            } else if (skillUseInfo.getType() == SkillType.绝对压制) {
                Polymorph.apply(this, skillUseInfo, attacker, defender, 1, 1);
            } else if (skillUseInfo.getType() == SkillType.末日降临) {
                Polymorph.apply(this, skillUseInfo, attacker, defender, -1, 1);
            } else if (skillUseInfo.getType() == SkillType.终极天谴) {
                Curse.apply(this, skillUseInfo.getSkill(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.终极祈祷) {
                Pray.apply(skillUseInfo.getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.再生) {
                Bless.apply(skillUseInfo.getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.终焉时刻) {
                Curse.apply(this, skillUseInfo.getSkill(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.终焉时刻极) {
                CurseMult.apply(this, skillUseInfo.getSkill(), attacker, defender, 10);
            } else if (skillUseInfo.getType() == SkillType.弑主) {
                CounterBite.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker);
                TheSword.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker);
            } else if (skillUseInfo.getType() == SkillType.士气振奋) {
                TheSword.apply(this, skillUseInfo, attacker);
            } else if (skillUseInfo.getType() == SkillType.魂之枷锁) {
                SoulChains.apply(this, skillUseInfo, attacker, defender, 5, 4);
            } else if (skillUseInfo.getType() == SkillType.死亡诅咒) {
                SoulChains.apply(this, skillUseInfo, attacker, defender, 1, 2);
            } else if (skillUseInfo.getType() == SkillType.血魂之咒) {
                SoulChains.apply(this, skillUseInfo, attacker, defender, 3, 3);
            } else if (skillUseInfo.getType() == SkillType.龙吼) {
                SoulChains.apply(this, skillUseInfo, attacker, defender, 3, 2);
            } else if (skillUseInfo.getType() == SkillType.离魂芳印 || skillUseInfo.getType() == SkillType.斗者 || skillUseInfo.getType() == SkillType.吞噬
                    || skillUseInfo.getType() == SkillType.生死界限 || skillUseInfo.getType() == SkillType.深海巨口 || skillUseInfo.getType() == SkillType.吞噬焰火) {
                Rapture.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.天使降临) {
                if (defender.getField().getAliveCards().size() >= 4) {
                    SoulCrash.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender);
                }
                if (defender.getField().getAliveCards().size() < 4) {
                    ReturnToHandAndDelay.apply(this, skillUseInfo.getAttachedUseInfo2().getSkill(), attacker, defender, 1, 1);
                }
            } else if (skillUseInfo.getType() == SkillType.神圣放逐) {
                ReturnToHandAndDelay.apply(this, skillUseInfo.getSkill(), attacker, defender, 1, 1);
            } else if (skillUseInfo.getType() == SkillType.逆羽罡风) {
                UnderworldCall.apply(this, skillUseInfo.getAttachedUseInfo1().getSkill(), attacker, defender, 3);
                RegressionSoul.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.原素召唤) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "网页版原素侍卫", "网页版原素将军");
            } else if (skillUseInfo.getType() == SkillType.小飞侠) {
                Supplication.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
                AllSpeedUp.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.生命之杯) {
                Bless.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker);
                LunaTouch.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.页游吞噬) {
                DevourMultiple.apply(this, skillUseInfo, attacker, defender, 2);
            } else if (skillUseInfo.getType() == SkillType.山崩) {
                Crumbling.apply(this, skillUseInfo.getSkill(), attacker, defender, 1, 1);
            } else if (skillUseInfo.getType() == SkillType.咒怨) {
                Grudge.apply(this, skillUseInfo, attacker, defender, 2);
            } else if (skillUseInfo.getType() == SkillType.白衣渡江) {
                GrudgeAt.apply(this, skillUseInfo, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.魔龙诅咒 || skillUseInfo.getType() == SkillType.暗黑咒术) {
                GrudgeHp.apply(this, skillUseInfo, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.帝国光辉) {
                Bless.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker);
                Rainfall.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.混乱之源) {
                SummonOpponent.apply(this, skillUseInfo, attacker, SummonType.Normal, 3, "混沌体", "混沌体", "混沌体");
            } else if (skillUseInfo.getType() == SkillType.地狱烈焰) {
                HeavenWrath.apply(this, skillUseInfo.getAttachedUseInfo1().getSkill(), attacker, defender);
                SuraFire.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.星座能量控制) {
                SuraFire.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
                IceMagic.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender, -1, 90, 0);
            } else if (skillUseInfo.getType() == SkillType.星座能量神秘) {
                EnergyIncrement.apply(skillUseInfo, this, attacker);
                SoulCrash.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.半数沉默 || skillUseInfo.getType() == SkillType.地煞倾覆 || skillUseInfo.getType() == SkillType.声呐
                    || skillUseInfo.getType() == SkillType.贪狼) {
                HalfSilence.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.锁魂) {
                SoulChains.apply(this, skillUseInfo, attacker, defender, 5, 1);
            } else if (skillUseInfo.getType() == SkillType.二重大灵轰) {
                ManaErode.apply(skillUseInfo.getSkill(), this, attacker, defender, 2);
            } else if (skillUseInfo.getType() == SkillType.海滨大作战) {
                SoulChains.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender, 5, 1);
                ManaErode.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker, defender, 2);
            } else if (skillUseInfo.getType() == SkillType.冲浪集结) {
                Horn.apply(skillUseInfo, this, attacker);
            } else if (skillUseInfo.getType() == SkillType.同调) {
                if (!FailureSkillUseInfoList.explode(this, attacker, defender)) {
                    HomologyOnlySelf.apply(this, skillUseInfo, attacker, attacker.getName());
                }
            } else if (skillUseInfo.getType() == SkillType.月神祈福) {
                Bless.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker);
                LunaBless.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.终焉之兆) {
                ReturnToHandAndDelay.apply(this, skillUseInfo.getSkill(), attacker, defender, 2, 1);
                Summon.apply(this, skillUseInfo, attacker, SummonType.Normal, 1, "终焉使魔");
            } else if (skillUseInfo.getType() == SkillType.月之召唤) {
                SummonWhenAttack.apply(this, skillUseInfo, attacker, 1, false, "暗月");
            } else if (skillUseInfo.getType() == SkillType.逐光 || skillUseInfo.getType() == SkillType.杀手回梦) {
                ReturnToHandAndDelay.apply(this, skillUseInfo.getSkill(), attacker, defender, 2, 2);
            } else if (skillUseInfo.getType() == SkillType.特殊体质) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 1, "真·预言之神", "谎言之神", "炎魔",
                        "大毒汁怪", "大毒汁之王", "巨大毒汁怪", "死亡天使");
            } else if (skillUseInfo.getType() == SkillType.星座能量热情) {
                Bless.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.觉醒月神降临) {
                if (attacker.getOwner().getField().getAliveCards().size() >= 5) {
                    LunaBless.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker);
                }
                if (attacker.getOwner().getField().getAliveCards().size() < 5) {
                    Bless.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, attacker);
                }
            } else if (skillUseInfo.getType() == SkillType.月之暗面) {
                SummonWhenAttack.apply(this, skillUseInfo, attacker, 1, false, "网页版暗之月蚀");
            } else if (skillUseInfo.getType() == SkillType.嗜血潜能 || skillUseInfo.getType() == SkillType.快速成长) {
                Erode.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender, null, false);
            } else if (skillUseInfo.getType() == SkillType.黑暗之门) {
                SummonOpponent.apply(this, skillUseInfo, attacker, SummonType.Normal, 1, "黑暗眷属");
            } else if (skillUseInfo.getType() == SkillType.死亡链接) {
                SoulLink.apply(this, skillUseInfo, attacker, defender, 5, 3);
            } else if (skillUseInfo.getType() == SkillType.死亡锁链) {
                SoulLink.apply(this, skillUseInfo, attacker, defender, 3, 3);
            } else if (skillUseInfo.getType() == SkillType.燎原之势) {
                SoulLink.apply(this, skillUseInfo, attacker, defender, 2, 3);
            } else if (skillUseInfo.getType() == SkillType.审判之印) {
                SoulChains.apply(this, skillUseInfo, attacker, defender, 4, 2);
            } else if (skillUseInfo.getType() == SkillType.奇袭) {
                if (defender.getOwner().getHand().size() >= 1) {
                    Transport.apply(this, skillUseInfo.getSkill(), attacker, defender);
                }
                if (defender.getOwner().getHand().size() < 1) {
                    AllSpeedUp.apply(skillUseInfo, this, attacker);
                }
            } else if (skillUseInfo.getType() == SkillType.陷阵) {
                ReturnCard.apply(this, skillUseInfo.getSkill(), attacker, defender, 3);
            } else if (skillUseInfo.getType() == SkillType.蓄势待发) {
                Supplication.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
                AllSpeedUp.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.卷土重来) {
                AllDelay.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.风势) {
                AllSpeedUp.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker);
                AllDelay.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.土豪卡组) {
                AddCard.apply(this, skillUseInfo, attacker, SummonType.Summoning, 2,
                        "爆弹强袭", "九霄龙吟", "炼狱清算者", "幻影剑魔", "熊猫教父", "漆黑魔导士",
                        "科学家·变异", "碧海绯樱", "酒吞童子", "白骨夫人", "黑白无常", "大天狗", "妲己", "雪女", "牛魔王",
                        "八岐大蛇", "金角银角", "终焉使者", "魅惑魔女", "原素曜灵", "幻镜魔导", "小栗丸", "魔幻神杯", "烈焰凤凰",
                        "盗宝松鼠");
            } else if (skillUseInfo.getType() == SkillType.龙城之志) {
                SummonWhenAttack.apply(this, skillUseInfo, attacker, 1, false, "龙城义士");
            } else if (skillUseInfo.getType() == SkillType.风暴之力) {
                SummonMult.apply(this, skillUseInfo, attacker, 1, false, "风眼", "雷云");
            } else if (skillUseInfo.getType() == SkillType.能量汇集) {
                AddSkillOpponent.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill(), 1, defender, 2);
            } else if (skillUseInfo.getType() == SkillType.圣火) {
                RedGun.apply(skillUseInfo, this, attacker, defender, 3);
                HolyFire.apply(skillUseInfo.getSkill(), this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.冲锋之令) {
                Supplication.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
                Horn.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.死亡恐惧) {
                Insane.apply(skillUseInfo, this, attacker, defender, 5, 100);
            } else if (skillUseInfo.getType() == SkillType.灵魂支配) {
                SoulControl.apply(this, skillUseInfo, attacker, defender);
                Revive.apply(this, skillUseInfo, attacker);
            } else if (skillUseInfo.getType() == SkillType.冰刃) {
                IceTouch.apply(skillUseInfo, this, attacker, defender, 1,100);
                IceTouch.apply(skillUseInfo, this, attacker, defender, 1,100);
                IceTouch.apply(skillUseInfo, this, attacker, defender, 1,100);
            } else if (skillUseInfo.getType() == SkillType.逆转之风) {
                Transport.apply(this, skillUseInfo.getSkill(), attacker, defender);
                RegressionSoul.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.兵粮寸断) {
                SoulChains.apply(this, skillUseInfo, attacker, defender, 3, 1);
            } else if (skillUseInfo.getType() == SkillType.肝胆俱裂) {
                Asthenia.apply(this, skillUseInfo, attacker, defender, 1, 1);
                SoulCrash.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.虚实) {
                ReturnToHandAndDelay.applySelf(this, skillUseInfo.getSkill(), attacker, -2, 1);
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 2,
                        "五子良将·徐晃", "五子良将·于禁", "五子良将·张辽", "五子良将·乐进", "五子良将·张合");
            } else if (skillUseInfo.getType() == SkillType.极寒冲击) {
                IceMagic.apply(skillUseInfo, this, attacker, defender, -1, 50, (40 + skillUseInfo.getSkill().getLevel() * 20) * defender.getField().getAliveCards().size());
            } else if (skillUseInfo.getType() == SkillType.天神下凡) {
                ThunderStrike.apply(skillUseInfo, this, attacker, defender, -1,100);
            } else if (skillUseInfo.getType() == SkillType.百发百中) {
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 2);
                HolyFire.apply(skillUseInfo.getSkill(), this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.疯长的植物) {
                Summon.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, SummonType.Random, 1,
                        "蒲公英仙子", "叶莲河童", "尖啸曼陀罗", "百花女神");
            } else if (skillUseInfo.getType() == SkillType.封魔神剑 || skillUseInfo.getType() == SkillType.对决 || skillUseInfo.getType() == SkillType.黑暗侵袭) {
                SealMagic.apply(skillUseInfo, this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.天道承负) {
                LunaTouch.apply(skillUseInfo.getSkill(), this, attacker);
                LunaTouch.apply(skillUseInfo.getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.常胜) {
                Supplication.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
                AllSpeedUp.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.血之眷顾) {
                Homology.apply(this, skillUseInfo, attacker, "德古拉");
            } else if (skillUseInfo.getType() == SkillType.意外的挖掘) {
                SummonOpponent.apply(this, skillUseInfo, attacker, SummonType.Normal, 2, "钻石巨石像", "钻石巨石像");
            } else if (skillUseInfo.getType() == SkillType.快速甬道) {
                SpeedUp.apply(skillUseInfo, this, attacker);
                HandCardAddSkillDelay.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill());
            } else if (skillUseInfo.getType() == SkillType.牢狱异火) {
                BurningFlame.apply(skillUseInfo, this, attacker, defender, -1);
                HolyFire.apply(skillUseInfo.getSkill(), this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.复仇者) {
                HeavenWrath.apply(this, skillUseInfo.getSkill(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.蚀月军团) {
                LunarEclipse.apply(this, skillUseInfo, attacker, defender, -1, 1);
            } else if (skillUseInfo.getType() == SkillType.死亡之舞) {
                if (defender.getField().getAliveCards().size() >= 5) {
                    Insane.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, -1, 100);
                }
                if (defender.getField().getAliveCards().size() < 5) {
                    UnderworldCall.apply(this, skillUseInfo.getAttachedUseInfo2().getSkill(), attacker, defender, 2);
                }
            } else if (skillUseInfo.getType() == SkillType.仁厚) {
                Supplication.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.灵魂试炼) {
                HandCardBuffOpponent.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender, SkillEffectType.ATTACK_CHANGE, -1);
                HalfSilence.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.绝尘) {
                SoulChains.apply(this, skillUseInfo, attacker, defender, 4, 3);
            } else if (skillUseInfo.getType() == SkillType.疾驰) {
                if (!FailureSkillUseInfoList.explode(this, attacker, defender)) {
                    Homology.apply(this, skillUseInfo, attacker, "爪黄飞电·幻影");
                }
            } else if (skillUseInfo.getType() == SkillType.牵丝诡术) {
                if (!FailureSkillUseInfoList.explode(this, attacker, defender)) {
                    Homology.apply(this, skillUseInfo, attacker, "魔幻人偶师");
                }
            } else if (skillUseInfo.getType() == SkillType.放逐之刃) {
                ThunderStrike.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, 3,75);
            } else if (skillUseInfo.getType() == SkillType.星决) {
                if (attacker.getOwner().getGrave().size() > 2) {
                    RegressionSoul.apply(this, skillUseInfo, attacker, defender);
                }
                if (attacker.getOwner().getGrave().size() < 2) {
                    Revive.apply(this, skillUseInfo, attacker);
                }
            } else if (skillUseInfo.getType() == SkillType.花果山美猴王) {
                if (attacker.getOwner().getField().size() < defender.getField().size()) {
                    SummonWhenAttack.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, 1, false, "猴子猴孙");
                } else {
                    SealMagic.apply(skillUseInfo, this, attacker, defender, 1);
                }
            } else if (skillUseInfo.getType() == SkillType.暗影奇袭 || skillUseInfo.getType() == SkillType.狩猎) {
                SealMagicLowAttack.apply(skillUseInfo, this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.天命判决) {
                GreatFireMagic.apply(skillUseInfo.getSkill(), this, attacker, defender, 3, true);
            } else if (skillUseInfo.getType() == SkillType.神谴) {
                RedGun.apply(skillUseInfo, this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.邪影智蚀) {
                if (!FailureSkillUseInfoList.explode(this, attacker, defender)) {
                    Homology.apply(this, skillUseInfo, attacker, "易命之昭");
                }
            } else if (skillUseInfo.getType() == SkillType.守望之力) {
                WatchkeepingPower.apply(this, skillUseInfo, attacker, defender, "冥狱守望者");
            } else if (skillUseInfo.getType() == SkillType.灵魂锁链) {
                SoulLink.apply(this, skillUseInfo, attacker, defender, 2, 2);
            } else if (skillUseInfo.getType() == SkillType.急救) {
                ReviveMultiple.apply(this, skillUseInfo, attacker);
            } else if (skillUseInfo.getType() == SkillType.噬主) {
                CounterBite.apply(skillUseInfo, this, attacker);
                Curse.apply(this, skillUseInfo.getSkill(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.权倾朝野) {
                if (attacker.getOwner().getHP() >= attacker.getOwner().getMaxHP() * 0.5) {
                    ContinuousFire.apply(this, skillUseInfo, attacker, defender);
                } else {
                    ContinuousFire.applyHealHero(this, skillUseInfo, attacker, defender);
                }
            } else if (skillUseInfo.getType() == SkillType.倾城) {
                NewBorn.apply(this, skillUseInfo, attacker, defender, 1);
                Bless.apply(skillUseInfo.getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.一夫当关) {
                if (defender.getField().getAliveCards().size() <= 3) {
                    OneDelay.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender);
                }
            } else if (skillUseInfo.getType() == SkillType.三昧真火) {
                ThunderStrike.apply(skillUseInfo, this, attacker, defender, 4,75);
            } else if (skillUseInfo.getType() == SkillType.名重天下 || skillUseInfo.getType() == SkillType.画意相通 || skillUseInfo.getType() == SkillType.诅咒之刃) {
                AddOpponentFiledCardSkill.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill(), defender);
            } else if (skillUseInfo.getType() == SkillType.知人善任) {
                AddFiledCardMultSkill.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill()
                        , skillUseInfo.getAttachedUseInfo2().getAttachedUseInfo1().getSkill(), skillUseInfo.getAttachedUseInfo2().getAttachedUseInfo2().getSkill());
            } else if (skillUseInfo.getType() == SkillType.乱箭破敌) {
                UnderworldCall.apply(this, skillUseInfo.getSkill(), attacker, defender, 7);
            } else if (skillUseInfo.getType() == SkillType.古神的低语 || skillUseInfo.getType() == SkillType.深渊之力) {
                Ancient.apply(this, skillUseInfo, attacker, defender, 3, 1);
            } else if (skillUseInfo.getType() == SkillType.古神的召唤) {
                AncientSummon.apply(this, skillUseInfo, attacker, defender, 5, 2);
            } else if (skillUseInfo.getType() == SkillType.深潜之力) {
                FireMagicHealHero.apply(skillUseInfo.getSkill(), this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.目标锁定) {
                Asthenia.apply(this, skillUseInfo, attacker, defender, 1, 2);
            } else if (skillUseInfo.getType() == SkillType.追风利刃) {
                SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, attacker, defender, 2);
            } else if (skillUseInfo.getType() == SkillType.风力失控) {
                AllDelayDouble.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.乱流) {
                Turbulence.apply(skillUseInfo.getSkill(), this, attacker, defender, 35, 35);
            } else if (skillUseInfo.getType() == SkillType.神鬼之术) {
                if (defender.getGrave().size() >= 2) {
                    MagicMark.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender, -1);
                } else {
                    Grudge.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, defender, 1);
                }
                ThunderStrike.apply(skillUseInfo, this, attacker, defender, 5,90);
            } else if (skillUseInfo.getType() == SkillType.黄天太平) {
                if (attacker.getOwner().getGrave().size() >= 1) {
                    RegressionSoul.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
                }
                if (attacker.getOwner().getGrave().size() < 1) {
                    Horn.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker);
                }
            } else if (skillUseInfo.getType() == SkillType.黑之诅咒) {
                DeathMarkMult.apply(this, skillUseInfo, attacker, defender, 3, 1);
            } else if (skillUseInfo.getType() == SkillType.恶龙诅咒) {
                DeathMarkMult.apply(this, skillUseInfo, attacker, defender, 5, 1);
            } else if (skillUseInfo.getType() == SkillType.毒物蔓延) {
                PoisonMagic.apply(skillUseInfo, this, attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.灵魂称量) {
                GreatFireMagic.apply(skillUseInfo.getSkill(), this, attacker, defender, 2, false);
                RegressionSoul.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.琴音共鸣) {
                Spread.apply(this, skillUseInfo, attacker, defender, 3, 6);
            } else if (skillUseInfo.getType() == SkillType.冥界之力) {
                SoulChains.apply(this, skillUseInfo, attacker, defender, 4, 3);
            } else if (skillUseInfo.getType() == SkillType.绝望之锁) {
                SoulLink.apply(this, skillUseInfo, attacker, defender, 10, 3);
            } else if (skillUseInfo.getType() == SkillType.守如山疾) {
                AllSpeedUp.apply(skillUseInfo, this, attacker);
            } else if (skillUseInfo.getType() == SkillType.弱者退散) {
                ReturnCardAndDelay.apply(this, skillUseInfo.getSkill(), attacker, defender, 2, 1);
            } else if (skillUseInfo.getType() == SkillType.我要打十个) {
                AddSkillOpponentOrder.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill(), skillUseInfo.getAttachedUseInfo2().getSkill(),
                        null, defender);
            } else if (skillUseInfo.getType() == SkillType.全体摧毁) {
                Destroy.apply(this, skillUseInfo.getSkill(), attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.觉醒放逐) {
                if (defender.getField().getAliveCards().size() > attacker.getOwner().getField().getAliveCards().size()) {
                    ReturnToHandAndDelay.apply(this, skillUseInfo.getSkill(), attacker, defender, 1, 1);
                }
                if (defender.getField().getAliveCards().size() <= attacker.getOwner().getField().getAliveCards().size()) {
                    Petrifaction.apply(skillUseInfo, this, attacker, defender, 60);
                }
            } else if (skillUseInfo.getType() == SkillType.自然之力) {
                Curse.apply(this, skillUseInfo.getSkill(), attacker, defender);
                Bless.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker);
            } else if (skillUseInfo.getType() == SkillType.寒霜之心) {
                IceTouch.apply(skillUseInfo, this, attacker, defender, 1,100);
            } else if (skillUseInfo.getType() == SkillType.棋布星罗 || skillUseInfo.getType() == SkillType.轮换) {
                ScatterHereAndThere.apply(skillUseInfo, this, attacker);
            } else if (skillUseInfo.getType() == SkillType.星夜之翼) {
                Supplication.apply(this, skillUseInfo, attacker, defender);
                AllSpeedUp.apply(skillUseInfo, this, attacker);
            } else if (skillUseInfo.getType() == SkillType.战争号角) {
                Supplication.apply(this, skillUseInfo, attacker, defender);
                Horn.apply(skillUseInfo, this, attacker);
            } else if (skillUseInfo.getType() == SkillType.堕落之印) {
                Deformation.apply(this, skillUseInfo, attacker, "堕天使");
            } else if (skillUseInfo.getType() == SkillType.恶魔之门) {
                Summon.apply(this, skillUseInfo, attacker, SummonType.Random, 2,
                        "巨斧战将", "亡灵支配者", "恐惧梦魇", "夜行神龙", "无头死骑", "缚魂者");
            } else if (skillUseInfo.getType() == SkillType.厄运缠身) {
                Doom.apply(this, skillUseInfo, attacker, defender, 5, 4);
            } else if (skillUseInfo.getType() == SkillType.厄运降临) {
                Doom.apply(this, skillUseInfo, attacker, defender, 3, 3);
            } else if (skillUseInfo.getType() == SkillType.断绝之力) {
                Doom.apply(this, skillUseInfo, attacker, defender, 2, 3);
            } else if (skillUseInfo.getType() == SkillType.压制) {
                Polymorph.apply(this, skillUseInfo, attacker, defender, 3, 1);
            } else if (skillUseInfo.getType() == SkillType.寄生) {
                Offspring.apply(this, skillUseInfo, attacker, defender, 5, 2);
            } else if (skillUseInfo.getType() == SkillType.裂地斩) {
                SealMagic.apply(skillUseInfo, this, attacker, defender, 1);
                SealMagic.apply(skillUseInfo, this, attacker, defender, 1);
                SealMagic.apply(skillUseInfo, this, attacker, defender, 1);
            } else if (skillUseInfo.getType() == SkillType.云雾缭绕) {
                SoulLink.apply(this, skillUseInfo, attacker, defender, 3, 3);
            } else if (skillUseInfo.getType() == SkillType.置换) {
                Substitution.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.蛛网束缚) {
                Asthenia.apply(this, skillUseInfo, attacker, defender, 4, 2);
            } else if (skillUseInfo.getType() == SkillType.引力支配) {
                Petrifaction.apply(skillUseInfo, this, attacker, defender, 100);
            } else if (skillUseInfo.getType() == SkillType.死亡闪烁) {
                Destroy.apply(this, skillUseInfo.getSkill(), attacker, defender, -1);
            } else if (skillUseInfo.getType() == SkillType.笔走龙蛇) {
                SummonMultipleBroilingSoul.apply(this, skillUseInfo, attacker, 4,
                        "天", "地", "人");
            } else if (skillUseInfo.getType() == SkillType.道法自然) {
                TaoistNature.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.紧急召集) {
                EmergencySummonOfIndenture.apply(this, skillUseInfo, attacker);
            } else if (skillUseInfo.getType() == SkillType.压迫力) {
                AddFiledCardMultSkill.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill()
                        , null, null);
            } else if (skillUseInfo.getType() == SkillType.上挑) {
                TurbulenceOppent.apply(skillUseInfo.getSkill(), this, attacker, defender, 30, 30);
            } else if (skillUseInfo.getType() == SkillType.替身术) {
                HomologyOnlySelf.apply(this, skillUseInfo, attacker, attacker.getName());
            } else if (skillUseInfo.getType() == SkillType.觉醒孤胆英雄) {
                if (defender.getField().getAliveCards().size() >= 3) {
                    ReturnCardAndDelay.apply(this, skillUseInfo.getAttachedUseInfo2().getSkill(), attacker, defender, 1, 1);
                }
                if (defender.getField().getAliveCards().size() < 3) {
                    GreatFireMagic.apply(skillUseInfo.getSkill(), this, attacker, defender, 1, false);
                }
            } else if (skillUseInfo.getType() == SkillType.百骑袭营) {
                Supplication.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.绽放烟花) {
                PolymorphBroilingSoul.apply(this, skillUseInfo, attacker, defender, 2, 1);
            } else if (skillUseInfo.getType() == SkillType.画境相通) {
                HomologyMult.apply(this, skillUseInfo, attacker, "汉宫春晓图");
            } else if (skillUseInfo.getType() == SkillType.英雄冢) {
                Ancient.apply(this, skillUseInfo, attacker, defender, 1, 1);
            } else if (skillUseInfo.getType() == SkillType.氛氲馥郁) {
                WitheringWord.apply(skillUseInfo, this, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.凤凰于飞) {
                if (defender.getField().getAliveCards().size() >= 4) {
                    Grudge.apply(this, skillUseInfo, attacker, defender, 2);
                }
            } else if (skillUseInfo.getType() == SkillType.御龙在天) {
                HomologyMult.apply(this, skillUseInfo, attacker, "九霄龙吟");
            } else if (skillUseInfo.getType() == SkillType.幽思) {
                AddFiledCardMultSkill.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill()
                        , null, null);
            } else if (skillUseInfo.getType() == SkillType.黑暗魔咒) {
                PuppetSummon.apply(this, skillUseInfo, attacker, defender, 4, 3);
            } else if (skillUseInfo.getType() == SkillType.长情) {
                Obsession.apply(this, skillUseInfo, attacker, 3, 3);
                HandCardAddOneSkill.apply(this, skillUseInfo, attacker, skillUseInfo.getAttachedUseInfo1().getSkill());
            } else if (skillUseInfo.getType() == SkillType.深情) {
                Obsession.apply(this, skillUseInfo, attacker, 3, 3);
            } else if (skillUseInfo.getType() == SkillType.幻境) {
                if(defender.getField().getAliveCards().size()>attacker.getOwner().getField().getAliveCards().size()){
                    Asthenia.apply(this, skillUseInfo, attacker, defender, 1, 2);
                }
                if(defender.getField().getAliveCards().size()<=attacker.getOwner().getField().getAliveCards().size()){
                    Supplication.apply(this, skillUseInfo, attacker, defender);
                }
            } else if (skillUseInfo.getType() == SkillType.毁灭之力) {
                HalfSilence.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defender);
                UnderworldCall.apply(this, skillUseInfo.getSkill(), attacker, defender, 4);
            }
        }
        if (!attacker.isDead() && status == 0) {
            for (SkillUseInfo skillUseInfo : attacker.getUsableNormalSkills()) {
                if (skillUseInfo.getType() == SkillType.连续魔法 || skillUseInfo.getType() == SkillType.黄天当立 || skillUseInfo.getType() == SkillType.连奏
                        || skillUseInfo.getType() == SkillType.神性爆发 || skillUseInfo.getType() == SkillType.时光迁跃
                        || skillUseInfo.getType() == SkillType.我们生命中的时光
                        || skillUseInfo.getType() == SkillType.法天象地 || skillUseInfo.getType() == SkillType.黄天太平
                        || skillUseInfo.getType() == SkillType.无限剑制) {
                    if (doomSkillMessgae(message, skillUseInfo, doomImpact)) {
                        break;
                    }
                    ContinuousMagic.apply(this, skillUseInfo, attacker, defender);
                    break;
                } else if (skillUseInfo.getType() == SkillType.莫测) {
                    if (doomSkillMessgae(message, skillUseInfo, doomImpact)) {
                        break;
                    }
                    ContinuousMagic.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, defender);
                    break;
                }
            }
        }
        if (!attacker.isDead() && !attacker.isSilent() && !attacker.justRevived()) {
            {
                RuneInfo rune = attacker.getOwner().getActiveRuneOf(RuneData.飞岩);
                if (rune != null) {
                    Snipe.apply(rune.getSkillUseInfo(), rune.getSkill(), this, attacker, defender, 1);
                }
            }
        }
    }

    //触发厄运技能
    public boolean doomSkillMessgae(String message, SkillUseInfo skillUseInfo, int impact) {
        if (this.getStage().getRandomizer().roll100(impact)) {
            String showMessage = message + "," + skillUseInfo.getSkill().getName() + "发动失败";
            this.getStage().getUI().showMessage(showMessage);
            return true;
        }
        return false;
    }

    public void resolvePostAttackSkills(CardInfo attacker, Player defender) {

    }

    public void resolveCounterAttackSkills(CardInfo attacker, CardInfo defender, Skill attackSkill,
                                           OnAttackBlockingResult result, OnDamagedResult damagedResult) throws HeroDieSignal {
        if (!FailureSkillUseInfoList.explode(this, defender, attacker.getOwner())) {
            if (isPhysicalAttackSkill(attackSkill) && damagedResult.actualDamage > 0) {
                for (SkillUseInfo skillUseInfo : defender.getUsableNormalSkills()) {
                    if (skillUseInfo.getType() == SkillType.反击) {
                        CounterAttack.apply(skillUseInfo.getSkill(), this, attacker, defender, result.getDamage());
                    } else if (skillUseInfo.getType() == SkillType.盾刺) {
                        Spike.apply(skillUseInfo.getSkill(), this, attacker, defender, attackSkill, result.getDamage());
                    } else if (skillUseInfo.getType() == SkillType.荆棘术 || skillUseInfo.getType() == SkillType.刚烈 || skillUseInfo.getType() == SkillType.荆棘刃甲) {
                        Spike.apply(skillUseInfo.getSkill(), this, attacker, defender, attackSkill, result.getDamage());
                    } else if (skillUseInfo.getType() == SkillType.森之领域) {
                        Spike.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, attackSkill, result.getDamage());
                    } else if (skillUseInfo.getType() == SkillType.大地之盾 || skillUseInfo.getType() == SkillType.寒冰之盾 || skillUseInfo.getType() == SkillType.禁区之王
                            || skillUseInfo.getType() == SkillType.清泉之盾 || skillUseInfo.getType() == SkillType.大地庇护) {
                        EarthShield.apply(skillUseInfo, this, attacker, defender);
                    } else if (skillUseInfo.getType() == SkillType.物理反弹 || skillUseInfo.getType() == SkillType.武形破剑击 || skillUseInfo.getType() == SkillType.反击屏障
                            || skillUseInfo.getType() == SkillType.星座能量直感 || skillUseInfo.getType() == SkillType.应激 || skillUseInfo.getType() == SkillType.反击阵列
                            || skillUseInfo.getType() == SkillType.诡谲之匿) {
                        PhysicalReflection.apply(skillUseInfo.getSkill(), this, attacker, defender, damagedResult.actualDamage);
                    } else if (skillUseInfo.getType() == SkillType.森之护佑) {
                        PhysicalReflection.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, damagedResult.actualDamage);
                    } else if (skillUseInfo.getType() == SkillType.天丛云) {
                        PhysicalReflection.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, attacker, defender, damagedResult.actualDamage);
                    } else if (skillUseInfo.getType() == SkillType.一闪) {
                        EarthShield.apply(skillUseInfo, this, attacker, defender);
                        PhysicalReflection.apply(skillUseInfo.getSkill(), this, attacker, defender, damagedResult.actualDamage);
                    } else if (skillUseInfo.getType() == SkillType.魔神之甲) {
                        Spike.apply(skillUseInfo.getSkill(), this, attacker, defender, attackSkill, result.getDamage());
                    } else if (skillUseInfo.getType() == SkillType.燃烧) {
                        Burning.apply(skillUseInfo, this, attacker, defender);
                    } else if (skillUseInfo.getType() == SkillType.护身烈焰 || skillUseInfo.getType() == SkillType.火焰之躯) {
                        Burning.apply(skillUseInfo, this, attacker, defender);
                    } else if (skillUseInfo.getType() == SkillType.烈焰之肤) {
                        Burning.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender);
                    } else if (skillUseInfo.getType() == SkillType.邪灵汲取 || skillUseInfo.getType() == SkillType.战意侵蚀) {
                        EnergyDrain.apply(skillUseInfo, this, attacker, defender, result, damagedResult);
                    } else if (skillUseInfo.getType() == SkillType.恶灵汲取 || skillUseInfo.getType() == SkillType.灵魂汲取 || skillUseInfo.getType() == SkillType.海渊之力) {
                        LifeDrain.apply(skillUseInfo, this, attacker, defender, result, damagedResult);
                    } else if (skillUseInfo.getType() == SkillType.星座能量坚韧 || skillUseInfo.getType() == SkillType.灵木之体 || skillUseInfo.getType() == SkillType.圣焰附体 || skillUseInfo.getType() == SkillType.真龙之魄) {
                        EnergyDrain.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, result, damagedResult);
                        LifeDrain.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender, result, damagedResult);
                    } else if (skillUseInfo.getType() == SkillType.熔岩护甲) {
                        EnergyDrain.apply(skillUseInfo.getAttachedUseInfo1(), this, attacker, defender, result, damagedResult);
                    } else if (skillUseInfo.getType() == SkillType.肉食者 || skillUseInfo.getType() == SkillType.快速成长) {
                        LifeDrain.apply(skillUseInfo.getAttachedUseInfo2(), this, attacker, defender, result, damagedResult);
                    } else if (skillUseInfo.getType() == SkillType.不灭原核) {
                        EnergyDrain.apply(skillUseInfo, this, attacker, defender, result, damagedResult);
                    } else if (skillUseInfo.getType() == SkillType.被插出五星) {
                        CounterSummon.apply(this, defender, skillUseInfo.getSkill(), 5);
                    } else if (skillUseInfo.getType() == SkillType.反射装甲 || skillUseInfo.getType() == SkillType.魔神加护 || skillUseInfo.getType() == SkillType.不息之风) {
                        ReflectionArmor.apply(skillUseInfo.getSkill(), this, attacker, defender, attackSkill, damagedResult.actualDamage);
                    } else if (skillUseInfo.getType() == SkillType.LETITGO || skillUseInfo.getType() == SkillType.击溃 || skillUseInfo.getType() == SkillType.高位逼抢) {
                        ReflectionArmor.apply(skillUseInfo.getSkill().getAttachedSkill2(), this, attacker, defender, attackSkill, damagedResult.actualDamage);
                    } else if (skillUseInfo.getType() == SkillType.逆转之刃) {
                        BladeOfReversal.apply(this, skillUseInfo, attacker, defender, 1, 1);
                    } else if (skillUseInfo.getType() == SkillType.失乐园) {
                        ParadiseLost.apply(this, skillUseInfo, attacker, defender);
                    } else if (skillUseInfo.getType() == SkillType.永劫深渊 || skillUseInfo.getType() == SkillType.辰星之子) {
                        PercentGetHp.apply(skillUseInfo.getSkill(), this, attacker);
                    }
                }
                if (!defender.isSilent() && !defender.justRevived()) {
                    {
                        RuneInfo rune = defender.getOwner().getActiveRuneOf(RuneData.雷盾);
                        if (rune != null && defender.getRuneActive()) {
                            Spike.apply(rune.getSkill(), this, attacker, defender, attackSkill, result.getDamage());
                        }
                    }
                    {
                        RuneInfo rune = defender.getOwner().getActiveRuneOf(RuneData.漩涡);
                        if (rune != null && defender.getRuneActive()) {
                            CounterAttack.apply(rune.getSkill(), this, attacker, defender, result.getDamage());
                        }
                    }
                    {
                        RuneInfo rune = defender.getOwner().getActiveRuneOf(RuneData.升阳);
                        if (rune != null && !defender.justRevived()) {
                            ReflectionArmor.apply(rune.getSkill(), this, attacker, defender, attackSkill, damagedResult.actualDamage);
                        }
                    }
                    if (!defender.isDead()) {
                        for (SkillUseInfo skillUseInfo : defender.getUsableNormalSkills()) {
                            if (skillUseInfo.getType() == SkillType.狂热 || skillUseInfo.getType() == SkillType.狂热之血 || skillUseInfo.getType() == SkillType.兽人之血) {
                                Zealot.apply(skillUseInfo, this, attacker, defender, result);
                            }
                        }
                        RuneInfo rune = defender.getOwner().getActiveRuneOf(RuneData.怒涛);
                        if (rune != null && defender.getRuneActive()) {
                            Zealot.apply(rune.getSkillUseInfo(), this, attacker, defender, result);
                        }
                    }
                }
                for (SkillUseInfo skillUseInfo : defender.getUsableNormalSkills()) {
                    if (skillUseInfo.getType() == SkillType.逃跑 || skillUseInfo.getType() == SkillType.强链原核 || skillUseInfo.getType() == SkillType.撤退
                            || skillUseInfo.getType() == SkillType.突围 || skillUseInfo.getType() == SkillType.迷影 || skillUseInfo.getType() == SkillType.马术
                            || skillUseInfo.getType() == SkillType.月之潮汐 || skillUseInfo.getType() == SkillType.诡谲之匿) {
                        Flee.apply(skillUseInfo.getSkill(), this, attacker, defender, damagedResult.actualDamage);
                    }
                }
            }
        }
    }

    //返回int类型，0表示不反弹，1表述反弹并且不受伤害，2表示反弹受伤害
    public int resolveMagicEchoSkill(EntityInfo attacker, CardInfo defender, Skill cardSkill) {
        if (!FailureSkillUseInfoList.exploded(this, defender, attacker.getOwner())) {
            if (attacker instanceof CardInfo) {
                CardInfo cardInfo = (CardInfo) attacker;
                if (cardInfo.isBoss()) {
                    return 0;
                }
            }
            if (defender.containsAllSkill(SkillType.奥术回声)) {
                return 1;
            }
        }
        return 0;
    }

    public OnAttackBlockingResult resolveHealBlockingSkills(EntityInfo healer, CardInfo healee, Skill cardSkill) {
        OnAttackBlockingResult result = new OnAttackBlockingResult(true, cardSkill.getImpact());
        if (healee.getStatus().containsStatus(CardStatusType.裂伤) ||
                healee.getStatus().containsStatus(CardStatusType.不屈)) {
            stage.getUI().healBlocked(healer, healee, cardSkill, null);
            result.setAttackable(false);
        }
        return result;
    }

    /**
     * Resolve attack blocking skills.
     *
     * @param attacker    The one that uses the skill to be blocked.
     * @param defender    The one that uses the skill to block the attacker.
     * @param attackSkill The skill that uses to attack the defender.
     * @param damage
     * @return
     * @throws HeroDieSignal
     */
    public OnAttackBlockingResult resolveAttackBlockingSkills(EntityInfo attacker, CardInfo defender,
                                                              Skill attackSkill, int damage) throws HeroDieSignal {
        OnAttackBlockingResult result = new OnAttackBlockingResult(true, 0);
        CardStatus status = attacker.getStatus();
        Unbending.isSkillEscaped(this, attacker, attackSkill, defender, result);
        if (!result.isAttackable()) {
            return result;
        }
        if (isPhysicalAttackSkill(attackSkill)) {
            // Physical attack could be blocked by Dodge or 麻痹, 冰冻, 锁定, 迷惑, 复活 status.
            CardInfo cardAttacker = (CardInfo) attacker;
            result.setDamage(damage);
            if (status.containsStatus(CardStatusType.冰冻) || status.containsStatus(CardStatusType.麻痹)
                    || status.containsStatus(CardStatusType.锁定) || status.containsStatus(CardStatusType.复活)
                    || status.containsStatus(CardStatusType.石化)) {
                stage.getUI().attackBlocked(cardAttacker, defender, attackSkill, null);
                result.setAttackable(false);
                return result;
            } else {
                List<CardStatusItem> blindItems = attacker.getStatus().getStatusOf(CardStatusType.致盲);
                if (!blindItems.isEmpty()) {
                    Skill skill = Blind.getDodgeSkill(blindItems);
                    result.setAttackable(!Dodge.apply(skill, this, cardAttacker, defender, result.getDamage()));
                    if (!result.isAttackable()) {
                        return result;
                    }
                }

                if (!FailureSkillUseInfoList.explode(this, defender, attacker.getOwner())) {
                    for (SkillUseInfo blockSkillUseInfo : defender.getUsableNormalSkills()) {
                        if (blockSkillUseInfo.getType() == SkillType.闪避 || blockSkillUseInfo.getType() == SkillType.龙胆 || blockSkillUseInfo.getType() == SkillType.直感 || blockSkillUseInfo.getType() == SkillType.敏捷
                                || blockSkillUseInfo.getType() == SkillType.隐蔽 || blockSkillUseInfo.getType() == SkillType.页游屏息 || blockSkillUseInfo.getType() == SkillType.神衣) {
                            result.setAttackable(!Dodge.apply(blockSkillUseInfo.getSkill(), this, cardAttacker, defender, result.getDamage()));
                            if (!result.isAttackable()) {
                                return result;
                            }
                        } else if (blockSkillUseInfo.getType() == SkillType.隐匿) {
                            result.setAttackable(!Dodge.apply(blockSkillUseInfo.getAttachedUseInfo1().getSkill(), this, cardAttacker, defender, result.getDamage()));
                            if (!result.isAttackable()) {
                                return result;
                            }
                        }
                    }
                    if (!defender.isSilent()) {
                        RuneInfo rune = defender.getOwner().getActiveRuneOf(RuneData.轻灵);
                        if (rune != null && !defender.justRevived()) {
                            result.setAttackable(!Dodge.apply(rune.getSkill(), this, cardAttacker, defender, result.getDamage()));
                            if (!result.isAttackable()) {
                                return result;
                            }
                        }
                    }
                    {
                        for (SkillUseInfo blockSkillUseInfo : defender.getUsableNormalSkills()) {
                            if (blockSkillUseInfo.getType() == SkillType.圣盾 || blockSkillUseInfo.getType() == SkillType.光之守护) {
                                if (resolveStopBlockSkill(blockSkillUseInfo.getSkill(), cardAttacker, defender)) {
                                    result.setAttackable(true);
                                } else
                                    result.setAttackable(HolyShield.apply(blockSkillUseInfo, this, cardAttacker, defender));
                                if (!result.isAttackable()) {
                                    return result;
                                }
                            }
                        }
                        for (SkillUseInfo blockSkillUseInfo : defender.getUsableNormalSkills()) {
                            if (blockSkillUseInfo.getType() == SkillType.无刀取 || blockSkillUseInfo.getType() == SkillType.神圣领域) {
                                if (resolveStopBlockSkill(blockSkillUseInfo.getSkill(), cardAttacker, defender)) {
                                    result.setAttackable(true);
                                } else
                                    result.setAttackable(HolyShield.apply(blockSkillUseInfo, this, cardAttacker, defender));
                                if (!result.isAttackable()) {
                                    return result;
                                }
                            }
                        }
                    }

                    resolveShieldBlockingSkills(cardAttacker, defender, true, result);
                    if (!result.isAttackable()) {
                        return result;
                    }

                    for (SkillUseInfo blockSkillUseInfo : defender.getUsableNormalSkills()) {
                        if (blockSkillUseInfo.getType() == SkillType.冰甲 ||
                                blockSkillUseInfo.getType() == SkillType.魔龙之血 ||
                                blockSkillUseInfo.getType() == SkillType.冰神附体 ||
                                blockSkillUseInfo.getType() == SkillType.神魔之甲 ||
                                blockSkillUseInfo.getType() == SkillType.森之护佑 ||
                                blockSkillUseInfo.getType() == SkillType.寒冰之盾) {
                            result.setDamage(IceArmor.apply(blockSkillUseInfo.getSkill(), this, cardAttacker, defender,
                                    result.getDamage()));
                        } else if (
                                blockSkillUseInfo.getType() == SkillType.酒意) {
                            result.setDamage(IceArmor.apply(blockSkillUseInfo.getAttachedUseInfo1().getSkill(), this, cardAttacker, defender,
                                    result.getDamage()));
                        }
                        if (!result.isAttackable()) {
                            return result;
                        }
                    }
                    if (!defender.isSilent()) {
                        {
                            RuneInfo rune = defender.getOwner().getActiveRuneOf(RuneData.冰封);
                            if (rune != null && !defender.justRevived()) {
                                result.setDamage(IceArmor.apply(rune.getSkill(), this, cardAttacker, defender,
                                        result.getDamage()));
                            }
                            if (!result.isAttackable()) {
                                return result;
                            }
                        }
                        {
                            RuneInfo rune = defender.getOwner().getActiveRuneOf(RuneData.岩壁);
                            if (rune != null && !defender.justRevived()) {
                                result.setDamage(Block.apply(rune.getSkill(), this, cardAttacker, defender, rune,
                                        result.getDamage()));
                            }
                            if (!result.isAttackable()) {
                                return result;
                            }
                        }
                    }

                    for (SkillUseInfo blockSkillUseInfo : defender.getUsableNormalSkills()) {
                        if (blockSkillUseInfo.getType() == SkillType.骑士守护 || blockSkillUseInfo.getType() == SkillType.骑士荣耀 || blockSkillUseInfo.getType() == SkillType.骑士信仰) {
                            result.setDamage(KnightGuardian.apply(this, blockSkillUseInfo.getSkill(), attacker, defender,
                                    attackSkill, result.getDamage()));
                        }
                    }

                    for (SkillUseInfo skillUseInfo : defender.getAllUsableSkills()) {
                        if (skillUseInfo.getType() == SkillType.生命链接 || skillUseInfo.getType() == SkillType.共生) {
                            List<CardInfo> victims = this.getAdjacentCards(defender.getOwner().getField(), defender.getPosition());
                            if (victims.size() <= 1) {
                                break;
                            }
                            this.getStage().getUI().useSkill(defender, victims, skillUseInfo.getSkill(), true);
                            result.setDamage(result.getDamage() / victims.size());
                            for (CardInfo victim : victims) {
                                this.getStage().getUI().attackCard(defender, victim, skillUseInfo.getSkill(), result.getDamage());
                                OnDamagedResult lifeChainResult = this.applyDamage(attacker, victim, skillUseInfo.getSkill(), result.getDamage());
                                if (lifeChainResult.cardDead) {
                                    this.resolveDeathSkills(attacker, victim, attackSkill, lifeChainResult);
                                }
                                if (attacker instanceof CardInfo) {
                                    this.resolvePostAttackSkills((CardInfo) attacker, victim, victim.getOwner(), attackSkill, lifeChainResult.actualDamage);
                                }
                            }
                            result.setAttackable(false);
                            return result;
                        }
                    }
                }
            }
        } else {
            result.setDamage(damage);
            boolean isAttackerDisabled = (status.containsStatus(CardStatusType.冰冻)
                    || status.containsStatus(CardStatusType.锁定)
                    || status.containsStatus(CardStatusType.复活)
                    || status.containsStatus(CardStatusType.石化));
            if (!attackSkill.isDeathSkill() && isAttackerDisabled) {
                // BUGBUG: Why we need go here? Hack 邪灵汲取 here temporarily. //weakenCard need// close
                result.setAttackable(false);
                //死亡的卡牌魔法技能正常发动
                if (attacker instanceof CardInfo) {
                    if (((CardInfo) attacker).isDead()) {
                        result.setAttackable(true);
                    } else if (((CardInfo) attacker).getIsSummon()) {
                        result.setAttackable(true);
                    }
                }
                if (!result.isAttackable()) {
                    stage.getUI().attackBlocked(attacker, defender, attackSkill, null);
                }
            }
            if (result.isAttackable()) {
                if (!FailureSkillUseInfoList.explode(this, defender, attacker.getOwner())) {
                    if (CounterMagic.apply(this, attackSkill, attacker, defender)) {
                        result.setAttackable(false);
                        return result;
                    }
                }

                if (NoEffect.isSkillBlocked(this, attackSkill, attacker, defender)) {
                    result.setAttackable(false);
                    return result;
                }
                if (NoWhenDemon.isSkillBlocked(this, attackSkill, attacker, defender)) {
                    result.setAttackable(false);
                    return result;
                }


                if (!FailureSkillUseInfoList.explode(this, defender, attacker.getOwner())) {
                    for (SkillUseInfo blockSkillUseInfo : defender.getUsableNormalSkills()) {
                        if (blockSkillUseInfo.getType() == SkillType.免疫 || blockSkillUseInfo.getType() == SkillType.结界立场
                                || blockSkillUseInfo.getType() == SkillType.影青龙 || blockSkillUseInfo.getType() == SkillType.龙战于野 || blockSkillUseInfo.getType() == SkillType.魔力泳圈
                                || blockSkillUseInfo.getType() == SkillType.禁区之王 || blockSkillUseInfo.getType() == SkillType.恶龙血脉 || blockSkillUseInfo.getType() == SkillType.不息神盾
                                || blockSkillUseInfo.getType() == SkillType.彻骨之寒 || blockSkillUseInfo.getType() == SkillType.灵能冲击 || blockSkillUseInfo.getType() == SkillType.嗜魔之体
                                || blockSkillUseInfo.getType() == SkillType.魔力抗性 || blockSkillUseInfo.getType() == SkillType.轮回渡厄 || blockSkillUseInfo.getType() == SkillType.明月渡我
                                || blockSkillUseInfo.getType() == SkillType.免疫风行 || blockSkillUseInfo.getType() == SkillType.优雅之姿 || blockSkillUseInfo.getType() == SkillType.神衣
                                || blockSkillUseInfo.getType() == SkillType.复仇之影 || blockSkillUseInfo.getType() == SkillType.死亡之矢 || blockSkillUseInfo.getType() == SkillType.神佑复苏
                                || blockSkillUseInfo.getType() == SkillType.弑魂夺魄 || blockSkillUseInfo.getType() == SkillType.不灭之魂 || blockSkillUseInfo.getType() == SkillType.雷神附体
                                || blockSkillUseInfo.getType() == SkillType.秘术投影 || blockSkillUseInfo.getType() == SkillType.夺命骨镰 || blockSkillUseInfo.getType() == SkillType.风势
                                || blockSkillUseInfo.getType() == SkillType.醉生梦死 || blockSkillUseInfo.getType() == SkillType.魂飞魄散 || blockSkillUseInfo.getType() == SkillType.摄魂之力
                                || blockSkillUseInfo.getType() == SkillType.三位一体 || blockSkillUseInfo.getType() == SkillType.不灭金身 || blockSkillUseInfo.getType() == SkillType.时间扭曲
                                || blockSkillUseInfo.getType() == SkillType.忠肝义胆 || blockSkillUseInfo.getType() == SkillType.异元干扰 || blockSkillUseInfo.getType() == SkillType.金元仙躯
                                || blockSkillUseInfo.getType() == SkillType.迷影森森 || blockSkillUseInfo.getType() == SkillType.魏文帝 || blockSkillUseInfo.getType() == SkillType.归心
                                || blockSkillUseInfo.getType() == SkillType.阴阳术轮回 || blockSkillUseInfo.getType() == SkillType.神赐之躯 || blockSkillUseInfo.getType() == SkillType.魏之恋
                                || blockSkillUseInfo.getType() == SkillType.三界行者 || blockSkillUseInfo.getType() == SkillType.起死回生 || blockSkillUseInfo.getType() == SkillType.乱世枭雄
                                || blockSkillUseInfo.getType() == SkillType.净世破魔 || blockSkillUseInfo.getType() == SkillType.圣剑 || blockSkillUseInfo.getType() == SkillType.流星
                                || blockSkillUseInfo.getType() == SkillType.海滨乐园 || blockSkillUseInfo.getType() == SkillType.奥术之源 || blockSkillUseInfo.getType() == SkillType.逆战光辉
                                || blockSkillUseInfo.getType() == SkillType.神兵天降 || blockSkillUseInfo.getType() == SkillType.沉默领域 || blockSkillUseInfo.getType() == SkillType.贪狼
                                || blockSkillUseInfo.getType() == SkillType.逆战 || blockSkillUseInfo.getType() == SkillType.三昧真火 || blockSkillUseInfo.getType() == SkillType.周旋
                                || blockSkillUseInfo.getType() == SkillType.吴之悌 || blockSkillUseInfo.getType() == SkillType.吴之恋 || blockSkillUseInfo.getType() == SkillType.白驹过隙
                                || blockSkillUseInfo.getType() == SkillType.拉莱耶领域 || blockSkillUseInfo.getType() == SkillType.太平清领书 || blockSkillUseInfo.getType() == SkillType.永生审判
                                || blockSkillUseInfo.getType() == SkillType.逆转之矢 || blockSkillUseInfo.getType() == SkillType.坚不可摧 || blockSkillUseInfo.getType() == SkillType.轮回天生
                                || blockSkillUseInfo.getType() == SkillType.无限剑制 || blockSkillUseInfo.getType() == SkillType.死亡女神 || blockSkillUseInfo.getType() == SkillType.弑神之剑
                                || blockSkillUseInfo.getType() == SkillType.不死金身 || blockSkillUseInfo.getType() == SkillType.七罪 || blockSkillUseInfo.getType() == SkillType.海姆冥界
                                || blockSkillUseInfo.getType() == SkillType.红尘缥缈仙 || blockSkillUseInfo.getType() == SkillType.平沙落雁
                                || blockSkillUseInfo.getType() == SkillType.天官帝君 || blockSkillUseInfo.getType() == SkillType.恒星之力 || blockSkillUseInfo.getType() == SkillType.紫电
                                || blockSkillUseInfo.getType() == SkillType.辞旧迎新 || blockSkillUseInfo.getType() == SkillType.热情似火 || blockSkillUseInfo.getType() == SkillType.入木三分) {
                            if (Immue.isSkillBlocked(this, blockSkillUseInfo.getSkill(), attackSkill, attacker, defender)) {
                                result.setAttackable(false);
                                return result;
                            }
                        }
                        // 神威既包含脱困又包含不动，还有技能既包含不动又抗沉默的，所以需要将if分开
                        if (blockSkillUseInfo.getType() == SkillType.脱困 ||
                                blockSkillUseInfo.getType() == SkillType.神威 ||
                                blockSkillUseInfo.getType() == SkillType.临 ||
                                blockSkillUseInfo.getType() == SkillType.村正 ||
                                blockSkillUseInfo.getType() == SkillType.敏捷 ||
                                blockSkillUseInfo.getType() == SkillType.月之守望 ||
                                blockSkillUseInfo.getType() == SkillType.紫气东来 ||
                                blockSkillUseInfo.getType() == SkillType.冰神附体 ||
                                blockSkillUseInfo.getType() == SkillType.以逸待劳 ||
                                blockSkillUseInfo.getType() == SkillType.不灭原核 ||
                                blockSkillUseInfo.getType() == SkillType.黄天当立 ||
                                blockSkillUseInfo.getType() == SkillType.时光迁跃 ||
                                blockSkillUseInfo.getType() == SkillType.骑士信仰 ||
                                blockSkillUseInfo.getType() == SkillType.灵力魔阵 ||
                                blockSkillUseInfo.getType() == SkillType.破阵弧光 ||
                                blockSkillUseInfo.getType() == SkillType.隐蔽 ||
                                blockSkillUseInfo.getType() == SkillType.女武神之辉 ||
                                blockSkillUseInfo.getType() == SkillType.无冕之王 ||
                                blockSkillUseInfo.getType() == SkillType.再生金蝉 ||
                                blockSkillUseInfo.getType() == SkillType.神之守护 ||
                                blockSkillUseInfo.getType() == SkillType.金蝉脱壳) {
                            if (Escape.isSkillEscaped(this, blockSkillUseInfo.getSkill(), attackSkill, attacker, defender)) {
                                result.setAttackable(false);
                                return result;
                            }
                        }
                        if (blockSkillUseInfo.getType().containsTag(SkillTag.不动)) {
                            if (Immobility.isSkillBlocked(this, blockSkillUseInfo.getSkill(), attackSkill, attacker, defender)) {
                                result.setAttackable(false);
                                return result;
                            }
                        }
                        RuneInfo rune = defender.getOwner().getActiveRuneOf(RuneData.明镜);
                        if (blockSkillUseInfo.getType().containsTag(SkillTag.抗沉默) || rune != null && !defender.isSilent()) {
                            if (attackSkill.getType().containsTag(SkillTag.沉默)) {
                                this.getStage().getUI().useSkill(defender, blockSkillUseInfo.getSkill(), true);
                                this.getStage().getUI().blockSkill(attacker, defender, blockSkillUseInfo.getSkill(), attackSkill);
                                result.setAttackable(false);
                                return result;
                            }
                        }
                    }
                    if (!defender.isSilent()) {
                        {
                            RuneInfo rune = defender.getOwner().getActiveRuneOf(RuneData.鬼步);
                            if (rune != null && !defender.justRevived()) {
                                if (Escape.isSkillEscaped(this, rune.getSkill(), attackSkill, attacker, defender)) {
                                    result.setAttackable(false);
                                    return result;
                                }
                            }
                        }
                        {
                            RuneInfo rune = defender.getOwner().getActiveRuneOf(RuneData.磐石);
                            if (rune != null && !defender.justRevived() && defender.isAlive()) {
                                if (Immobility.isSkillBlocked(this, rune.getSkill(), attackSkill, attacker, defender)) {
                                    result.setAttackable(false);
                                    return result;
                                }
                            }
                        }
                    }
                }
                if (isMagicalSkill(attackSkill) && damage > 0) {
                    // 治疗法术不受魔法印记影响
                    List<CardStatusItem> magicMarkStatusItems = defender.getStatus().getStatusOf(CardStatusType.魔印);
                    int maxDamage = 0;
                    for (CardStatusItem item : magicMarkStatusItems) {
                        Skill magicMarkSkill = item.getCause().getSkill();
                        this.getStage().getUI().useSkill(defender, magicMarkSkill, true);
                        int extraDamage = damage * magicMarkSkill.getImpact() / 100;
                        if (extraDamage > maxDamage) {
                            maxDamage = extraDamage;
                        }
                    }
                    result.setDamage(damage + maxDamage);
                }
                if (!FailureSkillUseInfoList.explode(this, defender, attacker.getOwner())) {
                    for (SkillUseInfo blockSkillUseInfo : defender.getUsableNormalSkills()) {
                        if (blockSkillUseInfo.getType() == SkillType.魔甲 ||
                                blockSkillUseInfo.getType() == SkillType.临 ||
                                blockSkillUseInfo.getType() == SkillType.铁骨衣 ||
                                blockSkillUseInfo.getType() == SkillType.神魔之甲 ||
                                blockSkillUseInfo.getType() == SkillType.体态丰盈 ||
                                blockSkillUseInfo.getType() == SkillType.却魔装甲 ||
                                blockSkillUseInfo.getType() == SkillType.蛇魔之甲 ||
                                blockSkillUseInfo.getType() == SkillType.灵蛇腹甲 ||
                                blockSkillUseInfo.getType() == SkillType.魔力抗性 ||
                                blockSkillUseInfo.getType() == SkillType.老当益壮 ||
                                blockSkillUseInfo.getType() == SkillType.不灭灵体) {
                            result.setDamage(MagicShield.apply(this, blockSkillUseInfo.getSkill(), attacker, defender,
                                    attackSkill, result.getDamage()));
                        } else if (blockSkillUseInfo.getType() == SkillType.护体石肤 || blockSkillUseInfo.getType() == SkillType.波涛护甲) {
                            result.setDamage(MagicShield.apply(this, blockSkillUseInfo.getAttachedUseInfo1().getSkill(), attacker, defender,
                                    attackSkill, result.getDamage()));
                        } else if (blockSkillUseInfo.getType() == SkillType.骑士守护 || blockSkillUseInfo.getType() == SkillType.骑士荣耀 || blockSkillUseInfo.getType() == SkillType.骑士信仰) {
                            result.setDamage(KnightGuardian.apply(this, blockSkillUseInfo.getSkill(), attacker, defender,
                                    attackSkill, result.getDamage()));
                        } else if (blockSkillUseInfo.getType() == SkillType.魔法装甲 || blockSkillUseInfo.getType() == SkillType.金魔装甲 || blockSkillUseInfo.getType() == SkillType.秘银
                                || blockSkillUseInfo.getType() == SkillType.魔法免疫 || blockSkillUseInfo.getType() == SkillType.正义之师) {
                            result.setDamage(MagicArmor.apply(this, blockSkillUseInfo.getSkill(), attacker, defender,
                                    attackSkill, result.getDamage()));
                        }
                        if (!result.isAttackable()) {
                            return result;
                        }
                    }
                    if (!defender.isSilent()) {
                        RuneInfo rune = defender.getOwner().getActiveRuneOf(RuneData.炎甲);
                        if (rune != null && defender.getRuneActive() && !defender.isSilent()) {
                            result.setDamage(MagicShield.apply(this, rune.getSkill(), attacker, defender,
                                    attackSkill, result.getDamage()));
                        }
                        if (!result.isAttackable()) {
                            return result;
                        }
                    }
                }
            }
        }

        return result;
    }

    public void resolveShieldBlockingSkills(CardInfo cardAttacker, CardInfo defender, boolean includeBlocking,
                                            OnAttackBlockingResult result) throws HeroDieSignal {
        if (!defender.isSilent() && includeBlocking) {
            RuneInfo rune = defender.getOwner().getActiveRuneOf(RuneData.止水);
            if (rune != null && defender.getRuneActive()) {
                result.setDamage(WaterArmor.apply(rune.getSkill(), this, cardAttacker, defender, result.getDamage()));
            }
        }
        for (SkillUseInfo blockSkillUseInfo : defender.getAllUsableSkillsInvalidSilence()) {
            if (blockSkillUseInfo.getType() == SkillType.王国之盾) {
                result.setDamage(RacialShield.apply(blockSkillUseInfo.getSkill(), this, cardAttacker,
                        defender, defender, result.getDamage(), Race.HELL));
            }
            if (blockSkillUseInfo.getType() == SkillType.森林之盾) {
                result.setDamage(RacialShield.apply(blockSkillUseInfo.getSkill(), this, cardAttacker,
                        defender, defender, result.getDamage(), Race.SAVAGE));
            }
            if (blockSkillUseInfo.getType() == SkillType.蛮荒之盾) {
                result.setDamage(RacialShield.apply(blockSkillUseInfo.getSkill(), this, cardAttacker,
                        defender, defender, result.getDamage(), Race.KINGDOM));
            }
            if (blockSkillUseInfo.getType() == SkillType.地狱之盾) {
                result.setDamage(RacialShield.apply(blockSkillUseInfo.getSkill(), this, cardAttacker,
                        defender, defender, result.getDamage(), Race.FOREST));
            }
            if (includeBlocking) {
                if (blockSkillUseInfo.getType() == SkillType.格挡 ||
                        blockSkillUseInfo.getType() == SkillType.魔龙之血 ||
                        blockSkillUseInfo.getType() == SkillType.钢铁之肤) {
                    result.setDamage(Block.apply(blockSkillUseInfo.getSkill(), this, cardAttacker, defender,
                            defender, result.getDamage()));
                } else if (
                        blockSkillUseInfo.getType() == SkillType.酒意) {
                    result.setDamage(Block.apply(blockSkillUseInfo.getAttachedUseInfo1().getSkill(), this, cardAttacker, defender,
                            defender, result.getDamage()));
                } else if (blockSkillUseInfo.getType() == SkillType.神亭酣战 || blockSkillUseInfo.getType() == SkillType.烈焰之肤 || blockSkillUseInfo.getType() == SkillType.诅咒铠甲
                        || blockSkillUseInfo.getType() == SkillType.克己奉公) {
                    result.setDamage(Block.apply(blockSkillUseInfo.getAttachedUseInfo2().getSkill(), this, cardAttacker, defender,
                            defender, result.getDamage()));
                } else if (blockSkillUseInfo.getType() == SkillType.金属装甲 || blockSkillUseInfo.getType() == SkillType.酒池肉林 || blockSkillUseInfo.getType() == SkillType.物理免疫
                        || blockSkillUseInfo.getType() == SkillType.兽人之肤 || blockSkillUseInfo.getType() == SkillType.金魔装甲 || blockSkillUseInfo.getType() == SkillType.金刚护体
                        || blockSkillUseInfo.getType() == SkillType.大地庇护 || blockSkillUseInfo.getType() == SkillType.森之领域 || blockSkillUseInfo.getType() == SkillType.物理屏障
                        || blockSkillUseInfo.getType() == SkillType.秘银 || blockSkillUseInfo.getType() == SkillType.正义之师 || blockSkillUseInfo.getType() == SkillType.真龙之魄
                        || blockSkillUseInfo.getType() == SkillType.刀枪不入 || blockSkillUseInfo.getType() == SkillType.失乐园) {
                    result.setDamage(PhysicalArmor.apply(blockSkillUseInfo.getSkill(), this, cardAttacker, defender,
                            result.getDamage()));
                } else if (blockSkillUseInfo.getType() == SkillType.水流护甲 || blockSkillUseInfo.getType() == SkillType.真夏通雨 || blockSkillUseInfo.getType() == SkillType.水流壁
                        || blockSkillUseInfo.getType() == SkillType.传承黯影 || blockSkillUseInfo.getType() == SkillType.回光返照 || blockSkillUseInfo.getType() == SkillType.圣泉护身
                        || blockSkillUseInfo.getType() == SkillType.清泉之盾 || blockSkillUseInfo.getType() == SkillType.魔力泳圈 || blockSkillUseInfo.getType() == SkillType.铁骨衣
                        || blockSkillUseInfo.getType() == SkillType.优雅之姿 || blockSkillUseInfo.getType() == SkillType.忠肝义胆 || blockSkillUseInfo.getType() == SkillType.不灭灵体
                        || blockSkillUseInfo.getType() == SkillType.迷影 || blockSkillUseInfo.getType() == SkillType.马术 || blockSkillUseInfo.getType() == SkillType.御魔长袍) {
                    result.setDamage(WaterArmor.apply(blockSkillUseInfo.getSkill(), this, cardAttacker, defender, result.getDamage()));
                } else if (blockSkillUseInfo.getType() == SkillType.波涛护甲) {
                    result.setDamage(WaterArmor.apply(blockSkillUseInfo.getAttachedUseInfo2().getSkill(), this, cardAttacker, defender, result.getDamage()));
                }
            }
        }
    }

    /**
     * @param killerCard
     * @param deadCard
     * @param cardSkill
     * @return Whether the dead card is revived.
     * @throws HeroDieSignal
     */
    public void resolveDeathSkills(EntityInfo killerCard, CardInfo deadCard, Skill cardSkill, OnDamagedResult result) throws HeroDieSignal {
        if (deadCard.hasDeadOnce()) {
            return;
        }
        // Two scenarios where death skill should be resolved:
        // 1. cardDead
        // 2. unbending triggered
        if (!result.cardDead && !result.unbending) {
            return;
        }
        if (result.cardDead && !result.unbending) {
            deadCard.setDeadOnce(true);
        }
        if (deadCard.isDead()) {
            deadCard.setIsDeathNow(true);
        }

        Player opponent = this.getStage().getOpponent(deadCard.getOwner());
        //位置調整,处理复合型结算时结算错误

        //处理羽扇虎拳
        if (deadCard.isDead()) {
            for (CardInfo attackFiled : opponent.getField().getAliveCards()) {
                if (!FailureSkillUseInfoList.explode(this, attackFiled, deadCard.getOwner())) {
                    for (SkillUseInfo skillUseInfo : attackFiled.getUsableNormalSkills()) {
                        if (skillUseInfo.getType() == SkillType.羽扇虎拳) {
                            PercentagAttackHero.apply(this, skillUseInfo.getSkill(), attackFiled, deadCard);
                        } else if (skillUseInfo.getType() == SkillType.天罡咒 || skillUseInfo.getType() == SkillType.王者之风 || skillUseInfo.getType() == SkillType.恶龙领域) {
                            Curse.apply(this, skillUseInfo.getSkill(), attackFiled, deadCard.getOwner());
                        } else if (skillUseInfo.getType() == SkillType.灰飞烟灭) {
                            Curse.apply(this, skillUseInfo.getAttachedUseInfo1().getSkill(), attackFiled, deadCard.getOwner());
                        }
                    }
                }
            }
        }

        for (SkillUseInfo deadCardSkillUseInfo : deadCard.getAllUsableSkills()) {
            if (deadCardSkillUseInfo.getSkill().isDeathSkill()) {
                if (deadCardSkillUseInfo.getType() == SkillType.烈焰风暴) {
                    FireMagic.apply(deadCardSkillUseInfo.getSkill(), this, deadCard, opponent, -1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.雷暴) {
                    LighteningMagic.apply(deadCardSkillUseInfo, this, deadCard, opponent, -1, 35);
                } else if (deadCardSkillUseInfo.getType() == SkillType.暴风雪) {
                    IceMagic.apply(deadCardSkillUseInfo, this, deadCard, opponent, -1, 30, 0);
                } else if (deadCardSkillUseInfo.getType() == SkillType.毒云) {
                    PoisonMagic.apply(deadCardSkillUseInfo, this, deadCard, opponent, -1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.瘟疫) {
                    Plague.apply(deadCardSkillUseInfo, this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.凋零真言) {
                    Plague.apply(deadCardSkillUseInfo, this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.治疗) {
                    Heal.apply(deadCardSkillUseInfo.getSkill(), this, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.甘霖) {
                    Rainfall.apply(deadCardSkillUseInfo.getSkill(), this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.月神的护佑) {
                    LunaBless.apply(deadCardSkillUseInfo.getSkill(), this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.祈祷) {
                    Pray.apply(deadCardSkillUseInfo.getSkill(), this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.诅咒) {
                    Curse.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.群体削弱) {
                    WeakenAll.apply(this, deadCardSkillUseInfo, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.烈火焚神) {
                    BurningFlame.apply(deadCardSkillUseInfo, this, deadCard, opponent, -1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.陷阱) {
                    Trap.apply(deadCardSkillUseInfo, this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.复活) {
                    Revive.apply(this, deadCardSkillUseInfo, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.摧毁) {
                    Destroy.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent, 1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.圣炎) {
                    HolyFire.apply(deadCardSkillUseInfo.getSkill(), this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.传送) {
                    Transport.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.回魂) {
                    Resurrection.apply(this, deadCardSkillUseInfo, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.归魂) {
                    RegressionSoul.apply(this, deadCardSkillUseInfo, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.时光倒流) {
                    TimeBack.apply(deadCardSkillUseInfo, this, deadCard, deadCard.getOwner(), opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.召唤炎魔) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, "炎魔");
                } else if (deadCardSkillUseInfo.getType() == SkillType.全体阻碍) {
                    AllDelay.apply(deadCardSkillUseInfo, this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.全体加速) {
                    AllSpeedUp.apply(deadCardSkillUseInfo, this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.战争怒吼) {
                    Soften.apply(deadCardSkillUseInfo, this, deadCard, opponent, -1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.时间溯行) {
                    TimeTravel.apply(deadCardSkillUseInfo, this, deadCard.getOwner(), opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.魔法毁灭) {
                    ManaErode.apply(deadCardSkillUseInfo.getSkill(), this, deadCard.getOwner(), opponent, -1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.万兽奔腾) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Random, 2,
                            "麒麟兽", "凤凰", "浮云青鸟", "九头妖蛇", "雷兽", "羽翼化蛇", "神谕火狐",
                            "齐天美猴王", "羽蛇神", "月蚀兽", "逐月恶狼", "逐日凶狼", "月之神兽", "山地兽", "逐月恶狼", "圣翼白虎",
                            "炙阳麒麟", "霜月麒麟", "雪峰飞狐", "九尾妖狐", "魔卡策划", "深海怪鱼", "冰霜巨蛙", "冰焰狼"
                    );
                } else if (deadCardSkillUseInfo.getType() == SkillType.狂野之怒) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Random, 2,
                            "凤凰", "浮云青鸟", "九头妖蛇", "雷兽", "羽翼化蛇", "神谕火狐",
                            "齐天美猴王", "羽蛇神", "月蚀兽", "逐月恶狼", "逐日凶狼", "月之神兽", "山地兽");
                } else if (deadCardSkillUseInfo.getType() == SkillType.武形降临) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Random, 1,
                            "武形火焰尊者", "武形神射尊者", "武形破拳尊者", "武形剑圣", "武形斗圣");
                } else if (deadCardSkillUseInfo.getType() == SkillType.镜像) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.常夏日光 || deadCardSkillUseInfo.getType() == SkillType.碎裂怒吼) {
                    Soften.apply(deadCardSkillUseInfo, this, deadCard, opponent, -1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.灵魂消散) {
                    SoulCrash.apply(deadCardSkillUseInfo, this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.夺魂) {
                    SoulControl.apply(this, deadCardSkillUseInfo, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.再生) {
                    Bless.apply(deadCardSkillUseInfo.getSkill(), this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.弱者溃散) {
                    ReturnCardAndDelay.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent, 2, -1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.地裂) {
                    GiantEarthquakesLandslides.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent, 1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.死无尽华尔兹) {
                    Insane.apply(deadCardSkillUseInfo, this, deadCard, opponent, -1, 100);
                } else if (deadCardSkillUseInfo.getType() == SkillType.死全领域沉默) {
                    Silence.apply(this, deadCardSkillUseInfo, deadCard, opponent, true, false);
                } else if (deadCardSkillUseInfo.getType() == SkillType.山崩) {
                    Crumbling.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent, 1, 1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.全体复活) {
                    ReviveMultiple.apply(this, deadCardSkillUseInfo, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.风暴之主) {
                    AddSelfCard.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Summoning, 1,
                            "风暴主宰");
                } else if (deadCardSkillUseInfo.getType() == SkillType.祈愿) {
                    Supplication.apply(this, deadCardSkillUseInfo, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.溃散) {
                    ReturnCardAndDelay.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, deadCard.getOwner(), 0, 1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.号角) {
                    Horn.apply(deadCardSkillUseInfo, this, deadCard);
                }
            } else {
                // IMPORTANT: Unbending card cannot trigger 自爆
                if (deadCardSkillUseInfo.getType() == SkillType.自爆 && !result.unbending) {
                    Explode.apply(this, deadCardSkillUseInfo.getSkill(), killerCard, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.舍命一击 && !result.unbending) {
                    Explode.apply(this, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill(), killerCard, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.匹夫之勇 && !result.unbending) {
                    Explode.apply(this, deadCardSkillUseInfo.getSkill(), killerCard, deadCard);
                    HandCardAddOneSkill.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getSkill());
                } else if (deadCardSkillUseInfo.getType() == SkillType.燕返 || deadCardSkillUseInfo.getType() == SkillType.上层精灵的挽歌 || deadCardSkillUseInfo.getType() == SkillType.海滨危机
                        || deadCardSkillUseInfo.getType() == SkillType.神性) {
                    if (cardSkill != null && (cardSkill.getType() == SkillType.侵蚀 || cardSkill.getType() == SkillType.页游吞噬
                            || cardSkill.getType() == SkillType.冰巨人吞噬 || cardSkill.getType() == SkillType.嗜血潜能
                            || cardSkill.getType() == SkillType.鬼才 || cardSkill.getType() == SkillType.驱虎吞狼
                            || cardSkill.getType() == SkillType.威慑 || cardSkill.getType() == SkillType.骁袭 || cardSkill.getType() == SkillType.克己奉公
                            || cardSkill.getType() == SkillType.百里 || cardSkill.getType() == SkillType.一夫当关 || cardSkill.getType() == SkillType.奔袭)) {
                        continue;
                    }
                    TsubameGaeshi.apply(deadCardSkillUseInfo, deadCardSkillUseInfo.getSkill(), this, opponent, deadCard, 200);
                } else if (deadCardSkillUseInfo.getType() == SkillType.袈裟斩燕返 && deadCard.isDead()) {
                    if (cardSkill != null && (cardSkill.getType() == SkillType.侵蚀 || cardSkill.getType() == SkillType.页游吞噬
                            || cardSkill.getType() == SkillType.冰巨人吞噬 || cardSkill.getType() == SkillType.嗜血潜能
                            || cardSkill.getType() == SkillType.鬼才 || cardSkill.getType() == SkillType.驱虎吞狼
                            || cardSkill.getType() == SkillType.威慑 || cardSkill.getType() == SkillType.骁袭 || cardSkill.getType() == SkillType.克己奉公
                            || cardSkill.getType() == SkillType.百里 || cardSkill.getType() == SkillType.一夫当关 || cardSkill.getType() == SkillType.奔袭)) {
                        continue;
                    }
                    TsubameGaeshi.apply(deadCardSkillUseInfo, deadCardSkillUseInfo.getSkill(), this, opponent, deadCard, 200);
                } else if (deadCardSkillUseInfo.getType() == SkillType.五子反弹 && deadCard.isDead()) {
                    if (cardSkill != null && (cardSkill.getType() == SkillType.侵蚀 || cardSkill.getType() == SkillType.页游吞噬
                            || cardSkill.getType() == SkillType.冰巨人吞噬 || cardSkill.getType() == SkillType.嗜血潜能
                            || cardSkill.getType() == SkillType.鬼才 || cardSkill.getType() == SkillType.驱虎吞狼
                            || cardSkill.getType() == SkillType.威慑 || cardSkill.getType() == SkillType.骁袭 || cardSkill.getType() == SkillType.克己奉公
                            || cardSkill.getType() == SkillType.百里 || cardSkill.getType() == SkillType.一夫当关 || cardSkill.getType() == SkillType.奔袭)) {
                        continue;
                    }
                    TsubameGaeshi.apply(deadCardSkillUseInfo, deadCardSkillUseInfo.getSkill(), this, opponent, deadCard, 250);
                } else if (deadCardSkillUseInfo.getType() == SkillType.厄运枪狙击 && deadCard.isDead()) {
                    SnipeOneNumber.apply(deadCardSkillUseInfo, deadCardSkillUseInfo.getSkill(), this, deadCard, opponent, 1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.学园骚乱) {
                    Insane.apply(deadCardSkillUseInfo, this, deadCard, opponent, -1, 50);
                } else if (deadCardSkillUseInfo.getType() == SkillType.逆命华舞) {
                    HandCardAddThreeSkill.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill());
                } else if (deadCardSkillUseInfo.getType() == SkillType.公平竞争) {
                    CounterBite.apply(deadCardSkillUseInfo, this, deadCard);
                    Curse.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.三振出局) {
                    GiantEarthquakesLandslides.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent, 1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.格式化) {
                    SoulCrash.apply(deadCardSkillUseInfo, this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.九转秘术) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, "九命猫神·幻影");
                } else if (deadCardSkillUseInfo.getType() == SkillType.九转禁术) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.北海报恩 || deadCardSkillUseInfo.getType() == SkillType.熔岩分身 || deadCardSkillUseInfo.getType() == SkillType.碎裂幻像
                        || deadCardSkillUseInfo.getType() == SkillType.绝境重生 || deadCardSkillUseInfo.getType() == SkillType.不溃 || deadCardSkillUseInfo.getType() == SkillType.刀魂附体) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.樱蝶重生) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, "网页版樱蝶仙子");
                } else if (deadCardSkillUseInfo.getType() == SkillType.魔化冥蝶) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, "网页版冥蝶妖姬");
                } else if (deadCardSkillUseInfo.getType() == SkillType.月影分身) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 2, deadCard.getName(), deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.寒心恨雪) {
                    Summon.apply(this, deadCardSkillUseInfo.getAttachedUseInfo1(), deadCard, SummonType.Normal, 1, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.我还会回来的) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, "大毒汁之王-5");
                } else if (deadCardSkillUseInfo.getType() == SkillType.栗子军团) {
                    RegressionSoul.apply(this, deadCardSkillUseInfo.getAttachedUseInfo1(), deadCard, opponent);
                    Summon.apply(this, deadCardSkillUseInfo.getAttachedUseInfo2(), deadCard, SummonType.Normal, 1, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.蛮荒我还会回来的) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, "蛮荒大毒汁之王-5");
                } else if (deadCardSkillUseInfo.getType() == SkillType.召唤玫瑰剑士) {
                    Summon.apply(this, deadCardSkillUseInfo.getAttachedUseInfo2(), deadCard, SummonType.Normal, 1,
                            "玫瑰甜心");
                } else if (deadCardSkillUseInfo.getType() == SkillType.英灵召唤 || deadCardSkillUseInfo.getType() == SkillType.英魂召集) {
                    Summon.apply(this, deadCardSkillUseInfo.getAttachedUseInfo2(), deadCard, SummonType.Random, 2,
                            "堕落英魂", "苍穹碧骑", "暗黑游侠", "冥河之主", "天界女武神", "暗影猎手", "湖上骑士");
                } else if (deadCardSkillUseInfo.getType() == SkillType.白帝托孤) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Random, 1,
                            "三国英魂卧龙", "三国英魂汉升", "三国英魂子龙", "三国英魂孟起");
                } else if (deadCardSkillUseInfo.getType() == SkillType.森林的梦幻) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Random, 2,
                            "梦境治愈师", "梦境耳语者", "梦境女神");
                } else if (deadCardSkillUseInfo.getType() == SkillType.生命湍流) {
                    Revive.apply(this, deadCardSkillUseInfo, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.大江山鬼王) {
                    SoulCrash.apply(deadCardSkillUseInfo, this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.八卦阵) {
                    Seal.apply(deadCardSkillUseInfo, this, deadCard, opponent);
                    RegressionSoul.apply(this, deadCardSkillUseInfo, deadCard, opponent);
                    HandCardAddSkillNormal.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getSkill(), 1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.传响) {
                    HandCardAddOneSkill.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill());
                } else if (deadCardSkillUseInfo.getType() == SkillType.诀隐) {
                    HandCardAddOneSkill.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill());
                } else if (deadCardSkillUseInfo.getType() == SkillType.挽歌) {
                    Bless.apply(deadCardSkillUseInfo.getSkill(), this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.自然恩泽) {
                    LunaBless.apply(deadCardSkillUseInfo.getSkill(), this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.恶龙血脉) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.崩坏) {
                    Crumbling.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent, 1, 1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.妖力侵蚀) {
                    SoulControl.apply(this, deadCardSkillUseInfo.getAttachedUseInfo2(), deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.离魂芳印 || deadCardSkillUseInfo.getType() == SkillType.时空封印 || deadCardSkillUseInfo.getType() == SkillType.吞噬
                        || deadCardSkillUseInfo.getType() == SkillType.斗者 || deadCardSkillUseInfo.getType() == SkillType.生死界限 || deadCardSkillUseInfo.getType() == SkillType.深海巨口
                        || deadCardSkillUseInfo.getType() == SkillType.吞噬焰火) {
                    Rapture.remove(this, deadCardSkillUseInfo, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.复仇亡灵) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Random, 1,
                            "网页版摄魂", "网页版贪魔", "网页版夺魄");
                } else if (deadCardSkillUseInfo.getType() == SkillType.化鹏) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1,
                            "网页版赤翼巨鹏");
                } else if (deadCardSkillUseInfo.getType() == SkillType.逆鳞) {
                    Snipe.apply(deadCardSkillUseInfo, deadCardSkillUseInfo.getSkill().getAttachedSkill1(), this, deadCard, opponent, -1);
                    Snipe.apply(deadCardSkillUseInfo, deadCardSkillUseInfo.getSkill().getAttachedSkill2(), this, deadCard, opponent, 3);
                } else if (deadCardSkillUseInfo.getType() == SkillType.幻化 || deadCardSkillUseInfo.getType() == SkillType.幻影
                        || deadCardSkillUseInfo.getType() == SkillType.日光浴) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.护主) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.安魂引) {
                    RegressionSoul.apply(this, deadCardSkillUseInfo.getAttachedUseInfo2(), deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.彼岸轮回) {
                    Resurrection.apply(this, deadCardSkillUseInfo, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.星座能量掌握) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.分裂) {
                    AddSelfCard.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Summoning, 1,
                            "八岐大蛇");
                } else if (deadCardSkillUseInfo.getType() == SkillType.分裂术) {
                    AddSelfCard.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Summoning, 1,
                            "八岐大蛇");
                    Bless.apply(deadCardSkillUseInfo.getAttachedUseInfo1().getSkill(), this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.魔偶替身) {
                    AddSelfCard.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Summoning, 1,
                            "魔幻人偶师");
                } else if (deadCardSkillUseInfo.getType() == SkillType.元素分离) {
                    SoulCrash.apply(deadCardSkillUseInfo, this, deadCard, opponent);
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.RandomSummoning, 3, "风暴熊猫", "土熊猫", "火熊猫");
                } else if (deadCardSkillUseInfo.getType() == SkillType.天丛云) {
                    GreatFireMagic.apply(deadCardSkillUseInfo.getAttachedUseInfo2().getSkill(), this, deadCard, opponent, 1, false);
                } else if (deadCardSkillUseInfo.getType() == SkillType.星座能量信念) {
                    Revive.apply(this, deadCardSkillUseInfo, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.余香) {
                    SummonWhenAttack.apply(this, deadCardSkillUseInfo, deadCard, 1, false, "网页版红玫瑰");
                } else if (deadCardSkillUseInfo.getType() == SkillType.归隐) {
                    Curse.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.大地吟咏) {
                    Revive.apply(this, deadCardSkillUseInfo, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.复仇之影) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.凤凰之怒) {
                    Polymorph.apply(this, deadCardSkillUseInfo, deadCard, opponent, 1, 1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.神之契约) {
                    LunaBless.apply(deadCardSkillUseInfo.getSkill(), this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.噩梦) {
                    Polymorph.apply(this, deadCardSkillUseInfo.getAttachedUseInfo1(), deadCard, opponent, -1, 1);
                    GreatFireMagic.apply(deadCardSkillUseInfo.getAttachedUseInfo2().getSkill(), this, deadCard, opponent, 1, false);
                } else if (deadCardSkillUseInfo.getType() == SkillType.萦梦) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.智慧之光) {
                    ReviveMultiple.apply(this, deadCardSkillUseInfo, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.死亡之矢 || deadCardSkillUseInfo.getType() == SkillType.贪魔伐罪) {
                    GreatFireMagic.apply(deadCardSkillUseInfo.getSkill(), this, deadCard, opponent, 1, false);
                } else if (deadCardSkillUseInfo.getType() == SkillType.炼狱魔枪) {
                    GreatFireMagic.apply(deadCardSkillUseInfo.getAttachedUseInfo1().getSkill(), this, deadCard, opponent, 1, false);
                } else if (deadCardSkillUseInfo.getType() == SkillType.天罡咒) {
                    SoulCrash.apply(deadCardSkillUseInfo, this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.反扑) {
                    Curse.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.重来) {
                    ReturnCardAndDelay.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent, 2, -1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.英雄之托) {
                    HandCardAddTwoSkill.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill());
                    HandCardAddTwoSkill.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getAttachedUseInfo2().getSkill());
                } else if (deadCardSkillUseInfo.getType() == SkillType.龙城之志) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.默示) {
                    GrudgeHp.apply(this, deadCardSkillUseInfo, deadCard, opponent, 2);
                } else if (deadCardSkillUseInfo.getType() == SkillType.哀歌) {
                    Bless.apply(deadCardSkillUseInfo.getAttachedUseInfo1().getSkill(), this, deadCard);
                    HeavenWrath.apply(this, deadCardSkillUseInfo.getAttachedUseInfo2().getSkill(), deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.安魂曲) {
                    SoulChains.apply(this, deadCardSkillUseInfo, deadCard, opponent, 3, 2);
                } else if (deadCardSkillUseInfo.getType() == SkillType.用兵之道) {
                    RedGun.apply(deadCardSkillUseInfo, this, deadCard, opponent, 3);
                } else if (deadCardSkillUseInfo.getType() == SkillType.花刺) {
                    WitheringWord.apply(deadCardSkillUseInfo, this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.生灭) {
                    Summon.apply(this, deadCardSkillUseInfo.getAttachedUseInfo2(), deadCard, SummonType.Normal, 1,
                            "雪女");
                } else if (deadCardSkillUseInfo.getType() == SkillType.呼风唤雨) {
                    AllDelay.apply(deadCardSkillUseInfo.getAttachedUseInfo2(), this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.疯长的植物) {
                    Summon.apply(this, deadCardSkillUseInfo.getAttachedUseInfo2(), deadCard, SummonType.Normal, 1,
                            "吞噬守卫者", "深渊吞噬者", "巨噬藤");
                } else if (deadCardSkillUseInfo.getType() == SkillType.天道承负) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1, "黑白无常+无常索命");
                } else if (deadCardSkillUseInfo.getType() == SkillType.幽魂铁骑) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 2,
                            "死域军神", "死域军神");
                } else if (deadCardSkillUseInfo.getType() == SkillType.诈降) {
                    AllDelay.apply(deadCardSkillUseInfo, this, deadCard, deadCard.getOwner());
                } else if (deadCardSkillUseInfo.getType() == SkillType.诱敌深入) {
                    AddSkillOpponentFactor.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill(), opponent, 0);
                    MagicMark.apply(this, deadCardSkillUseInfo.getAttachedUseInfo2(), deadCard, opponent, -1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.踢雪) {
                    IceTouch.apply(deadCardSkillUseInfo, this, deadCard, opponent, 5,100);
                } else if (deadCardSkillUseInfo.getType() == SkillType.绝境之志) {
                    Destroy.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent, -1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.放逐之刃) {
                    ReturnCardAndDelay.apply(this, deadCardSkillUseInfo.getAttachedUseInfo2().getSkill(), deadCard, opponent, 1, 3);
                } else if (deadCardSkillUseInfo.getType() == SkillType.猛禽) {
                    RegressionSoul.apply(this, deadCardSkillUseInfo.getAttachedUseInfo1(), deadCard, opponent);
                    SpeedUp.apply(deadCardSkillUseInfo.getAttachedUseInfo2(), this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.恐惧降临) {
                    WitheringWord.apply(deadCardSkillUseInfo.getAttachedUseInfo2(), this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.星眸) {
                    Destroy.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, opponent, -1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.逆战光辉) {
                    Bless.apply(deadCardSkillUseInfo.getAttachedUseInfo1().getSkill(), this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.灵体召回) {
                    Homology.apply(this, deadCardSkillUseInfo, deadCard, "齐天大圣");
                } else if (deadCardSkillUseInfo.getType() == SkillType.如影随形) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Normal, 1,
                            "虚影");
                } else if (deadCardSkillUseInfo.getType() == SkillType.气氛感染) {
                    HandCardAddOneSkill.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill());
                } else if (deadCardSkillUseInfo.getType() == SkillType.急救) {
                    SoulControl.apply(this, deadCardSkillUseInfo, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.逆战) {
                    Bless.apply(deadCardSkillUseInfo.getSkill(), this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.古神呼唤) {
                    SpeedUpOpponent.apply(deadCardSkillUseInfo, this, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.星象归位) {
                    HandCardAddOneSkill.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill());
                } else if (deadCardSkillUseInfo.getType() == SkillType.冥界召唤) {
                    if (!FailureSkillUseInfoList.explode(this, deadCard, opponent)) {
                        Homology.apply(this, deadCardSkillUseInfo, deadCard, "奥西里斯");
                    }
                } else if (deadCardSkillUseInfo.getType() == SkillType.余音袅袅) {
                    AddHandOneCardMultSkill.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill()
                            , deadCardSkillUseInfo.getAttachedUseInfo2().getSkill(), null);
                } else if (deadCardSkillUseInfo.getType() == SkillType.疏影横斜) {
                    Asthenia.apply(this, deadCardSkillUseInfo, deadCard, opponent, 5, 2);
                } else if (deadCardSkillUseInfo.getType() == SkillType.替身术) {
                    AddSelfCard.apply(this, deadCardSkillUseInfo.getAttachedUseInfo1(), deadCard, SummonType.Summoning, 1,
                            deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.雪影分身) {
                    AddSelfCard.apply(this, deadCardSkillUseInfo, deadCard, SummonType.Summoning, 1,
                            deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.传承之力) {
                    HandCardAddSkillNormal.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill(), 1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.故技重演) {
                    TurbulenceDeck.apply(deadCardSkillUseInfo.getSkill(), this, deadCard, opponent, 70);
                } else if (deadCardSkillUseInfo.getType() == SkillType.暗影之佑) {
                    RegressionSoul.apply(this, deadCardSkillUseInfo, deadCard, opponent);
                } else if (deadCardSkillUseInfo.getType() == SkillType.进退自如) {
                    AddFiledCardMultSkill.apply(this, deadCardSkillUseInfo, deadCard, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill()
                            , null, null);
                } else if (deadCardSkillUseInfo.getType() == SkillType.庚子守护) {
                    MouseGuard.apply(this, deadCardSkillUseInfo, deadCard, opponent, 2, 4, 0);
                } else if (deadCardSkillUseInfo.getType() == SkillType.秘术守护) {
                    MouseGuard.apply(this, deadCardSkillUseInfo, deadCard, opponent, 0, 4, 1);
                } else if (deadCardSkillUseInfo.getType() == SkillType.星光重耀) {
                    SummonMultiple.apply(this, deadCardSkillUseInfo, deadCard, 2, "深邃之星");
                } else if (deadCardSkillUseInfo.getType() == SkillType.戍边) {
                    HomologyOnlySelf.apply(this, deadCardSkillUseInfo, deadCard, deadCard.getName());
                } else if (deadCardSkillUseInfo.getType() == SkillType.魔力彩蛋) {
                    Summon.apply(this, deadCardSkillUseInfo, deadCard, SummonType.RandomSummoning, 2,
                            "堕落英魂", "苍穹碧骑", "暗黑游侠", "冥河之主", "天界女武神", "暗影猎手", "湖上骑士");
                } else if (deadCardSkillUseInfo.getType() == SkillType.妙笔生花) {
                    if (!result.soulCrushed && !result.unbending) {
                        result.soulCrushed = SelfOut.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, "汉宫春晓图");
                    }
                } else if (deadCardSkillUseInfo.getType() == SkillType.追思) {
                    if (!result.soulCrushed && !result.unbending) {
                        result.soulCrushed = SelfOutOpponent.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, "汉宫春晓", opponent);
                    }
                } else if (deadCardSkillUseInfo.getType() == SkillType.睹物思人) {
                    SummonMultipleExtraSkill.apply(this, deadCardSkillUseInfo, deadCard, 3, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill(),
                            "画境·汉宫春晓");
                } else if (deadCardSkillUseInfo.getType() == SkillType.御龙在天) {
                    AddCard.apply(this, deadCardSkillUseInfo.getAttachedUseInfo2(), deadCard, SummonType.Summoning, 1,
                            "九霄龙吟");
                } else if (deadCardSkillUseInfo.getType() == SkillType.破魔) {
                    RedGun.apply(deadCardSkillUseInfo, this, deadCard, opponent, 3);
                } else if (deadCardSkillUseInfo.getType() == SkillType.诅咒之印) {
                    CounterBite.apply(deadCardSkillUseInfo, this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.国之坚壁) {
                    Bless.apply(deadCardSkillUseInfo.getAttachedUseInfo1().getSkill(), this, deadCard);
                } else if (deadCardSkillUseInfo.getType() == SkillType.守墓者) {
                    GuardGrave.reset(deadCardSkillUseInfo);
                }
            }
        }

        if (!deadCard.isSilent()) {
            RuneInfo rune = deadCard.getOwner().getActiveRuneOf(RuneData.爆裂);
            // IMPORTANT: Unbending card cannot trigger 爆裂
            if (rune != null && !deadCard.justRevived() && !result.unbending) {
                Explode.apply(this, rune.getSkill(), killerCard, deadCard);
            }
            rune = deadCard.getOwner().getActiveRuneOf(RuneData.背水);
            // IMPORTANT: Unbending card cannot trigger 背水
            if (rune != null && deadCard.getRuneActive() && !result.unbending) {
                TsubameGaeshi.apply(null, rune.getSkill(), this, opponent, deadCard, 200);
            }
        }
        if (deadCard.isDead()) {
            List<CardStatusItem> cardStatusItemList = deadCard.getStatus().getAllItems();
            for (CardStatusItem item : cardStatusItemList) {
                if (item.getType() == CardStatusType.死印) {
                    DeathMark.explode(this, deadCard, result);
                } else if (item.getType() == CardStatusType.死咒) {
                    ControlGhost.explode(this, deadCard, result, "摄魂", "噬血", "贪魔", "夺魄");
                } else if (item.getType() == CardStatusType.黄天) {
                    SkeletonArmy.explode(this, deadCard, result, "战场亡魂", "战场亡灵");
                } else if (item.getType() == CardStatusType.祭奠) {
                    SacrificeBuff.explode(this, deadCard, result, "圣翼白虎", "气冲冲仙人掌", " 钢铁蛮牛兽", "迅捷豹女");
                } else if (item.getType() == CardStatusType.献祭) {
                    DeathSacrifice.explode(this, deadCard, result, "骨龙", "骸骨大将", "灵俑暗杀者");
                } else if (item.getType() == CardStatusType.炼成) {
                    HumanRefining.explode(this, deadCard, result, "进化材料");
                } else if (item.getType() == CardStatusType.咒怨) {
                    Grudge.Infected(this, deadCard);
                } else if (item.getType() == CardStatusType.咒恨) {
                    GrudgeAt.Infected(this, deadCard);
                } else if (item.getType() == CardStatusType.咒皿) {
                    GrudgeHp.Infected(this, deadCard);
                } else if (item.getType() == CardStatusType.链接) {
                    SoulLink.explode(this, deadCard);
                } else if (item.getType() == CardStatusType.蛇影) {
                    SnakeShadow.explode(this, deadCard, result, "狂暴羽蛇分身");
                } else if (item.getType() == CardStatusType.蚀月) {
                    LunarEclipse.explode(this, deadCard, result, "月蚀兽", "晦月");
                } else if (item.getType() == CardStatusType.海啸) {
                    AncientSummon.explode(this, deadCard, result, "海啸");
                } else if (item.getType() == CardStatusType.傀儡) {
                    PuppetSummon.explode(this, deadCard, result, "火焰傀儡");
                }
            }
        }
        boolean reincarnated = false;
        if (deadCard.isDead()&&!result.unbending && !result.soulCrushed && !result.soulControlDead && !deadCard.getStatus().containsStatus(CardStatusType.魂殇)) {
            if (cardSkill == null || !(cardSkill.getType() == SkillType.重整 || cardSkill.getType() == SkillType.不朽岿岩
                    || cardSkill.getType() == SkillType.不息神盾 || cardSkill.getType() == SkillType.再生金蝉
                    || cardSkill.getType() == SkillType.烈火炙魂 || cardSkill.getType() == SkillType.据守
                    || cardSkill.getType() == SkillType.卷土重来 || cardSkill.getType() == SkillType.摄魂之力
                    || cardSkill.getType() == SkillType.情况紧急 || cardSkill.getType() == SkillType.风暴雷云
                    || cardSkill.getType() == SkillType.复仇者 || cardSkill.getType() == SkillType.游击
                    || cardSkill.getType() == SkillType.死亡女神 || cardSkill.getType() == SkillType.虚实相生
                    || cardSkill.getType() == SkillType.周旋 || cardSkill.getType() == SkillType.海姆冥界
                    || cardSkill.getType() == SkillType.迂回
                    || cardSkill.getType() == SkillType.柳暗花明
                    || cardSkill.getType() == SkillType.金蝉脱壳
                    || (cardSkill.getType() == SkillType.天道无常 && killerCard == deadCard)
                    || (cardSkill.getType() == SkillType.凤凰于飞 && killerCard == deadCard))) {
                // 被扼杀的卡牌无法转生

                for (SkillUseInfo deadCardSkillUseInfo : deadCard.getAllUsableSkills()) {
                    if (deadCardSkillUseInfo.getType() == SkillType.转生 ||
                            deadCardSkillUseInfo.getType() == SkillType.武形秘仪 ||
                            deadCardSkillUseInfo.getType() == SkillType.花族秘术 ||
                            deadCardSkillUseInfo.getType() == SkillType.洪荒之术 ||
                            deadCardSkillUseInfo.getType() == SkillType.六道轮回 ||
                            deadCardSkillUseInfo.getType() == SkillType.武形秘术 ||
                            deadCardSkillUseInfo.getType() == SkillType.武形秘法 ||
                            deadCardSkillUseInfo.getType() == SkillType.涅盘 ||
                            deadCardSkillUseInfo.getType() == SkillType.神性 ||
                            deadCardSkillUseInfo.getType() == SkillType.粗中有细 ||
                            deadCardSkillUseInfo.getType() == SkillType.战术性撤退 ||
                            deadCardSkillUseInfo.getType() == SkillType.巧变 ||
                            deadCardSkillUseInfo.getType() == SkillType.凤凰涅盘 ||
                            deadCardSkillUseInfo.getType() == SkillType.诲人不倦 ||
                            deadCardSkillUseInfo.getType() == SkillType.页游鞠躬尽瘁 ||
                            deadCardSkillUseInfo.getType() == SkillType.心转之术 ||
                            deadCardSkillUseInfo.getType() == SkillType.天选之子 ||
                            deadCardSkillUseInfo.getType() == SkillType.轮回渡厄 ||
                            deadCardSkillUseInfo.getType() == SkillType.逆战光辉 ||
                            deadCardSkillUseInfo.getType() == SkillType.明月渡我 ||
                            deadCardSkillUseInfo.getType() == SkillType.生物进化 ||
                            deadCardSkillUseInfo.getType() == SkillType.精神补完 ||
                            deadCardSkillUseInfo.getType() == SkillType.永生的诅咒 ||
                            deadCardSkillUseInfo.getType() == SkillType.炼狱魔枪 ||
                            deadCardSkillUseInfo.getType() == SkillType.晦月 ||
                            deadCardSkillUseInfo.getType() == SkillType.武侯 ||
                            deadCardSkillUseInfo.getType() == SkillType.净身明礼 ||
                            deadCardSkillUseInfo.getType() == SkillType.进退自如 ||
                            deadCardSkillUseInfo.getType() == SkillType.不溃) {
                        if (Reincarnation.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, result.unbending, opponent)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.蝶语 || deadCardSkillUseInfo.getType() == SkillType.雷狱牢囚) {
                        if (Reincarnation.apply(this, deadCardSkillUseInfo.getAttachedUseInfo2().getSkill(), deadCard, result.unbending, opponent)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.司命 || deadCardSkillUseInfo.getType() == SkillType.不灭定律 || deadCardSkillUseInfo.getType() == SkillType.不灭
                            || deadCardSkillUseInfo.getType() == SkillType.顽强 || deadCardSkillUseInfo.getType() == SkillType.我又回来了 || deadCardSkillUseInfo.getType() == SkillType.时空置换
                            || deadCardSkillUseInfo.getType() == SkillType.蚀月 || deadCardSkillUseInfo.getType() == SkillType.不灭金身 || deadCardSkillUseInfo.getType() == SkillType.阴阳术轮回
                            || deadCardSkillUseInfo.getType() == SkillType.壮心不已 || deadCardSkillUseInfo.getType() == SkillType.天下英主 || deadCardSkillUseInfo.getType() == SkillType.起死回生
                            || deadCardSkillUseInfo.getType() == SkillType.魔镜穿梭 || deadCardSkillUseInfo.getType() == SkillType.活性细胞 || deadCardSkillUseInfo.getType() == SkillType.冥界守护
                            || deadCardSkillUseInfo.getType() == SkillType.白驹过隙 || deadCardSkillUseInfo.getType() == SkillType.筑巢 || deadCardSkillUseInfo.getType() == SkillType.重生与希望
                            || deadCardSkillUseInfo.getType() == SkillType.恒星之力 || deadCardSkillUseInfo.getType() == SkillType.入木三分 || deadCardSkillUseInfo.getType() == SkillType.鞠躬尽瘁) {
                        if (Reborn.apply(this, deadCardSkillUseInfo, deadCard, result.unbending)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.不灭原质) {
                        Bless.apply(deadCardSkillUseInfo.getAttachedUseInfo1().getSkill(), this, deadCard);
                        LunaBless.apply(deadCardSkillUseInfo.getAttachedUseInfo2().getSkill(), this, deadCard);
                        if (Reborn.apply(this, deadCardSkillUseInfo, deadCard, result.unbending)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.雪幕) {
                        if (Reborn.apply(this, deadCardSkillUseInfo.getAttachedUseInfo1(), deadCard, result.unbending)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.回生 || deadCardSkillUseInfo.getType() == SkillType.不凋花) {
                        if (Retrogradation.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, result.unbending)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.灵魂脱壳) {
                        if (Reborn.apply(this, deadCardSkillUseInfo, deadCard, result.unbending)) {
                            reincarnated = true;
                            break;
                        } else {
                            Retrogradation.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, result.unbending);
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.还魂) {
                        if (Reincarnation.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, result.unbending, opponent)) {
                            reincarnated = true;
                            break;
                        } else {
                            Retrogradation.apply(this, deadCardSkillUseInfo.getSkill(), deadCard, result.unbending);
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.安魂引) {
                        if (Reincarnation.apply(this, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill(), deadCard, result.unbending, opponent)) {
                            reincarnated = true;
                            break;
                        } else {
                            Retrogradation.apply(this, deadCardSkillUseInfo.getAttachedUseInfo1().getSkill(), deadCard, result.unbending);
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.真龙九现) {
                        if (MultipleReincarnation.apply(this, deadCardSkillUseInfo, deadCard, result.unbending, opponent, 65, 15, 19)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.终焉脱壳 || deadCardSkillUseInfo.getType() == SkillType.脱壳) {
                        if (MultipleReincarnation.apply(this, deadCardSkillUseInfo, deadCard, result.unbending, opponent, 50, 0, 50)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.飞天揽月 || deadCardSkillUseInfo.getType() == SkillType.生生不息 || deadCardSkillUseInfo.getType() == SkillType.卧薪尝胆) {
                        if (MultipleReincarnation.apply(this, deadCardSkillUseInfo, deadCard, result.unbending, opponent, 40, 40, 0)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.永生魔咒) {
                        if (MultipleReincarnation.apply(this, deadCardSkillUseInfo, deadCard, result.unbending, opponent, 0, 20, 80)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.金身 || deadCardSkillUseInfo.getType() == SkillType.金刚之躯 || deadCardSkillUseInfo.getType() == SkillType.金元仙躯) {
                        int rate = deadCardSkillUseInfo.getSkill().getImpact();
                        if (MultipleReincarnation.apply(this, deadCardSkillUseInfo, deadCard, result.unbending, opponent, rate, 0, rate)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.难知如阴) {
                        if (MultipleReincarnation.apply(this, deadCardSkillUseInfo, deadCard, result.unbending, opponent, 50, 0, 50)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.轮回天生) {
                        RegressionSoul.apply(this, deadCardSkillUseInfo.getAttachedUseInfo1(), deadCard, opponent);
                        if (Reborn.apply(this, deadCardSkillUseInfo, deadCard, result.unbending)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.不死金身 || deadCardSkillUseInfo.getType() == SkillType.不灭之志) {
                        if (MultipleReincarnation.apply(this, deadCardSkillUseInfo, deadCard, result.unbending, opponent, 40, 0, 40)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.举棋若定) {
                        if (MultipleReincarnation.apply(this, deadCardSkillUseInfo, deadCard, result.unbending, opponent, 50, 50, 0)) {
                            reincarnated = true;
                            break;
                        }
                    } else if (deadCardSkillUseInfo.getType() == SkillType.红尘缥缈仙) {
                        if (MultipleReincarnation.apply(this, deadCardSkillUseInfo, deadCard, result.unbending, opponent, 60, 19, 20)) {
                            reincarnated = true;
                            break;
                        }
                    }
                }
                if (!reincarnated && !deadCard.isSilent()) {
                    RuneInfo rune = deadCard.getOwner().getActiveRuneOf(RuneData.秽土);
                    if (rune != null && !deadCard.justRevived()) {
                        if (Reincarnation.apply(this, rune.getSkill(), deadCard, result.unbending, opponent)) {
                            reincarnated = true;
                        }
                    }
                }
            }
        }

//        if (!result.unbending && !reincarnated && !deadCard.isAlive()) {
//            for (SkillUseInfo deadCardSkillUseInfo : deadCard.getAllUsableSkills()) {
//                if (deadCardSkillUseInfo.getGiveSkill() == 1) {
//                    deadCard.removeSkill(deadCardSkillUseInfo);
//                }
//            }
//            deadCard.setSummonNumber(0);
//            deadCard.setAddDelay(0);
//            deadCard.setRuneActive(false);
//            resolveLeaveSkills(deadCard);
//        }
//        if (!result.unbending && !reincarnated && !deadCard.isAlive()) {
//            if (!deadCard.isSummonedMinion()) {
//                deadCard.reset();
//            }
//        }
        if (!result.unbending && !deadCard.isAlive()) {
            resetDeadCard(deadCard);
        } else if (!result.unbending && deadCard.isAlive()) {
            //非不屈状态下,卡牌反场移除附加技能
            GiveSideSkill.removeAll(this, null, deadCard);
        }

        // HACKHACK: Cannot find better way to handle 不屈/
        //改变不屈的去掉buff位置，为GiveSideSkill做的处理
        if (result.soulControlDead) {
            deadCard.restoreOwner();
        }
        if (deadCard.getOwner().getBeforeDeath().contains(deadCard)) {
            deadCard.getOwner().getBeforeDeath().removeCard(deadCard);
            ParadiseLost.remove(this, deadCard, opponent);
            deadCard.getOwner().getGrave().addCard(deadCard);
        } else if (result.soulCrushed) {
            ParadiseLost.remove(this, deadCard, opponent);
        }
        deadCard.setIsDeathNow(false);
    }

    public void resetDeadCard(CardInfo deadCard) {
        deadCard.setSummonNumber(0);
        deadCard.setAddDelay(0);
        deadCard.setRuneActive(false);
        resolveLeaveSkills(deadCard);
        if (!deadCard.isSummonedMinion()) {
            deadCard.reset();
        }
    }

    //特殊添加，法术扼杀类技能导致死亡时不触发死契
    public void resetDeadCard(OnDamagedResult result, CardInfo deadCard) {
        Player opponent = this.getStage().getOpponent(deadCard.getOwner());
        if (result.cardDead && !result.unbending) {
            deadCard.setSummonNumber(0);
            deadCard.setAddDelay(0);
            deadCard.setRuneActive(false);
            resolveLeaveSkills(deadCard);
            ParadiseLost.removeCard(this, deadCard, opponent);
            if (!deadCard.isSummonedMinion()) {
                deadCard.reset();
            }
        }
    }

    public void resolvePostAttackSkills(CardInfo attacker, CardInfo defender, Player defenderHero, Skill attackSkill,
                                        int normalAttackDamage) throws HeroDieSignal {
        for (SkillUseInfo skillUseInfo : attacker.getUsableNormalSkills()) {
            if (!attacker.isDead()) {
                if (skillUseInfo.getType() == SkillType.吸血 ||
                        skillUseInfo.getType() == SkillType.蛇吻 ||
                        skillUseInfo.getType() == SkillType.鬼彻 ||
                        skillUseInfo.getType() == SkillType.武圣 ||
                        skillUseInfo.getType() == SkillType.狂暴 ||
                        skillUseInfo.getType() == SkillType.村正) {
                    BloodDrain.apply(skillUseInfo.getSkill(), this, attacker, defender, normalAttackDamage);
                }
                if (skillUseInfo.getType() == SkillType.樱魂 || skillUseInfo.getType() == SkillType.神亭酣战 || skillUseInfo.getType() == SkillType.肉食者
                        || skillUseInfo.getType() == SkillType.幽灵幻象) {
                    BloodDrain.apply(skillUseInfo.getSkill().getAttachedSkill1(), this, attacker, defender, normalAttackDamage);
                }
            }
        }
        if (!attacker.isDead() && !attacker.isSilent()) {
            RuneInfo rune = attacker.getOwner().getActiveRuneOf(RuneData.赤谷);
            if (rune != null && !attacker.justRevived()) {
                BloodDrain.apply(rune.getSkill(), this, attacker, defender, normalAttackDamage);
            }
        }
    }

    public void resolveExtraAttackSkills(CardInfo attacker, CardInfo defender, Player defenderHero, Skill attackSkill, OnDamagedResult damageResult, boolean firstSkill) throws HeroDieSignal {
        int normalAttackDamage = damageResult.actualDamage;
        if (!FailureSkillUseInfoList.explode(this, attacker, defenderHero)) {
            for (SkillUseInfo skillUseInfo : attacker.getUsableNormalSkills()) {
                if (!attacker.isDead()) {
                    if (skillUseInfo.getType() == SkillType.穿刺 || skillUseInfo.getType() == SkillType.英雄之敌 || skillUseInfo.getType() == SkillType.头槌破门 || skillUseInfo.getType() == SkillType.横扫千军
                            || skillUseInfo.getType() == SkillType.一夫之勇) {
                        Penetration.apply(skillUseInfo.getSkill(), this, attacker, defenderHero, normalAttackDamage);
                    } else if (skillUseInfo.getType() == SkillType.精准打击 || skillUseInfo.getType() == SkillType.精准射击) {
                        Penetration.apply(skillUseInfo.getSkill(), this, attacker, defenderHero, normalAttackDamage);
                    } else if (skillUseInfo.getType() == SkillType.削弱 || skillUseInfo.getType() == SkillType.缴械) {
                        Weaken.apply(this, skillUseInfo, attacker, defender, normalAttackDamage);
                    } else if (skillUseInfo.getType() == SkillType.裂伤) {
                        Wound.apply(this, skillUseInfo, attackSkill, attacker, defender, normalAttackDamage);
                    } else if (skillUseInfo.getType() == SkillType.嗜血 || skillUseInfo.getType() == SkillType.亮银 || skillUseInfo.getType() == SkillType.暴食 || skillUseInfo.getType() == SkillType.狂暴之刃
                            || skillUseInfo.getType() == SkillType.地煞倾覆 || skillUseInfo.getType() == SkillType.大小通吃 || skillUseInfo.getType() == SkillType.巨象猛冲) {
                        BloodThirsty.apply(this, skillUseInfo, attacker, normalAttackDamage);
                    } else if (skillUseInfo.getType() == SkillType.连锁攻击 || skillUseInfo.getType() == SkillType.女武神之辉) {
                        ChainAttack.apply(this, skillUseInfo, attacker, defender, attackSkill, damageResult.originalDamage);
                    } else if (skillUseInfo.getType() == SkillType.疾病) {
                        Disease.apply(skillUseInfo, this, attacker, defender, normalAttackDamage);
                    } else if (skillUseInfo.getType() == SkillType.贪吃 || skillUseInfo.getType() == SkillType.魔龙之怒) {
                        BloodThirsty.apply(this, skillUseInfo, attacker, normalAttackDamage);
                    } else if (skillUseInfo.getType() == SkillType.毒刃 || skillUseInfo.getType() == SkillType.毒杀) {
                        PosionBlade.apply(this, skillUseInfo, attacker, defender, normalAttackDamage);
                    } else if (skillUseInfo.getType() == SkillType.鲜血记忆 || skillUseInfo.getType() == SkillType.生命汲取) {
                        VampiricTouch.apply(this, skillUseInfo, attacker, normalAttackDamage,false);
                    } else if (skillUseInfo.getType() == SkillType.迷雾滋养) {
                        VampiricTouch.apply(this, skillUseInfo, attacker, normalAttackDamage,true);
                    }
                }
                if (skillUseInfo.getType() == SkillType.武形天火击) {
                    if (!defender.isDead() && defender.getStatus().containsStatus(CardStatusType.燃烧)) {
                        Destroy.apply(this, skillUseInfo.getSkill(), attacker, defender);
                    }
                } else if (skillUseInfo.getType() == SkillType.幻影军团 || skillUseInfo.getType() == SkillType.幻影奇袭
                        || skillUseInfo.getType() == SkillType.圣翼军团 || skillUseInfo.getType() == SkillType.秘术投影
                        || skillUseInfo.getType() == SkillType.王国卫士 || skillUseInfo.getType() == SkillType.幻影斩
                        || skillUseInfo.getType() == SkillType.血之眷顾 || skillUseInfo.getType() == SkillType.勇冠三军
                        || skillUseInfo.getType() == SkillType.深海幻影 || skillUseInfo.getType() == SkillType.蛇影重重
                        || skillUseInfo.getType() == SkillType.护身 || skillUseInfo.getType() == SkillType.快速增值) {
                    SummonWhenAttack.apply(this, skillUseInfo, attacker, 1, true, attacker.getName());
                } else if (skillUseInfo.getType() == SkillType.幽灵幻象) {
                    SummonWhenAttack.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, 1, true, attacker.getName());
                } else if (skillUseInfo.getType() == SkillType.灵猴棒法) {
                    MonkeyStick.apply(this, skillUseInfo, attacker, defender);
                } else if (skillUseInfo.getType() == SkillType.离魂剑) {
                    SoulControlMutiple.apply(this, skillUseInfo, attacker, defenderHero);
                } else if (skillUseInfo.getType() == SkillType.震慑 || skillUseInfo.getType() == SkillType.白虹) {
                    ReturnToHandAndDelay.apply(this, skillUseInfo.getSkill(), attacker, defenderHero, 2, 2);
                } else if (defender.isAlive()) {
                    UnderworldTrio.apply(this, attacker, defender);
                }
            }
            if (!attacker.isDead() && !attacker.isSilent()) {
                {
                    RuneInfo rune = attacker.getOwner().getActiveRuneOf(RuneData.洞察);
                    if (rune != null && !attacker.justRevived()) {
                        BloodThirsty.apply(this, rune.getSkillUseInfo(), attacker, normalAttackDamage);
                    }
                }
                {
                    RuneInfo rune = attacker.getOwner().getActiveRuneOf(RuneData.狂战);
                    if (rune != null && !attacker.justRevived()) {
                        Penetration.apply(rune.getSkillUseInfo().getSkill(), this, attacker, defenderHero, normalAttackDamage);
                    }
                }
            }
        }
    }

    public void resolveMultAttackSkills(CardInfo attacker, CardInfo defender, Player defenderHero, Skill attackSkill, OnDamagedResult damageResult, boolean firstSkill) throws HeroDieSignal {
        if (!FailureSkillUseInfoList.explode(this, attacker, defenderHero)) {
            for (SkillUseInfo skillUseInfo : attacker.getUsableNormalSkills()) {
                if (!attacker.isDead()) {
                    if (skillUseInfo.getType() == SkillType.高级连击) {
                        MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, attackSkill, firstSkill, 100);
                        break;
                    } else if (skillUseInfo.getType() == SkillType.狂舞 || skillUseInfo.getType() == SkillType.夺命骨镰 || skillUseInfo.getType() == SkillType.追击
                            || skillUseInfo.getType() == SkillType.连续突刺 || skillUseInfo.getType() == SkillType.采佩什 || skillUseInfo.getType() == SkillType.冥狱鞭挞) {
                        MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, attackSkill, firstSkill, 100);
                        break;
                    } else if (skillUseInfo.getType() == SkillType.正义之师) {
                        MultipleAttack.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defenderHero, attackSkill, firstSkill, 50);
                        break;
                    } else if (skillUseInfo.getType() == SkillType.破坏之爪) {
                        MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, attackSkill, firstSkill, 40);
                        break;
                    } else if (skillUseInfo.getType() == SkillType.青龙偃月 || skillUseInfo.getType() == SkillType.盘踞之物) {
                        MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, attackSkill, firstSkill, 1);
                        break;
                    } else if (skillUseInfo.getType() == SkillType.二段斩 || skillUseInfo.getType() == SkillType.二段斩强袭) {
                        MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, attackSkill, firstSkill, 70);
                        break;
                    } else if (skillUseInfo.getType() == SkillType.巨象猛冲) {
                        MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, attackSkill, firstSkill, 80);
                        break;
                    } else if (skillUseInfo.getType() == SkillType.精武) {
                        MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, attackSkill, firstSkill, 100);
                        break;
                    }
                }
            }
        }
    }

    public void resolvePreAttackHeroSkills(CardInfo attacker, Player defenderPlayer) throws HeroDieSignal {
        for (SkillUseInfo skillUseInfo : attacker.getUsableNormalSkills()) {
            if (skillUseInfo.getType() == SkillType.英雄杀手 || skillUseInfo.getType() == SkillType.英雄之敌 || skillUseInfo.getType() == SkillType.头槌破门
                    || skillUseInfo.getType() == SkillType.龙战于野 || skillUseInfo.getType() == SkillType.超级英雄杀手 || skillUseInfo.getType() == SkillType.杀手回梦
                    || skillUseInfo.getType() == SkillType.陷阵) {
                HeroKiller.apply(this, skillUseInfo, attacker, defenderPlayer);
            } else if (skillUseInfo.getType() == SkillType.夜袭) {
                HeroKiller.apply(this, skillUseInfo.getAttachedUseInfo2(), attacker, defenderPlayer);
            } else if (skillUseInfo.getType() == SkillType.凯撒之击) {
                CaeserAttack.apply(this, skillUseInfo, attacker, defenderPlayer);
            } else if (skillUseInfo.getType() == SkillType.厨具召唤) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defenderPlayer, 1, 500);
            } else if (skillUseInfo.getType() == SkillType.神兵召唤 ||
                    skillUseInfo.getType() == SkillType.混铁棍 ||
                    skillUseInfo.getType() == SkillType.天降神兵 ||
                    skillUseInfo.getType() == SkillType.神兵降临 ||
                    skillUseInfo.getType() == SkillType.觉醒神兵召唤 && attacker.isAwaken(skillUseInfo, Race.SAVAGE, 2) ||
                    skillUseInfo.getType() == SkillType.星座能量清醒 ||
                    skillUseInfo.getType() == SkillType.阿拉希血统 ||
                    skillUseInfo.getType() == SkillType.夺命骨镰 ||
                    skillUseInfo.getType() == SkillType.破魔神兵 ||
                    skillUseInfo.getType() == SkillType.神兵天降 ||
                    skillUseInfo.getType() == SkillType.死亡女神 ||
                    skillUseInfo.getType() == SkillType.利器) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defenderPlayer, 500, 1700);
            } else if (skillUseInfo.getType() == SkillType.觉醒青龙偃月 && attacker.isAwaken(skillUseInfo, Race.FOREST, 3)) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defenderPlayer, 1300, 1780);
            } else if (skillUseInfo.getType() == SkillType.圣器召唤 || skillUseInfo.getType() == SkillType.王牌飞刀 || skillUseInfo.getType() == SkillType.突袭) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defenderPlayer, 300, 1300);
            } else if (skillUseInfo.getType() == SkillType.极寒裂伤) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defenderPlayer, 600, 1200);
            } else if (skillUseInfo.getType() == SkillType.陨星攻击) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defenderPlayer, 700, 1600);
            } else if (skillUseInfo.getType() == SkillType.良驹) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defenderPlayer, 1, 3000);
            } else if (skillUseInfo.getType() == SkillType.神兵) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defenderPlayer, 1500, 2500);
            }
        }
    }

    /**
     * @param attacker
     * @param defender
     * @throws HeroDieSignal
     */
    public void resolvePreAttackCardSkills(CardInfo attacker, CardInfo defender) throws HeroDieSignal {
        for (SkillUseInfo skillUseInfo : attacker.getUsableNormalSkills()) {
            if (skillUseInfo.getType() == SkillType.圣光) {
                RacialAttackSkill.apply(this, skillUseInfo, attacker, defender, Race.HELL);
            } else if (skillUseInfo.getType() == SkillType.要害) {
                RacialAttackSkill.apply(this, skillUseInfo, attacker, defender, Race.SAVAGE);
            } else if (skillUseInfo.getType() == SkillType.暗杀) {
                RacialAttackSkill.apply(this, skillUseInfo, attacker, defender, Race.KINGDOM);
            } else if (skillUseInfo.getType() == SkillType.污染) {
                RacialAttackSkill.apply(this, skillUseInfo, attacker, defender, Race.FOREST);
            } else if (skillUseInfo.getType() == SkillType.暴击 || skillUseInfo.getType() == SkillType.鹰眼 || skillUseInfo.getType() == SkillType.致命一击
                    || skillUseInfo.getType() == SkillType.巨斧横扫) {
                CriticalAttack.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.神兵召唤 ||
                    skillUseInfo.getType() == SkillType.混铁棍 ||
                    skillUseInfo.getType() == SkillType.天降神兵 ||
                    skillUseInfo.getType() == SkillType.神兵降临 ||
                    skillUseInfo.getType() == SkillType.觉醒神兵召唤 && attacker.isAwaken(skillUseInfo, Race.SAVAGE, 2) ||
                    skillUseInfo.getType() == SkillType.星座能量清醒 ||
                    skillUseInfo.getType() == SkillType.阿拉希血统 ||
                    skillUseInfo.getType() == SkillType.夺命骨镰 ||
                    skillUseInfo.getType() == SkillType.破魔神兵 ||
                    skillUseInfo.getType() == SkillType.神兵天降 ||
                    skillUseInfo.getType() == SkillType.死亡女神 ||
                    skillUseInfo.getType() == SkillType.利器) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defender, 500, 1700);
            } else if (skillUseInfo.getType() == SkillType.觉醒青龙偃月 && attacker.isAwaken(skillUseInfo, Race.FOREST, 3)) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defender, 1300, 1780);
            } else if (skillUseInfo.getType() == SkillType.厨具召唤) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defender, 1, 500);
            } else if (skillUseInfo.getType() == SkillType.圣器召唤 || skillUseInfo.getType() == SkillType.王牌飞刀 || skillUseInfo.getType() == SkillType.突袭) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defender, 300, 1300);
            } else if (skillUseInfo.getType() == SkillType.极寒裂伤) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defender, 600, 1200);
            } else if (skillUseInfo.getType() == SkillType.陨星攻击) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defender, 700, 1600);
            } else if (skillUseInfo.getType() == SkillType.良驹) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defender, 1, 3000);
            } else if (skillUseInfo.getType() == SkillType.神兵) {
                WeaponSummon.apply(this, skillUseInfo, attacker, defender, 1500, 2500);
            } else if (skillUseInfo.getType() == SkillType.穷追猛打 || skillUseInfo.getType() == SkillType.灵击 || skillUseInfo.getType() == SkillType.死亡践踏) {
                Pursuit.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.战意 || skillUseInfo.getType() == SkillType.鬼王之怒 || skillUseInfo.getType() == SkillType.大江山鬼王 || skillUseInfo.getType() == SkillType.正义追击
                    || skillUseInfo.getType() == SkillType.战神 || skillUseInfo.getType() == SkillType.一夫之勇) {
                Wrath.apply(this, skillUseInfo, attacker, defender);
            } else if (skillUseInfo.getType() == SkillType.凯撒之击) {
                CaeserAttack.apply(this, skillUseInfo, attacker, defender);
            }
        }
        if (!attacker.isSilent()) {
            {
                RuneInfo rune = attacker.getOwner().getActiveRuneOf(RuneData.绝杀);
                if (rune != null && !attacker.justRevived()) {
                    Wrath.apply(this, rune.getSkillUseInfo(), attacker, defender);
                }
            }
            {
                RuneInfo rune = attacker.getOwner().getActiveRuneOf(RuneData.寒伤);
                if (rune != null && !attacker.justRevived()) {
                    CriticalAttack.apply(this, rune.getSkillUseInfo(), attacker, defender);
                }
            }
            {
                RuneInfo rune = attacker.getOwner().getActiveRuneOf(RuneData.扬旗);
                if (rune != null && !attacker.justRevived()) {
                    Pursuit.apply(this, rune.getSkillUseInfo(), attacker, defender);
                }
            }
            {
                if (!attacker.justRevived()) {
                    List<CardInfo> cards = attacker.getOwner().getField().getAliveCards();
                    for (CardInfo card : cards) {
                        for (SkillUseInfo skillUseInfo : card.getUsableNormalSkills()) {
                            if (skillUseInfo.getType() == SkillType.群体追击) {
                                Pursuit.apply(this, skillUseInfo, attacker, defender);
                            }
                        }
                    }
                }
            }
        }
    }

    public void removeTempEffects(CardInfo card) {
        if (card == null) {
            return;
        }
        for (SkillEffect effect : card.getEffects()) {
            SkillType type = effect.getCause().getType();
            if (type == SkillType.圣光 || type == SkillType.要害 || type == SkillType.暗杀 || type == SkillType.污染) {
                RacialAttackSkill.remove(this, effect.getCause(), card);
            } else if (type == SkillType.暴击 || type == SkillType.鹰眼 || type == SkillType.致命一击
                    || type == SkillType.巨斧横扫) {
                CriticalAttack.remove(this, effect.getCause(), card);
            } else if (type == SkillType.穷追猛打 || type == SkillType.灵击 || type == SkillType.死亡践踏) {
                Pursuit.remove(this, effect.getCause(), card);
            } else if (type == SkillType.背刺 || type == SkillType.大背刺 || type == SkillType.深海之力 || type == SkillType.闪耀突击 || type == SkillType.突击
                    || type == SkillType.潜伏之力) {
                BackStab.remove(this, effect.getCause(), card);
            } else if (type == SkillType.战意 || type == SkillType.鬼王之怒 || type == SkillType.大江山鬼王 || type == SkillType.正义追击
                    || type == SkillType.战神 || type == SkillType.一夫之勇) {
                Wrath.remove(this, effect.getCause(), card);
            } else if (type == SkillType.趁胜追击) {
                WinningPursuit.remove(this, effect.getCause(), card);
            } else if (type == SkillType.复仇) {
                Revenge.remove(this, effect.getCause(), card);
            } else if (type == SkillType.奋战 || type == SkillType.英勇打击) {
                BraveFight.remove(this, effect.getCause(), card);
            } else if (type == SkillType.樱魂) {
                BraveFight.remove(this, effect.getCause().getAttachedUseInfo2(), card);
            } else if (type == SkillType.振奋 || type == SkillType.会心一击) {
                Arouse.remove(this, effect.getCause(), card);
            } else if (type == SkillType.英雄杀手 || type == SkillType.英雄之敌 || type == SkillType.头槌破门
                    || type == SkillType.龙战于野 || type == SkillType.超级英雄杀手 || type == SkillType.杀手回梦
                    || type == SkillType.陷阵) {
                HeroKiller.remove(this, effect.getCause(), card);
            } else if (type == SkillType.夜袭) {
                HeroKiller.remove(this, effect.getCause(), card);
            } else if (type == SkillType.凯撒之击) {
                CaeserAttack.remove(this, effect.getCause(), card);
            } else if (type == SkillType.神兵召唤 || type == SkillType.天降神兵 || type == SkillType.神兵降临 || type == SkillType.厨具召唤 || type == SkillType.觉醒神兵召唤 || type == SkillType.觉醒青龙偃月 ||
                    type == SkillType.圣器召唤 || type == SkillType.突袭 || type == SkillType.极寒裂伤 || type == SkillType.陨星攻击 || type == SkillType.王牌飞刀 || type == SkillType.混铁棍
                    || type == SkillType.阿拉希血统 || type == SkillType.星座能量清醒 || type == SkillType.夺命骨镰 || type == SkillType.破魔神兵 || type == SkillType.良驹
                    || type == SkillType.神兵天降 || type == SkillType.死亡女神 || type == SkillType.神兵 || type == SkillType.利器) {
                WeaponSummon.remove(this, effect.getCause(), card);
            }
        }
    }

    public OnDamagedResult applyDamage(EntityInfo attacker, CardInfo defender, Skill skill, int damage) throws HeroDieSignal {
        OnDamagedResult result = new OnDamagedResult();
        List<CardStatusItem> unbendingStatusItems = defender.getStatus().getStatusOf(CardStatusType.不屈);

        for (SkillUseInfo skillUseInfo : defender.getAllNormalSkills()) {
            if (skillUseInfo.getType() == SkillType.魔族之血 || skillUseInfo.getType() == SkillType.邪甲术 || skillUseInfo.getType() == SkillType.不朽原核
                    || skillUseInfo.getType() == SkillType.白袍银甲 || skillUseInfo.getType() == SkillType.魔王之血 || skillUseInfo.getType() == SkillType.魔神加护
                    || skillUseInfo.getType() == SkillType.嗜血潜能 || skillUseInfo.getType() == SkillType.灵木之体 || skillUseInfo.getType() == SkillType.神赐之躯
                    || skillUseInfo.getType() == SkillType.异变 || skillUseInfo.getType() == SkillType.魔女之息 || skillUseInfo.getType() == SkillType.坚不可摧) {

                //减伤技能只对敌方造成的伤害生效
                if (attacker.getOwner() == defender.getOwner()) {
                    break;
                }

                if (isPhysicalAttackSkill(skill)) {
                    if (attacker instanceof CardInfo) {
                        if (resolveStopBlockSkill(skillUseInfo.getSkill(), (CardInfo) attacker, defender)) {
                            break;
                        }
                    }
                }
                damage = (damage - skillUseInfo.getSkill().getImpact()) > 0 ? (damage - skillUseInfo.getSkill().getImpact()) : 0;
                stage.getUI().useSkill(defender, skillUseInfo.getSkill(), true);
                break;
            }
        }

        if (!unbendingStatusItems.isEmpty()) {
            if (skill != null && skill.getType().containsTag(SkillTag.法术扼杀) && damage > 0) {
                this.removeStatus(defender, CardStatusType.不屈);
            }
            //攻击不屈卡不能吸血 2018-07-08 add
//            else if (skill != null && (skill.getType() == SkillType.吸血 || skill.getType() == SkillType.蛇吻 || skill.getType() == SkillType.鬼彻
//                    || skill.getType() == SkillType.武圣 || skill.getType() == SkillType.村正 || skill.getType() == SkillType.樱魂 || skill.getType() == SkillType.狂暴|| skill.getType() == SkillType.神亭酣战|| skill.getType() == SkillType.肉食者)) {
//                // 不屈状态下可以吸血
//            }
            else {
                this.getStage().getUI().unbend(defender, unbendingStatusItems.get(0));
                damage = 0;
            }
        }
        int actualDamage = defender.applyDamage(damage);
        result.originalDamage += damage;
        result.actualDamage += actualDamage;

        if (defender.getHP() > 0 && isPhysicalAttackSkill(skill)) {
            for (SkillUseInfo skillUseInfo : defender.getUsableNormalSkills()) {
                if (skillUseInfo.getType() == SkillType.游击 || skillUseInfo.getType() == SkillType.周旋 || skillUseInfo.getType() == SkillType.死亡女神 || skillUseInfo.getType() == SkillType.虚实相生
                        || skillUseInfo.getType() == SkillType.海姆冥界 || skillUseInfo.getType() == SkillType.迂回) {
                    Player opponentPlayer = this.getStage().getOpponent(defender.getOwner());
                    ReformingDefender.apply(this, skillUseInfo, defender, opponentPlayer);
                }
            }
        }
        if (defender.getHP() <= 0) {
            result.cardDead = true;
            boolean scapegoat = false;
            DeadType scapegoatType = null;
            if (skill != null && (skill.getType() == SkillType.古神的低语 || skill.getType() == SkillType.古神的低语伪)) {
                result.cardDead = true;
            } else {
                for (SkillUseInfo skillUseInfo : defender.getUsableNormalSkills()) {
                    if (skillUseInfo.getType() == SkillType.不屈 ||
                            skillUseInfo.getType() == SkillType.鬼王之怒 ||
                            skillUseInfo.getType() == SkillType.坚毅 ||
                            skillUseInfo.getType() == SkillType.暗之献祭 ||
                            skillUseInfo.getType() == SkillType.暗之归还 ||
                            skillUseInfo.getType() == SkillType.武形秘法 ||
                            skillUseInfo.getType() == SkillType.蝶息 ||
                            skillUseInfo.getType() == SkillType.逆鳞 ||
                            skillUseInfo.getType() == SkillType.坚韧 ||
                            skillUseInfo.getType() == SkillType.空城 ||
                            skillUseInfo.getType() == SkillType.嗜魔之体 ||
                            skillUseInfo.getType() == SkillType.不灭之魂 ||
                            skillUseInfo.getType() == SkillType.突围 ||
                            skillUseInfo.getType() == SkillType.不息之风 ||
                            skillUseInfo.getType() == SkillType.贪魔伐罪 ||
                            skillUseInfo.getType() == SkillType.三界行者 ||
                            skillUseInfo.getType() == SkillType.圣灵护佑 ||
                            skillUseInfo.getType() == SkillType.疏影横斜 ||
                            skillUseInfo.getType() == SkillType.堕落之印 ||
                            skillUseInfo.getType() == SkillType.星光重耀 ||
                            skillUseInfo.getType() == SkillType.刀魂附体 ||
                            skillUseInfo.getType() == SkillType.剧毒之咬 ||
                            skillUseInfo.getType() == SkillType.顽疾) {
                        // BUGBUG: The original game does not set cardDead to false
                        // result.cardDead = false
                        result.unbending = Unbending.apply(skillUseInfo, this, defender);
                    } else if (skillUseInfo.getType() == SkillType.怨起) {
                        result.unbending = UnbendingAwaken.apply(skillUseInfo, this, defender);
                    } else if (skillUseInfo.getType() == SkillType.战团之魂 || skillUseInfo.getType() == SkillType.七罪) {
                        result.unbending = UnbendingAwaken.applyMore(skillUseInfo, this, defender);
                    } else if (skillUseInfo.getType() == SkillType.太平清领书 || skillUseInfo.getType() == SkillType.断绝之翼 || skillUseInfo.getType() == SkillType.审判之翼
                            || skillUseInfo.getType() == SkillType.毁灭之翼) {
                        Player opponent = this.getStage().getOpponent(defender.getOwner());
                        result.unbending = UnbendingAwaken.applyOpponentLess(skillUseInfo, this, defender, opponent);
                    } else if (skillUseInfo.getType() == SkillType.赤焰战场 || skillUseInfo.getType() == SkillType.独行) {
                        Player opponent = this.getStage().getOpponent(defender.getOwner());
                        result.unbending = UnbendingAwaken.applyLess(skillUseInfo, this, defender, opponent);
                    } else if (skillUseInfo.getType() == SkillType.蚀月军团) {
                        result.unbending = UnbendingAwaken.applyCardName(skillUseInfo, this, defender, "月蚀兽");
                    } else if (skillUseInfo.getType() == SkillType.沐浴龙血) {
                        result.unbending = UnbendingAwaken.applyCardName(skillUseInfo, this, defender, "徒壁幼龙");
                    } else if (skillUseInfo.getType() == SkillType.一夫当关) {
                        result.unbending = UnbendingAwaken.applyCardName(skillUseInfo, this, defender, "旷世绝恋·貂蝉");
                    } else if (skillUseInfo.getType() == SkillType.铿锵排奡) {
                        result.unbending = UnbendingAwaken.applySpecialCardName(skillUseInfo, this, defender);
                    } else if (skillUseInfo.getType() == SkillType.俊才 || skillUseInfo.getType() == SkillType.永生审判) {
                        result.unbending = UnbendingAwaken.applyCount(skillUseInfo, this, defender);
                    } else if (skillUseInfo.getType() == SkillType.代罪) {
                        scapegoatType = Scapegoat.apply(skillUseInfo, this, defender,attacker,skill);
                        scapegoat = true;
                    } else if (skillUseInfo.getType() == SkillType.紊乱) {
                        Player opponent = this.getStage().getOpponent(defender.getOwner());
                        scapegoatType = Disorder.apply(skillUseInfo, this, defender, opponent,attacker,skill);
                        scapegoat = true;
                    } else if (skillUseInfo.getType() == SkillType.神鬼之医) {
                        Player opponent = this.getStage().getOpponent(defender.getOwner());
                        scapegoatType = DisorderMult.apply(skillUseInfo, this, defender, opponent, 1,0,1,attacker,skill);
                        scapegoat = true;
                    } else if (skillUseInfo.getType() == SkillType.逆转之矢 || skillUseInfo.getType() == SkillType.风雨无阻) {
                        Player opponent = this.getStage().getOpponent(defender.getOwner());
                        scapegoatType = DisorderMult.apply(skillUseInfo, this, defender, opponent, 1,1,9999,attacker,skill);
                        scapegoat = true;
                    }
                }
                //判断执念
                if(!result.unbending&&!(scapegoat&&scapegoatType==DeadType.FeignDead)){
                    result.unbending = Obsession.explode(this,defender);
                }
            }
            if(scapegoat){
                DeadType deadType = scapegoatType;
                if (deadType == DeadType.SoulCrushed) {
                    result.soulCrushed = true;
                } else if (deadType == DeadType.SoulControlDead) {
                    result.soulControlDead = true;
                }else if(deadType == DeadType.FeignDead){
                    result.unbending = true;
                    result.cardDead = false;
                }
            } else if (!result.unbending) {
                DeadType deadType = cardDead(attacker, skill, defender);
                if (deadType == DeadType.SoulCrushed) {
                    result.soulCrushed = true;
                } else if (deadType == DeadType.SoulControlDead) {
                    result.soulControlDead = true;
                }
            } else {
                result.cardDead = false;
            }
        }

        if (result.cardDead) {

        }
        return result;
    }

    public DeadType cardDead(EntityInfo attacker, Skill killingSkill, CardInfo deadCard) {
        if (deadCard.hasDeadOnce()) {
            // 由于技能多重触发可能造成cardDead被多次调用
            return DeadType.AlreadyDead;
        }
        this.stage.getUI().cardDead(deadCard);
        Player owner = deadCard.getOwner();
        deadCard.getPosition(); // Save cached position
        Field field = owner.getField();
        // Set field position to null
        for (int i = 0; i < field.size(); ++i) {
            CardInfo card = field.getCard(i);
            if (deadCard == card) {
                field.expelCard(i);
                if (card.getStatus().containsStatus(CardStatusType.召唤)) {
                    // 被召唤的卡牌不进入墓地，而是直接死亡
                    return DeadType.PhantomDiminished;
                }
                if (!(card.isDemon() || card.isBoss())) {
                    List<CardStatusItem> astheniaStatus = card.getStatus().getStatusOf(CardStatusType.虚化);
                    if (!astheniaStatus.isEmpty()) {
                        for (CardStatusItem cardStatusItem : astheniaStatus) {
                            if (Asthenia.explode(this, deadCard, cardStatusItem.getEffect())) {
                                card.restoreOwner();
                                owner.getOutField().addCard(card);
                                return DeadType.SoulCrushed;
                            }
                        }
                    }
                    if (killingSkill != null && (killingSkill.getType() == SkillType.古神的低语 || killingSkill.getType() == SkillType.古神的低语伪)) {
                        int rate = killingSkill.getImpact2();
                        if (getStage().getRandomizer().roll100(rate)) {
                            this.getStage().getUI().useSkill(attacker, killingSkill, true);
                            card.restoreOwner();
                            owner.getOutField().addCard(card);
                            return DeadType.SoulCrushed;
                        }
                    }
                }
                if (killingSkill != null && killingSkill.getType().containsTag(SkillTag.法术扼杀) && deadCard.getRace() != Race.BOSS && deadCard.getRace() != Race.DEMON) {
                    this.getStage().getUI().useSkill(attacker, killingSkill, true);
                    card.restoreOwner();
                    owner.getOutField().addCard(card);
                    return DeadType.SoulCrushed;
                }
                if (killingSkill != null && (killingSkill.getType() == SkillType.对决 || killingSkill.getType() == SkillType.封魔神剑 || killingSkill.getType() == SkillType.花果山美猴王
                        || killingSkill.getType() == SkillType.黑暗侵袭 || killingSkill.getType() == SkillType.暗影奇袭 || killingSkill.getType() == SkillType.裂地斩
                        || killingSkill.getType() == SkillType.龙斗)
                        && deadCard.getRace() != Race.BOSS && deadCard.getRace() != Race.DEMON) {
                    if (this.getStage().getRandomizer().roll100(killingSkill.getImpact2())) {
                        this.getStage().getUI().useSkill(attacker, killingSkill, true);
                        card.restoreOwner();
                        owner.getOutField().addCard(card);
                        return DeadType.SoulCrushed;
                    }
                }
                if (attacker instanceof CardInfo && this.isPhysicalAttackSkill(killingSkill)) {
                    CardInfo attackCard = (CardInfo) attacker;
                    Obsession.remove(this,attackCard); //物理攻击死亡则移除执念技能
                    if (deadCard.getRace() != Race.BOSS && deadCard.getRace() != Race.DEMON) {
                        //逆流符文
                        RuneInfo rune = attacker.getOwner().getActiveRuneOf(RuneData.逆流);
                        if (rune != null && attackCard.getRuneActive()) {
                            this.getStage().getUI().useSkill(attacker, rune.getSkill(), true);
                            card.restoreOwner();
                            owner.getOutField().addCard(card);
                            return DeadType.SoulCrushed;
                        }
                        for (SkillUseInfo skillUseInfo : attackCard.getUsableNormalSkills()) {
                            if (deadCard.getRace() == Race.BOSS || deadCard.getRace() == Race.DEMON) {
                                break;
                            }
                            if (skillUseInfo.getType() == SkillType.扼杀 || skillUseInfo.getType() == SkillType.无双 || skillUseInfo.getType() == SkillType.双斩
                                    || skillUseInfo.getType() == SkillType.淘汰 || skillUseInfo.getType() == SkillType.溶骨的毒酒 || skillUseInfo.getType() == SkillType.狂暴之刃
                                    || skillUseInfo.getType() == SkillType.潜伏之力) {
                                this.getStage().getUI().useSkill(attacker, skillUseInfo.getSkill(), true);
                                card.restoreOwner();
                                owner.getOutField().addCard(card);
                                return DeadType.SoulCrushed;
                            } else if (skillUseInfo.getType() == SkillType.追魂 || skillUseInfo.getType() == SkillType.义绝 || skillUseInfo.getType() == SkillType.勤能补拙) {
                                int impact2 = skillUseInfo.getSkill().getImpact2();
                                if (getStage().getRandomizer().roll100(impact2)) {
                                    this.getStage().getUI().useSkill(attacker, skillUseInfo.getSkill(), true);
                                    card.restoreOwner();
                                    owner.getOutField().addCard(card);
                                    return DeadType.SoulCrushed;
                                }
                            }
                        }
                    }
                }
                if (card.getOriginalOwner() != null && card.getOriginalOwner() != card.getOwner()) {
                    Player opponent = deadCard.getOwner();
                    card.restoreOwner();
                    //    card.getOwner().getGrave().addCard(card);
                    card.getOwner().getBeforeDeath().addCard(card);
                    card.switchOwner(opponent);
                    return DeadType.SoulControlDead;
                }
                card.restoreOwner();
                // card.getOwner().getGrave().addCard(card);
                card.getOwner().getBeforeDeath().addCard(card);
                break;
            }
        }
        return DeadType.Normal;
    }

    public DeadType cardDeadBeforeScapegoat(EntityInfo attacker, Skill killingSkill, CardInfo deadCard) {
        if (deadCard.hasDeadOnce()) {
            // 由于技能多重触发可能造成cardDead被多次调用
            return DeadType.AlreadyDead;
        }
        this.stage.getUI().cardDead(deadCard);
        Player owner = deadCard.getOwner();
        deadCard.getPosition(); // Save cached position
        Field field = owner.getField();
        for (int i = 0; i < field.size(); ++i) {
            CardInfo card = field.getCard(i);
            if (deadCard == card) {
                if (card.getStatus().containsStatus(CardStatusType.召唤)) {
                    // 被召唤的卡牌不进入墓地，而是直接死亡
                    field.expelCard(i);
                    return DeadType.PhantomDiminished;
                }
                if (!(card.isDemon() || card.isBoss())) {
                    List<CardStatusItem> astheniaStatus = card.getStatus().getStatusOf(CardStatusType.虚化);
                    if (!astheniaStatus.isEmpty()) {
                        for (CardStatusItem cardStatusItem : astheniaStatus) {
                            if (Asthenia.explode(this, deadCard, cardStatusItem.getEffect())) {
                                field.expelCard(i);
                                card.restoreOwner();
                                owner.getOutField().addCard(card);
                                return DeadType.SoulCrushed;
                            }
                        }
                    }
                    if (killingSkill != null && (killingSkill.getType() == SkillType.古神的低语 || killingSkill.getType() == SkillType.古神的低语伪)) {
                        int rate = killingSkill.getImpact2();
                        if (getStage().getRandomizer().roll100(rate)) {
                            this.getStage().getUI().useSkill(attacker, killingSkill, true);
                            field.expelCard(i);
                            card.restoreOwner();
                            owner.getOutField().addCard(card);
                            return DeadType.SoulCrushed;
                        }
                    }
                }
                if (killingSkill != null && killingSkill.getType().containsTag(SkillTag.法术扼杀) && deadCard.getRace() != Race.BOSS && deadCard.getRace() != Race.DEMON) {
                    this.getStage().getUI().useSkill(attacker, killingSkill, true);
                    field.expelCard(i);
                    card.restoreOwner();
                    owner.getOutField().addCard(card);
                    return DeadType.SoulCrushed;
                }
                if (killingSkill != null && (killingSkill.getType() == SkillType.对决 || killingSkill.getType() == SkillType.封魔神剑 || killingSkill.getType() == SkillType.花果山美猴王
                        || killingSkill.getType() == SkillType.黑暗侵袭 || killingSkill.getType() == SkillType.暗影奇袭 || killingSkill.getType() == SkillType.裂地斩
                        || killingSkill.getType() == SkillType.龙斗)
                        && deadCard.getRace() != Race.BOSS && deadCard.getRace() != Race.DEMON) {
                    if (this.getStage().getRandomizer().roll100(killingSkill.getImpact2())) {
                        this.getStage().getUI().useSkill(attacker, killingSkill, true);
                        field.expelCard(i);
                        card.restoreOwner();
                        owner.getOutField().addCard(card);
                        return DeadType.SoulCrushed;
                    }
                }
                if (attacker instanceof CardInfo && this.isPhysicalAttackSkill(killingSkill)) {
                    CardInfo attackCard = (CardInfo) attacker;
                    if (deadCard.getRace() != Race.BOSS && deadCard.getRace() != Race.DEMON) {
                        //逆流符文
                        RuneInfo rune = attacker.getOwner().getActiveRuneOf(RuneData.逆流);
                        if (rune != null && attackCard.getRuneActive()) {
                            this.getStage().getUI().useSkill(attacker, rune.getSkill(), true);
                            field.expelCard(i);
                            card.restoreOwner();
                            owner.getOutField().addCard(card);
                            return DeadType.SoulCrushed;
                        }
                        for (SkillUseInfo skillUseInfo : attackCard.getUsableNormalSkills()) {
                            if (deadCard.getRace() == Race.BOSS || deadCard.getRace() == Race.DEMON) {
                                break;
                            }
                            if (skillUseInfo.getType() == SkillType.扼杀 || skillUseInfo.getType() == SkillType.无双 || skillUseInfo.getType() == SkillType.双斩
                                    || skillUseInfo.getType() == SkillType.淘汰 || skillUseInfo.getType() == SkillType.溶骨的毒酒 || skillUseInfo.getType() == SkillType.狂暴之刃
                                    || skillUseInfo.getType() == SkillType.潜伏之力) {
                                this.getStage().getUI().useSkill(attacker, skillUseInfo.getSkill(), true);
                                field.expelCard(i);
                                card.restoreOwner();
                                owner.getOutField().addCard(card);
                                return DeadType.SoulCrushed;
                            } else if (skillUseInfo.getType() == SkillType.追魂 || skillUseInfo.getType() == SkillType.义绝 || skillUseInfo.getType() == SkillType.勤能补拙) {
                                int impact2 = skillUseInfo.getSkill().getImpact2();
                                if (getStage().getRandomizer().roll100(impact2)) {
                                    this.getStage().getUI().useSkill(attacker, skillUseInfo.getSkill(), true);
                                    field.expelCard(i);
                                    card.restoreOwner();
                                    owner.getOutField().addCard(card);
                                    return DeadType.SoulCrushed;
                                }
                            }
                        }
                    }
                }
                if (card.getOriginalOwner() != null && card.getOriginalOwner() != card.getOwner()) {
                    Player opponent = deadCard.getOwner();
                    field.expelCard(i);
                    card.restoreOwner();
                    //    card.getOwner().getGrave().addCard(card);
                    card.getOwner().getBeforeDeath().addCard(card);
                    card.switchOwner(opponent);
                    return DeadType.SoulControlDead;
                }
//                card.restoreOwner();
                // card.getOwner().getGrave().addCard(card);
//                card.getOwner().getBeforeDeath().addCard(card);
                break;
            }
        }
        return DeadType.Normal;
    }

    public void attackHero(EntityInfo attacker, Player defenderPlayer, Skill cardSkill, int damage)
            throws HeroDieSignal {
        if (attacker == null) {
            return;
        }
        try {
            if (isPhysicalAttackSkill(cardSkill) && attacker.getStatus().containsStatus(CardStatusType.麻痹)) {
                return;
            }
            stage.getUI().useSkillToHero(attacker, defenderPlayer, cardSkill);
            if (damage >= 0) {
                if (!(cardSkill != null && (cardSkill.getType() == SkillType.背水 || cardSkill.getType() == SkillType.良禽择木 || cardSkill.getType() == SkillType.反向溅射
                        || cardSkill.getType() == SkillType.画境乾坤 || cardSkill.getType() == SkillType.翼刃
                        || cardSkill.getType() == SkillType.自动扣血))) {
                    CounterAttackHero.explode(this, attacker, defenderPlayer, damage);
                }
                if (damage > 0) {
                    if (isPhysicalAttackSkill(cardSkill)) {
                        if (attacker instanceof CardInfo) {
                            CardInfo attackerCard = (CardInfo) attacker;
                            for (EquipmentInfo equipmentInfo : defenderPlayer.getEquipmentBox().getEquipmentInfos()) {
                                for (SkillUseInfo equipmentSkillUserInfo : equipmentInfo.getSkillUseInfoList()) {
                                    if (equipmentSkillUserInfo.getType() == SkillType.装备护体) {
                                        if (!resolveStopBlockSkill(equipmentSkillUserInfo.getSkill(), attackerCard, defenderPlayer)) {
                                            int impact = equipmentSkillUserInfo.getSkill().getImpact3();
                                            if (impact > 0) {
                                                damage = damage * impact / 100;
                                            }
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备衰老) {
                                        damage = EnergyDrainByEquipment.apply(equipmentSkillUserInfo, this, attackerCard, equipmentInfo, damage);
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备水甲) {
                                        if (!resolveStopBlockSkill(equipmentSkillUserInfo.getSkill(), attackerCard, defenderPlayer)) {
                                            damage = WaterArmorByEquipment.apply(equipmentSkillUserInfo.getSkill(), this, attackerCard, defenderPlayer, damage);
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备腐朽) {
                                        LifeDrainByEquipment.apply(equipmentSkillUserInfo, this, attackerCard, equipmentInfo);
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备流云) {
                                        if (!resolveStopBlockSkill(equipmentSkillUserInfo.getSkill(), attackerCard, defenderPlayer)) {
                                            int dodgeRate = equipmentSkillUserInfo.getSkill().getImpact3();
                                            if (getStage().getRandomizer().roll100(dodgeRate)) {
                                                damage = 0;
                                            }
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备吸收) {
                                        if (!resolveStopBlockSkill(equipmentSkillUserInfo.getSkill(), attackerCard, defenderPlayer)) {
                                            int impact = equipmentSkillUserInfo.getSkill().getImpact3();
                                            if (impact > 0) {
                                                damage = damage - impact;
                                            }
                                            if (damage < 0) {
                                                damage = 0;
                                            }
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备逆刃) {
                                        CounterAttackByEquipment.apply(equipmentSkillUserInfo.getSkill(), this, attackerCard, equipmentInfo, damage);
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备石肤) {
                                        if (!resolveStopBlockSkill(equipmentSkillUserInfo.getSkill(), attackerCard, defenderPlayer)) {
                                            int impact = equipmentSkillUserInfo.getSkill().getImpact3();
                                            if (damage > impact) {
                                                damage = impact;
                                            }
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备炙炎) {
                                        BurningByEquipment.apply(equipmentSkillUserInfo, this, attackerCard, equipmentInfo);
                                    }
                                }
                            }
                        }
                    }
                }
                int remainingDamage = damage;
                if (!(cardSkill != null && (cardSkill.getType() == SkillType.自动扣血))) {
                    remainingDamage = ImpregnableDefenseHeroBuff.explode(this, attacker, defenderPlayer, remainingDamage);
                }
                if (remainingDamage > defenderPlayer.getHP()) {
                    remainingDamage = defenderPlayer.getHP();
                }
                remainingDamage = this.resolveAttackHeroBlockingSkills(attacker, defenderPlayer, cardSkill, remainingDamage);
                if (defenderPlayer.getHP() < remainingDamage) {
                    remainingDamage = this.resolveHeroUnbending(attacker, defenderPlayer, cardSkill, remainingDamage);
                }
                damage = remainingDamage;
                if (remainingDamage > 0) {
                    stage.getUI().attackHero(attacker, defenderPlayer, cardSkill, remainingDamage);
                    defenderPlayer.setHP(defenderPlayer.getHP() - remainingDamage);
                }
                if (damage > 0) {
                    if (isPhysicalAttackSkill(cardSkill)) {
                        Player attackPlayer = attacker.getOwner();
                        if (damage > 0) {
                            for (EquipmentInfo equipmentInfo : attackPlayer.getEquipmentBox().getEquipmentInfos()) {
                                for (SkillUseInfo equipmentSkillUserInfo : equipmentInfo.getSkillUseInfoList()) {
                                    if (equipmentSkillUserInfo.getType() == SkillType.装备裂魂) {
                                        int skillNumber = equipmentSkillUserInfo.getSkillNumber();
                                        if (skillNumber < 0) {
                                            equipmentSkillUserInfo.setSkillNumber(0);
                                        }
                                        if (skillNumber < 1) {
                                            ContinuousFire.apply(this, equipmentSkillUserInfo, equipmentInfo, defenderPlayer);
                                            equipmentSkillUserInfo.setSkillNumber(skillNumber + 1);
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备死咒) {
                                        int skillNumber = equipmentSkillUserInfo.getSkillNumber();
                                        if (skillNumber < 0) {
                                            equipmentSkillUserInfo.setSkillNumber(0);
                                        }
                                        if (skillNumber < equipmentSkillUserInfo.getSkill().getImpact2()) {
                                            Curse.apply(this, equipmentSkillUserInfo.getSkill(), equipmentInfo, defenderPlayer);
                                            equipmentSkillUserInfo.setSkillNumber(skillNumber + 1);
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备箭雨) {
                                        int skillNumber = equipmentSkillUserInfo.getSkillNumber();
                                        int impact = equipmentSkillUserInfo.getSkill().getImpact();
                                        if (skillNumber < 0) {
                                            equipmentSkillUserInfo.setSkillNumber(0);
                                        }
                                        if (skillNumber < 1) {
                                            if (impact == 1) {
                                                Snipe.apply(equipmentSkillUserInfo, equipmentSkillUserInfo.getSkill(), this, attacker, defenderPlayer, 3);
                                            } else if (impact == 2) {
                                                Snipe.apply(equipmentSkillUserInfo, equipmentSkillUserInfo.getSkill(), this, attacker, defenderPlayer, 5);
                                            } else if (impact == 3) {
                                                Snipe.apply(equipmentSkillUserInfo, equipmentSkillUserInfo.getSkill(), this, attacker, defenderPlayer, -1);
                                            }
                                            equipmentSkillUserInfo.setSkillNumber(skillNumber + 1);
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备操魂) {
                                        int skillNumber = equipmentSkillUserInfo.getSkillNumber();
                                        int impact2 = equipmentSkillUserInfo.getSkill().getImpact2();
                                        if (skillNumber < 0) {
                                            if (impact2 == 1) {
                                                equipmentSkillUserInfo.setSkillNumber(10);
                                                skillNumber = 10;
                                            } else if (impact2 == 2) {
                                                equipmentSkillUserInfo.setSkillNumber(9999);
                                                skillNumber = 9999;
                                            } else if (impact2 == 3) {
                                                equipmentSkillUserInfo.setSkillNumber(9999);
                                                skillNumber = 9999;
                                            }
                                        }
                                        if (skillNumber > 0 && !equipmentSkillUserInfo.getIsUsed()) {
                                            if (impact2 < 3) {
                                                ResurrectionByEquipment.apply(this, equipmentSkillUserInfo, equipmentInfo);
                                            } else {
                                                RegressionSoul.apply(this, equipmentSkillUserInfo, attackPlayer, defenderPlayer);
                                            }
                                            equipmentSkillUserInfo.setIsUsed(true);
                                            equipmentSkillUserInfo.setSkillNumber(skillNumber - 1);
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备威压) {
                                        int skillNumber = equipmentSkillUserInfo.getSkillNumber();
                                        if (skillNumber < 0) {
                                            equipmentSkillUserInfo.setSkillNumber(0);
                                        }
                                        if (skillNumber < equipmentSkillUserInfo.getSkill().getImpact2()) {
                                            WeakenAllByEquipment.apply(this, equipmentSkillUserInfo, equipmentInfo, defenderPlayer);
                                            equipmentSkillUserInfo.setSkillNumber(skillNumber + 1);
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备星辉) {
                                        int skillNumber = equipmentSkillUserInfo.getSkillNumber();
                                        if (skillNumber < 0) {
                                            equipmentSkillUserInfo.setSkillNumber(0);
                                        }
                                        if (skillNumber < 1) {
                                            int impact = equipmentSkillUserInfo.getSkill().getLevel();
                                            int impact2 = equipmentSkillUserInfo.getSkill().getImpact2();
                                            int impact3 = equipmentSkillUserInfo.getSkill().getImpact3();
                                            if (impact == 1) {
                                                IceMagic.apply(equipmentSkillUserInfo, this, equipmentInfo, attacker.getOwner(), 1, impact2, impact3);
                                            } else if (impact == 2) {
                                                IceMagic.apply(equipmentSkillUserInfo, this, equipmentInfo, attacker.getOwner(), 3, impact2, impact3);
                                            } else if (impact == 3) {
                                                IceMagic.apply(equipmentSkillUserInfo, this, equipmentInfo, attacker.getOwner(), -1, impact2, impact3);
                                            }
                                            equipmentSkillUserInfo.setSkillNumber(skillNumber + 1);
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备绝杀) {
                                        int skillNumber = equipmentSkillUserInfo.getSkillNumber();
                                        if (skillNumber < 0) {
                                            equipmentSkillUserInfo.setSkillNumber(0);
                                        }
                                        if (skillNumber < 1) {
                                            int impact = equipmentSkillUserInfo.getSkill().getLevel();
                                            if (impact == 1) {
                                                UnderworldCallByEquipment.apply(this, equipmentSkillUserInfo.getSkill(), equipmentInfo, defenderPlayer, 1);
                                            } else if (impact == 2) {
                                                UnderworldCallByEquipment.apply(this, equipmentSkillUserInfo.getSkill(), equipmentInfo, defenderPlayer, 2);
                                            } else if (impact == 3) {
                                                UnderworldCallByEquipment.apply(this, equipmentSkillUserInfo.getSkill(), equipmentInfo, defenderPlayer, 3);
                                            }
                                            equipmentSkillUserInfo.setSkillNumber(skillNumber + 1);
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备震击) {
                                        int skillNumber = equipmentSkillUserInfo.getSkillNumber();
                                        if (skillNumber < 0) {
                                            equipmentSkillUserInfo.setSkillNumber(0);
                                        }
                                        if (skillNumber < 1) {
                                            int impact = equipmentSkillUserInfo.getSkill().getLevel();
                                            if (impact == 1) {
                                                RedGun.apply(equipmentSkillUserInfo, this, equipmentInfo, defenderPlayer, 1);
                                            } else if (impact == 2) {
                                                RedGun.apply(equipmentSkillUserInfo, this, equipmentInfo, defenderPlayer, 3);
                                            } else if (impact == 3) {
                                                RedGun.apply(equipmentSkillUserInfo, this, equipmentInfo, defenderPlayer, 5);
                                            }
                                            equipmentSkillUserInfo.setSkillNumber(skillNumber + 1);
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备顺劈) {
                                        int impact = equipmentSkillUserInfo.getSkill().getLevel();
                                        if (impact == 1) {
                                            RedGunByEquipment.apply(equipmentSkillUserInfo, this, equipmentInfo, defenderPlayer, 1);
                                        } else if (impact == 2) {
                                            RedGunByEquipment.apply(equipmentSkillUserInfo, this, equipmentInfo, defenderPlayer, 1);
                                        } else if (impact == 3) {
                                            RedGunByEquipment.apply(equipmentSkillUserInfo, this, equipmentInfo, defenderPlayer, 1);
                                        }
                                    } else if (equipmentSkillUserInfo.getType() == SkillType.装备饮魂) {
                                        int level = equipmentSkillUserInfo.getSkill().getLevel();
                                        int skillNumber = equipmentSkillUserInfo.getSkillNumber();
                                        if (skillNumber < 0) {
                                            equipmentSkillUserInfo.setSkillNumber(0);
                                        }
                                        if (level == 1) {
                                            if (skillNumber < 3) {
                                                Pray.apply(equipmentSkillUserInfo.getSkill(), this, equipmentInfo);
                                            }
                                        } else if (level == 2) {
                                            if (skillNumber < 5) {
                                                Pray.apply(equipmentSkillUserInfo.getSkill(), this, equipmentInfo);
                                            }
                                        } else if (level == 3) {
                                            Pray.apply(equipmentSkillUserInfo.getSkill(), this, equipmentInfo);
                                        }
                                        equipmentSkillUserInfo.setSkillNumber(skillNumber + 1);
                                    }
                                }
                            }
                        }
                    } else if (damage > 0 && cardSkill != null && (cardSkill.getType() == SkillType.穿刺 || cardSkill.getType() == SkillType.英雄之敌
                            || cardSkill.getType() == SkillType.头槌破门 || cardSkill.getType() == SkillType.横扫千军
                            || cardSkill.getType() == SkillType.一夫之勇 || cardSkill.getType() == SkillType.精准打击 || cardSkill.getType() == SkillType.精准射击)) {
                        if (attacker instanceof CardInfo) {
                            CardInfo attackerCard = (CardInfo) attacker;
                            for (SkillUseInfo skillUseInfo : attackerCard.getUsableNormalSkills()) {
                                if (skillUseInfo.getType() == SkillType.灵魂腐朽 || skillUseInfo.getType() == SkillType.拔刀斩) {
                                    PsionicDecay.apply(skillUseInfo, this, attackerCard, defenderPlayer, damage);
                                }
                            }
                        }
                    }
                }
            } else {
                if (defenderPlayer.getHP() - damage > defenderPlayer.getMaxHP()) {
                    damage = defenderPlayer.getHP() - defenderPlayer.getMaxHP();
                }
                stage.getUI().healHero(attacker, defenderPlayer, cardSkill, -damage);
                defenderPlayer.setHP(defenderPlayer.getHP() - damage);
            }
            if (defenderPlayer.getHP() > defenderPlayer.getMaxHP()) {
                throw new CardFantasyRuntimeException("Hero MaxHP < HP");
            }
        } finally {
            if (attacker instanceof CardInfo) {
                CardInfo attackerCard = (CardInfo) attacker;
                this.removeStatus(attackerCard, CardStatusType.麻痹);
            }
        }
    }

    public void resolveExtraAttackHeroSkills(CardInfo attacker, Player defenderHero, Boolean firstSkill) throws HeroDieSignal {
        boolean multAttackFlag = true;
        if (!FailureSkillUseInfoList.explode(this, attacker, defenderHero)) {
            for (SkillUseInfo skillUseInfo : attacker.getUsableNormalSkills()) {
                if (skillUseInfo.getType() == SkillType.幻影军团 || skillUseInfo.getType() == SkillType.幻影奇袭
                        || skillUseInfo.getType() == SkillType.圣翼军团 || skillUseInfo.getType() == SkillType.秘术投影 || skillUseInfo.getType() == SkillType.王国卫士
                        || skillUseInfo.getType() == SkillType.血之眷顾 || skillUseInfo.getType() == SkillType.勇冠三军
                        || skillUseInfo.getType() == SkillType.深海幻影 || skillUseInfo.getType() == SkillType.蛇影重重
                        || skillUseInfo.getType() == SkillType.护身 || skillUseInfo.getType() == SkillType.快速增值) {
                    SummonWhenAttack.apply(this, skillUseInfo, attacker, 1, true, attacker.getName());
                } else if (skillUseInfo.getType() == SkillType.高级连击 && multAttackFlag) {
                    MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, null, firstSkill, 100);
                    multAttackFlag = false;
                } else if ((skillUseInfo.getType() == SkillType.狂舞 || skillUseInfo.getType() == SkillType.夺命骨镰 || skillUseInfo.getType() == SkillType.追击
                        || skillUseInfo.getType() == SkillType.连续突刺 || skillUseInfo.getType() == SkillType.采佩什 || skillUseInfo.getType() == SkillType.冥狱鞭挞) && multAttackFlag) {
                    MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, null, firstSkill, 100);
                    multAttackFlag = false;
                } else if ((skillUseInfo.getType() == SkillType.破坏之爪) && multAttackFlag) {
                    MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, null, firstSkill, 40);
                    multAttackFlag = false;
                } else if (skillUseInfo.getType() == SkillType.正义之师 && multAttackFlag) {
                    MultipleAttack.apply(this, skillUseInfo.getAttachedUseInfo1(), attacker, defenderHero, null, firstSkill, 50);
                    multAttackFlag = false;
                } else if (skillUseInfo.getType() == SkillType.离魂剑) {
                    SoulControlMutiple.apply(this, skillUseInfo, attacker, defenderHero);
                } else if ((skillUseInfo.getType() == SkillType.青龙偃月 || skillUseInfo.getType() == SkillType.盘踞之物) && multAttackFlag) {
                    MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, null, firstSkill, 1);
                    multAttackFlag = false;
                } else if ((skillUseInfo.getType() == SkillType.二段斩 || skillUseInfo.getType() == SkillType.二段斩强袭) && multAttackFlag) {
                    MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, null, firstSkill, 70);
                    multAttackFlag = false;
                } else if ((skillUseInfo.getType() == SkillType.巨象猛冲) && multAttackFlag) {
                    MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, null, firstSkill, 80);
                    multAttackFlag = false;
                } else if ((skillUseInfo.getType() == SkillType.精武) && multAttackFlag) {
                    MultipleAttack.apply(this, skillUseInfo, attacker, defenderHero, null, firstSkill, 100);
                    multAttackFlag = false;
                }
            }
        }
    }

    private int resolveAttackHeroBlockingSkills(EntityInfo attacker, Player defenderPlayer, Skill cardSkill,
                                                int damage) throws HeroDieSignal {
        int remainingDamage = damage;
        if (cardSkill == null) {
            remainingDamage = damage;
        } else if (cardSkill.getType() == SkillType.自动扣血) {
            return remainingDamage;
        }
//        boolean exitFlag=true;
//        for(;exitFlag;) {
//            exitFlag = false;
        for (CardInfo defender : defenderPlayer.getField().getAliveCards()) {
            if (!FailureSkillUseInfoList.explode(this, defender, attacker.getOwner())) {
                if (defender == null || defender.isDead()) {
                    continue;
                }
                for (SkillUseInfo defenderSkill : defender.getUsableNormalSkills()) {
                    if (defenderSkill.getType().containsTag(SkillTag.守护)) {
//                        exitFlag = true;
                        remainingDamage = Guard.apply(defenderSkill.getSkill(), cardSkill, this, attacker, defender, remainingDamage);
                        if (remainingDamage == 0) {
                            return 0;
                        }
                    }
                }
            }
        }
//        }
        return remainingDamage;
    }

    private int resolveHeroUnbending(EntityInfo attacker, Player defenderPlayer, Skill cardSkill,
                                     int damage) throws HeroDieSignal {
        int remainingDamage = damage;
        for (CardInfo defender : defenderPlayer.getField().getAliveCards()) {
            if (!FailureSkillUseInfoList.explode(this, defender, attacker.getOwner())) {
                if (defender == null || defender.isDead()) {
                    continue;
                }
                for (SkillUseInfo defenderSkill : defender.getUsableNormalSkills()) {
                    if (defenderSkill.getType() == SkillType.否决 || defenderSkill.getType() == SkillType.云隐时现) {
                        if (UnbendingHero.apply(defenderSkill, this, defender, defenderPlayer)) {
                            return 0;
                        }
                    }
                }
            }
        }
        return remainingDamage;
    }

    public void resolveCardRoundEndingSkills(CardInfo card, Player defender) throws HeroDieSignal {
        if (card == null) {
            return;
        }
        CardStatus status = card.getStatus();
        if (status.containsStatus(CardStatusType.锁定) || status.containsStatus(CardStatusType.石化)) {
            return;
        }
        for (SkillUseInfo cardSkillUseInfo : card.getUsableNormalSkills()) {
            if (cardSkillUseInfo.getType() == SkillType.回春 ||
                    cardSkillUseInfo.getType() == SkillType.自愈 ||
                    cardSkillUseInfo.getType() == SkillType.月恩术) {
                if (!FailureSkillUseInfoList.explode(this, card, defender)) {
                    Rejuvenate.apply(cardSkillUseInfo.getSkill(), this, card);
                }
            } else if (
                    cardSkillUseInfo.getType() == SkillType.圣母回声 ||
                            cardSkillUseInfo.getType() == SkillType.守护之翼 ||
                            cardSkillUseInfo.getType() == SkillType.兵 ||
                            cardSkillUseInfo.getType() == SkillType.大地吟咏 ||
                            cardSkillUseInfo.getType() == SkillType.神佑复苏 ||
                            cardSkillUseInfo.getType() == SkillType.圣光奏鸣曲 ||
                            cardSkillUseInfo.getType() == SkillType.亚平宁之蓝 ||
                            cardSkillUseInfo.getType() == SkillType.圣母咏叹调 ||
                            cardSkillUseInfo.getType() == SkillType.虚实相生) {
                Rejuvenate.apply(cardSkillUseInfo.getSkill(), this, card);
            } else if (cardSkillUseInfo.getType() == SkillType.闭月 || cardSkillUseInfo.getType() == SkillType.浴火 || cardSkillUseInfo.getType() == SkillType.护体石肤
                    || cardSkillUseInfo.getType() == SkillType.酒意 || cardSkillUseInfo.getType() == SkillType.隐匿 || cardSkillUseInfo.getType() == SkillType.异变
                    || cardSkillUseInfo.getType() == SkillType.海渊之力) {
                Rejuvenate.apply(cardSkillUseInfo.getAttachedUseInfo2().getSkill(), this, card);
            } else if (cardSkillUseInfo.getType() == SkillType.圣母吟咏) {
                PercentGetHp.apply(cardSkillUseInfo.getSkill(), this, card);
            } else if (cardSkillUseInfo.getType() == SkillType.重整 || cardSkillUseInfo.getType() == SkillType.不朽岿岩
                    || cardSkillUseInfo.getType() == SkillType.不息神盾 || cardSkillUseInfo.getType() == SkillType.再生金蝉
                    || cardSkillUseInfo.getType() == SkillType.卷土重来 || cardSkillUseInfo.getType() == SkillType.摄魂之力
                    || cardSkillUseInfo.getType() == SkillType.柳暗花明 || cardSkillUseInfo.getType() == SkillType.复仇者
                    || cardSkillUseInfo.getType() == SkillType.无限剑制 || cardSkillUseInfo.getType() == SkillType.金蝉脱壳) {
                Reforming.apply(this, cardSkillUseInfo, card, defender);
            } else if (cardSkillUseInfo.getType() == SkillType.天道无常) {
                if (defender.getField().getAliveCards().size() >= 4) {
                    Reforming.apply(this, cardSkillUseInfo, card, defender);
                }
            } else if (cardSkillUseInfo.getType() == SkillType.凤凰于飞) {
                if (defender.getField().getAliveCards().size() < 4) {
                    Reforming.apply(this, cardSkillUseInfo, card, defender);
                }
            } else if (cardSkillUseInfo.getType() == SkillType.烈火炙魂 || cardSkillUseInfo.getType() == SkillType.据守) {
                ReformingMult.apply(this, cardSkillUseInfo, card, defender);
            } else if (cardSkillUseInfo.getType() == SkillType.情况紧急) {
                if (card.getOwner().getHP() < card.getOwner().getMaxHP() * 0.5) {
                    Reforming.apply(this, cardSkillUseInfo, card, defender);
                }
            } else if (cardSkillUseInfo.getType() == SkillType.风暴雷云) {
                ReformingAwaken.apply(this, cardSkillUseInfo, card, defender, "风眼");
            } else if (cardSkillUseInfo.getType() == SkillType.一夫之勇) {
                ReformingAwaken.apply(this, cardSkillUseInfo, card, defender, "冀州双雄·文丑");
            }
        }
        if (!card.isSilent()) {
            RuneInfo rune = card.getOwner().getActiveRuneOf(RuneData.复苏);
            if (rune != null && !card.justRevived()) {
                Rejuvenate.apply(rune.getSkill(), this, card);
            }
        }
    }

    public OnDamagedResult attackCard(CardInfo attacker, CardInfo defender, SkillUseInfo skillUseInfo) throws HeroDieSignal {
        return attackCard(attacker, defender, skillUseInfo, attacker.getCurrentAT(), true);
    }

    public OnDamagedResult attackCard(CardInfo attacker, CardInfo defender, SkillUseInfo skillUseInfo, int damage, boolean firstSkill) throws HeroDieSignal {
        Skill skill = skillUseInfo == null ? null : skillUseInfo.getSkill();
        boolean bingo = !attacker.getStatus().containsStatus(CardStatusType.麻痹);
        this.stage.getUI().useSkill(attacker, defender, null, bingo);

        OnAttackBlockingResult blockingResult = stage.getResolver().resolveAttackBlockingSkills(
                attacker, defender, skill, damage);
        if (!blockingResult.isAttackable()) {
            //   this.removeStatus(attacker, CardStatusType.不屈);
            return null;
        }
        if (!FailureSkillUseInfoList.explode(this, defender, attacker.getOwner())) {
            if (skill == null) {
                for (SkillUseInfo cardSkillUseInfo : attacker.getAllUsableSkills()) {
                    if (cardSkillUseInfo.getType() == SkillType.斩杀 || cardSkillUseInfo.getType() == SkillType.送葬之刃 || cardSkillUseInfo.getType() == SkillType.页游击溃
                            || cardSkillUseInfo.getType() == SkillType.无双 || cardSkillUseInfo.getType() == SkillType.双斩 || cardSkillUseInfo.getType() == SkillType.屏息 || cardSkillUseInfo.getType() == SkillType.淘汰
                            || cardSkillUseInfo.getType() == SkillType.破坏之爪 || cardSkillUseInfo.getType() == SkillType.义绝) {
                        SuddenKill.apply(this, cardSkillUseInfo, attacker, defender, blockingResult);
                    }
                }
            }
        }

        this.stage.getUI().attackCard(attacker, defender, skill, blockingResult.getDamage());
        OnDamagedResult damagedResult = stage.getResolver().applyDamage(attacker, defender, skill, blockingResult.getDamage());
        // this.removeStatus(attacker, CardStatusType.不屈);

        resolvePostAttackSkills(attacker, defender, defender.getOwner(), skill, damagedResult.actualDamage);
        stage.getResolver().resolveDeathSkills(attacker, defender, skill, damagedResult);

        resolveExtraAttackSkills(attacker, defender, defender.getOwner(), skill, damagedResult, firstSkill);
        resolveCounterAttackSkills(attacker, defender, skill, blockingResult, damagedResult);
        resolveMultAttackSkills(attacker, defender, defender.getOwner(), skill, damagedResult, firstSkill);

        return damagedResult;
    }

    public CardInfo pickHealee(EntityInfo healer) {
        Field field = healer.getOwner().getField();
        CardInfo healee = null;
        for (CardInfo card : field.getAliveCards()) {
            if (card.getStatus().containsStatus(CardStatusType.不屈)) {
                continue;
            }
            if (healee == null || card.getLostHP() > healee.getLostHP()) {
                healee = card;
            }
        }
        return healee;
    }

    public void resolveRacialBuffSkills(CardInfo card, Field myField) {

    }

    public void resolveEnteringSkills(CardInfo card, Field myField, Field opField, CardInfo reviver) throws HeroDieSignal {

    }

    public void resolveFirstClassSummoningSkills(CardInfo card, Player player, Player enemy, boolean isMinion) throws HeroDieSignal {
        // 召唤物不享受加成
        if (!isMinion) {
            for (SkillUseInfo skillUseInfo : card.getOriginalOwner().getCardBuffs()) {
                Skill skill = skillUseInfo.getSkill();
                if (skill instanceof BuffSkill) {
                    if (!((BuffSkill) skill).canApplyTo(card)) {
                        continue;
                    }
                }
                if (skillUseInfo.getType() == SkillType.军团王国之力) {
                    LegionBuff.apply(this, card, skillUseInfo, Race.KINGDOM);
                } else if (skillUseInfo.getType() == SkillType.军团森林之力) {
                    LegionBuff.apply(this, card, skillUseInfo, Race.FOREST);
                } else if (skillUseInfo.getType() == SkillType.军团蛮荒之力) {
                    LegionBuff.apply(this, card, skillUseInfo, Race.SAVAGE);
                } else if (skillUseInfo.getType() == SkillType.军团地狱之力) {
                    LegionBuff.apply(this, card, skillUseInfo, Race.HELL);
                } else if (skillUseInfo.getType() == SkillType.原始体力调整) {
                    BasicHpBuff.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.原始攻击调整) {
                    BasicAtBuff.apply(this, skillUseInfo, card);
                }
            }
        }

        if (!card.isDead()) {
            card.applySurvivalStatus();
        }

        // Racial buff
        for (CardInfo fieldCard : player.getField().getAliveCards()) {
            // 主动种族BUFF
            if (!FailureSkillUseInfoList.explode(this, fieldCard, enemy)) {
                for (SkillUseInfo skillUseInfo : fieldCard.getUsableNormalSkills()) {
                    if (skillUseInfo.getType() == SkillType.王国之力) {
                        RacialBuff.apply(this, skillUseInfo, fieldCard, Race.KINGDOM, SkillEffectType.ATTACK_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.王国守护) {
                        RacialBuff.apply(this, skillUseInfo, fieldCard, Race.KINGDOM, SkillEffectType.MAXHP_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.森林之力) {
                        RacialBuff.apply(this, skillUseInfo, fieldCard, Race.FOREST, SkillEffectType.ATTACK_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.森林守护) {
                        RacialBuff.apply(this, skillUseInfo, fieldCard, Race.FOREST, SkillEffectType.MAXHP_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.森之星河) {
                        RacialBuff.apply(this, skillUseInfo.getAttachedUseInfo1(), fieldCard, Race.FOREST, SkillEffectType.ATTACK_CHANGE);
                        RacialBuff.apply(this, skillUseInfo.getAttachedUseInfo2(), fieldCard, Race.FOREST, SkillEffectType.MAXHP_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.王国悠歌) {
                        RacialBuff.apply(this, skillUseInfo.getAttachedUseInfo1(), fieldCard, Race.KINGDOM, SkillEffectType.ATTACK_CHANGE);
                        RacialBuff.apply(this, skillUseInfo.getAttachedUseInfo2(), fieldCard, Race.KINGDOM, SkillEffectType.MAXHP_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.蛮荒之力) {
                        RacialBuff.apply(this, skillUseInfo, fieldCard, Race.SAVAGE, SkillEffectType.ATTACK_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.蛮荒守护) {
                        RacialBuff.apply(this, skillUseInfo, fieldCard, Race.SAVAGE, SkillEffectType.MAXHP_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.地狱之力) {
                        RacialBuff.apply(this, skillUseInfo, fieldCard, Race.HELL, SkillEffectType.ATTACK_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.地狱守护) {
                        RacialBuff.apply(this, skillUseInfo, fieldCard, Race.HELL, SkillEffectType.MAXHP_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.本源之力) {
                        RacialBuff.apply(this, skillUseInfo, fieldCard, null, SkillEffectType.ATTACK_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.本源守护 || skillUseInfo.getType() == SkillType.乐善好施) {
                        RacialBuff.apply(this, skillUseInfo, fieldCard, null, SkillEffectType.MAXHP_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.根源之力 || skillUseInfo.getType() == SkillType.战争狂热) {
                        TogetherBuff.apply(this, skillUseInfo, fieldCard, null);
                    } else if (skillUseInfo.getType() == SkillType.生命符文) {
                        CoefficientBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.MAXHP_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.倾城之舞) {
                        CoefficientBuff.apply(this, skillUseInfo.getAttachedUseInfo2(), fieldCard, card, null, SkillEffectType.MAXHP_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.魏之勇) {
                        CountryBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.MAXHP_CHANGE, "三国英魂孟德", "三国英魂仲达", "三国樱魂文远", "三国英魂元让", "三国英魂甄姬", "三国英魂文若");
                    } else if (skillUseInfo.getType() == SkillType.魏之力) {
                        CountryBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.ATTACK_CHANGE, "三国英魂孟德", "三国英魂仲达", "三国樱魂文远", "三国英魂元让", "三国英魂甄姬", "三国英魂文若");
                    } else if (skillUseInfo.getType() == SkillType.曹魏无双) {
                        CountryBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.MAXHP_CHANGE, "三国英魂孟德", "三国英魂仲达", "三国樱魂文远", "三国英魂元让", "三国英魂甄姬", "三国英魂文若");
                        CountryBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.ATTACK_CHANGE, "三国英魂孟德", "三国英魂仲达", "三国樱魂文远", "三国英魂元让", "三国英魂甄姬", "三国英魂文若");
                    } else if (skillUseInfo.getType() == SkillType.蜀之勇) {
                        CountryBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.MAXHP_CHANGE, "三国英魂子龙", "三国英魂翼德", "三国英魂卧龙", "三国英魂孔明", "三国英魂孟起", "三国英魂云长", "三国英魂汉升", "三国英魂玄德", "三国英魂星彩");
                    } else if (skillUseInfo.getType() == SkillType.蜀之力) {
                        CountryBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.ATTACK_CHANGE, "三国英魂子龙", "三国英魂翼德", "三国英魂卧龙", "三国英魂孔明", "三国英魂孟起", "三国英魂云长", "三国英魂汉升", "三国英魂玄德", "三国英魂星彩");
                    } else if (skillUseInfo.getType() == SkillType.蜀汉无双) {
                        CountryBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.MAXHP_CHANGE, "三国英魂子龙", "三国英魂翼德", "三国英魂卧龙", "三国英魂孔明", "三国英魂孟起", "三国英魂云长", "三国英魂汉升", "三国英魂玄德", "三国英魂星彩");
                        CountryBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.ATTACK_CHANGE, "三国英魂子龙", "三国英魂翼德", "三国英魂卧龙", "三国英魂孔明", "三国英魂孟起", "三国英魂云长", "三国英魂汉升", "三国英魂玄德", "三国英魂星彩");
                    } else if (skillUseInfo.getType() == SkillType.吴之勇) {
                        CountryBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.MAXHP_CHANGE, "三国英魂大乔", "三国英魂仲谋", "三国英魂子敬", "三国英魂伯言", "三国英魂子义");
                    } else if (skillUseInfo.getType() == SkillType.吴之力) {
                        CountryBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.ATTACK_CHANGE, "三国英魂大乔", "三国英魂仲谋", "三国英魂子敬", "三国英魂伯言", "三国英魂子义");
                    } else if (skillUseInfo.getType() == SkillType.江东无双) {
                        CountryBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.MAXHP_CHANGE, "三国英魂大乔", "三国英魂仲谋", "三国英魂子敬", "三国英魂伯言", "三国英魂子义");
                        CountryBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.ATTACK_CHANGE, "三国英魂大乔", "三国英魂仲谋", "三国英魂子敬", "三国英魂伯言", "三国英魂子义");
                    } else if (skillUseInfo.getType() == SkillType.战歌之鼓) {
                        CoefficientBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.ATTACK_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.神圣守护) {
                        HolyGuard.apply(this, skillUseInfo, fieldCard);
                    } else if (skillUseInfo.getType() == SkillType.坚壁) {
                        CoefficientThreeBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.MAXHP_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.剑域) {
                        CoefficientThreeBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.ATTACK_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.北海报恩) {
                        CoefficientThreeBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.ATTACK_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.偶像演出) {
                        CoefficientThreeBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.MAXHP_CHANGE);
                        CoefficientThreeBuff.apply(this, skillUseInfo, fieldCard, card, null, SkillEffectType.ATTACK_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.王国同调) {
                        Synchrome.apply(this, skillUseInfo, fieldCard, card, Race.KINGDOM);
                    } else if (skillUseInfo.getType() == SkillType.森林同调) {
                        Synchrome.apply(this, skillUseInfo, fieldCard, card, Race.FOREST);
                    } else if (skillUseInfo.getType() == SkillType.蛮荒同调) {
                        Synchrome.apply(this, skillUseInfo, fieldCard, card, Race.SAVAGE);
                    } else if (skillUseInfo.getType() == SkillType.地狱同调) {
                        Synchrome.apply(this, skillUseInfo, fieldCard, card, Race.HELL);
                    } else if (skillUseInfo.getType() == SkillType.森之助) {
                        CoefficientBuffExcludeSummon.apply(this, skillUseInfo.getAttachedUseInfo1(), fieldCard, card, Race.FOREST, SkillEffectType.ATTACK_CHANGE);
                        CoefficientBuffExcludeSummon.apply(this, skillUseInfo.getAttachedUseInfo2(), fieldCard, card, Race.FOREST, SkillEffectType.MAXHP_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.羽扇虎拳 || skillUseInfo.getType() == SkillType.蚀月之光) {
                        Bless.apply(skillUseInfo.getSkill(), this, fieldCard);
                    } else if (skillUseInfo.getType() == SkillType.王者之风 || skillUseInfo.getType() == SkillType.圣灵领域) {
                        Pray.apply(skillUseInfo.getSkill(), this, fieldCard);
                    } else if (skillUseInfo.getType() == SkillType.王之军阵) {
                        CoefficientThreeBuff.apply(this, skillUseInfo.getAttachedUseInfo1(), fieldCard, card, null, SkillEffectType.MAXHP_CHANGE);
                        CoefficientThreeBuff.apply(this, skillUseInfo.getAttachedUseInfo2(), fieldCard, card, null, SkillEffectType.ATTACK_CHANGE);
                    } else if (skillUseInfo.getType() == SkillType.星座能量热情) {
                        //TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "摩羯座",200,400);
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "原摩羯座", 200, 400);
                    } else if (skillUseInfo.getType() == SkillType.星座能量清醒) {
                        TogetherBuffOfStar.apply(this, skillUseInfo.getAttachedUseInfo1(), fieldCard, "水瓶座", 200, 400);
                        TogetherBuffOfStar.apply(this, skillUseInfo.getAttachedUseInfo1(), fieldCard, "原水瓶座", 200, 400);
                    } else if (skillUseInfo.getType() == SkillType.星座能量直感) {
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "金牛座", 200, 400);
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "原金牛座", 200, 400);
                    } else if (skillUseInfo.getType() == SkillType.星座能量信念) {
                        // TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "处女座",200,400);
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "原处女座", 200, 400);
                    } else if (skillUseInfo.getType() == SkillType.星座能量神秘) {
                        // TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "双鱼座",200,400);
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "原双鱼座", 200, 400);
                    } else if (skillUseInfo.getType() == SkillType.星座能量智慧) {
                        // TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "狮子座",200,400);
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "原狮子座", 200, 400);
                    } else if (skillUseInfo.getType() == SkillType.星座能量平衡) {
                        // TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "白羊座",200,400);
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "原白羊座", 200, 400);
                    } else if (skillUseInfo.getType() == SkillType.星座能量掌握) {
                        //  TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "射手座",200,400);
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "原射手座", 200, 400);
                    } else if (skillUseInfo.getType() == SkillType.星座能量控制) {
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "天秤座", 200, 400);
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "原天秤座", 200, 400);
                    } else if (skillUseInfo.getType() == SkillType.星座能量坚韧) {
                        // TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "巨蟹座",200,400);
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "原巨蟹座", 200, 400);
                    } else if (skillUseInfo.getType() == SkillType.星座能量思考) {
                        //  TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "双子座",200,400);
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "原双子座", 200, 400);
                    } else if (skillUseInfo.getType() == SkillType.星座能量力量) {
                        //   TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "天蝎座",200,400);
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "原天蝎座", 200, 400);
                    } else if (skillUseInfo.getType() == SkillType.固守) {
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "龙城飞将", 0, 1000);
                    } else if (skillUseInfo.getType() == SkillType.仁厚 || skillUseInfo.getType() == SkillType.克己奉公 || skillUseInfo.getType() == SkillType.诱敌深入
                            || skillUseInfo.getType() == SkillType.反间) {
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "吴大帝·孙权", 0, 1000);
                    } else if (skillUseInfo.getType() == SkillType.勇冠三军) {
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "冀州双雄·文丑", 400, 0);
                    } else if (skillUseInfo.getType() == SkillType.勇武) {
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "宣花斧", fieldCard.getInitAT(), 0);
                    } else if (skillUseInfo.getType() == SkillType.真言结界) {
                        TogetherBuffOfStar.apply(this, skillUseInfo, fieldCard, "快雪时晴", 0, 1600);
                    }
                }
            }
        }

        // 降临系技能（除了降临复活之外）
        int position = card.getPosition();
        if (position < 0 || player.getField().getCard(position) == null) {
            // Killed or returned by other summoning skills
            card.setIsSummon(false);
            return;
        }
        for (SkillUseInfo skillUseInfo : card.getAllUsableSkills()) {
            if (!card.isAlive()) {
                //card is dead or return hand or deck
                break;
            }
            if (skillUseInfo.getSkill().isSummonSkill()) {
                if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                    continue;
                }
                if (skillUseInfo.getType() == SkillType.烈焰风暴) {
                    FireMagic.apply(skillUseInfo.getSkill(), this, card, enemy, -1);
                } else if (skillUseInfo.getType() == SkillType.雷暴) {
                    LighteningMagic.apply(skillUseInfo, this, card, enemy, -1, 35);
                } else if (skillUseInfo.getType() == SkillType.暴风雪) {
                    IceMagic.apply(skillUseInfo, this, card, enemy, -1, 30, 0);
                } else if (skillUseInfo.getType() == SkillType.寒霜冲击) {
                    IceMagic.apply(skillUseInfo, this, card, enemy, -1, 50, (5 + skillUseInfo.getSkill().getLevel() * 5) * enemy.getField().getAliveCards().size());
                } else if (skillUseInfo.getType() == SkillType.极寒冲击) {
                    IceMagic.apply(skillUseInfo, this, card, enemy, -1, 50, (40 + skillUseInfo.getSkill().getLevel() * 20) * enemy.getField().getAliveCards().size());
                } else if (skillUseInfo.getType() == SkillType.霜焰) {
                    IceMagic.apply(skillUseInfo, this, card, enemy, -1, 50, 120 * enemy.getField().getAliveCards().size());
                } else if (skillUseInfo.getType() == SkillType.寒冰触碰) {
                    IceTouch.apply(skillUseInfo, this, card, enemy, 3,50);
                } else if (skillUseInfo.getType() == SkillType.审判之剑) {
                    IceTouch.apply(skillUseInfo, this, card, enemy, 3,70);
                } else if (skillUseInfo.getType() == SkillType.圣炎 || skillUseInfo.getType() == SkillType.热血战士) {
                    HolyFire.apply(skillUseInfo.getSkill(), this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.魔力碎片) {
                    IceTouch.apply(skillUseInfo, this, card, enemy, 3,75);
                } else if (skillUseInfo.getType() == SkillType.法力风暴 || skillUseInfo.getType() == SkillType.魔法毁灭 || skillUseInfo.getType() == SkillType.屠戮) {
                    ManaErode.apply(skillUseInfo.getSkill(), this, card, enemy, -1);
                } else if (skillUseInfo.getType() == SkillType.毒云) {
                    PoisonMagic.apply(skillUseInfo, this, card, enemy, -1);
                } else if (skillUseInfo.getType() == SkillType.剧毒新星) {
                    PoisonMagic.apply(skillUseInfo, this, card, enemy, -1);
                } else if (skillUseInfo.getType() == SkillType.瘟疫) {
                    Plague.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.灵魂消散) {
                    SoulCrash.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.凋零真言) {
                    WitheringWord.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.治疗) {
                    Heal.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.甘霖) {
                    Rainfall.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.月神的护佑 || skillUseInfo.getType() == SkillType.月之守护 || skillUseInfo.getType() == SkillType.月之守望) {
                    LunaBless.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.月神的触碰) {
                    LunaTouch.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.祈祷) {
                    Pray.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.魔力法阵) {
                    MagicMark.apply(this, skillUseInfo, card, enemy, -1);
                } else if (skillUseInfo.getType() == SkillType.诅咒) {
                    Curse.apply(this, skillUseInfo.getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.群体削弱) {
                    WeakenAll.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.烈火焚神) {
                    BurningFlame.apply(skillUseInfo, this, card, enemy, -1);
                } else if (skillUseInfo.getType() == SkillType.陷阱) {
                    Trap.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.送还) {
                    Return.apply(this, skillUseInfo.getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.精神污染) {
                    Insane.apply(skillUseInfo, this, card, enemy, 3, 0);
                } else if (skillUseInfo.getType() == SkillType.摧毁) {
                    Destroy.apply(this, skillUseInfo.getSkill(), card, enemy, 1);
                } else if (skillUseInfo.getType() == SkillType.传送 || skillUseInfo.getType() == SkillType.代表月亮消灭你) {
                    Transport.apply(this, skillUseInfo.getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.归魂) {
                    RegressionSoul.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.号角) {
                    Horn.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.祈愿) {
                    Supplication.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.封印) {
                    Seal.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.业火) {
                    HellFire.apply(skillUseInfo, this, card, enemy, 3);
                } else if (skillUseInfo.getType() == SkillType.关小黑屋) {
                    Enprison.apply(this, skillUseInfo.getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.净化) {
                    Purify.apply(skillUseInfo, this, card, -1);
                } else if (skillUseInfo.getType() == SkillType.战争怒吼 || skillUseInfo.getType() == SkillType.常夏日光 || skillUseInfo.getType() == SkillType.碎裂怒吼) {
                    Soften.apply(skillUseInfo, this, card, enemy, -1);
                } else if (skillUseInfo.getType() == SkillType.阻碍) {
                    OneDelay.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.全体阻碍) {
                    AllDelay.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.烈焰风暴) {
                    FireMagic.apply(skillUseInfo.getSkill(), this, card, enemy, -1);
                } else if (
                        skillUseInfo.getType() == SkillType.圣光洗礼 || skillUseInfo.getType() == SkillType.森林沐浴 ||
                                skillUseInfo.getType() == SkillType.蛮荒威压 || skillUseInfo.getType() == SkillType.地狱同化) {
                    RaceChange.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.全体加速) {
                    AllSpeedUp.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.沉默) {
                    Silence.apply(this, skillUseInfo, card, enemy, false, false);
                } else if (skillUseInfo.getType() == SkillType.回魂) {
                    Resurrection.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.全体沉默) {
                    // 降临全体沉默全场只能发动一次，全领域沉默可以无限发动
                    Silence.apply(this, skillUseInfo, card, enemy, true, true);
                } else if (skillUseInfo.getType() == SkillType.魅惑之舞) {
                    Confusion.apply(skillUseInfo, this, card, enemy, -1);
                } else if (skillUseInfo.getType() == SkillType.无限全体沉默) {
                    Silence.apply(this, skillUseInfo, card, enemy, true, false);
                } else if (!isMinion && skillUseInfo.getType() == SkillType.镜像) {
                    // 镜像召唤的单位可以被连锁攻击
                    Summon.apply(this, skillUseInfo, card, SummonType.Normal, 1, card.getName());
                } else if (skillUseInfo.getType() == SkillType.仙子召唤) {
                    Summon.apply(this, skillUseInfo, card, SummonType.Normal, 2, "蝶语仙子", "蝶语仙子");
                } else if (skillUseInfo.getType() == SkillType.星之所在) {
                    Summon.apply(this, skillUseInfo, card, SummonType.RandomSummoning, 2,
                            "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座",
                            "天秤座", "射手座", "天蝎座", "摩羯座", "水瓶座", "双鱼座");
                } else if (skillUseInfo.getType() == SkillType.原星之所在) {
                    Summon.apply(this, skillUseInfo, card, SummonType.RandomSummoning, 2,
                            "原白羊座", "原金牛座", "原双子座", "原巨蟹座", "原狮子座", "原处女座",
                            "原天秤座", "原射手座", "原天蝎座", "原摩羯座", "原水瓶座", "原双鱼座");
                } else if (skillUseInfo.getType() == SkillType.页游星之所在) {
                    Summon.apply(this, skillUseInfo, card, SummonType.RandomSummoning, 2,
                            "网页版白羊座", "网页版金牛座", "网页版双子座", "网页版巨蟹座", "网页版狮子座", "网页版处女座",
                            "天秤座", "射手座", "天蝎座", "摩羯座", "网页版水瓶座", "网页版双鱼座");
                } else if (skillUseInfo.getType() == SkillType.灵龙轰咆) {
                    Summon.apply(this, skillUseInfo, card, SummonType.RandomSummoning, 2,
                            "光明之龙", "金属巨龙", "黄金金属巨龙", "元素灵龙", "暴怒霸龙", "毁灭之龙", "幽灵巨龙",
                            "水晶巨龙", "毒雾羽龙", "黄金毒龙", "地魔龙", "邪狱魔龙", "混沌之龙", "地狱雷龙");
                } else if (skillUseInfo.getType() == SkillType.万兽奔腾) {
                    Summon.apply(this, skillUseInfo, card, SummonType.RandomSummoning, 2,
                            "麒麟兽", "凤凰", "浮云青鸟", "九头妖蛇", "雷兽", "羽翼化蛇", "神谕火狐",
                            "齐天美猴王", "羽蛇神", "月蚀兽", "逐月恶狼", "逐日凶狼", "月之神兽", "山地兽");
                } else if (skillUseInfo.getType() == SkillType.狂野之怒) {
                    Summon.apply(this, skillUseInfo, card, SummonType.RandomSummoning, 2,
                            "凤凰", "浮云青鸟", "九头妖蛇", "雷兽", "羽翼化蛇", "神谕火狐",
                            "齐天美猴王", "羽蛇神", "月蚀兽", "逐月恶狼", "逐日凶狼", "月之神兽", "山地兽");
                } else if (skillUseInfo.getType() == SkillType.三国英才) {
                    Summon.apply(this, skillUseInfo, card, SummonType.RandomSummoning, 1,
                            "三国英魂卧龙", "三国英魂仲达", "三国英魂孔明", "三国英魂子敬", "三国英魂伯言", "三国英魂文若");
                } else if (skillUseInfo.getType() == SkillType.三国武魂) {
                    Summon.apply(this, skillUseInfo, card, SummonType.RandomSummoning, 1,
                            "三国英魂子龙", "三国英魂翼德", "三国英魂奉先", "三国英魂孟起", "三国樱魂文远", "三国英魂云长", "三国英魂元让", "三国英魂汉升", "三国英魂子义");
                } else if (skillUseInfo.getType() == SkillType.星河召唤) {
                    Summon.apply(this, skillUseInfo, card, SummonType.RandomSummoning, 2,
                            "精灵游骑兵", "蝶语仙子", "人马大贤者", "洞察之鹰", "森林弹唱者", "森林女神");
                } else if (skillUseInfo.getType() == SkillType.祈福) {
                    Bless.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.山崩) {
                    Crumbling.apply(this, skillUseInfo.getSkill(), card, enemy, 1, 1);
                } else if (skillUseInfo.getType() == SkillType.烈焰审判) {
                    UnderworldCall.apply(this, skillUseInfo.getSkill(), card, enemy, 3);
                } else if (skillUseInfo.getType() == SkillType.神性祈求) {
                    Purify.apply(skillUseInfo, this, card, -1);
                } else if (skillUseInfo.getType() == SkillType.夺魂) {
                    SoulControl.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.封锁) {
                    WeakenAll.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.远古召唤) {
                    AddCard.apply(this, skillUseInfo, card, SummonType.Summoning, 1,
                            "元素巨人");
                } else if (skillUseInfo.getType() == SkillType.冰天雪地) {
                    IceMagic.apply(skillUseInfo, this, card, enemy, -1, 0, 160 * enemy.getField().getAliveCards().size());
                } else if (skillUseInfo.getType() == SkillType.擒拿) {
                    Curse.apply(this, skillUseInfo.getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.天谴 || skillUseInfo.getType() == SkillType.末世术) {
                    HeavenWrath.apply(this, skillUseInfo.getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.退散) {
                    ReturnCard.apply(this, skillUseInfo.getSkill(), card, enemy, 1);
                } else if (skillUseInfo.getType() == SkillType.时空封印) {
                    Rapture.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.无尽华尔兹) {
                    Insane.apply(skillUseInfo, this, card, enemy, -1, 100);
                } else if (skillUseInfo.getType() == SkillType.群星之怒) {
                    if (enemy.getField().getAliveCards().size() >= 5) {
                        SoulCrash.apply(skillUseInfo.getAttachedUseInfo1(), this, card, enemy);
                    }
                    if (enemy.getField().getAliveCards().size() < 5) {
                        ThunderStrike.apply(skillUseInfo.getAttachedUseInfo2(), this, card, enemy, 3,75);
                    }
                } else if (skillUseInfo.getType() == SkillType.星辰变) {
                    if (card.getOwner().getHand().size() >= 1) {
                        Horn.apply(skillUseInfo.getAttachedUseInfo1(), this, card);
                    }
                    if (card.getOwner().getHand().size() < 1) {
                        Supplication.apply(this, skillUseInfo.getAttachedUseInfo2(), card, enemy);
                    }
                } else if (skillUseInfo.getType() == SkillType.自毁) {
                    AlchemyFailure.apply(this, skillUseInfo, skillUseInfo.getSkill(), card);
                } else if (skillUseInfo.getType() == SkillType.雷霆一击) {
                    ThunderStrike.apply(skillUseInfo, this, card, enemy, 3,75);
                } else if (skillUseInfo.getType() == SkillType.新生) {
                    NewBorn.apply(this, skillUseInfo, card, enemy, 1);
                } else if (skillUseInfo.getType() == SkillType.死亡诅咒) {
                    SoulChains.apply(this, skillUseInfo, card, enemy, 1, 2);
                } else if (skillUseInfo.getType() == SkillType.琴音共鸣) {
                    Spread.apply(this, skillUseInfo, card, enemy, 3, 6);
                } else if (skillUseInfo.getType() == SkillType.能量转化) {
                    ContinuousFire.applyHealHero(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.死亡链接) {
                    SoulLink.apply(this, skillUseInfo, card, enemy, 5, 3);
                }
            } else if (!skillUseInfo.getSkill().isDeathSkill()) {
                if (skillUseInfo.getType() == SkillType.反噬 || skillUseInfo.getType() == SkillType.恶魔契约) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    CounterBite.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.星云锁链 || skillUseInfo.getType() == SkillType.星团锁链 || skillUseInfo.getType() == SkillType.荼蘼盛放) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    NebulaChain.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.进军之势) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    NebulaChain.applyMult(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.先锋突袭) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, card, enemy, 1);
                } else if (skillUseInfo.getType() == SkillType.邪灵退散) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    SoulCrash.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.进军之令) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Supplication.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.救赎) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Bless.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.掠影) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, card, enemy, 1);
                } else if (skillUseInfo.getType() == SkillType.天下桃李) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            HandCardAddSkillLong.apply(this, skillUseInfo, card, skillUseInfo.getSkill());
                        }
                        continue;
                    }
                    NebulaChain.apply(skillUseInfo, this, card);
                    HandCardAddSkillLong.apply(this, skillUseInfo, card, skillUseInfo.getSkill());
                } else if (skillUseInfo.getType() == SkillType.分解反应) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            AllDelay.apply(skillUseInfo.getAttachedUseInfo2(), this, card, enemy);
                        }
                        continue;
                    }
                    Destroy.apply(this, skillUseInfo.getAttachedUseInfo1().getSkill(), card, enemy, 1);
                    AllDelay.apply(skillUseInfo.getAttachedUseInfo2(), this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.王牌飞刀) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Seal.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.咆哮 || skillUseInfo.getType() == SkillType.瓦解) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            Transport.apply(this, skillUseInfo.getSkill(), card, enemy);
                        }
                        continue;
                    }
                    Destroy.apply(this, skillUseInfo.getSkill(), card, enemy, 1);
                    Transport.apply(this, skillUseInfo.getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.制裁之拳) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Destroy.apply(this, skillUseInfo.getSkill(), card, enemy, -1);
                } else if (skillUseInfo.getType() == SkillType.翼龙之首) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Destroy.apply(this, skillUseInfo.getAttachedUseInfo1().getSkill(), card, enemy, -1);
                } else if (skillUseInfo.getType() == SkillType.火力压制) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    RedGun.apply(skillUseInfo.getAttachedUseInfo1(), this, card, enemy, 3);
                    AllDelay.apply(skillUseInfo.getAttachedUseInfo2(), this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.虚梦) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Transport.apply(this, skillUseInfo.getAttachedUseInfo2().getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.影青龙) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.Normal, 1, card.getName());
                } else if (skillUseInfo.getType() == SkillType.上层精灵的挽歌) {
                    Resurrection.apply(this, skillUseInfo, card);
                } else if (!isMinion && skillUseInfo.getType() == SkillType.镜魔) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo.getAttachedUseInfo2(), card, SummonType.Summoning, 1, card.getName());
                } else if (skillUseInfo.getType() == SkillType.全领域沉默) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Silence.apply(this, skillUseInfo, card, enemy, true, false);
                } else if (skillUseInfo.getType() == SkillType.召唤玫瑰剑士) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo.getAttachedUseInfo1(), card, SummonType.Summoning, 1,
                            "花舞剑士");
                } else if (skillUseInfo.getType() == SkillType.英灵召唤 || skillUseInfo.getType() == SkillType.英魂唤醒) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo.getAttachedUseInfo1(), card, SummonType.RandomSummoning, 2,
                            "三国英魂玄德", "三国英魂子龙", "三国英魂汉升", "三国英魂张角", "三国英魂仲颖", "三国英魂貂蝉", "三国英魂奉先");
                } else if (skillUseInfo.getType() == SkillType.剑道) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.Normal, 2,
                            "武形剑圣", "武形剑圣");
                } else if (skillUseInfo.getType() == SkillType.伎町迷影 || skillUseInfo.getType() == SkillType.三位一体 || skillUseInfo.getType() == SkillType.迷影森森) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.Normal, 2,
                            card.getName(), card.getName());
                } else if (skillUseInfo.getType() == SkillType.猫神的低语) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.Normal, 2,
                            "九命猫神", "九命猫神");
                } else if (skillUseInfo.getType() == SkillType.桃园结义) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.Summoning, 2,
                            "三国英魂云长", "三国英魂翼德");
                } else if (skillUseInfo.getType() == SkillType.灵龟羁绊) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.Summoning, 1,
                            "巨岛龟幼崽");
                } else if (skillUseInfo.getType() == SkillType.舌战群儒) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Insane.apply(skillUseInfo, this, card, enemy, -1, 70);
                } else if (skillUseInfo.getType() == SkillType.纷争乱境) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            Erode.apply(this, skillUseInfo.getAttachedUseInfo2(), card, enemy, null, true);
                        }
                        continue;
                    }
                    Insane.apply(skillUseInfo.getAttachedUseInfo1(), this, card, enemy, -1, 100);
                    Erode.apply(this, skillUseInfo.getAttachedUseInfo2(), card, enemy, null, true);
                } else if (skillUseInfo.getType() == SkillType.合纵连横) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    GiantEarthquakesLandslides.apply(this, skillUseInfo.getSkill(), card, enemy, 1);
                } else if (skillUseInfo.getType() == SkillType.铁壁 || skillUseInfo.getType() == SkillType.金汤 || skillUseInfo.getType() == SkillType.铁壁方阵
                        || skillUseInfo.getType() == SkillType.光之守护 || skillUseInfo.getType() == SkillType.聚能立场 || skillUseInfo.getType() == SkillType.护主
                        || skillUseInfo.getType() == SkillType.龙之守护 || skillUseInfo.getType() == SkillType.不动如山 || skillUseInfo.getType() == SkillType.良禽择木
                        || skillUseInfo.getType() == SkillType.国之坚壁) {
                    ImpregnableDefenseHeroBuff.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.铜墙铁壁) {
                    ImpregnableDefenseHeroBuff.apply(this, skillUseInfo, card);
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Bless.apply(skillUseInfo.getAttachedUseInfo1().getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.背水 || skillUseInfo.getType() == SkillType.良禽择木 || skillUseInfo.getType() == SkillType.反向溅射 || skillUseInfo.getType() == SkillType.翼刃) {
                    CounterAttackHero.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.画境乾坤) {
                    CounterAttackHero.apply(skillUseInfo, this, card);
                    ImpregnableDefenseHeroBuff.apply(this, skillUseInfo.getAttachedUseInfo1(), card);
                } else if (skillUseInfo.getType() == SkillType.驱虎吞狼) {
                    ImpregnableDefenseHeroBuff.apply(this, skillUseInfo.getAttachedUseInfo2(), card);
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Erode.apply(this, skillUseInfo.getAttachedUseInfo1(), card, enemy, null, true);
                } else if (skillUseInfo.getType() == SkillType.御龙在天) {
                    ImpregnableDefenseHeroBuff.apply(this, skillUseInfo.getAttachedUseInfo1(), card);
                } else if (skillUseInfo.getType() == SkillType.魔神加护) {
                    ImpregnableDefenseHeroBuff.apply(this, skillUseInfo.getAttachedUseInfo2(), card);
                } else if (skillUseInfo.getType() == SkillType.侵蚀 || skillUseInfo.getType() == SkillType.页游吞噬 || skillUseInfo.getType() == SkillType.威慑
                        || skillUseInfo.getType() == SkillType.骁袭 || skillUseInfo.getType() == SkillType.克己奉公 || skillUseInfo.getType() == SkillType.百里
                        || skillUseInfo.getType() == SkillType.一夫当关 || skillUseInfo.getType() == SkillType.奔袭) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Erode.apply(this, skillUseInfo, card, enemy, null, true);
                } else if (skillUseInfo.getType() == SkillType.鬼才) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Erode.apply(this, skillUseInfo.getAttachedUseInfo1(), card, enemy, null, true);
                } else if (skillUseInfo.getType() == SkillType.突突突) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddSkillOpponent.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill(), 1, enemy, 0);
                } else if (skillUseInfo.getType() == SkillType.雀之引) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    RegressionSoul.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.修罗道) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Genie.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.决胜时刻) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    TimeTravel.apply(skillUseInfo, this, card.getOwner(), enemy);
                } else if (skillUseInfo.getType() == SkillType.误人子弟) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Confusion.apply(skillUseInfo.getAttachedUseInfo1(), this, card, enemy, -1);
                } else if (skillUseInfo.getType() == SkillType.支配亡灵) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    ControlGhost.apply(this, skillUseInfo, card, enemy, -1, 3);
                } else if (skillUseInfo.getType() == SkillType.人体炼成) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    HumanRefining.apply(this, skillUseInfo, card, enemy, -1, 3);
                } else if (skillUseInfo.getType() == SkillType.骸骨转化) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    DeathSacrifice.apply(this, skillUseInfo, card, enemy, 1, 2);
                } else if (skillUseInfo.getType() == SkillType.涤罪神启) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            Bless.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, card);
                        }
                        continue;
                    }
                    SoulCrash.apply(skillUseInfo.getAttachedUseInfo1(), this, card, enemy);
                    Bless.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.噬血狂袭) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            Pray.apply(skillUseInfo.getSkill(), this, card);
                        }
                        continue;
                    }
                    Curse.apply(this, skillUseInfo.getSkill(), card, enemy);
                    Pray.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.降归魂 || skillUseInfo.getType() == SkillType.彼岸轮回) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    RegressionSoul.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.诀隐) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    RegressionSoul.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.禁术全体阻碍) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AllDelay.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.禁术无尽华尔兹) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Insane.apply(skillUseInfo, this, card, enemy, -1, 100);
                } else if (skillUseInfo.getType() == SkillType.禁术全领域沉默) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Silence.apply(this, skillUseInfo, card, enemy, true, false);
                } else if (skillUseInfo.getType() == SkillType.禁术救赎) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Bless.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.禁术末世降临) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    HeavenWrath.apply(this, skillUseInfo.getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.招魂术) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Resurrection.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.消逝) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AlchemyFailure.apply(this, skillUseInfo, skillUseInfo.getSkill(), card);
                } else if (skillUseInfo.getType() == SkillType.噩梦马戏团) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.Normal, 3,
                            "镜魔", "镜魔", "镜魔");
                } else if (skillUseInfo.getType() == SkillType.万里追魂) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Transport.apply(this, skillUseInfo.getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.潜摧) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Destroy.apply(this, skillUseInfo.getSkill(), card, enemy, 1);
                } else if (skillUseInfo.getType() == SkillType.盘球大师) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Seal.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.桑巴之舞) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Insane.apply(skillUseInfo, this, card, enemy, -1, 100);
                } else if (skillUseInfo.getType() == SkillType.亡魂咒印) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddSkillOpponent.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill(), 3, enemy, 0);
                } else if (skillUseInfo.getType() == SkillType.星座能量力量) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Crumbling.apply(this, skillUseInfo.getSkill(), card, enemy, 1, 1);
                } else if (skillUseInfo.getType() == SkillType.雪幕) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            IceMagic.apply(skillUseInfo.getAttachedUseInfo2(), this, card, enemy, -1, 70, 0);
                        }
                        continue;
                    }
                    IceMagic.apply(skillUseInfo.getAttachedUseInfo2(), this, card, enemy, -1, 0, 160 * enemy.getField().getAliveCards().size());
                    IceMagic.apply(skillUseInfo.getAttachedUseInfo2(), this, card, enemy, -1, 70, 0);
                } else if (skillUseInfo.getType() == SkillType.猫神的低语) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.Summoning, 1,
                            "帝国审判者");
                } else if (skillUseInfo.getType() == SkillType.彻骨之寒) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    IceTouch.apply(skillUseInfo, this, card, enemy, 3,50);
                } else if (skillUseInfo.getType() == SkillType.凛冬将至) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    IceTouch.apply(skillUseInfo, this, card, enemy, -1,50);
                } else if (skillUseInfo.getType() == SkillType.舍身) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Curse.apply(this, skillUseInfo.getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.海滨骚乱) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            Insane.apply(skillUseInfo, this, card, enemy, 3, 150);
                        }
                        continue;
                    }
                    AddSkillOpponent.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill(), 1, enemy, 0);
                    Insane.apply(skillUseInfo, this, card, enemy, 3, 150);
                } else if (skillUseInfo.getType() == SkillType.闪耀突击) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Horn.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.逐光追梦) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    ReturnCard.apply(this, skillUseInfo.getSkill(), card, enemy, 5);
                } else if (skillUseInfo.getType() == SkillType.反间情报) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Insane.apply(skillUseInfo, this, card, enemy, 3, 0);
                } else if (skillUseInfo.getType() == SkillType.放飞自我) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Supplication.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.星座能量思考) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AllSpeedUp.apply(skillUseInfo.getAttachedUseInfo1(), this, card);
                } else if (skillUseInfo.getType() == SkillType.星座能量智慧) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Curse.apply(this, skillUseInfo.getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.星座能量热情) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddSkillOpponent.apply(this, skillUseInfo.getAttachedUseInfo2(), card, skillUseInfo.getAttachedUseInfo2().getAttachedUseInfo1().getSkill(), 1, enemy, 0);
                } else if (skillUseInfo.getType() == SkillType.解惑) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            HandCardAddOneSkill.apply(this, skillUseInfo, card, skillUseInfo.getSkill());
                        }
                        continue;
                    }
                    Purify.apply(skillUseInfo, this, card, -1);
                    HandCardAddOneSkill.apply(this, skillUseInfo, card, skillUseInfo.getSkill());
                } else if (skillUseInfo.getType() == SkillType.选课代表) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    HandCardAddOneSkill.apply(this, skillUseInfo, card, skillUseInfo.getSkill());
                } else if (skillUseInfo.getType() == SkillType.离魂剑) {
                    SoulControlMutiple.applySetNumber(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.骸骨大军) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    SkeletonArmy.apply(this, skillUseInfo, card, enemy, -1, 4);
                } else if (skillUseInfo.getType() == SkillType.戾气诅咒) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            Pray.apply(skillUseInfo.getSkill(), this, card);
                        }
                        continue;
                    }
                    Curse.apply(this, skillUseInfo.getSkill(), card, enemy);
                    Pray.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.永生的诅咒) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    SoulChains.apply(this, skillUseInfo, card, enemy, -1, 2);
                } else if (skillUseInfo.getType() == SkillType.凤凰之怒) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Polymorph.apply(this, skillUseInfo, card, enemy, 1, 1);
                } else if (skillUseInfo.getType() == SkillType.形散如烟 || skillUseInfo.getType() == SkillType.审判之翼) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Asthenia.apply(this, skillUseInfo, card, enemy, 4, 3);
                } else if (skillUseInfo.getType() == SkillType.曹魏之主 || skillUseInfo.getType() == SkillType.魏文帝) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    HomologyMult.apply(this, skillUseInfo, card, "三国英魂·孟德", "三国英魂·甄姬", "三国英魂·仲达");
                } else if (skillUseInfo.getType() == SkillType.醉生梦死 || skillUseInfo.getType() == SkillType.摄魂之力) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    HomologyMult.apply(this, skillUseInfo, card, "酒吞童子", "大天狗", "雪女", "八岐大蛇");
                } else if (skillUseInfo.getType() == SkillType.竹取) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    HomologyMult.apply(this, skillUseInfo, card, "辉夜姬");
                } else if (skillUseInfo.getType() == SkillType.魂飞魄散) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            HomologyMult.apply(this, skillUseInfo, card, "酒吞童子", "大天狗", "雪女", "八岐大蛇");
                        }
                        continue;
                    }
                    Asthenia.apply(this, skillUseInfo, card, enemy, 4, 3);
                    HomologyMult.apply(this, skillUseInfo, card, "酒吞童子", "大天狗", "雪女", "八岐大蛇");
                } else if (skillUseInfo.getType() == SkillType.据守) {
                    ImpregnableDefenseHeroBuff.apply(this, skillUseInfo.getAttachedUseInfo2(), card);
                } else if (skillUseInfo.getType() == SkillType.土豪卡组) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddCard.apply(this, skillUseInfo, card, SummonType.Summoning, 2,
                            "爆弹强袭", "九霄龙吟", "炼狱清算者", "幻影剑魔", "熊猫教父", "漆黑魔导士",
                            "科学家·变异", "碧海绯樱", "酒吞童子", "白骨夫人", "黑白无常", "大天狗", "妲己", "雪女", "牛魔王",
                            "八岐大蛇", "金角银角", "终焉使者", "魅惑魔女", "原素曜灵", "幻镜魔导", "小栗丸", "魔幻神杯", "烈焰凤凰",
                            "盗宝松鼠");
                } else if (skillUseInfo.getType() == SkillType.献祭巫术) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    SacrificeBuff.apply(this, skillUseInfo, card, enemy, 1, 2);
                } else if (skillUseInfo.getType() == SkillType.时间扭曲) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AllDelay.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.木牛流马) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            Horn.apply(skillUseInfo.getAttachedUseInfo2(), this, card);
                        }
                        continue;
                    }
                    RegressionSoul.apply(this, skillUseInfo.getAttachedUseInfo1(), card, enemy);
                    Horn.apply(skillUseInfo.getAttachedUseInfo2(), this, card);
                } else if (skillUseInfo.getType() == SkillType.契约式神) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddCardAndExtraSkill.apply(this, skillUseInfo, card, SummonType.Normal, 1,
                            "酒吞童子-15", "大天狗-15", "雪女-15", "八岐大蛇-15", "辉夜姬-15");
                } else if (skillUseInfo.getType() == SkillType.妙笔生花) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddCardAndExtraSkill.apply(this, skillUseInfo, card, SummonType.Summoning, 1,
                            "汉宫春晓图-15");
                } else if (skillUseInfo.getType() == SkillType.蜀汉后主) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    HomologyMult.apply(this, skillUseInfo, card, "三国英魂·孔明", "三国英魂·玄德", "三国英魂·子龙");
                } else if (skillUseInfo.getType() == SkillType.下自成蹊) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Horn.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.生灭) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo.getAttachedUseInfo1(), card, SummonType.Summoning, 1,
                            "酒吞童子");
                } else if (skillUseInfo.getType() == SkillType.呼风唤雨) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AllSpeedUp.apply(skillUseInfo.getAttachedUseInfo1(), this, card);
                } else if (skillUseInfo.getType() == SkillType.魏之恋) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddCard.apply(this, skillUseInfo, card, SummonType.Summoning, 1,
                            "三国英魂·甄姬");
                } else if (skillUseInfo.getType() == SkillType.圣翼裁决) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    ThunderStrike.apply(skillUseInfo, this, card, enemy, -1,100);
                } else if (skillUseInfo.getType() == SkillType.一骑当千) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    HalfSilence.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.蛇影迷踪) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    SnakeShadow.apply(this, skillUseInfo, card, enemy, 3, 2);
                } else if (skillUseInfo.getType() == SkillType.无常索命) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            WeakenAll.apply(this, skillUseInfo, card, enemy);
                        }
                        continue;
                    }
                    SoulCrash.apply(skillUseInfo.getAttachedUseInfo1(), this, card, enemy);
                    WeakenAll.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.月之潮汐) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Bless.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.残月之辉) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            RemoveDebuffStatus.apply(skillUseInfo, this, card, 2);
                        }
                        continue;
                    }
                    Purify.apply(skillUseInfo, this, card, -1);
                    RemoveDebuffStatus.apply(skillUseInfo, this, card, 2);
                } else if (skillUseInfo.getType() == SkillType.永夜) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    BloodPaint.apply(skillUseInfo.getSkill(), this, card, enemy, 5);
                } else if (skillUseInfo.getType() == SkillType.暗影侵蚀) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    HandSword.apply(this, skillUseInfo, card, enemy, 5);
                } else if (skillUseInfo.getType() == SkillType.反间) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    SummonOpponent.apply(this, skillUseInfo, card, SummonType.Normal, 1, "诈降卡牌");
                } else if (skillUseInfo.getType() == SkillType.烈火攻心) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    GreatFireMagic.apply(skillUseInfo.getSkill(), this, card, enemy, -1,true);
                } else if (skillUseInfo.getType() == SkillType.淬毒之刃) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    PoisonMagic.apply(skillUseInfo, this, card, enemy, -1);
                } else if (skillUseInfo.getType() == SkillType.支配者) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Petrifaction.apply(skillUseInfo, this, card, enemy, 80);
                } else if (skillUseInfo.getType() == SkillType.柔光) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Petrifaction.apply(skillUseInfo.getAttachedUseInfo1(), this, card, enemy, 80);
                } else if (skillUseInfo.getType() == SkillType.海滨乐园) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.Normal, 2,
                            "悠风奏者", "悠风奏者");
                } else if (skillUseInfo.getType() == SkillType.腐化之地) {
                    FailureSkillUseInfoList.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.号令三军) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.RandomSummoning, 1,
                            "东吴四杰·周瑜", "东吴四杰·鲁肃", "东吴四杰·吕蒙", "东吴四杰·陆逊");
                } else if (skillUseInfo.getType() == SkillType.自我改造 || skillUseInfo.getType() == SkillType.成长 || skillUseInfo.getType() == SkillType.不灭之志) {
                    SelfBuff.apply(this, skillUseInfo, card, SkillEffectType.MAXHP_CHANGE);
                } else if (skillUseInfo.getType() == SkillType.吞噬焰火) {
                    SelfBuff.apply(this, skillUseInfo.getAttachedUseInfo1(), card, SkillEffectType.MAXHP_CHANGE);
                } else if (skillUseInfo.getType() == SkillType.冥界之力 || skillUseInfo.getType() == SkillType.白虹) {
                    SelfBuff.apply(this, skillUseInfo, card, SkillEffectType.ATTACK_CHANGE);
                } else if (skillUseInfo.getType() == SkillType.流星) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            AllSpeedUp.apply(skillUseInfo.getAttachedUseInfo2(), this, card);
                        }
                        continue;
                    }
                    Supplication.apply(this, skillUseInfo.getAttachedUseInfo1(), card, enemy);
                    AllSpeedUp.apply(skillUseInfo.getAttachedUseInfo2(), this, card);
                } else if (skillUseInfo.getType() == SkillType.活性细胞) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    RegressionSoul.apply(this, skillUseInfo.getAttachedUseInfo1(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.风驰电掣) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    RegressionSoul.apply(this, skillUseInfo, card, enemy);
                    AllSpeedUp.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.残影) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddSelfCard.apply(this, skillUseInfo, card, SummonType.Summoning, 1,
                            "爪黄飞电·幻影");
                } else if (skillUseInfo.getType() == SkillType.霜火交织) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            HolyFire.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, card, enemy);
                        }
                        continue;
                    }
                    IceTouch.apply(skillUseInfo.getAttachedUseInfo1(), this, card, enemy, 3,50);
                    HolyFire.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.实验失败) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Polymorph.apply(this, skillUseInfo, card, enemy, 1, 1);
                } else if (skillUseInfo.getType() == SkillType.恐惧降临) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Silence.apply(this, skillUseInfo.getAttachedUseInfo1(), card, enemy, false, false);
                } else if (skillUseInfo.getType() == SkillType.冥界守护) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    SoulChains.apply(this, skillUseInfo, card, enemy, 4, 1);
                } else if (skillUseInfo.getType() == SkillType.金刚不坏之躯) {
                    SelfDoubleBuff.apply(this, skillUseInfo, card, 600,6000);
                } else if (skillUseInfo.getType() == SkillType.神魔附体) {
                    SelfDoubleBuff.apply(this, skillUseInfo, card, 500,10000);
                } else if (skillUseInfo.getType() == SkillType.如影随形) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    IntegrateIntoOne.apply(this, skillUseInfo, card, "虚影");
                } else if (skillUseInfo.getType() == SkillType.邪影智蚀) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Insane.apply(skillUseInfo, this, card, enemy, 1, 200);
                } else if (skillUseInfo.getType() == SkillType.幽冥暗影) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.Summoning, 1,
                            "幽冥暗影");
                } else if (skillUseInfo.getType() == SkillType.幽冥诅咒) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    GrudgeHp.apply(this, skillUseInfo, card, enemy, 2);
                } else if (skillUseInfo.getType() == SkillType.超能力光线) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            Insane.apply(skillUseInfo, this, card, enemy, 1, 100);
                        }
                        continue;
                    }
                    Confusion.apply(skillUseInfo, this, card, enemy, 1);
                    Insane.apply(skillUseInfo, this, card, enemy, 1, 100);
                } else if (skillUseInfo.getType() == SkillType.竹影婆娑) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            NebulaChain.apply(skillUseInfo, this, card);
                        }
                        continue;
                    }
                    Horn.apply(skillUseInfo, this, card);
                    NebulaChain.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.大圣归来) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AllSpeedUp.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.吴之悌) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddSelfCard.apply(this, skillUseInfo, card, SummonType.Summoning, 1,
                            "三国英魂·仲谋");
                } else if (skillUseInfo.getType() == SkillType.吴之恋) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddSelfCard.apply(this, skillUseInfo, card, SummonType.Summoning, 1,
                            "三国英魂·大乔");
                } else if (skillUseInfo.getType() == SkillType.邪恶献祭) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddSkillOpponent.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill(), 2, enemy, 3);
                } else if (skillUseInfo.getType() == SkillType.苏醒) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    HomologyMult.apply(this, skillUseInfo, card, "克苏鲁");
                } else if (skillUseInfo.getType() == SkillType.古神的低语伪) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Ancient.apply(this, skillUseInfo, card, enemy, 3, 1);
                } else if (skillUseInfo.getType() == SkillType.不朽) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            SoulCrash.apply(skillUseInfo, this, card, enemy);
                        }
                        continue;
                    }
                    AddFiledCardMultSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill()
                            , null, null);
                    SoulCrash.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.剑鸣出鞘 || skillUseInfo.getType() == SkillType.天地大同) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddFiledCardMultSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill()
                            , null, null);
                } else if (skillUseInfo.getType() == SkillType.双飞燕) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            AddFiledCardMultSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill()
                                    , null, null);
                        }
                        continue;
                    }
                    NebulaChain.apply(skillUseInfo, this, card);
                    AddFiledCardMultSkill.apply(this, skillUseInfo, card, skillUseInfo.getAttachedUseInfo1().getSkill()
                            , null, null);
                } else if (skillUseInfo.getType() == SkillType.神谕 || skillUseInfo.getType() == SkillType.冰与火之歌 || skillUseInfo.getType() == SkillType.沉默之境
                        || skillUseInfo.getType() == SkillType.禁语) {
                    SummonStopSkillUseInfoList.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.断绝之翼) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    SoulLink.apply(this, skillUseInfo, card, enemy, 5, 3);
                } else if (skillUseInfo.getType() == SkillType.逆转之矢) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Curse.apply(this, skillUseInfo.getAttachedUseInfo1().getSkill(), card, enemy);
                } else if (skillUseInfo.getType() == SkillType.毒刃攻心) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Grudge.apply(this, skillUseInfo, card, enemy, 1);
                } else if (skillUseInfo.getType() == SkillType.传承之力) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Horn.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.棋布星罗) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Bless.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.俊才 || skillUseInfo.getType() == SkillType.永生审判 || skillUseInfo.getType() == SkillType.军团之首) {
                    UnbendingAwaken.resetCount(skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.滋养) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    NewBornByName.apply(this, skillUseInfo, card, enemy, 1, "饲育之母");
                } else if (skillUseInfo.getType() == SkillType.地狱之火) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Plague.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.疾行突击) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            Horn.apply(skillUseInfo.getAttachedUseInfo1(), this, card);
                        }
                        continue;
                    }
                    SoulChains.apply(this, skillUseInfo, card, enemy, 5, 4);
                    Horn.apply(skillUseInfo.getAttachedUseInfo1(), this, card);
                } else if (skillUseInfo.getType() == SkillType.眩目之光) {
                    SummonReturnSkillUseInfoList.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.双子星) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Cooperation.apply(this, skillUseInfo, card, "璀璨之星", false);
                } else if (skillUseInfo.getType() == SkillType.上古神剑 || skillUseInfo.getType() == SkillType.三花聚顶) {
                    CardEndSkillUseInfoList.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.笔走龙蛇) {
                    SummonMultipleBroilingSoul.reset(skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.绽放烟花) {
                    PolymorphBroilingSoul.reset(skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.病情加重) {
                    ContinuousFireMult.reset(skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.诸行无常) {
                    DeformationCondition.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.戍边) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AddSelfCard.apply(this, skillUseInfo.getAttachedUseInfo1(), card, SummonType.Summoning, 1,
                            card.getName());
                } else if (skillUseInfo.getType() == SkillType.决胜之心) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    SoulLink.apply(this, skillUseInfo, card, enemy, 1, 3);
                } else if (skillUseInfo.getType() == SkillType.百骑袭营) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Horn.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.魔力彩蛋) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.RandomSummoning, 2,
                            "三国英魂·玄德", "三国英魂·子龙", "三国英魂·汉升", "三国英魂·张角", "三国英魂·仲颖", "三国英魂·貂蝉", "三国英魂·奉先");
                } else if (skillUseInfo.getType() == SkillType.氛氲馥郁) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Seal.apply(skillUseInfo, this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.紫电) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            AllSpeedUp.apply(skillUseInfo, this, card);
                        }
                        continue;
                    }
                    RegressionSoul.apply(this, skillUseInfo, card, enemy);
                    AllSpeedUp.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.辞旧迎新) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    AllSpeedUp.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.热情似火) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    HolyFire.apply(skillUseInfo.getSkill(), this, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.心之祈祷) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            Bless.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, card);
                        }
                        continue;
                    }
                    Supplication.apply(this, skillUseInfo.getAttachedUseInfo1(), card, enemy);
                    Bless.apply(skillUseInfo.getAttachedUseInfo2().getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.比翼) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                            continue;
                        } else {
                            Supplication.apply(this, skillUseInfo, card, enemy);
                        }
                        continue;
                    }
                    NebulaChain.apply(skillUseInfo, this, card);
                    Supplication.apply(this, skillUseInfo, card, enemy);
                } else if (skillUseInfo.getType() == SkillType.顽疾) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    HomologyOnlySelf.apply(this, skillUseInfo, card, card.getName());
                } else if (skillUseInfo.getType() == SkillType.神兽降世) {
                    if (SummonStopSkillUseInfoList.explode(this, card, enemy)) {
                        continue;
                    }
                    Summon.apply(this, skillUseInfo, card, SummonType.RandomSummoning, 1,
                            "四神之青龙", "四神之白虎", "四神之朱雀", "四神之玄武");
                }
            }
        }
        card.setIsSummon(false);
        //敌方发动眩目之光
        SummonReturnSkillUseInfoList.explode(this, card, enemy);
    }

    // reviver: for most of the cases, it should be null.
    // It is only set when the summoning skill performer is revived by another card.
    public void resolveSecondClassSummoningSkills(List<CardInfo> summonedCards, Field myField, Field opField, Skill summonSkill, boolean isSummoning) throws HeroDieSignal {
        for (CardInfo card : summonedCards) {
            if (null == card) {
                continue;
            }
            CardStatus status = card.getStatus();
            if (status.containsStatus(CardStatusType.冰冻) || status.containsStatus(CardStatusType.锁定) || status.containsStatus(CardStatusType.石化)) {
                continue;
            }
            int position = card.getPosition();
            if (position < 0 || myField.getCard(position) == null) {
                // Killed or returned by other summoning skills
                continue;
            }
            for (SkillUseInfo skillUseInfo : card.getAllUsableSkills()) {
                if (skillUseInfo.getType() == SkillType.时光倒流 && !skillUseInfo.getSkill().isDeathSkill() || skillUseInfo.getType() == SkillType.星座能量平衡 && !skillUseInfo.getSkill().isDeathSkill()) {
                    if (!card.hasUsed(skillUseInfo)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, opField.getOwner())) {
                            if (getStage().hasUsed(skillUseInfo) && getStage().hasPlayerUsed(skillUseInfo.getOwner().getOwner())) {
                                continue;
                            } else {
//                            getStage().setUsed(skillUseInfo, true);
                                card.setUsed(skillUseInfo);
                            }
                            continue;
                        }
                        TimeBack.apply(skillUseInfo, this, card, myField.getOwner(), opField.getOwner());
                    }
                } else if (skillUseInfo.getType() == SkillType.献祭 || skillUseInfo.getType() == SkillType.血祭) {
                    if (!card.hasUsed(skillUseInfo)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, opField.getOwner())) {
                            card.setUsed(skillUseInfo);
                            continue;
                        }
                        if (!FailureSkillUseInfoList.explode(this, card, opField.getOwner())) {
                            Sacrifice.apply(this, skillUseInfo, card, summonSkill);
                        }
                    }
                }
                //调整侵蚀一段降临发动
//                else if (skillUseInfo.getType() == SkillType.侵蚀) {
//                    Erode.apply(this, skillUseInfo, card, opField.getOwner(), summonSkill);
//                } else if (skillUseInfo.getType() == SkillType.鬼才) {
//                    Erode.apply(this, skillUseInfo.getAttachedUseInfo1(), card, opField.getOwner(), summonSkill);
//                } else if (skillUseInfo.getType() == SkillType.驱虎吞狼) {
//                    Erode.apply(this, skillUseInfo.getAttachedUseInfo1(), card, opField.getOwner(), summonSkill);
//                }
                else if (skillUseInfo.getType() == SkillType.复活 && skillUseInfo.getSkill().isSummonSkill() && isSummoning) {
                    if (!card.hasUsed(skillUseInfo)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, opField.getOwner())) {
                            if (!card.hasUsed(skillUseInfo)) {
                                card.setUsed(skillUseInfo);
                            }
                            continue;
                        }
                        Revive.apply(this, skillUseInfo, card);
                        card.setUsed(skillUseInfo);
                    }
                } else if (skillUseInfo.getType() == SkillType.荣耀降临 && isSummoning) {
                    if (!card.hasUsed(skillUseInfo)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, opField.getOwner())) {
                            if (!card.hasUsed(skillUseInfo)) {
                                card.setUsed(skillUseInfo);
                            }
                            continue;
                        }
                        Revive.apply(this, skillUseInfo.getAttachedUseInfo1(), card);
                        card.setUsed(skillUseInfo);
                    }
                } else if (skillUseInfo.getType() == SkillType.返生术 && isSummoning) {
                    if (!card.hasUsed(skillUseInfo)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, opField.getOwner())) {
                            if (!card.hasUsed(skillUseInfo)) {
                                card.setUsed(skillUseInfo);
                            }
                            continue;
                        }
                        Revive.apply(this, skillUseInfo, card);
                        card.setUsed(skillUseInfo);
                    }
                } else if (skillUseInfo.getType() == SkillType.荆棘守护 && isSummoning) {
                    if (!card.hasUsed(skillUseInfo)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, opField.getOwner())) {
                            if (!card.hasUsed(skillUseInfo)) {
                                card.setUsed(skillUseInfo);
                            }
                            continue;
                        }
                        Revive.apply(this, skillUseInfo, card);
                        card.setUsed(skillUseInfo);
                    }
                } else if (skillUseInfo.getType() == SkillType.蚀月之光 && isSummoning) {
                    if (!card.hasUsed(skillUseInfo)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, opField.getOwner())) {
                            if (!card.hasUsed(skillUseInfo)) {
                                card.setUsed(skillUseInfo);
                            }
                            continue;
                        }
                        Revive.apply(this, skillUseInfo, card);
                        card.setUsed(skillUseInfo);
                    }
                } else if (skillUseInfo.getType() == SkillType.灵魂献祭 && isSummoning) {
                    if (!card.hasUsed(skillUseInfo)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, opField.getOwner())) {
                            if (!card.hasUsed(skillUseInfo)) {
                                card.setUsed(skillUseInfo);
                            }
                            continue;
                        }
                        Revive.apply(this, skillUseInfo, card);
                        Sacrifice.apply(this, skillUseInfo, card, summonSkill);
                    }
                } else if (skillUseInfo.getType() == SkillType.制衡) {
                    if (!card.hasUsed(skillUseInfo)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, opField.getOwner())) {
                            if (!card.hasUsed(skillUseInfo)) {
                                card.setUsed(skillUseInfo);
                            }
                            continue;
                        }
                        Sacrifice.apply(this, skillUseInfo, card, summonSkill);
                        if (isSummoning) {
                            Revive.apply(this, skillUseInfo, card);
                            Revive.apply(this, skillUseInfo, card);
                        }
                    }
                } else if (skillUseInfo.getType() == SkillType.圣翼军团 && isSummoning) {
                    if (!card.hasUsed(skillUseInfo)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, opField.getOwner())) {
                            if (!card.hasUsed(skillUseInfo)) {
                                card.setUsed(skillUseInfo);
                            }
                            continue;
                        }
                        Revive.apply(this, skillUseInfo, card);
                        card.setUsed(skillUseInfo);
                    }
                } else if (skillUseInfo.getType() == SkillType.圣光复活) {
                    if (!card.hasUsed(skillUseInfo)) {
                        if (SummonStopSkillUseInfoList.explode(this, card, opField.getOwner())) {
                            if (!card.hasUsed(skillUseInfo)) {
                                card.setUsed(skillUseInfo);
                            }
                            continue;
                        }
                        if (isSummoning) {
                            Revive.apply(this, skillUseInfo, card);
                            Revive.apply(this, skillUseInfo, card);
                        }
                    }
                }
            }
        }
    }

    public void resolveLeaveSkills(CardInfo card) {
        for (SkillUseInfo deadCardSkillUseInfo : card.getUsableNormalSkills()) {
            if (deadCardSkillUseInfo.getType() == SkillType.王国之力) {
                RacialBuff.remove(this, deadCardSkillUseInfo, card, Race.KINGDOM);
            } else if (deadCardSkillUseInfo.getType() == SkillType.王国守护) {
                RacialBuff.remove(this, deadCardSkillUseInfo, card, Race.KINGDOM);
            } else if (deadCardSkillUseInfo.getType() == SkillType.森林之力) {
                RacialBuff.remove(this, deadCardSkillUseInfo, card, Race.FOREST);
            } else if (deadCardSkillUseInfo.getType() == SkillType.森林守护) {
                RacialBuff.remove(this, deadCardSkillUseInfo, card, Race.FOREST);
            } else if (deadCardSkillUseInfo.getType() == SkillType.森之星河) {
                RacialBuff.remove(this, deadCardSkillUseInfo.getAttachedUseInfo1(), card, Race.FOREST);
                RacialBuff.remove(this, deadCardSkillUseInfo.getAttachedUseInfo2(), card, Race.FOREST);
            } else if (deadCardSkillUseInfo.getType() == SkillType.王国悠歌) {
                RacialBuff.remove(this, deadCardSkillUseInfo.getAttachedUseInfo1(), card, Race.KINGDOM);
                RacialBuff.remove(this, deadCardSkillUseInfo.getAttachedUseInfo2(), card, Race.KINGDOM);
            } else if (deadCardSkillUseInfo.getType() == SkillType.蛮荒之力) {
                RacialBuff.remove(this, deadCardSkillUseInfo, card, Race.SAVAGE);
            } else if (deadCardSkillUseInfo.getType() == SkillType.蛮荒守护) {
                RacialBuff.remove(this, deadCardSkillUseInfo, card, Race.SAVAGE);
            } else if (deadCardSkillUseInfo.getType() == SkillType.地狱之力) {
                RacialBuff.remove(this, deadCardSkillUseInfo, card, Race.HELL);
            } else if (deadCardSkillUseInfo.getType() == SkillType.地狱守护) {
                RacialBuff.remove(this, deadCardSkillUseInfo, card, Race.HELL);
            } else if (deadCardSkillUseInfo.getType() == SkillType.本源之力) {
                RacialBuff.remove(this, deadCardSkillUseInfo, card, null);
            } else if (deadCardSkillUseInfo.getType() == SkillType.本源守护 || deadCardSkillUseInfo.getType() == SkillType.乐善好施) {
                RacialBuff.remove(this, deadCardSkillUseInfo, card, null);
            } else if (deadCardSkillUseInfo.getType() == SkillType.根源之力 || deadCardSkillUseInfo.getType() == SkillType.战争狂热) {
                TogetherBuff.remove(this, deadCardSkillUseInfo, card, null);
            } else if (deadCardSkillUseInfo.getType() == SkillType.神圣守护) {
                HolyGuard.remove(this, deadCardSkillUseInfo, card);
            } else if (deadCardSkillUseInfo.getType() == SkillType.生命符文) {
                CoefficientBuff.remove(this, deadCardSkillUseInfo, card, null);
            } else if (deadCardSkillUseInfo.getType() == SkillType.倾城之舞) {
                CoefficientBuff.remove(this, deadCardSkillUseInfo.getAttachedUseInfo2(), card, null);
            } else if (deadCardSkillUseInfo.getType() == SkillType.战歌之鼓) {
                CoefficientBuff.remove(this, deadCardSkillUseInfo, card, null);
            } else if (deadCardSkillUseInfo.getType() == SkillType.坚壁) {
                CoefficientThreeBuff.remove(this, deadCardSkillUseInfo, card);
            } else if (deadCardSkillUseInfo.getType() == SkillType.剑域) {
                CoefficientThreeBuff.remove(this, deadCardSkillUseInfo, card);
            } else if (deadCardSkillUseInfo.getType() == SkillType.偶像演出) {
                CoefficientThreeBuff.remove(this, deadCardSkillUseInfo, card);
            } else if (deadCardSkillUseInfo.getType() == SkillType.王之军阵) {
                CoefficientThreeBuff.remove(this, deadCardSkillUseInfo.getAttachedUseInfo1(), card);
                CoefficientThreeBuff.remove(this, deadCardSkillUseInfo.getAttachedUseInfo2(), card);
            } else if (deadCardSkillUseInfo.getType() == SkillType.魏之勇) {
                CountryBuff.remove(this, deadCardSkillUseInfo, card, null);
            } else if (deadCardSkillUseInfo.getType() == SkillType.魏之力) {
                CountryBuff.remove(this, deadCardSkillUseInfo, card, null);
            } else if (deadCardSkillUseInfo.getType() == SkillType.曹魏无双) {
                CountryBuff.remove(this, deadCardSkillUseInfo, card, null);
            } else if (deadCardSkillUseInfo.getType() == SkillType.军团王国之力
                    || deadCardSkillUseInfo.getType() == SkillType.军团森林之力
                    || deadCardSkillUseInfo.getType() == SkillType.军团蛮荒之力
                    || deadCardSkillUseInfo.getType() == SkillType.军团地狱之力) {
                LegionBuff.remove(this, deadCardSkillUseInfo, card);
            } else if (deadCardSkillUseInfo.getType() == SkillType.森之助) {
                CoefficientBuffExcludeSummon.remove(this, deadCardSkillUseInfo.getAttachedUseInfo1(), card, Race.FOREST);
                CoefficientBuffExcludeSummon.remove(this, deadCardSkillUseInfo.getAttachedUseInfo2(), card, Race.FOREST);
            } else if (deadCardSkillUseInfo.getType() == SkillType.星座能量热情 || deadCardSkillUseInfo.getType() == SkillType.星座能量清醒
                    || deadCardSkillUseInfo.getType() == SkillType.星座能量直感 || deadCardSkillUseInfo.getType() == SkillType.星座能量信念
                    || deadCardSkillUseInfo.getType() == SkillType.星座能量神秘 || deadCardSkillUseInfo.getType() == SkillType.星座能量智慧
                    || deadCardSkillUseInfo.getType() == SkillType.星座能量平衡 || deadCardSkillUseInfo.getType() == SkillType.星座能量掌握
                    || deadCardSkillUseInfo.getType() == SkillType.星座能量控制 || deadCardSkillUseInfo.getType() == SkillType.星座能量坚韧
                    || deadCardSkillUseInfo.getType() == SkillType.星座能量思考 || deadCardSkillUseInfo.getType() == SkillType.星座能量力量) {
                TogetherBuffOfStar.remove(this, deadCardSkillUseInfo, card);
            } else if (deadCardSkillUseInfo.getType() == SkillType.固守) {
                TogetherBuffOfStar.remove(this, deadCardSkillUseInfo, card);
            } else if (deadCardSkillUseInfo.getType() == SkillType.仁厚 || deadCardSkillUseInfo.getType() == SkillType.克己奉公 || deadCardSkillUseInfo.getType() == SkillType.诱敌深入
                    || deadCardSkillUseInfo.getType() == SkillType.反间 || deadCardSkillUseInfo.getType() == SkillType.真言结界) {
                TogetherBuffOfStar.remove(this, deadCardSkillUseInfo, card);
            } else if (deadCardSkillUseInfo.getType() == SkillType.勇冠三军) {
                TogetherBuffOfStar.remove(this, deadCardSkillUseInfo, card);
            } else if (deadCardSkillUseInfo.getType() == SkillType.勇武) {
                TogetherBuffOfStar.remove(this, deadCardSkillUseInfo, card);
            }
        }
        GiveSideSkill.removeAll(this, null, card);
        ImpregnableDefenseHeroBuff.remove(card, this); //铁壁的移除和buff的移除放在一起
        CounterAttackHero.remove(card, this); //背水的移除和buff的移除放在一起
        FailureSkillUseInfoList.remove(card, this); //腐化之地的移除和buff的移除放在一起
        SummonReturnSkillUseInfoList.remove(card, this); //眩目之光的移除和buff的移除放在一起
        CardEndSkillUseInfoList.remove(card, this); //上古神剑的移除和buff的移除放在一起
    }

    public void removeGiveSkills(CardInfo card) {
        for (SkillUseInfo cardSkillUseInfo : card.getUsableNormalSkills()) {
            if (cardSkillUseInfo.getGiveSkill() == 1) {
                card.removeGiveSkill();
                break;
            }
        }
    }

    public void resolveDebuff(CardInfo card, CardStatusType debuffType) throws HeroDieSignal {
        if (card == null) {
            return;
        }
        List<CardStatusItem> items = card.getStatus().getStatusOf(debuffType);
        for (CardStatusItem item : items) {
            this.stage.getUI().debuffDamage(card, item, item.getEffect());

            OnDamagedResult result = this.applyDamage(item.getCause().getOwner(), card, item.getCause().getSkill(), item.getEffect());
            this.resolveDeathSkills(item.getCause().getOwner(), card, item.getCause().getSkill(), result);
            if (result.cardDead) {
                break;
            }
        }
    }

    public void resolveAddATDebuff(CardInfo card, CardStatusType debuffType) throws HeroDieSignal {
        if (card == null) {
            return;
        }
        List<CardStatusItem> items = card.getStatus().getStatusOf(debuffType);
        for (CardStatusItem item : items) {
            SkillUseInfo skillUseInfo = item.getCause();
            this.stage.getUI().adjustAT(skillUseInfo.getOwner(), card, -item.getEffect(), skillUseInfo.getSkill());
            card.addEffect(new SkillEffect(SkillEffectType.ATTACK_CHANGE, skillUseInfo, -item.getEffect(), true));
        }
    }

    public BlockStatusResult resolveBlockStatusSkills(EntityInfo attacker, CardInfo victim, SkillUseInfo skillUseInfo, CardStatusItem item) {
        if (!FailureSkillUseInfoList.exploded(this, victim, attacker.getOwner())) {
            if (Unbending.isStatusEscaped(this, item, victim)) {
                return new BlockStatusResult(true);
            }
            for (RuneInfo rune : victim.getOwner().getRuneBox().getRunes()) {
                if (!rune.isActivated()) {
                    continue;
                }
                if (rune.is(RuneData.鬼步) && !victim.isSilent()) {
                    if (Escape.isStatusEscaped(rune.getSkill(), this, item, victim)) {
                        return new BlockStatusResult(true);
                    }
                }
            }
            for (SkillUseInfo blockSkillUseInfo : victim.getUsableNormalSkills()) {
                if (blockSkillUseInfo.getType() == SkillType.脱困 ||
                        blockSkillUseInfo.getType() == SkillType.神威 ||
                        blockSkillUseInfo.getType() == SkillType.临 ||
                        blockSkillUseInfo.getType() == SkillType.村正 ||
                        blockSkillUseInfo.getType() == SkillType.敏捷 ||
                        blockSkillUseInfo.getType() == SkillType.灵力魔阵 ||
                        blockSkillUseInfo.getType() == SkillType.月之守望 ||
                        blockSkillUseInfo.getType() == SkillType.冰神附体 ||
                        blockSkillUseInfo.getType() == SkillType.以逸待劳 ||
                        blockSkillUseInfo.getType() == SkillType.不灭原核 ||
                        blockSkillUseInfo.getType() == SkillType.黄天当立 ||
                        blockSkillUseInfo.getType() == SkillType.破阵弧光 ||
                        blockSkillUseInfo.getType() == SkillType.时光迁跃 ||
                        blockSkillUseInfo.getType() == SkillType.骑士信仰 ||
                        blockSkillUseInfo.getType() == SkillType.隐蔽 ||
                        blockSkillUseInfo.getType() == SkillType.女武神之辉 ||
                        blockSkillUseInfo.getType() == SkillType.无冕之王 ||
                        blockSkillUseInfo.getType() == SkillType.再生金蝉 ||
                        blockSkillUseInfo.getType() == SkillType.神之守护 ||
                        blockSkillUseInfo.getType() == SkillType.金蝉脱壳) {
                    if (Escape.isStatusEscaped(blockSkillUseInfo.getSkill(), this, item, victim)) {
                        return new BlockStatusResult(true);
                    }
                }
            }
        }
        return new BlockStatusResult(false);
    }

    private void setCardToField(CardInfo card, int flag) {
        Player player = card.getOwner();
        //添加一个flag，召唤的卡牌不会重置状态，为了解决召唤卡牌降临死亡和限定技能
        if (flag == 0) {
            card.resetStart();
        }
        card.setIsSummon(true);
        this.stage.getUI().summonCard(player, card);
        // 夺魂可以从敌方卡组召唤
        if (card.getOriginalOwner().getGrave().contains(card)) {
            card.getOriginalOwner().getGrave().removeCard(card);
        }
        player.getField().addCard(card);
        card.setSummonNumber(1);
        card.setAddDelay(0);
        player.getHand().removeCard(card);
        // 星云锁链之类可以从卡组直接召唤的情况
        player.getDeck().removeCard(card);
    }

    public void summonCard(Player player, CardInfo summonedCard, CardInfo reviver, boolean isMinion, Skill summonSkill, int flag) throws HeroDieSignal {
        Player enemy = this.getStage().getOpponent(player);
        setCardToField(summonedCard, flag);
        this.resolveFirstClassSummoningSkills(summonedCard, player, enemy, isMinion);
        // this.resolveSecondClassSummoningSkills(summonedCards, player.getField(), enemy.getField(), summonSkill, true);
        //取消召唤类技能直接发动二段技能。
    }

    // 重整类技能召唤卡牌
    public void summonCardReforming(Player player, CardInfo summonedCard, CardInfo reviver, boolean isMinion, SkillUseInfo summonSkillUseInfo, int flag) throws HeroDieSignal {
        Player enemy = this.getStage().getOpponent(player);
        setCardToField(summonedCard, flag);
        this.resolveFirstClassSummoningSkills(summonedCard, player, enemy, isMinion);
        // this.resolveSecondClassSummoningSkills(summonedCards, player.getField(), enemy.getField(), summonSkill, true);
        //取消召唤类技能直接发动二段技能。
    }

    // 星罗棋布类技能召唤卡牌
    public void summonCardScatter(Player player, CardInfo summonedCard, CardInfo reviver, boolean isMinion, SkillUseInfo summonSkillUseInfo, int flag) throws HeroDieSignal {
        Player enemy = this.getStage().getOpponent(player);
        setCardToField(summonedCard, flag);
        this.resolveFirstClassSummoningSkills(summonedCard, player, enemy, isMinion);
        List<CardInfo> cardInfoList = new ArrayList<>();
        cardInfoList.add(summonedCard);
        this.resolveSecondClassSummoningSkills(cardInfoList, player.getField(), enemy.getField(), summonSkillUseInfo.getSkill(), true);
        //取消召唤类技能直接发动二段技能。
    }

    public void summonCardIndenture(Player player, CardInfo summonedCard, IndentureInfo reviver, boolean isMinion, Skill summonSkill, int flag) throws HeroDieSignal {
        Player enemy = this.getStage().getOpponent(player);
        setCardToField(summonedCard, flag);
        this.resolveFirstClassSummoningSkills(summonedCard, player, enemy, isMinion);
        // this.resolveSecondClassSummoningSkills(summonedCards, player.getField(), enemy.getField(), summonSkill, true);
        //取消召唤类技能直接发动二段技能。
    }

    /**
     * 1. Process racial buff skills
     * 2. Process summoning skills
     *
     * @param player
     * @param isMinion
     * @param reviver
     * @throws HeroDieSignal
     */
    public void summonCards(Player player, CardInfo reviver, boolean isMinion) throws HeroDieSignal {
        Player enemy = this.getStage().getOpponent(player);
        List<CardInfo> summonedCards = new ArrayList<CardInfo>();
//        while (true) {
//            CardInfo summonedCard = null;
//            for (CardInfo card : player.getHand().toList()) {
//                if (card.getSummonDelay() == 0) {
//                    summonedCard = card;
//                    break;
//                }
//            }
//            if (summonedCard == null) {
//                break;
//            }
//            summonedCards.add(summonedCard);
//            setCardToField(summonedCard, 0);
//            this.resolveFirstClassSummoningSkills(summonedCard, player, enemy, isMinion);
//        }
        for (CardInfo card : player.getHand().toList()) {
            summonedCards.add(card);
        }
        for (CardInfo summonedCard : summonedCards) {
            if (summonedCard.getSummonDelay() == 0) {
                if (player.getHand().contains(summonedCard)) {
                    setCardToField(summonedCard, 0);
                    this.resolveFirstClassSummoningSkills(summonedCard, player, enemy, isMinion);
                }
            }
        }
        List<CardInfo> fieldCards = player.getField().toList();

        for (CardInfo card : fieldCards) {
            this.stage.getResolver().removeStatus(card, CardStatusType.复活);
        }
//      改变发动技能是所有卡牌不是当回合召唤卡牌
//        this.resolveSecondClassSummoningSkills(summonedCards, player.getField(), enemy.getField(), null, true);
        this.resolveSecondClassSummoningSkills(fieldCards, player.getField(), enemy.getField(), null, true);
    }

    /**
     * @param cardSkill
     * @param attacker
     * @param defender
     * @return Whether block is disabled
     */
    public boolean resolveCounterBlockSkill(Skill cardSkill, CardInfo attacker, CardInfo defender) {
        for (SkillUseInfo attackerSkillUseInfo : attacker.getUsableNormalSkills()) {
            if (attackerSkillUseInfo.getType() == SkillType.弱点攻击 ||
                    attackerSkillUseInfo.getType() == SkillType.会心一击 ||
                    attackerSkillUseInfo.getType() == SkillType.三千世界 ||
                    attackerSkillUseInfo.getType() == SkillType.亮银 ||
                    attackerSkillUseInfo.getType() == SkillType.鹰眼 ||
                    attackerSkillUseInfo.getType() == SkillType.九转禁术 ||
                    attackerSkillUseInfo.getType() == SkillType.刀语 ||
                    attackerSkillUseInfo.getType() == SkillType.一文字 ||
                    attackerSkillUseInfo.getType() == SkillType.魔王之怒 ||
                    attackerSkillUseInfo.getType() == SkillType.武圣) {
                return WeakPointAttack.isBlockSkillDisabled(this, attackerSkillUseInfo.getSkill(), cardSkill, attacker, defender);
            } else if (attackerSkillUseInfo.getType() == SkillType.斩杀 ||
                    attackerSkillUseInfo.getType() == SkillType.送葬之刃 ||
                    attackerSkillUseInfo.getType() == SkillType.页游击溃 ||
                    attackerSkillUseInfo.getType() == SkillType.无双 ||
                    attackerSkillUseInfo.getType() == SkillType.双斩 ||
                    attackerSkillUseInfo.getType() == SkillType.屏息 ||
                    attackerSkillUseInfo.getType() == SkillType.淘汰 ||
                    attackerSkillUseInfo.getType() == SkillType.义绝 ||
                    attackerSkillUseInfo.getType() == SkillType.破坏之爪) {
                return SuddenKill.isBlockSkillDisabled(this, attackerSkillUseInfo.getSkill(), cardSkill, attacker, defender);
            }
        }
        if (!attacker.isDead() && !attacker.isSilent() && !attacker.justRevived()) {
            {
                RuneInfo rune = attacker.getOwner().getActiveRuneOf(RuneData.鹰眼);
                if (rune != null && !attacker.justRevived()) {
                    return WeakPointAttack.isBlockSkillDisabled(this, rune.getSkill(), cardSkill, attacker, defender);
                }
            }
        }
        return false;
    }

    public boolean resolveStopBlockSkill(Skill cardSkill, CardInfo attacker, EntityInfo defender) {
        if (!FailureSkillUseInfoList.exploded(this, attacker, defender.getOwner())) {
            for (SkillUseInfo attackerSkillUseInfo : attacker.getUsableNormalSkills()) {
                if (attackerSkillUseInfo.getType() == SkillType.破军 || attackerSkillUseInfo.getType() == SkillType.原素裂变 || attackerSkillUseInfo.getType() == SkillType.溶骨的毒酒
                        || attackerSkillUseInfo.getType() == SkillType.狂舞
                        || attackerSkillUseInfo.getType() == SkillType.死亡收割 || attackerSkillUseInfo.getType() == SkillType.地裂劲
                        || attackerSkillUseInfo.getType() == SkillType.战舞 || attackerSkillUseInfo.getType() == SkillType.追击
                        || attackerSkillUseInfo.getType() == SkillType.狂性 || attackerSkillUseInfo.getType() == SkillType.灵魂腐朽
                        || attackerSkillUseInfo.getType() == SkillType.剧毒之咬) {
                    return DefeatArmy.isDefenSkillDisabled(this, attackerSkillUseInfo.getSkill(), cardSkill, attacker, defender);
                } else if (attackerSkillUseInfo.getType() == SkillType.夜袭 || attackerSkillUseInfo.getType() == SkillType.利器) {
                    return DefeatArmy.isDefenSkillDisabled(this, attackerSkillUseInfo.getAttachedUseInfo1().getSkill(), cardSkill, attacker, defender);
                }
            }
            if (!attacker.isDead() && !attacker.isSilent() && !attacker.justRevived()) {
                {
                    RuneInfo rune = attacker.getOwner().getActiveRuneOf(RuneData.破军);
                    if (rune != null && !attacker.justRevived()&&attacker.getRuneActive()) {
                        return DefeatArmy.isDefenSkillDisabled(this, rune.getSkill(), cardSkill, attacker, defender);
                    }
                }
            }
        }
        return false;
    }

    public void resolveTrumpetHorn(CardInfo summonCard) throws HeroDieSignal {
        for (SkillUseInfo skillUseInfo : summonCard.getAllNormalSkills()) {
            if (skillUseInfo.getType() == SkillType.深渊号角) {
                TrumpetHorn.apply(skillUseInfo, this, summonCard);
            }
        }
    }

    public boolean resolveStopHolyFire(Player defender) {
        for (CardInfo defenderCard : defender.getField().getAliveCards()) {
            for (SkillUseInfo defenderSkillUseInfo : defenderCard.getUsableNormalSkills()) {
                if (defenderSkillUseInfo.getType() == SkillType.庇护 || defenderSkillUseInfo.getType() == SkillType.浴火 || defenderSkillUseInfo.getType() == SkillType.圣骸
                        || defenderSkillUseInfo.getType() == SkillType.缚魂领域
                        || defenderSkillUseInfo.getType() == SkillType.兵粮寸断 || defenderSkillUseInfo.getType() == SkillType.庇护) {
                    this.getStage().getUI().useSkill(defenderCard, defender, defenderSkillUseInfo.getSkill(), true);
                    return false;
                }
            }
        }
        return true;
    }

    public boolean resolveStopDelay(Player defender) {
        Player opponent = this.getStage().getOpponent(defender);
        for (CardInfo defenderCard : defender.getField().getAliveCards()) {
            if (!FailureSkillUseInfoList.exploded(this, defenderCard, opponent)) {
                for (SkillUseInfo skillUseInfo : defenderCard.getUsableNormalSkills()) {
                    if (skillUseInfo.getType() == SkillType.稳定 || skillUseInfo.getType() == SkillType.守如山 || skillUseInfo.getType() == SkillType.守如山疾) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean resolveStopCardDelay(CardInfo defenderCard) {
        if (defenderCard.containsUsableSkill(SkillType.风行) || defenderCard.containsUsableSkill(SkillType.免疫风行) || defenderCard.containsUsableSkill(SkillType.徐如林)) {
            return true;
        }
        return false;
    }

    public boolean resolveIsImmune(CardInfo defender, int type) {
        //0包括法反，1只有免疫
        Player opponent = this.getStage().getOpponent(defender.getOwner());
        if (!FailureSkillUseInfoList.exploded(this, defender, opponent)) {
            if (type == 0) {
                for (SkillUseInfo blockSkillUseInfo : defender.getAllUsableSkillsIgnoreSilence()) {
                    if (blockSkillUseInfo.getType() == SkillType.免疫 || blockSkillUseInfo.getType() == SkillType.结界立场
                            || blockSkillUseInfo.getType() == SkillType.影青龙 || blockSkillUseInfo.getType() == SkillType.龙战于野 || blockSkillUseInfo.getType() == SkillType.魔力泳圈
                            || blockSkillUseInfo.getType() == SkillType.禁区之王 || blockSkillUseInfo.getType() == SkillType.恶龙血脉 || blockSkillUseInfo.getType() == SkillType.不息神盾
                            || blockSkillUseInfo.getType() == SkillType.彻骨之寒 || blockSkillUseInfo.getType() == SkillType.灵能冲击 || blockSkillUseInfo.getType() == SkillType.嗜魔之体
                            || blockSkillUseInfo.getType() == SkillType.魔力抗性 || blockSkillUseInfo.getType() == SkillType.轮回渡厄 || blockSkillUseInfo.getType() == SkillType.明月渡我
                            || blockSkillUseInfo.getType() == SkillType.免疫风行 || blockSkillUseInfo.getType() == SkillType.优雅之姿 || blockSkillUseInfo.getType() == SkillType.神衣
                            || blockSkillUseInfo.getType() == SkillType.复仇之影 || blockSkillUseInfo.getType() == SkillType.死亡之矢 || blockSkillUseInfo.getType() == SkillType.神佑复苏
                            || blockSkillUseInfo.getType() == SkillType.弑魂夺魄 || blockSkillUseInfo.getType() == SkillType.不灭之魂 || blockSkillUseInfo.getType() == SkillType.雷神附体
                            || blockSkillUseInfo.getType() == SkillType.秘术投影 || blockSkillUseInfo.getType() == SkillType.夺命骨镰 || blockSkillUseInfo.getType() == SkillType.风势
                            || blockSkillUseInfo.getType() == SkillType.醉生梦死 || blockSkillUseInfo.getType() == SkillType.魂飞魄散 || blockSkillUseInfo.getType() == SkillType.摄魂之力
                            || blockSkillUseInfo.getType() == SkillType.三位一体 || blockSkillUseInfo.getType() == SkillType.不灭金身 || blockSkillUseInfo.getType() == SkillType.时间扭曲
                            || blockSkillUseInfo.getType() == SkillType.忠肝义胆 || blockSkillUseInfo.getType() == SkillType.异元干扰 || blockSkillUseInfo.getType() == SkillType.金元仙躯
                            || blockSkillUseInfo.getType() == SkillType.迷影森森 || blockSkillUseInfo.getType() == SkillType.魏文帝 || blockSkillUseInfo.getType() == SkillType.归心
                            || blockSkillUseInfo.getType() == SkillType.阴阳术轮回 || blockSkillUseInfo.getType() == SkillType.神赐之躯 || blockSkillUseInfo.getType() == SkillType.魏之恋
                            || blockSkillUseInfo.getType() == SkillType.三界行者 || blockSkillUseInfo.getType() == SkillType.起死回生 || blockSkillUseInfo.getType() == SkillType.乱世枭雄
                            || blockSkillUseInfo.getType() == SkillType.净世破魔 || blockSkillUseInfo.getType() == SkillType.海滨乐园 || blockSkillUseInfo.getType() == SkillType.圣剑
                            || blockSkillUseInfo.getType() == SkillType.奥术之源 || blockSkillUseInfo.getType() == SkillType.流星 || blockSkillUseInfo.getType() == SkillType.逆战光辉
                            || blockSkillUseInfo.getType() == SkillType.神兵天降 || blockSkillUseInfo.getType() == SkillType.沉默领域 || blockSkillUseInfo.getType() == SkillType.贪狼
                            || blockSkillUseInfo.getType() == SkillType.逆战 || blockSkillUseInfo.getType() == SkillType.三昧真火 || blockSkillUseInfo.getType() == SkillType.周旋
                            || blockSkillUseInfo.getType() == SkillType.吴之悌 || blockSkillUseInfo.getType() == SkillType.吴之恋 || blockSkillUseInfo.getType() == SkillType.白驹过隙
                            || blockSkillUseInfo.getType() == SkillType.拉莱耶领域 || blockSkillUseInfo.getType() == SkillType.太平清领书 || blockSkillUseInfo.getType() == SkillType.永生审判
                            || blockSkillUseInfo.getType() == SkillType.逆转之矢 || blockSkillUseInfo.getType() == SkillType.坚不可摧 || blockSkillUseInfo.getType() == SkillType.轮回天生
                            || blockSkillUseInfo.getType() == SkillType.无限剑制 || blockSkillUseInfo.getType() == SkillType.死亡女神 || blockSkillUseInfo.getType() == SkillType.弑神之剑
                            || blockSkillUseInfo.getType() == SkillType.不死金身 || blockSkillUseInfo.getType() == SkillType.七罪 || blockSkillUseInfo.getType() == SkillType.海姆冥界
                            || blockSkillUseInfo.getType() == SkillType.红尘缥缈仙 || blockSkillUseInfo.getType() == SkillType.平沙落雁
                            || blockSkillUseInfo.getType() == SkillType.天官帝君 || blockSkillUseInfo.getType() == SkillType.恒星之力 || blockSkillUseInfo.getType() == SkillType.紫电
                            || blockSkillUseInfo.getType() == SkillType.辞旧迎新 || blockSkillUseInfo.getType() == SkillType.热情似火 || blockSkillUseInfo.getType() == SkillType.入木三分) {
                        return true;
                    } else if (blockSkillUseInfo.getType() == SkillType.法力反射 ||
                            blockSkillUseInfo.getType() == SkillType.镜面装甲 ||
                            blockSkillUseInfo.getType() == SkillType.花族秘术 ||
                            blockSkillUseInfo.getType() == SkillType.不夜蔷薇 ||
                            blockSkillUseInfo.getType() == SkillType.魔法克星 ||
                            blockSkillUseInfo.getType() == SkillType.武形秘术) {
                        return true;
                    }
                }

            } else if (type == 1) {
                for (SkillUseInfo blockSkillUseInfo : defender.getAllUsableSkillsIgnoreSilence()) {
                    if (blockSkillUseInfo.getType() == SkillType.免疫 || blockSkillUseInfo.getType() == SkillType.结界立场
                            || blockSkillUseInfo.getType() == SkillType.影青龙 || blockSkillUseInfo.getType() == SkillType.龙战于野 || blockSkillUseInfo.getType() == SkillType.魔力泳圈
                            || blockSkillUseInfo.getType() == SkillType.禁区之王 || blockSkillUseInfo.getType() == SkillType.恶龙血脉 || blockSkillUseInfo.getType() == SkillType.不息神盾
                            || blockSkillUseInfo.getType() == SkillType.彻骨之寒 || blockSkillUseInfo.getType() == SkillType.灵能冲击 || blockSkillUseInfo.getType() == SkillType.嗜魔之体
                            || blockSkillUseInfo.getType() == SkillType.魔力抗性 || blockSkillUseInfo.getType() == SkillType.轮回渡厄 || blockSkillUseInfo.getType() == SkillType.明月渡我
                            || blockSkillUseInfo.getType() == SkillType.免疫风行 || blockSkillUseInfo.getType() == SkillType.优雅之姿 || blockSkillUseInfo.getType() == SkillType.神衣
                            || blockSkillUseInfo.getType() == SkillType.复仇之影 || blockSkillUseInfo.getType() == SkillType.死亡之矢 || blockSkillUseInfo.getType() == SkillType.神佑复苏
                            || blockSkillUseInfo.getType() == SkillType.弑魂夺魄 || blockSkillUseInfo.getType() == SkillType.不灭之魂 || blockSkillUseInfo.getType() == SkillType.雷神附体
                            || blockSkillUseInfo.getType() == SkillType.秘术投影 || blockSkillUseInfo.getType() == SkillType.夺命骨镰 || blockSkillUseInfo.getType() == SkillType.风势
                            || blockSkillUseInfo.getType() == SkillType.醉生梦死 || blockSkillUseInfo.getType() == SkillType.魂飞魄散 || blockSkillUseInfo.getType() == SkillType.摄魂之力
                            || blockSkillUseInfo.getType() == SkillType.三位一体 || blockSkillUseInfo.getType() == SkillType.不灭金身 || blockSkillUseInfo.getType() == SkillType.时间扭曲
                            || blockSkillUseInfo.getType() == SkillType.忠肝义胆 || blockSkillUseInfo.getType() == SkillType.异元干扰 || blockSkillUseInfo.getType() == SkillType.金元仙躯
                            || blockSkillUseInfo.getType() == SkillType.迷影森森 || blockSkillUseInfo.getType() == SkillType.魏文帝 || blockSkillUseInfo.getType() == SkillType.归心
                            || blockSkillUseInfo.getType() == SkillType.阴阳术轮回 || blockSkillUseInfo.getType() == SkillType.神赐之躯 || blockSkillUseInfo.getType() == SkillType.魏之恋
                            || blockSkillUseInfo.getType() == SkillType.三界行者 || blockSkillUseInfo.getType() == SkillType.起死回生 || blockSkillUseInfo.getType() == SkillType.乱世枭雄
                            || blockSkillUseInfo.getType() == SkillType.净世破魔 || blockSkillUseInfo.getType() == SkillType.圣剑 || blockSkillUseInfo.getType() == SkillType.流星
                            || blockSkillUseInfo.getType() == SkillType.海滨乐园 || blockSkillUseInfo.getType() == SkillType.奥术之源 || blockSkillUseInfo.getType() == SkillType.逆战光辉
                            || blockSkillUseInfo.getType() == SkillType.神兵天降 || blockSkillUseInfo.getType() == SkillType.沉默领域 || blockSkillUseInfo.getType() == SkillType.贪狼
                            || blockSkillUseInfo.getType() == SkillType.逆战 || blockSkillUseInfo.getType() == SkillType.三昧真火 || blockSkillUseInfo.getType() == SkillType.周旋
                            || blockSkillUseInfo.getType() == SkillType.吴之悌 || blockSkillUseInfo.getType() == SkillType.吴之恋 || blockSkillUseInfo.getType() == SkillType.白驹过隙
                            || blockSkillUseInfo.getType() == SkillType.拉莱耶领域 || blockSkillUseInfo.getType() == SkillType.太平清领书 || blockSkillUseInfo.getType() == SkillType.永生审判
                            || blockSkillUseInfo.getType() == SkillType.逆转之矢 || blockSkillUseInfo.getType() == SkillType.坚不可摧 || blockSkillUseInfo.getType() == SkillType.轮回天生
                            || blockSkillUseInfo.getType() == SkillType.无限剑制 || blockSkillUseInfo.getType() == SkillType.死亡女神 || blockSkillUseInfo.getType() == SkillType.弑神之剑
                            || blockSkillUseInfo.getType() == SkillType.不死金身 || blockSkillUseInfo.getType() == SkillType.七罪 || blockSkillUseInfo.getType() == SkillType.海姆冥界
                            || blockSkillUseInfo.getType() == SkillType.红尘缥缈仙 || blockSkillUseInfo.getType() == SkillType.平沙落雁
                            || blockSkillUseInfo.getType() == SkillType.天官帝君 || blockSkillUseInfo.getType() == SkillType.恒星之力 || blockSkillUseInfo.getType() == SkillType.紫电
                            || blockSkillUseInfo.getType() == SkillType.辞旧迎新 || blockSkillUseInfo.getType() == SkillType.热情似火 || blockSkillUseInfo.getType() == SkillType.入木三分) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean resolverCounterAttackBlockSkill(Skill counterAttackSkill, CardInfo attacker, CardInfo counterAttacker) {
        for (SkillUseInfo skillUseInfo : attacker.getUsableNormalSkills()) {
            if (skillUseInfo.getType() == SkillType.灵巧 ||
                    skillUseInfo.getType() == SkillType.武形秘仪 ||
                    skillUseInfo.getType() == SkillType.修罗道 ||
                    skillUseInfo.getType() == SkillType.页游屏息 ||
                    skillUseInfo.getType() == SkillType.直感) {
                return Agile.isCounterAttackSkillDisabled(this, skillUseInfo.getSkill(), counterAttackSkill, attacker, counterAttacker);
            }
        }
        return false;
    }

    public void activateRunes(Player player, Player enemy) {
        activateCardRunes(player);//设置卡牌是否能激活符文
        for (RuneInfo rune : player.getRuneBox().getRunes()) {
            if (rune.getEnergy() <= 0) {
                continue;
            }
            if (rune.getName().equals("背水") && player.getField().size() == 0) {
                continue;
            }
            if (rune.getName().equals("逆流") && player.getField().size() == 0) {
                continue;
            }
            if (rune.getName().equals("止水") && player.getField().size() == 0) {
                continue;
            }
            boolean shouldActivate = false;
            RuneActivator activator = rune.getActivator();
            if (activator.getType() == RuneActivationType.HeroHP) {
                if (player.getHP() < activator.getThreshold() * player.getMaxHP() / 100) {
                    shouldActivate = true;
                }
            } else if (activator.getType() == RuneActivationType.Field) {
                Player playerToCheck = activator.shouldCheckEnemy() ? enemy : player;
                int activatorCardCount = 0;
                if (activator.getRace() == null) {
                    activatorCardCount = playerToCheck.getField().getAliveCards().size();
                } else {
                    for (CardInfo card : playerToCheck.getField().getAliveCards()) {
                        if (card.getRace() == activator.getRace()) {
                            ++activatorCardCount;
                        }
                    }
                }
                if (activatorCardCount > activator.getThreshold()) {
                    shouldActivate = true;
                }
            } else if (activator.getType() == RuneActivationType.Grave) {
                Player playerToCheck = activator.shouldCheckEnemy() ? enemy : player;
                int activatorCardCount = 0;
                if (activator.getRace() == null) {
                    activatorCardCount = playerToCheck.getGrave().size();
                } else {
                    for (CardInfo card : playerToCheck.getGrave().toList()) {
                        if (card.getRace() == activator.getRace()) {
                            ++activatorCardCount;
                        }
                    }
                }
                if (activatorCardCount > activator.getThreshold()) {
                    shouldActivate = true;
                }
            } else if (activator.getType() == RuneActivationType.Hand) {
                Player playerToCheck = activator.shouldCheckEnemy() ? enemy : player;
                int activatorCardCount = 0;
                if (activator.getRace() == null) {
                    activatorCardCount = playerToCheck.getHand().size();
                } else {
                    for (CardInfo card : playerToCheck.getHand().toList()) {
                        if (card.getRace() == activator.getRace()) {
                            ++activatorCardCount;
                        }
                    }
                }
                if (activatorCardCount > activator.getThreshold()) {
                    shouldActivate = true;
                }
            } else if (activator.getType() == RuneActivationType.HandLess) {
                Player playerToCheck = activator.shouldCheckEnemy() ? enemy : player;
                int activatorCardCount = 0;
                if (activator.getRace() == null) {
                    activatorCardCount = playerToCheck.getHand().size();
                } else {
                    for (CardInfo card : playerToCheck.getHand().toList()) {
                        if (card.getRace() == activator.getRace()) {
                            ++activatorCardCount;
                        }
                    }
                }
                if (activatorCardCount <= activator.getThreshold()) {
                    shouldActivate = true;
                }
            } else if (activator.getType() == RuneActivationType.Deck) {
                Player playerToCheck = activator.shouldCheckEnemy() ? enemy : player;
                int activatorCardCount = 0;
                if (activator.getRace() == null) {
                    activatorCardCount = playerToCheck.getDeck().size();
                } else {
                    for (CardInfo card : playerToCheck.getDeck().toList()) {
                        if (card.getRace() == activator.getRace()) {
                            ++activatorCardCount;
                        }
                    }
                }
                if (activatorCardCount < activator.getThreshold()) {
                    shouldActivate = true;
                }
            } else if (activator.getType() == RuneActivationType.Round) {
                if (stage.getRound() > activator.getThreshold()) {
                    shouldActivate = true;
                }
            } else if (activator.getType() == RuneActivationType.FieldDiff) {
                shouldActivate = enemy.getField().getAliveCards().size() - player.getField().getAliveCards().size() > activator.getThreshold();
            }

            if (!shouldActivate) {
                continue;
            }

            // Special logic for 永冻 & 春风 & 清泉 & 冰封 & 灼魂.
            if (rune.is(RuneData.清泉)) {
                if (player.getField().getAliveCards().isEmpty()) {
                    shouldActivate = false;
                } else {
                    boolean anyCardWounded = false;
                    for (CardInfo card : player.getField().getAliveCards()) {
                        if (card.isWounded()) {
                            anyCardWounded = true;
                            break;
                        }
                    }
                    if (!anyCardWounded) {
                        shouldActivate = false;
                    }
                }
            } else if (rune.is(RuneData.春风) || rune.is(RuneData.冰封)) {
                if (player.getField().getAliveCards().isEmpty()) {
                    shouldActivate = false;
                }
            }

            if (shouldActivate) {
                this.stage.getUI().activateRune(rune);
                rune.activate();
            }
        }
    }

    //守墓技能发动
    public void guardGrave(Player player, Player enemy) throws HeroDieSignal {
        for (CardInfo graveCard : player.getGrave().toList()) {
            for (SkillUseInfo skillUseInfo : graveCard.getAllUsableSkillsIgnoreSilence()) {
                if (skillUseInfo.getType() == SkillType.守墓者) {
                    GuardGrave.explode(this,graveCard,skillUseInfo);
                    break;
                }
            }
        }
    }

    //召唤契约
    public void activateIndentures(Player player, Player enemy) throws HeroDieSignal {
        for (IndentureInfo indenture : player.getIndentureBox().getIndentureInfos()) {
            IndentureActivator activator = indenture.getIndentureActivator();
            if (activator.getType() == RuneActivationType.HeroHPLess) {
                if (player.getHP() < indenture.getEffectNumber() * player.getMaxHP() / 100) {
                    SummonOfIndenture.apply(this, indenture);
                }
            } else if (activator.getType() == RuneActivationType.Field) {
                Player playerToCheck = activator.shouldCheckEnemy() ? enemy : player;
                int activatorCardCount = 0;
                if (activator.getRace() == null) {
                    activatorCardCount = playerToCheck.getField().getAliveCards().size();
                } else {
                    for (CardInfo card : playerToCheck.getField().getAliveCards()) {
                        if (card.getRace() == activator.getRace()) {
                            ++activatorCardCount;
                        }
                    }
                }
                if (activatorCardCount > indenture.getEffectNumber()) {
                    SummonOfIndenture.apply(this, indenture);
                }
            } else if (activator.getType() == RuneActivationType.HeroHpMore) {
                if (player.getHP() > indenture.getEffectNumber() * player.getMaxHP() / 100) {
                    SummonOfIndenture.apply(this, indenture);
                }
            } else if (activator.getType() == RuneActivationType.RoundMore) {
                if (stage.getRound() > indenture.getEffectNumber()) {
                    SummonOfIndenture.apply(this, indenture);
                }
            } else if (activator.getType() == RuneActivationType.RoundLess) {
                if (stage.getRound() < indenture.getEffectNumber()) {
                    SummonOfIndenture.apply(this, indenture);
                }
            } else if (activator.getType() == RuneActivationType.FieldDiff) {
                if (enemy.getField().getAliveCards().size() - player.getField().getAliveCards().size() > indenture.getEffectNumber()) {
                    SummonOfIndenture.apply(this, indenture);
                }
            } else if (activator.getType() == RuneActivationType.Deck) {
                Player playerToCheck = activator.shouldCheckEnemy() ? enemy : player;
                int activatorCardCount = 0;
                if (activator.getRace() == null) {
                    activatorCardCount = playerToCheck.getDeck().size();
                } else {
                    for (CardInfo card : playerToCheck.getDeck().toList()) {
                        if (card.getRace() == activator.getRace()) {
                            ++activatorCardCount;
                        }
                    }
                }
                if (activatorCardCount < indenture.getEffectNumber()) {
                    SummonOfIndenture.apply(this, indenture);
                }
            } else if (activator.getType() == RuneActivationType.Grave) {
                Player playerToCheck = activator.shouldCheckEnemy() ? enemy : player;
                int activatorCardCount = 0;
                if (activator.getRace() == null) {
                    activatorCardCount = playerToCheck.getGrave().size();
                } else {
                    for (CardInfo card : playerToCheck.getGrave().toList()) {
                        if (card.getRace() == activator.getRace()) {
                            ++activatorCardCount;
                        }
                    }
                }
                if (activatorCardCount > indenture.getEffectNumber()) {
                    SummonOfIndenture.apply(this, indenture);
                }
            } else if (activator.getType() == RuneActivationType.Hand) {
                Player playerToCheck = activator.shouldCheckEnemy() ? enemy : player;
                int activatorCardCount = 0;
                if (activator.getRace() == null) {
                    activatorCardCount = playerToCheck.getHand().size();
                } else {
                    for (CardInfo card : playerToCheck.getHand().toList()) {
                        if (card.getRace() == activator.getRace()) {
                            ++activatorCardCount;
                        }
                    }
                }
                if (activatorCardCount > indenture.getEffectNumber()) {
                    SummonOfIndenture.apply(this, indenture);
                }
            } else if (activator.getType() == RuneActivationType.DobleOrRace) {
                Player playerToCheck = activator.shouldCheckEnemy() ? enemy : player;
                int activatorCardCount = 0;
                for (CardInfo card : playerToCheck.getField().getAliveCards()) {
                    if (card.getRace() == Race.KINGDOM || card.getRace() == Race.FOREST) {
                        ++activatorCardCount;
                    }
                }
                if (activatorCardCount > indenture.getEffectNumber()) {
                    SummonOfIndenture.apply(this, indenture);
                }
            } else if (activator.getType() == RuneActivationType.DoubleRace) {
                Player playerToCheck = activator.shouldCheckEnemy() ? enemy : player;
                int activatorCardCount = 0;
                int kingdomCount = 0;
                int forestCount = 0;
                for (CardInfo card : playerToCheck.getField().getAliveCards()) {
                    if (card.getRace() == Race.KINGDOM) {
                        ++kingdomCount;
                    } else if (card.getRace() == Race.FOREST) {
                        ++forestCount;
                    }
                }
                activatorCardCount = kingdomCount > forestCount ? forestCount : kingdomCount;
                if (activatorCardCount > indenture.getEffectNumber()) {
                    SummonOfIndenture.apply(this, indenture);
                }
            } else if (activator.getType() == RuneActivationType.FourRace) {
                int activatorCardCount = 0;
                activatorCardCount = player.getField().getAliveCards().size();
                if (activatorCardCount > indenture.getEffectNumber()) {
                    SummonOfIndenture.apply(this, indenture);
                }
            } else if (activator.getType() == RuneActivationType.FieldLess) {
                Player playerToCheck = activator.shouldCheckEnemy() ? enemy : player;
                int activatorCardCount = 0;
                if (activator.getRace() == null) {
                    activatorCardCount = playerToCheck.getField().getAliveCards().size();
                } else {
                    for (CardInfo card : playerToCheck.getField().getAliveCards()) {
                        if (card.getRace() == activator.getRace()) {
                            ++activatorCardCount;
                        }
                    }
                }
                if (activatorCardCount < indenture.getEffectNumber()) {
                    SummonOfIndenture.apply(this, indenture);
                }
            }
        }
        List<EquipmentInfo> equipmentInfos = player.getEquipmentBox().getEquipmentInfos();
        for (EquipmentInfo equipmentInfo : equipmentInfos) {
            for (SkillUseInfo equipmentSkillUserInfo : equipmentInfo.getSkillUseInfoList()) {
                if (equipmentSkillUserInfo.getType() == SkillType.装备恶魔诅咒) {
                    if (stage.getRound() < 16) {
                        CurseByEquipment.apply(this, equipmentSkillUserInfo.getSkill(), equipmentInfo, enemy);
                    }
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备瘟疫蔓延) {
                    if (player.getOwner().getHP() < player.getOwner().getMaxHP() * 0.6) {
                        GrudgeByEquipment.apply(this, equipmentSkillUserInfo, equipmentInfo, enemy, 2, 2);
                    }
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备雷神之锤) {
                    if (enemy.getField().getAliveCards().size()>7) {
                       RedGunByDamnationEquipment.apply(equipmentSkillUserInfo, this, equipmentInfo, enemy, 3);
                    }
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备围攻) {
                    if (stage.getRound() < 16) {
                        NebulaChainByEquipment.apply(equipmentSkillUserInfo, this, equipmentInfo, enemy);
                    }
                } else if (equipmentSkillUserInfo.getType() == SkillType.装备恶魔重生) {
                    if (player.getField().getAliveCards().size()>1) {
                        AddFiledCardMultSkillByEquipment.apply(this, equipmentSkillUserInfo, equipmentInfo, equipmentSkillUserInfo.getAttachedUseInfo1().getSkill());
                    }
                }

            }
        }
    }

    public void activateCardRunes(Player player) {
        for (CardInfo card : player.getField().getAliveCards()) {
            card.setRuneActive(true);
        }
    }

    public void deactivateRunes(Player player) {
        for (RuneInfo rune : player.getRuneBox().getRunes()) {
            if (!rune.isActivated()) {
                continue;
            }
            stage.getUI().deactivateRune(rune);
            rune.deactivate();
            for (CardInfo card : player.getField().getAliveCards()) {
                for (SkillEffect effect : card.getEffects()) {
                    if (effect.getCause().equals(rune.getSkillUseInfo())) {
                        if (rune.getSkill().getType().containsTag(SkillTag.永久)) {
                            continue;
                        }
                        if (effect.getType() == SkillEffectType.ATTACK_CHANGE) {
                            stage.getUI().loseAdjustATEffect(card, effect);
                        } else if (effect.getType() == SkillEffectType.MAXHP_CHANGE) {
                            stage.getUI().loseAdjustHPEffect(card, effect);
                        } else if (effect.getType() == SkillEffectType.SKILL_USED) {
                            // DO NOTHING..
                        } else {
                            throw new CardFantasyRuntimeException("Invalid skill effect type " + effect.getType());
                        }
                        card.removeEffect(effect);
                    }
                }
            }
        }
    }

    public void resolvePreAttackRune(Player attackerHero, Player defenderHero) throws HeroDieSignal {
        for (RuneInfo rune : attackerHero.getRuneBox().getRunes()) {
            if (!rune.isActivated()) {
                continue;
            }
            if (rune.is(RuneData.荒芜)) {
                PoisonMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, 1);
            } else if (rune.is(RuneData.沼泽)) {
                PoisonMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, 3);
            } else if (rune.is(RuneData.岩晶)) {
                EnergyArmor.apply(this, rune.getSkillUseInfo(), rune, 1);
            } else if (rune.is(RuneData.毒砂)) {
                PoisonMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, 1);
            } else if (rune.is(RuneData.深渊)) {
                PoisonMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, 3);
            } else if (rune.is(RuneData.陨星)) {
                Plague.apply(rune.getSkillUseInfo(), this, rune, defenderHero);
            } else if (rune.is(RuneData.死域)) {
                PoisonMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, -1);
            } else if (rune.is(RuneData.霜冻)) {
                IceMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, 1, 45, 0);
            } else if (rune.is(RuneData.寒潮)) {
                IceMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, 3, 35, 0);
            } else if (rune.is(RuneData.冰锥)) {
                IceMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, 1, 45, 0);
            } else if (rune.is(RuneData.暴雨)) {
                WeakenAll.apply(this, rune.getSkillUseInfo(), rune, defenderHero);
            } else if (rune.is(RuneData.清泉)) {
                Rainfall.apply(rune.getSkill(), this, rune);
            } else if (rune.is(RuneData.雪崩)) {
                IceMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, 3, 35, 0);
            } else if (rune.is(RuneData.圣泉)) {
                Pray.apply(rune.getSkill(), this, rune);
            } else if (rune.is(RuneData.永冻)) {
                IceMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, -1, 30, 0);
            } else if (rune.is(RuneData.闪电)) {
                LighteningMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, 1, 50);
            } else if (rune.is(RuneData.雷云)) {
                LighteningMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, 3, 40);
            } else if (rune.is(RuneData.霹雳)) {
                LighteningMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, 1, 50);
            } else if (rune.is(RuneData.飞羽)) {
                Snipe.apply(rune.getSkillUseInfo(), rune.getSkill(), this, rune, defenderHero, 1);
            } else if (rune.is(RuneData.飓风)) {
                LighteningMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, 3, 40);
            } else if (rune.is(RuneData.春风)) {
                EnergyArmor.apply(this, rune.getSkillUseInfo(), rune, -1);
            } else if (rune.is(RuneData.雷狱)) {
                LighteningMagic.apply(rune.getSkillUseInfo(), this, rune, defenderHero, -1, 35);
            } else if (rune.is(RuneData.火拳)) {
                FireMagic.apply(rune.getSkill(), this, rune, defenderHero, 1);
            } else if (rune.is(RuneData.热浪)) {
                FireMagic.apply(rune.getSkill(), this, rune, defenderHero, 3);
            } else if (rune.is(RuneData.流火)) {
                FireMagic.apply(rune.getSkill(), this, rune, defenderHero, 1);
            } else if (rune.is(RuneData.红莲)) {
                Heal.apply(rune.getSkill(), this, rune);
            } else if (rune.is(RuneData.冥火)) {
                BurningFlame.apply(rune.getSkillUseInfo(), this, rune, defenderHero, -1);
            } else if (rune.is(RuneData.淬炼)) {
                AttackUp.apply(this, rune.getSkillUseInfo(), rune, -1);
            } else if (rune.is(RuneData.焚天)) {
                FireMagic.apply(rune.getSkill(), this, rune, defenderHero, 3);
            } else if (rune.is(RuneData.灼魂)) {
                HeavenWrath.apply(this, rune.getSkill(), rune, defenderHero);
            } else if (rune.is(RuneData.灭世)) {
                FireMagic.apply(rune.getSkill(), this, rune, defenderHero, -1);
            } else if (rune.is(RuneData.玄石)) {
                AllSpeedUp.apply(rune.getSkillUseInfo(), this, rune);
            } else if (rune.is(RuneData.龙吟)) {
                Bless.apply(rune.getSkillUseInfo().getSkill(), this, rune);
            } else if (rune.is(RuneData.神祈)) {
                Purify.apply(rune.getSkillUseInfo(), this, rune, -1);
            } else if (rune.is(RuneData.风暴)) {
                ManaErode.apply(rune.getSkill(), this, rune, defenderHero, -1);
            } else if (rune.is(RuneData.封闭)) {
                Silence.apply(this, rune.getSkillUseInfo(), rune, defenderHero, true, false);
            } else if (rune.is(RuneData.冥途)) {
                HolyFire.apply(rune.getSkillUseInfo().getSkill(), this, rune, defenderHero);
            } else if (rune.is(RuneData.玉轮)) {
                Polymorph.apply(this, rune.getSkillUseInfo(), rune, defenderHero, 1, 1);
            } else if (rune.is(RuneData.终焉)) {
                GiantEarthquakesLandslides.apply(this, rune.getSkillUseInfo().getSkill(), rune, defenderHero, 1);
            } else if (rune.is(RuneData.景星)) {
                Supplication.apply(this, rune.getSkillUseInfo(), rune, defenderHero);
            } else if (rune.is(RuneData.祥瑞)) {
                RegressionSoul.apply(this, rune.getSkillUseInfo(), rune, attackerHero);
            } else if (rune.is(RuneData.彼岸)) {
                int victimCount = 1;
                int effectNumber = 2;
                int level = rune.getSkillUseInfo().getSkill().getLevel();
                if (level < 3) {
                    victimCount = level;
                    effectNumber = 2;
                } else {
                    victimCount = level - 1;
                    effectNumber = 4;
                }
                SoulChains.apply(this, rune.getSkillUseInfo(), rune, defenderHero, victimCount, effectNumber);
            } else if (rune.is(RuneData.鬼面)) {
                int victimCount = 1;
                int effectNumber = 1;
                int level = rune.getSkillUseInfo().getSkill().getLevel();
                if (level < 2) {
                    victimCount = 1;
                } else if (level == 5) {
                    victimCount = 3;
                } else {
                    victimCount = 2;
                }
                if (level < 3) {
                    effectNumber = 1;
                } else {
                    effectNumber = 2;
                }
                Asthenia.apply(this, rune.getSkillUseInfo(), rune, defenderHero, victimCount, effectNumber);
            } else if (rune.is(RuneData.甘露)) {
                RemoveDebuffSkill.apply(rune.getSkillUseInfo(), this, rune, -1, defenderHero);
            }
        }
    }

    public void killCard(CardInfo attacker, CardInfo victim, Skill cardSkill) throws HeroDieSignal {
        int originalDamage = victim.getHP();
        int actualDamage = victim.applyDamage(victim.getHP());
        DeadType deadType = this.cardDead(attacker, cardSkill, victim);
        OnDamagedResult onDamagedResult = new OnDamagedResult();
        if (deadType == DeadType.SoulCrushed) {
            onDamagedResult.soulCrushed = true;
        } else if (deadType == DeadType.SoulControlDead) {
            onDamagedResult.soulControlDead = true;
        }
        onDamagedResult.cardDead = true;
        onDamagedResult.actualDamage = actualDamage;
        onDamagedResult.originalDamage = originalDamage;
        onDamagedResult.unbending = false;
        victim.removeStatus(CardStatusType.不屈);
        this.resolveDeathSkills(attacker, victim, cardSkill, onDamagedResult);
    }

    public void resolvePrecastSkills(CardInfo card, Player defenderHero, boolean flag) throws HeroDieSignal {
        if (!FailureSkillUseInfoList.explode(this, card, defenderHero)) {
            for (SkillUseInfo skillUseInfo : card.getUsablePrecastSkills()) {
                if (skillUseInfo.getType() == SkillType.凋零真言) {
                    WitheringWord.apply(skillUseInfo, this, card, defenderHero);
                } else if (skillUseInfo.getType() == SkillType.灵王的轰击 || skillUseInfo.getType() == SkillType.法力侵蚀 || skillUseInfo.getType() == SkillType.核弹头) {
                    ManaErode.apply(skillUseInfo.getSkill(), this, card, defenderHero, 1);
                } else if (skillUseInfo.getType() == SkillType.神性祈求) {
                    Purify.apply(skillUseInfo, this, card, -1);
                } else if (skillUseInfo.getType() == SkillType.寒霜冲击) {
                    IceMagic.apply(skillUseInfo, this, card, defenderHero, -1, 50, (5 + skillUseInfo.getSkill().getLevel() * 5) * defenderHero.getField().getAliveCards().size());
                } else if (skillUseInfo.getType() == SkillType.全体加速) {
                    AllSpeedUp.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.神行术) {
                    AllSpeedUp.apply(skillUseInfo, this, card);
                } else if (skillUseInfo.getType() == SkillType.混乱领域) {
                    Confusion.apply(skillUseInfo, this, card, defenderHero, 3);
                } else if (skillUseInfo.getType() == SkillType.拔刀术 || skillUseInfo.getType() == SkillType.厌战) {
                    TheSword.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.镜像 && flag) {
                    Summon.apply(this, skillUseInfo, card, SummonType.Normal, 1, card.getName());
                } else if (skillUseInfo.getType() == SkillType.误人子弟) {
                    Confusion.apply(skillUseInfo.getAttachedUseInfo2(), this, card, defenderHero, -1);
                } else if (skillUseInfo.getType() == SkillType.鬼神乱舞) {
                    MultipleSnipe.apply(skillUseInfo, skillUseInfo.getSkill(), this, card, defenderHero, 1);
                } else if (skillUseInfo.getType() == SkillType.一刀斩) {
                    SnipeOneNumber.apply(skillUseInfo, skillUseInfo.getSkill(), this, card, defenderHero, 1);
                } else if (skillUseInfo.getType() == SkillType.幻影 && flag) {
                    Summon.apply(this, skillUseInfo, card, SummonType.Normal, 1, card.getName());
                } else if (skillUseInfo.getType() == SkillType.请帮帮我) {
                    Supplication.apply(this, skillUseInfo, card, defenderHero);
                } else if (skillUseInfo.getType() == SkillType.全体阻碍) {
                    AllDelay.apply(skillUseInfo, this, card, defenderHero);
                } else if (skillUseInfo.getType() == SkillType.胜者为王) {
                    Transport.apply(this, skillUseInfo.getSkill(), card, defenderHero);
                } else if (skillUseInfo.getType() == SkillType.无上荣耀) {
                    Bless.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.御剑) {
                    RedGun.apply(skillUseInfo, this, card, defenderHero, 1);
                } else if (skillUseInfo.getType() == SkillType.虚幻之影) {
                    SummonWhenAttack.apply(this, skillUseInfo, card, 1, false, "魅魔之影");
                }
            }
            for (SkillUseInfo skillUseInfo : card.getUsableNormalSkills()) {
                if (skillUseInfo.getType() == SkillType.异元干扰) {
                    AllDelay.apply(skillUseInfo, this, card, defenderHero);
                }
            }
        }
    }

    public void resolvePostcastSkills(CardInfo card, Player defenderHero) throws HeroDieSignal {
        if (!FailureSkillUseInfoList.explode(this, card, defenderHero)) {
            for (SkillUseInfo skillUseInfo : card.getUsablePostcastSkills()) {
                if (skillUseInfo.getType() == SkillType.灵王的轰击) {
                    ManaErode.apply(skillUseInfo.getSkill(), this, card, defenderHero, 1);
                } else if (skillUseInfo.getType() == SkillType.修罗地火攻) {
                    SuraFire.apply(this, skillUseInfo, card, defenderHero);
                } else if (skillUseInfo.getType() == SkillType.寒霜冲击) {
                    IceMagic.apply(skillUseInfo, this, card, defenderHero, -1, 50, (5 + skillUseInfo.getSkill().getLevel() * 5) * defenderHero.getField().getAliveCards().size());
                } else if (skillUseInfo.getType() == SkillType.回魂) {
                    Resurrection.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.青囊) {
                    Revive.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.洛神) {
                    // 镜像召唤的单位可以被连锁攻击
                    Summon.apply(this, skillUseInfo, card, SummonType.Normal, 1, card.getName());
                } else if (skillUseInfo.getType() == SkillType.镜像) {
                    // 镜像召唤的单位可以被连锁攻击
                    Summon.apply(this, skillUseInfo, card, SummonType.Normal, 1, card.getName());
                } else if (skillUseInfo.getType() == SkillType.归魂) {
                    RegressionSoul.apply(this, skillUseInfo, card, defenderHero);
                } else if (skillUseInfo.getType() == SkillType.生命湍流) {
                    RegressionSoul.apply(this, skillUseInfo, card, defenderHero);
                } else if (skillUseInfo.getType() == SkillType.霜焰) {
                    IceMagic.apply(skillUseInfo, this, card, defenderHero, -1, 50, 120 * defenderHero.getField().getAliveCards().size());
                } else if (skillUseInfo.getType() == SkillType.不死灵药) {
                    Revive.apply(this, skillUseInfo, card);
                } else if (skillUseInfo.getType() == SkillType.地狱守望) {
                    SoulControlAll.apply(this, skillUseInfo, card, defenderHero);
                    HolyFire.apply(skillUseInfo.getSkill(), this, card, defenderHero);
                } else if (skillUseInfo.getType() == SkillType.地狱之火) {
                    Plague.apply(skillUseInfo, this, card, defenderHero);
                } else if (skillUseInfo.getType() == SkillType.祈福) {
                    Bless.apply(skillUseInfo.getSkill(), this, card);
                } else if (skillUseInfo.getType() == SkillType.流光回梦) {
                    ReturnToHandAndDelay.apply(this, skillUseInfo.getSkill(), card, defenderHero, 2, 1);
                } else if (skillUseInfo.getType() == SkillType.深渊诅咒) {
                    GrudgeAt.apply(this, skillUseInfo, card, defenderHero, 3);
                } else if (skillUseInfo.getType() == SkillType.翼龙之首) {
                    SealMagic.apply(skillUseInfo.getAttachedUseInfo2(), this, card, defenderHero, 1);
                }
            }
        }
    }

    public static class BlockStatusResult {
        private boolean blocked;

        public BlockStatusResult(boolean blocked) {
            this.blocked = blocked;
        }

        public boolean isBlocked() {
            return blocked;
        }
    }
}
