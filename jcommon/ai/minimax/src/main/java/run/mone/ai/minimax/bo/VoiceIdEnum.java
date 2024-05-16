package run.mone.ai.minimax.bo;

public enum VoiceIdEnum {

    /**
     * 青涩青年音色：male-qn-qingse
     */
    male_qn_qingse("male-qn-qingse"),

    /**
     * 精英青年音色：male-qn-jingying
     */
    male_qn_jingying("male-qn-jingying"),

    /**
     * 霸道青年音色：male-qn-badao
     */
    male_qn_badao("male-qn-badao"),

    /**
     * 青年大学生音色：male-qn-daxuesheng
     */
    male_qn_daxuesheng("male-qn-daxuesheng"),

    /**
     * 少女音色：female-shaonv
     */
    female_shaonv("female-shaonv"),

    /**
     * 御姐音色：female-yujie
     */
    female_yujie("female-yujie"),

    /**
     * 成熟女性音色：female-chengshu
     */
    female_chengshu("female-chengshu"),

    /**
     * 甜美女性音色：female-tianmei
     */
    female_tianmei("female-tianmei"),

    /**
     * 男性主持人：presenter_male
     */
    presenter_male("presenter_male"),

    /**
     * 女性主持人：presenter_female
     */
    presenter_female("presenter_female"),

    /**
     * 男性有声书1：audiobook_male_1
     */
    audiobook_male_1("audiobook_male_1"),

    /**
     * 男性有声书2：audiobook_male_2
     */
    audiobook_male_2("audiobook_male_2"),

    /**
     * 女性有声书1：audiobook_female_1
     */
    audiobook_female_1("audiobook_female_1"),

    /**
     * 女性有声书2：audiobook_female_2
     */
    audiobook_female_2("audiobook_female_2"),

    /**
     * 青涩青年音色-beta：male-qn-qingse-jingpin
     */
    male_qn_qingse_jingpin("male-qn-qingse-jingpin"),

    /**
     * 精英青年音色-beta：male-qn-jingying-jingpin
     */
    male_qn_jingying_jingpin("male-qn-jingying-jingpin"),

    /**
     * 霸道青年音色-beta：male-qn-badao-jingpin
     */
    male_qn_badao_jingpin("male-qn-badao-jingpin"),

    /**
     * 青年大学生音色-beta：male-qn-daxuesheng-jingpin
     */
    male_qn_daxuesheng_jingpin("male-qn-daxuesheng-jingpin"),

    /**
     * 少女音色-beta：female-shaonv-jingpin
     */
    female_shaonv_jingpin("female-shaonv-jingpin"),

    /**
     * 御姐音色-beta：female-yujie-jingpin
     */
    female_yujie_jingpin("female-yujie-jingpin"),

    /**
     * 成熟女性音色-beta：female-chengshu-jingpin
     */
    female_chengshu_jingpin("female-chengshu-jingpin"),

    /**
     * 甜美女性音色-beta：female-tianmei-jingpin
     */
    female_tianmei_jingpin("female-tianmei-jingpin");

    public String voiceId;

    VoiceIdEnum(String id) {
        this.voiceId = id;
    }
}
