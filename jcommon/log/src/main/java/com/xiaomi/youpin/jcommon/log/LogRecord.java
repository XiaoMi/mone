package com.xiaomi.youpin.jcommon.log;

import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
public class LogRecord {

    private String line;
    private String message;
    private String tag;
    private String threadName;
    private String level;
    private String traceId;
    private String time;
    private String className;
    private String methodName;
    private String pid;
    private String ip;
    private String appName;
    private String group;
    private long timestamp;
    private String errorInfo;
    private String params;
    private String result;
    private String code;
    private String owner;
    private long costTime;
    private String errorSource;
    /**
     * 扩展字段
     */
    private String extra;

}
