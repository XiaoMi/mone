package com.xiaomi.mone.monitor.service;

public interface SendSmsService {
    void sendSms(String address, String templateId, String paramJsonStr);
    void batchSendSms(String addresses, String templateId, String paramJson);
}
