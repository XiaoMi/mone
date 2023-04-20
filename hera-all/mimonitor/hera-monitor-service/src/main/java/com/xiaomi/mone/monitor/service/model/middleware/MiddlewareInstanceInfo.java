package com.xiaomi.mone.monitor.service.model.middleware;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/10/20 9:40 上午
 */
@Data
@Slf4j
public class MiddlewareInstanceInfo implements Serializable {

    String projectName;
    String userName;
    String password;
    String domainPort;//ip:port
    String dataBaseName;
    String type;//db\redis
    String timeStamp;

    String url;


}
