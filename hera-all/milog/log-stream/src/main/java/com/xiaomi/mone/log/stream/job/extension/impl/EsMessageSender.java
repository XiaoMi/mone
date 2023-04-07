package com.xiaomi.mone.log.stream.job.extension.impl;

import com.xiaomi.mone.es.EsProcessor;
import com.xiaomi.mone.log.stream.job.extension.MessageSender;
import com.xiaomi.mone.log.utils.DateUtils;

import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/22 15:36
 */
public class EsMessageSender implements MessageSender {

    private final EsProcessor esProcessor;

    private final String index;

    public EsMessageSender(EsProcessor esProcessor, String index) {
        this.esProcessor = esProcessor;
        this.index = index;
    }


    @Override
    public Boolean send(Map<String, Object> data) throws Exception {
        String time = DateUtils.getTime();
        String esIndex = index + "-" + time;
        esProcessor.bulkInsert(esIndex, data);
        return true;
    }
}
