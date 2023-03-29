package com.xiaomi.mone.monitor.controller;

import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.prometheus.CreateTemplateParam;
import com.xiaomi.mone.monitor.service.prometheus.GrafanaTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangxiaowei6
 * @date 2022/3/29
 */
@Slf4j
@RestController
public class GrafanaTemplateController {

    @Autowired
    GrafanaTemplateService grafanaTemplateService;

    @Autowired
    ScrapeJobController scrapeJobController;

    @PostMapping("/mimonitor/createTemplate")
    public Result createTemplate(HttpServletRequest request, @RequestBody CreateTemplateParam param) {
        if (!param.check()) {
            log.info("createTemplate param error :{}", param);
            return Result.fail(ErrorCode.invalidParamError);
        }
        String user = scrapeJobController.checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        return  grafanaTemplateService.createGrafanaTemplate(param);
    }

    @PostMapping("/mimonitor/deleteTemplate")
    public Result deleteTemplate(HttpServletRequest request, Integer id) {
        String user = scrapeJobController.checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        return grafanaTemplateService.deleteGrafanaTemplate(id);
    }

    @GetMapping("/mimonitor/getTemplate")
    public Result getTemplate(HttpServletRequest request,Integer id){
        String user = scrapeJobController.checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        return grafanaTemplateService.getGrafanaTemplate(id);
    }

    @PostMapping("/mimonitor/updateTemplate")
    public Result updateTemplate(HttpServletRequest request, @RequestBody CreateTemplateParam param) {
        if (!param.check()) {
            log.info("updateTemplate param error :{}", param);
            return Result.fail(ErrorCode.invalidParamError);
        }
        String user = scrapeJobController.checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        return  grafanaTemplateService.updateGrafanaTemplate(param);
    }

    @GetMapping("/mimonitor/listTemplate")
    public Result listTemplate(HttpServletRequest request, Integer pageSize, Integer page) {
        String user = scrapeJobController.checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        //如果不传默认为看第一页前十条
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (page == null || page == 0) {
            page = 1;
        }
        return grafanaTemplateService.listGrafanaTemplate(pageSize, page);
    }
}
