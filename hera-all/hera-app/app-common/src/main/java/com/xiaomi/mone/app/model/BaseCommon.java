package com.xiaomi.mone.app.model;

import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/9 17:51
 */
@Data
public class BaseCommon {
    private Long ctime;

    private Long utime;

    private String creator;

    private String updater;
}
