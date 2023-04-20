package com.xiaomi.mone.log.manager.service.nacos;

import com.google.gson.Gson;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/16 15:25
 */
public interface DynamicConfigProvider<T> {

    Gson gson = new Gson();
    /**
     * 获取配置
     * @param appName
     * @return
     */
    T getConfig(String appName);
}
