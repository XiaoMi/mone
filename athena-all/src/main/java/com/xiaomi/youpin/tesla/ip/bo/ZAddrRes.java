package com.xiaomi.youpin.tesla.ip.bo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author caobaoyu
 * @description:
 * @date 2023-06-07 14:40
 */
@Data
public class ZAddrRes {

    private String addr;

    private String version;

    private String athenaConfig;

    /**
     * athena dashboard的server地址(前端那个服务)
     */
    private String athenaDashServer;

    /**
     * 支持哪些模型
     */
    private List<ModelRes> models;

    private List<ModelRes> modelsV2;

    private String userName;
}
