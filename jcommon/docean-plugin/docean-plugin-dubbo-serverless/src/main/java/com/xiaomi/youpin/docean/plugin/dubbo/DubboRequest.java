package com.xiaomi.youpin.docean.plugin.dubbo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 1/16/21
 */
@Data
public class DubboRequest implements Serializable {

    private String serviceName;
    private String methodName;
    private String group = "";
    private String version = "";
    private int timeout = 1000;
    private String[] parameterTypes;
    private Object[] args;

    /**
     * 地址(如果有,就是定向发送)
     */
    private String addr;

    private String ip;

}
