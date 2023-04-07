package com.xiaomi.mone.log.stream.job.extension;

import com.xiaomi.mone.log.stream.compensate.MqMessageDTO;

import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/22 15:35
 */
public interface MessageSender {

    Boolean send(Map<String, Object> data) throws Exception;


    boolean compensateSend(MqMessageDTO compensateMsg);

}
