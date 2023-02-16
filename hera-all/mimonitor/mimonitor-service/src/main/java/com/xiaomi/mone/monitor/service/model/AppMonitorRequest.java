package com.xiaomi.mone.monitor.service.model;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/8/13 1:11 下午
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppMonitorRequest implements Serializable {

    String appName;
    Integer page;
    Integer pageSize;
    Integer viewType;//0 我的应用；1 我关注的应用；
    Integer area;//0
    Integer distinct;//1 true 0 false只有在不指定viewType的情况下生效，
    Integer platFormType;//平台类型
    List<ProjectInfo> projectList;
    private long duration = 1800L;//统计最近30分钟告警
    private boolean needPage;
    private Long startTimeCurrent;//当前页签数字统计开始时间
    private Long endTimeCurrent;//当前页签数字统计结束时间
    private Long startTime;//页签数字统计开始时间
    private Long endTime;//页签数字统计结束时间
    private String metricType;//页签数字统计指标类型
    private String methodName;//页签数字统计方法名

    public void qryInit() {
        if (page == null || page <= 0) {
            page = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        if (pageSize > 99) {
            pageSize = 99;
        }
    }


}
