package com.xiaomi.mone.monitor.service.model.prometheus;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/9/3 9:51 上午
 */
@Data
public class MetricDetail implements Serializable {

    /**
     * 域：jaegerquery/china_tesla/youpin-tesla"
     */
    private String domain;

    /**
     * 应用信息
     * projectId_projectName
     */
    private String serviceName;

    /**
     *
     * serverIp
     */
    private String host;

    /**
     * 指标类型
     * http/dubbo/mysql
     */
    private String type;

    /**
     * 指标子类型：
     * error - 异常数据
     * timeout - 慢查询数据
     */
    private String errorType;

    /**
     * 异常码
     */
    private String errorCode;

    /**
     * 耗时
     */
    private String duration;

    /**
     * type-value
     * http-path;
     * dubbo-serviceName/methodName;
     * mysql-sql
     */
    private String url;

    /**
     * mysql
     */
    private String dataSource;

    private String traceId;

    private String timestamp;

    private Long createTime;

}
