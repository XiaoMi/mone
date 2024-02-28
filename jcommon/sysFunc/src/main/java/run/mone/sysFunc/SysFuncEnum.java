package run.mone.sysFunc;

import static run.mone.sysFunc.SysFuncConst.FUNC_NAME_SUBSTRING;
import static run.mone.sysFunc.SysFuncConst.FUNC_NAME_UUID;

public enum SysFuncEnum {

    //报警级别
    SUBSTRING(FUNC_NAME_SUBSTRING, "common", "截取字符串", "返回从指定开始索引到结束索引之间的子字符串，但不包括结束索引位置的字符"),
    UUID(FUNC_NAME_UUID, "common", "唯一标识符", "标准化的唯一标识符");

    public String cname;
    public String name;
    public String type;
    public String desc;

    SysFuncEnum(String name, String type, String cname, String desc) {
        this.name = name;
        this.type = type;
        this.cname = cname;
        this.desc = desc;
    }
}
