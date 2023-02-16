package com.xiaomi.mone.log.manager.service;

/**
 * @author wtt
 * @version 1.0
 * @description 配置同步nacos的中间配置
 * @date 2021/7/19 16:10
 */
public interface MilogConfigNacosService {

    void publishStreamConfig(Long spaceId, Long tailId, Integer type, Integer projectType, String motorRoomEn);

    void publishNameSpaceConfig(String motorRoomEn, Long spaceId, Long storeId, Long tailId, Integer type, String changeType);

    void removeStreamConfig(Long id);
}
