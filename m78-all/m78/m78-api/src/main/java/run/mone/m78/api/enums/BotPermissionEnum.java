package run.mone.m78.api.enums;

import lombok.Getter;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-05 18:12
 */
@Getter
public enum BotPermissionEnum {

    PRIVATE(0,"私有"),
    PUBLIC(1,"公开");

    private Integer code;
    private String desc;

    BotPermissionEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }



}
