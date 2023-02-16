package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DashboardDTO implements Serializable {

    private String dashboardName;

    private String dashboardId;

    private List<DashboardGraphDTO> graphList;

}
