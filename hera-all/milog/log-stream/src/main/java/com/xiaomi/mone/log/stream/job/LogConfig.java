package com.xiaomi.mone.log.stream.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/22 15:47
 */
@Data
public class LogConfig {

    private Long logTailId;
    private Long logStoreId;
    private Long logSpaceId;
}
