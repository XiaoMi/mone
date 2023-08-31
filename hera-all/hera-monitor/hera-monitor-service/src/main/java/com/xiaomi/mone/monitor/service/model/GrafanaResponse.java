package com.xiaomi.mone.monitor.service.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/7/10 12:48 下午
 */
@Data
public class GrafanaResponse implements Serializable {
    Integer id;
    Integer version;
    String slug;
    String status;
    String uid;
    String url;
    String mimonitor_version;

//    {"id":4,"slug":"ye-wu-jian-kong-zzytest","status":"success","uid":"mione","url":"/d/mione/ye-wu-jian-kong-zzytest","version":5}
}
