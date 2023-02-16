package com.xiaomi.mone.monitor.service.scrapeJob.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.alertmanager.AlertManager;
import com.xiaomi.mone.monitor.service.alertmanager.client.Request;
import com.xiaomi.mone.monitor.service.alertmanager.client.model.HttpMethodName;
import com.xiaomi.mone.monitor.service.alertmanager.impl.MiCloudAlertManager;
import com.xiaomi.mone.monitor.service.scrapeJob.ScrapeJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value="miCloudScrapeJob")
public class MiCloudScrapeJob implements ScrapeJob {

    @Value("${alarm.domain}")
    private String alarmDomain;

    @NacosValue("${iam.ak:noconfig}")
    private String cloudAk;
    @NacosValue("${iam.sk:noconfig}")
    private String cloudSk;

    @Autowired
    MiCloudAlertManager miCloudAlertManager;

    public static final String alarm_job_option_uri = "/api/v1/scrape-config";

    @Override
    public Result addScrapeJob(JsonObject param, String identifyId, String user) {
        StringBuilder url = new StringBuilder(alarmDomain).append(alarm_job_option_uri);
        Request request = miCloudAlertManager.createRequest(HttpMethodName.POST, url.toString(), identifyId, user);
        request.setBody(String.valueOf(param));
        Result<JsonElement> jsonObjectResult = miCloudAlertManager.executeRequest(request);
        return Result.success(jsonObjectResult.getData());
    }

    @Override
    public Result editScrapeJob(Integer jobId, JsonObject param, String identifyId, String user) {
        StringBuilder url = new StringBuilder(alarmDomain).append(MiCloudAlertManager.alarm_job_option_uri).append("/").append(jobId);
        Request request = miCloudAlertManager.createRequest(HttpMethodName.PUT, url.toString(), identifyId, user);
        request.setBody(param.toString());
        Result<JsonElement> jsonObjectResult = miCloudAlertManager.executeRequest(request);
        return Result.success(jsonObjectResult.getData());
    }

    @Override
    public Result delScrapeJob(Integer jobId, String identifyId, String user) {
          StringBuilder url = new StringBuilder(alarmDomain).append(MiCloudAlertManager.alarm_job_option_uri).append("/").append(jobId);
          Request request = miCloudAlertManager.createRequest(HttpMethodName.DELETE, url.toString(), identifyId, user);
          Result<JsonElement> jsonObjectResult = miCloudAlertManager.executeRequest(request);
          return Result.success(jsonObjectResult.getData());
    }

    @Override
    public Result queryScrapeJob(Integer jobId, String identifyId, String user) {
         StringBuilder url = new StringBuilder(alarmDomain).append(MiCloudAlertManager.alarm_job_option_uri)
                .append("/").append(jobId);
        Request request = miCloudAlertManager.createRequest(HttpMethodName.GET, url.toString(), identifyId, user);
        Result<JsonElement> jsonObjectResult = miCloudAlertManager.executeRequest(request);
        return Result.success(jsonObjectResult.getData());
    }
}
