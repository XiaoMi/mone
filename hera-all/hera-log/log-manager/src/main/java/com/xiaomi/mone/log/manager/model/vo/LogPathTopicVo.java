package com.xiaomi.mone.log.manager.model.vo;

import com.xiaomi.mone.log.api.model.meta.MQConfig;
import lombok.Builder;
import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/8/17 15:42
 */
@Data
@Builder
public class LogPathTopicVo {
    /**
     * 日志路径
     */
    private String logPath;
    /**
     * tailId
     */
    private Long tailId;

    private String source;
    /**
     * 解析脚本
     */
    private String parseScript;
    /**
     * 日志格式
     */
    private String valueList;
    /**
     * mq相关配置
     */
    private MQConfig mqConfig;
    /**
     * 服务别名
     */
    private String serveAlias;

}
