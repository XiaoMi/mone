package com.xiaomi.mone.monitor.service;

import java.util.List;

public interface MiFeiShuService {
    List<String> getBatchPhoneByUserNames(List<String> userNames);
    boolean batchSendMsg(List<String> emails, String msg);
}
