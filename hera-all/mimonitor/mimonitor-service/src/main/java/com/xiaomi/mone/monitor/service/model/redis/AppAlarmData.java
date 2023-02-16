/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xiaomi.mone.monitor.service.model.redis;

import lombok.Builder;
import lombok.Data;

/**
 * app告警数据
 * @author zhanggaofeng1
 */
@Data
@Builder
public class AppAlarmData {

    private Long id;
    private String name;
    private Long iamTreeId;
    private Integer httpExceptionNum;
    private Integer httpClientExceptionNum;
    private Integer dubboExceptionNum;
    private Integer dubboPExceptionNum;
    private Integer sqlExceptionNum;
    private Integer redisExceptionNum;
    private Integer dubboCSlowQueryNum;
    private Integer dubboProviderSlowQueryNum;

    private Integer grpcServerErrorNum;
    private Integer grpcClientErrorNum;
    private Integer apusServerErrorNum;
    private Integer apusClientErrorNum;
    private Integer thriftServerErrorNum;
    private Integer thriftClientErrorNum;

    private Integer grpcClientSlowQueryNum;
    private Integer grpcServerSlowQueryNum;
    private Integer thriftClientSlowQueryNum;
    private Integer thriftServerSlowQueryNum;
    private Integer apusClientSlowQueryNum;
    private Integer apusServerSlowQueryNum;

    private Integer sqlSlowQueryNum;
    private Integer alertTotal;
    private Integer exceptionTotal;
    private Integer slowTotal;
    private Integer logExceptionNum;

}
