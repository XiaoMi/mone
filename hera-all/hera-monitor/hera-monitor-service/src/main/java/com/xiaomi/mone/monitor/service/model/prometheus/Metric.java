package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/8/16 11:42 上午
 */
@Data
public class Metric implements Serializable {

    private String application;
    private String instance;
    private String ip;
    private String job;
    private String replica;
    private String serverIp;

    //dubbo label
    private String methodName;
    private String serviceName;

    //sql
    private String sqlMethod;
    private String sql;
    private String dataSource;

    //redis
    private String host;
    private String port;
    private String dbindex;
    private String method;

    //env
    private String serverEnv;
    private String serverZone;

    //container info
    private String container_label_PROJECT_ID;
    private String name;//
    private String container;//
    private String pod;//
    private String namespace;//

    private String lastCreateTime;

    private double value;

    private String timestamp;

    private String traceId;

    private String service; //应用的服务维度

    //下游服务信息
    private String clientProjectName;
    private String clientEnv;
    private String clientIp;


}
