package run.mone.z.desensitization.api.common;

public enum SensitiveWordTypeEnum {
    All(0, "不限制"),
    FiledKey(1, "属性名"),
    FiledValue(2, "属性值");
    public final int code;
    public final String description;

    SensitiveWordTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static boolean check(Integer type){
        if(type==null){
            return false;
        }
        SensitiveWordTypeEnum[] values = SensitiveWordTypeEnum.values();
        for (SensitiveWordTypeEnum envEnum : values) {
            if(type==envEnum.getCode()){
                return true;
            }
        }
        return false;
    }
}
