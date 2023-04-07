package com.xiaomi.mone.log.stream.job.extension.impl;

import com.xiaomi.mone.es.EsProcessor;
import com.xiaomi.mone.log.stream.compensate.MqMessageDTO;
import com.xiaomi.mone.log.stream.compensate.MqMessageProduct;
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

    private EsProcessor esProcessor;

    private final String index;

    /**
     * Compensating message MQ queue.
     */
    private MqMessageProduct compensateMsgProduct;

    public EsMessageSender(String index, MqMessageProduct compensateMsgProduct) {
        this.index = index;
        this.compensateMsgProduct = compensateMsgProduct;
    }

    public EsProcessor getEsProcessor() {
        return esProcessor;
    }

    public void setEsProcessor(EsProcessor esProcessor) {
        this.esProcessor = esProcessor;
    }

    @Override
    public Boolean send(Map<String, Object> data) throws Exception {
        String time = DateUtils.getTime();
        String esIndex = index + "-" + time;
        esProcessor.bulkInsert(esIndex, data);
        return true;
    }

    @Override
    public boolean compensateSend(MqMessageDTO compensateMsg) {
        if (null != compensateMsgProduct) {
            compensateMsgProduct.product(compensateMsg);
            return true;
        }

        return false;
    }
}
