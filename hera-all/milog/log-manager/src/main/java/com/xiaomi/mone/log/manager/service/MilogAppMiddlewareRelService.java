package com.xiaomi.mone.log.manager.service;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/23 11:14
 */
public interface MilogAppMiddlewareRelService {
    void bindingTailConfigRel(Long tailId, Long milogAppId, Long configId, String topicName);

    void defaultBindingAppTailConfigRel(Long id, Long milogAppId, Long middleWareId, String topicName, Integer batchSendSize);

}
