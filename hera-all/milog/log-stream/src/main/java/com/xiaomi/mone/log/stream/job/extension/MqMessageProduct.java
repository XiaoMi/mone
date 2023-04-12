package com.xiaomi.mone.log.stream.job.extension;

import com.xiaomi.mone.log.stream.job.compensate.MqMessageDTO;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/2/8 16:33
 */
public interface MqMessageProduct {

    void product(String ak, String sk, String serviceUrl, String topic, List<String> msg);

    void product(MqMessageDTO msg);
}
