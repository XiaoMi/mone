package com.xiaomi.mone.log.manager.model.vo;

import com.xiaomi.mone.log.model.LogtailConfig;
import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/1/5 10:47
 */
@Data
public class LogTailSendLokiVo {
    private String spaceName;
    private Long spaceId;
    private String storeName;
    private Long storeId;
    private String tailName;
    private Long tailId;
    private String keyList;

    private LogtailConfig config;

}
