package com.xiaomi.youpin.tesla.ip.bo.robot;

import lombok.Data;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/12/11 10:48
 */
@Data
public class MessageReq {

    private String project;

    private String id;

    private String role;

    private String message;

    private long time;

    private String eventType;

    private Map<String, String> meta;

    private Map<String, String> mapData;


}
