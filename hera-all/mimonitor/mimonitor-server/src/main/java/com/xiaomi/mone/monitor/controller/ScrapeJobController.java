package com.xiaomi.mone.monitor.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.prometheus.JobService;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author zhangxiaowei6
 */

@RestController
@Slf4j
public class ScrapeJobController {

    @Autowired
    JobService jobService;

    private final Gson gson = new Gson();

    //接收jobJson去请求prometheus
    @NacosValue(value = "${grafana.backend.users}", autoRefreshed = true)
    private String grafanaBackendUsers;

    //接收jobJson去请求云平台prometheus
    @PostMapping("/mimonitor/createScrapeJob")
    public Result createScrapeJob(HttpServletRequest request, String jobDesc, @RequestBody String body) {
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        if (StringUtils.isNotEmpty(body)) {
            return jobService.createJob(null, user, body, jobDesc);
        }
        return Result.fail(ErrorCode.RequestBodyIsEmpty);

    }

    //查看prometheus创建的job
    @GetMapping("/mimonitor/searchScrapeJob")
    public Result searchScrapeJob(HttpServletRequest request, Integer id) {
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        if (id != null && id != 0) {
            return jobService.searchJob(null, user, id);
        }
        return Result.fail(ErrorCode.ScrapeIdIsEmpty);
    }

    //删除prometheus创建的job
    @PostMapping("/mimonitor/deleteScrapeJob")
    public Result deleteScrapeJob(HttpServletRequest request, @RequestBody String body) {
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);
        Integer primaryId = jsonObject.get("primaryId").getAsInt();
        if (primaryId != null && primaryId != 0) {
            return jobService.deleteJob(null, user, primaryId);
        }
        return Result.fail(ErrorCode.invalidParamError);
    }

    //更新prometheus创建的job
    @PostMapping("/mimonitor/updateScrapeJob")
    public Result updateScrapeJob(HttpServletRequest request, String jobDesc, Integer primaryId, @RequestBody String body) {
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        if (primaryId != null && primaryId != 0 && StringUtils.isNotEmpty(body)) {
            return jobService.updateJob(null, user, body, primaryId, jobDesc);
        }
        return Result.fail(ErrorCode.invalidParamError);
    }

    //查找prometheus创建的job列表
    @GetMapping("/mimonitor/searchScrapeJobList")
    public Result searchScrapeJobList(HttpServletRequest request, Integer pageSize, Integer page) {
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        //如果不传默认为看第一页前十条
        if (pageSize == 0) {
            pageSize = 10;
        }
        if (page == 0) {
            page = 1;
        }
        return jobService.searchJobList(null, user, pageSize, page);
    }

    //检测用户时候有权限操作
    public String checkUser(HttpServletRequest request) {
        AuthUserVo userInfo = UserUtil.getUser();
        if (userInfo == null) {
            return "";
        }
        String user = userInfo.genFullAccount();
        log.info("ScrapeJobController checkUser user:{}", user);
        if (Arrays.stream(grafanaBackendUsers.split(",")).collect(Collectors.toList()).contains(user)) {
            return user;
        } else {
            return "";
        }
    }

}