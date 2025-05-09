package run.mone.agentx.dto.enums;

/**
 * 收藏类型枚举
 */
public enum FavoriteType {
    AGENT(1, "Agent");

    private final Integer code;
    private final String desc;

    FavoriteType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static FavoriteType fromCode(Integer code) {
        for (FavoriteType type : FavoriteType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid favorite type code: " + code);
    }
} 