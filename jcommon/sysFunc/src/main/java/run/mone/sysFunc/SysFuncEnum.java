package run.mone.sysFunc;

import static run.mone.sysFunc.SysFuncConst.*;

public enum SysFuncEnum {

    SUBSTRING(FUNC_NAME_SUBSTRING, "common", "截取字符串", "${java.substring(ceshi, 1, 3)}",
            "返回从指定开始索引到结束索引之间的子字符串，但不包括结束索引位置的字符"),
    UUID(FUNC_NAME_UUID, "common", "唯一标识符", "${java.uuid()}",
            "标准化的唯一标识符"),
    RANDOM_NUMBER(FUNC_NAME_RANDOM_NUMBER, "common", "随机数", "${java.randomNumber(2, 11)}",
            "生成指定范围的整型数字"),
    TIME_STAMP(FUNC_NAME_TIME_STAMP, "common", "时间戳（毫秒）", "${java.timeStamp()}",
            "获取的是当前时间戳，单位毫秒"),
    RANDOM_STRING(FUNC_NAME_RANDOM_STRING, "common", "随机字符串", "${java.randomString(10)}",
            "[A-Za-z0-9]范围的中指定长度的随机字符串"),
    PHONE_NUM(FUNC_NAME_PHONE_NUM, "common", "随机手机号码", "${java.phoneNum()}",
            "中国区地区的手机号码"),
    LOWER_CASE(FUNC_NAME_LOWER_CASE, "common", "字符转小写", "${java.lowerCase(TxtContent)}",
            "字符转小写"),
    UPPER_CASE(FUNC_NAME_UPPER_CASE, "common", "字符转大写", "${java.upperCase(TxtContent)}",
            "字符转大写"),
    RANDOM_DOUBLE(FUNC_NAME_RANDOM_DOUBLE, "common", "随机小数", "${java.randomDouble(10,50,3)}",
            "生成随机小数，三个参数分别是最小值，最大值和小数长度"),
    DATE_TO_TIME_STAMP(FUNC_NAME_DATE_TO_TIME_STAMP, "common", "日期转换时间戳", "${java.dateToTimeStamp(2024-03-29 10:30:12,yyyy-MM-dd HH:mm:ss)}",
            "两个参数分别是时间字符串和时间格式，两个保持一致，转换成毫秒单位的时间戳"),
    TIME_STAMP_TO_DATE(FUNC_NAME_TIME_STAMP_TO_DATE, "common", "时间戳转日期字符", "${java.timeStampToDate(1711680624906,yyyy-MM-dd HH:mm:ss)}",
            "两个参数分别是毫秒时间戳和要输出的日期字符串的格式"),
    SELECT(FUNC_NAME_SELECT, "common", "随机选择元素", "${java.select(true,false)}",
            "参数是多个使用逗号分隔的元素，随机选择其中的一个进行返回"),
    ;

    public final String cname;
    public final String name;
    public final String type;
    public final String desc;
    public final String example;

    SysFuncEnum(String name, String type, String cname, String example, String desc) {
        this.name = name;
        this.type = type;
        this.cname = cname;
        this.desc = desc;
        this.example = example;
    }
}
