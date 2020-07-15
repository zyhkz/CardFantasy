package cfvbaibai.cardfantasy.engine;

import cfvbaibai.cardfantasy.NonSerializable;

public class CardStatusItem {
    private CardStatusType type;
    private int effect;
    private int effectNumber;//负面buff持续的回合数
    @NonSerializable
    private SkillUseInfo cause;

    private CardStatusItem(CardStatusType type, int effect, SkillUseInfo cause) {
        this.type = type;
        this.effect = effect;
        this.cause = cause;
        this.effectNumber=1;
    }

    public SkillUseInfo getCause() {
        return this.cause;
    }

    public CardStatusType getType() {
        return type;
    }

    public void setType(CardStatusType type) {
        this.type = type;
    }

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public static CardStatusItem paralyzed(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.麻痹, 0, cause);
    }

    public static CardStatusItem frozen(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.冰冻, 0, cause);
    }

    public static CardStatusItem poisoned(int effect, SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.中毒, effect, cause);
    }

    public static CardStatusItem trapped(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.锁定, 0, cause);
    }

    public static CardStatusItem burning(int effect, SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.燃烧, effect, cause);
    }

    public static CardStatusItem wound(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.裂伤, 0, cause);
    }

    public static CardStatusItem weak(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.复活, 0, cause);
    }

    public static CardStatusItem confused(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.迷惑, 0, cause);
    }

    public static CardStatusItem softened(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.弱化, 0, cause);
    }

    public static CardStatusItem summoned(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.召唤, 0, cause);
    }

    public static CardStatusItem faint(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.晕眩, 0, cause);
    }

    public static CardStatusItem unbending(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.不屈, 0, cause);
    }

    public static CardStatusItem deathMark(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.死印, 0, cause);
    }

    public static CardStatusItem magicMark(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.魔印, 0, cause);
    }
    
    public static CardStatusItem blind(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.致盲, 0, cause);
    }

    public static CardStatusItem raceChange(SkillUseInfo cause) {
        CardStatusType type = null;
        switch (cause.getType().getImpact(0)) {
            case 0: type = CardStatusType.王国; break;
            case 1: type = CardStatusType.森林; break;
            case 2: type = CardStatusType.蛮荒; break;
            case 3: type = CardStatusType.地狱; break;
            case 4: type = CardStatusType.魔族; break;
        }
        return new CardStatusItem(type, 0, cause);
    }
    
    public static CardStatusItem slience(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.沉默, 0, cause);
    }

    public static CardStatusItem sheep(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.变羊, 0, cause);
    }

    public static CardStatusItem deathDamnation(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.死咒, 0, cause);
    }

    public static CardStatusItem lunarEclipse(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.蚀月, 0, cause);
    }

    public static CardStatusItem snakeShadow(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.蛇影, 0, cause);
    }

    public static CardStatusItem petrifaction(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.石化, 0, cause);
    }

    public static CardStatusItem doom(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.厄运, 0, cause);
    }



    public static CardStatusItem deathSacrifice(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.献祭, 0, cause);
    }

    public static CardStatusItem humanRefining(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.炼成, 0, cause);
    }

    public static CardStatusItem sacrifice(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.祭奠, 0, cause);
    }

    public static CardStatusItem soulWound(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.魂殇, 0, cause);
    }

    public static CardStatusItem asthenia(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.虚化, 0, cause);
    }

    public static CardStatusItem soulLink(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.链接, 0, cause);
    }

    public static CardStatusItem yellowSky(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.黄天, 0, cause);
    }

    public static CardStatusItem grudge(int effect,SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.咒怨, effect, cause);
    }

    public static CardStatusItem grudgeAt(int effect,SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.咒恨, effect, cause);
    }

    public static CardStatusItem grudgeHp(int effect,SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.咒皿, effect, cause);
    }

    public static CardStatusItem ancientSummon(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.海啸, 0, cause);
    }

    public static CardStatusItem puppetSummon(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.傀儡, 0, cause);
    }

    public static CardStatusItem ancient(int effect,SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.远古, effect, cause);
    }

    public static CardStatusItem spread(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.扩散, 0, cause);
    }

    public static CardStatusItem rapture(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.离魂, 0, cause);
    }

    public static CardStatusItem paradiseLost(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.失乐, 0, cause);
    }

    public static CardStatusItem offspring(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.子嗣, 0, cause);
    }

    public static CardStatusItem mouseGuard(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.庚子, 0, cause);
    }

    public static CardStatusItem obsession(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.执念, 0, cause);
    }

    public static CardStatusItem Trumpet(SkillUseInfo cause) {
        return new CardStatusItem(CardStatusType.深渊, 0, cause);
    }

    public String getShortDesc() {
        StringBuffer sb = new StringBuffer();
        sb.append(getType().name());
        if (getType().isQuantitive()) {
            sb.append("(");
            sb.append(getEffect());
            sb.append(")");
        }
        sb.append(":");
        sb.append(cause.getSkill().getType().name());
        sb.append(cause.getSkill().getLevel());
        sb.append(":");
        sb.append(cause.getOwner().getShortDesc());
        return sb.toString();
    }

    public int getEffectNumber() {
        return effectNumber;
    }

    public void setEffectNumber(int effectNumber) {
        this.effectNumber = effectNumber;
    }
}