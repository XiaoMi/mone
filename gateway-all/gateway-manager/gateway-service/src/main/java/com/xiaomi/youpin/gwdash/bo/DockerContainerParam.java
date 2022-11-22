package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;

/**
 * @author tsingfu
 */
@Data
public class DockerContainerParam {
    private String ip;
    private long envId;

    /**
     * 请求来源 mone、miline
     */
    private String reqSource;
    private String containerId;
}
