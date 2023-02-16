package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/9/14 9:07 下午
 */
@Data
public class AlarmAlertTeamData implements Serializable {
    private Integer id;
    private String type;
    private String name;
}
