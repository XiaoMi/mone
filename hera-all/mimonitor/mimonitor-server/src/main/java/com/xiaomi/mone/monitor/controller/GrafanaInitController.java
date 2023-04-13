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

    @NacosValue(value = "${prometheus.url}", autoRefreshed = true)
    private String prometheusUrl;

    @PostConstruct
    public void init() {
        try {
           log.info("begin createDefaultGrafanaResource");
            heraDashboardService.createDefaultScrapeJob();
            heraDashboardService.createDefaultDashboardTemplate();
            DashboardDTO dataSourceDTO = new DashboardDTO();
            if (StringUtils.isBlank(dataSourceDTO.getPrometheusDatasource())) {
                dataSourceDTO.setPrometheusDatasource(prometheusUrl);
            }
            if (StringUtils.isBlank(dataSourceDTO.getUsername())) {
                dataSourceDTO.setUsername(DashboardConstant.GRAFANA_USER_NAME);
            }
            if (StringUtils.isBlank(dataSourceDTO.getPassword())) {
                dataSourceDTO.setPassword(DashboardConstant.GRAFANA_PASSWORD);
            }
            if (StringUtils.isBlank(dataSourceDTO.getDashboardFolderName())) {
                dataSourceDTO.setDashboardFolderName(DashboardConstant.DEFAULT_FOLDER_NAME);
            }
            Result dashboard = heraDashboardService.createGrafanaResources(dataSourceDTO);
        } catch (Exception e) {
            log.error("GrafanaInitController init error:", e);
            throw new RuntimeException("GrafanaInitController init error");
        }
    }

    @PostMapping(path = "/api/grafanaTemplate/apply")
    public Result applyGrafanaTemplate() {
        //apply 模板
       //heraDashboardService.createDefaultScrapeJob();
       heraDashboardService.createDefaultDashboardTemplate();
        return Result.success("");
    }


    //1、申请grafana mione api key
    //2、通过api key创建hera目录,及prometheus数据源
    //3、根据api key及模板文件，生成容器、物理机、接口大盘等各种图表
    //4、将上述生成的信息，通过更改nacos配置来推送给mimonitor
    @GetMapping(path = "/api/grafanaResources/create")
    public Result createDashboard() {
        DashboardDTO dataSourceDTO = new DashboardDTO();
        if (StringUtils.isBlank(dataSourceDTO.getPrometheusDatasource())) {
            dataSourceDTO.setPrometheusDatasource(prometheusUrl);
        }
        if (StringUtils.isBlank(dataSourceDTO.getUsername())) {
            dataSourceDTO.setUsername(DashboardConstant.GRAFANA_USER_NAME);
        }
        if (StringUtils.isBlank(dataSourceDTO.getPassword())) {
            dataSourceDTO.setPassword(DashboardConstant.GRAFANA_PASSWORD);
        }
        if (StringUtils.isBlank(dataSourceDTO.getDashboardFolderName())) {
            dataSourceDTO.setDashboardFolderName(DashboardConstant.DEFAULT_FOLDER_NAME);
        }
        Result dashboard = heraDashboardService.createGrafanaResources(dataSourceDTO);
        return Result.success("ok");
    }
}
