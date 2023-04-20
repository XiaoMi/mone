package com.xiaomi.mone.log.manager.service.nacos;

import java.util.List;

public interface FetchStreamMachineService {
    /**
     * 获取stream机器的标识
     *
     * @return
     */
    List<String> streamMachineUnique();
}
