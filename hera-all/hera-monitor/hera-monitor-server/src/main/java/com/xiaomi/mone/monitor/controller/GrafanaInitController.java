package com.xiaomi.mone.monitor.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.monitor.DashboardConstant;
import com.xiaomi.mone.monitor.bo.DashboardDTO;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.HeraDashboardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author zhangxiaowei6
 * @date 2023-02-22
 */
@Slf4j
@RestController
public class GrafanaInitController {

    @Autowired
    HeraDashboardService heraDashboardService;

    @PostMapping(path = "/api/grafanaTemplate/apply")
    public Result applyGrafanaTemplate() {
        return Result.success("");
    }


    @GetMapping(path = "/api/grafanaResources/create")
    public Result createDashboard() {
        return Result.success("ok");
    }
}
