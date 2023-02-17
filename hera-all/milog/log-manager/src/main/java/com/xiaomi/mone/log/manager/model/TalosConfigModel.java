package com.xiaomi.mone.log.manager.model;

import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/8 20:19
 */
@Data
public class TalosConfigModel {

    private String talosAccessKey;
    private String talosAccessSecret;
    private String talosAccessClusterInfo;
    private String talosAccessTopic;

}
