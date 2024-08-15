package com.xiaomi.youpin.tesla.ip.bo.robot;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/12/11 16:54
 */
@Builder
@Data
public class EventRes {

    private String messageId;

    private String type;

    private String data;

    private boolean show;

    private Map<String,String> meta;


}
