package com.xiaomi.mone.log.manager.service.nacos;

import com.google.gson.Gson;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/15 15:16
 */
public interface DynamicConfigPublisher<T> {
    Gson gson = new Gson();

    /**
     * 数据持久化到nacos
     *
     * @param app
     * @param configs
     */
    void publish(String app, T configs);

    /**
     * 移除配置
     * @param app
     */
    void remove(String dataId);
}
