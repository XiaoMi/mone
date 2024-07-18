package com.xiaomi.youpin.tesla.ip.bo.robot;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/12/11 14:12
 */
@Data
@Builder
public class MessageRes implements Serializable {

    private String id;

    private String role;

    private String type;

    private String message;

}
