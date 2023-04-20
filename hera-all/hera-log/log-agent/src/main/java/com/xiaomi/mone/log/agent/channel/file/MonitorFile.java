package com.xiaomi.mone.log.agent.channel.file;

import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/4 16:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorFile {
    /**
     * 真实的文件路径地址
     */
    private String realFilePath;
    /**
     * 监听文件变化的表达式，eg:/home/work/log/server.log.*
     */
    private String monitorFileExpress;
    /**
     * 根据表达式生成正则解释器用于后边匹配
     */
    private Pattern filePattern;
    /**
     * 单个文件采集完就结束
     */
    private boolean collectOnce;

    /**
     * 日志类型，由于opentelemetry日志特殊，监听时需要特殊处理
     */
    private LogTypeEnum logTypeEnum;

    public MonitorFile(String realFilePath, String monitorFileExpress, LogTypeEnum logTypeEnum, boolean collectOnce) {
        this.realFilePath = realFilePath;
        this.monitorFileExpress = monitorFileExpress;
        this.filePattern = Pattern.compile(monitorFileExpress);
        this.logTypeEnum = logTypeEnum;
        this.collectOnce = collectOnce;
    }

    public static MonitorFile of(String realFilePath, String monitorFileExpress, LogTypeEnum logTypeEnum, boolean collectOnce) {
        return new MonitorFile(realFilePath, monitorFileExpress, logTypeEnum, collectOnce);
    }

}
