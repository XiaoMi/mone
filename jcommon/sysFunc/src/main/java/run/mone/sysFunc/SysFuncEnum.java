package run.mone.sysFunc;

import static run.mone.sysFunc.SysFuncConst.*;

public enum SysFuncEnum {

    //报警级别
    SUBSTRING(FUNC_NAME_SUBSTRING, "common", "截取字符串", "${java.substring(ceshi, 1, 3)}",
            "返回从指定开始索引到结束索引之间的子字符串，但不包括结束索引位置的字符"),
    UUID(FUNC_NAME_UUID, "common", "唯一标识符", "{java.uuid()}",
            "标准化的唯一标识符"),
    RANDOM_NUMBER(FUNC_NAME_RANDOM_NUMBER, "common", "随机数", "${java.randomNumber(2, 11)}",
            "生成指定范围的整型数字");

    public String cname;
    public String name;
    public String type;
    public String desc;
    public String example;

    SysFuncEnum(String name, String type, String cname, String example, String desc) {
        this.name = name;
        this.type = type;
        this.cname = cname;
        this.desc = desc;
        this.example = example;
    }
}
