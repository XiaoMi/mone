package run.mone.ai.minimax.bo;

public enum VoiceIdEnum {

    /**
     * 青涩青年音色：male-qn-qingse
     */
    male_qn_qingse("male-qn-qingse","青涩青年音色"),

    /**
     * 精英青年音色：male-qn-jingying
     */
    male_qn_jingying("male-qn-jingying","精英青年音色"),

    /**
     * 霸道青年音色：male-qn-badao
     */
    male_qn_badao("male-qn-badao","霸道青年音色"),

    /**
     * 青年大学生音色：male-qn-daxuesheng
     */
    male_qn_daxuesheng("male-qn-daxuesheng","青年大学生音色"),

    /**
     * 少女音色：female-shaonv
     */
    female_shaonv("female-shaonv","少女音色"),

    /**
     * 御姐音色：female-yujie
     */
    female_yujie("female-yujie", "御姐音色"),

    /**
     * 成熟女性音色：female-chengshu
     */
    female_chengshu("female-chengshu", "成熟女性音色"),

    /**
     * 甜美女性音色：female-tianmei
     */
    female_tianmei("female-tianmei", "甜美女性音色"),

    /**
     * 男性主持人：presenter_male
     */
    presenter_male("presenter_male","男性主持人"),

    /**
     * 女性主持人：presenter_female
     */
    presenter_female("presenter_female","女性主持人"),

    /**
     * 男性有声书1：audiobook_male_1
     */
    audiobook_male_1("audiobook_male_1","男性有声书1"),

    /**
     * 男性有声书2：audiobook_male_2
     */
    audiobook_male_2("audiobook_male_2","男性有声书2"),

    /**
     * 女性有声书1：audiobook_female_1
     */
    audiobook_female_1("audiobook_female_1","女性有声书1"),

    /**
     * 女性有声书2：audiobook_female_2
     */
    audiobook_female_2("audiobook_female_2","女性有声书2"),

    /**
     * 青涩青年音色-beta：male-qn-qingse-jingpin
     */
    male_qn_qingse_jingpin("male-qn-qingse-jingpin","青涩青年音色-beta"),

    /**
     * 精英青年音色-beta：male-qn-jingying-jingpin
     */
    male_qn_jingying_jingpin("male-qn-jingying-jingpin","精英青年音色-beta"),

    /**
     * 霸道青年音色-beta：male-qn-badao-jingpin
     */
    male_qn_badao_jingpin("male-qn-badao-jingpin","霸道青年音色-beta"),

    /**
     * 青年大学生音色-beta：male-qn-daxuesheng-jingpin
     */
    male_qn_daxuesheng_jingpin("male-qn-daxuesheng-jingpin","青年大学生音色-beta"),

    /**
     * 少女音色-beta：female-shaonv-jingpin
     */
    female_shaonv_jingpin("female-shaonv-jingpin","少女音色-beta"),

    /**
     * 御姐音色-beta：female-yujie-jingpin
     */
    female_yujie_jingpin("female-yujie-jingpin","御姐音色-beta"),

    /**
     * 成熟女性音色-beta：female-chengshu-jingpin
     */
    female_chengshu_jingpin("female-chengshu-jingpin","成熟女性音色-beta"),

    /**
     * 甜美女性音色-beta：female-tianmei-jingpin
     */
    female_tianmei_jingpin("female-tianmei-jingpin","甜美女性音色-beta");

    public String voiceId;

    public String description;

    VoiceIdEnum(String id, String description) {
        this.voiceId = id;
        this.description = description;
    }
}
