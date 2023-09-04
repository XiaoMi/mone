package com.xiaomi.mone.monitor.dao.model;

import com.google.gson.Gson;
import lombok.Data;
import lombok.ToString;

/**
 * @author gaoxihui
 * @date 2022/4/19 5:01 下午
 */
@Data
@ToString
public class AlarmHealthQuery {

    private String appName;
    private String owner;
    private Integer projectId;
    private Integer appSource;

}
