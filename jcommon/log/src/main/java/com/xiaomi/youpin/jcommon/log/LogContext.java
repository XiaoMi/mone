package com.xiaomi.youpin.jcommon.log;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
@Builder
public class LogContext {

    private String tag;

    private String code;

    private String param;

    private String result;

    private String traceId;

    private long costTime;

    private String extra;

    private long timestamp;

    private String level;

    private String appName;

    private String errorSource;

    private String methodName;

}
