package com.xiaomi.mone.log.manager.model.bo.alert;

import lombok.Data;

import java.util.List;

@Data
public class AlertQueryResult {

    private List<AlertBo> alerts;
    private int count;

}
