package com.xiaomi.mone.monitor.service.model.alarm.duty;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2023/6/8 1:59 下午
 */
@Data
public class UserInfo implements Serializable {

    private String user;
    private Long start_time;
    private Long end_time;
}
