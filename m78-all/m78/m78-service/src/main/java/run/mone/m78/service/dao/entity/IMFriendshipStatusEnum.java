package run.mone.m78.service.dao.entity;

/**
 * @author wmin
 * @date 2024/5/9
 */
public enum IMFriendshipStatusEnum {
    PENDING(0, "pending"),
    ACCEPTED(1, "accepted"),
    REJECTED(2, "rejected"),
    BLOCKED(3, "blocked"),
    DELETED(4, "deleted");

    private final int code;
    private final String desc;

    IMFriendshipStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }
}
