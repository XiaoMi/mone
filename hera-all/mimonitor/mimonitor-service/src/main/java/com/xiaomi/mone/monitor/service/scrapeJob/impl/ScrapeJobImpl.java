package com.xiaomi.mone.monitor.service.scrapeJob.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.alertmanager.client.Request;
import com.xiaomi.mone.monitor.service.alertmanager.client.model.HttpMethodName;
import com.xiaomi.mone.monitor.service.scrapeJob.ScrapeJob;
import com.xiaomi.youpin.prometheus.agent.api.service.PrometheusAlertService;
import com.xiaomi.youpin.prometheus.agent.api.service.PrometheusScrapeJobService;
import com.xiaomi.youpin.prometheus.agent.param.alert.RuleAlertParam;
import com.xiaomi.youpin.prometheus.agent.param.scrapeConfig.ScrapeConfigParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "openSourceScrapeJob")
public class ScrapeJobImpl implements ScrapeJob {

    @Value("${dubbo.group.alert}")
    private String alert;
    @Reference(registry = "registryConfig", check = false, interfaceClass = PrometheusScrapeJobService.class, group = "${dubbo.group.alert}")
    PrometheusScrapeJobService prometheusScrapeJobService;

    @Override
    public Result addScrapeJob(JsonObject param, String identifyId, String user) {
        Result result = null;
        try {
            ScrapeConfigParam scrapeConfigParam = new Gson().fromJson(new Gson().toJson(param), ScrapeConfigParam.class);
            com.xiaomi.youpin.prometheus.agent.result.Result scrapeResult = prometheusScrapeJobService.CreateScrapeConfig(scrapeConfigParam);
            log.info("addScrapeJob: {}", scrapeResult);
            result = new Gson().fromJson(new Gson().toJson(scrapeResult), Result.class);

            log.info("open scrape job add, request : {} ,result:{}", new Gson().toJson(scrapeConfigParam), new Gson().toJson(scrapeResult));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.success(result.getData());
    }

    @Override
    public Result editScrapeJob(Integer jobId, JsonObject param, String identifyId, String user) {
        Result result = null;
        try {
            ScrapeConfigParam scrapeConfigParam = new Gson().fromJson(new Gson().toJson(param), ScrapeConfigParam.class);
            com.xiaomi.youpin.prometheus.agent.result.Result scrapeResult = prometheusScrapeJobService.UpdateScrapeConfig(String.valueOf(jobId), scrapeConfigParam);
            log.info("editScrapeJob: {}", scrapeResult);
            result = new Gson().fromJson(new Gson().toJson(scrapeResult), Result.class);
            log.info("open scrape job edit, request : {} ,result:{}", new Gson().toJson(scrapeConfigParam), new Gson().toJson(scrapeResult));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.success(result.getData());
    }

    @Override
    public Result delScrapeJob(Integer jobId, String identifyId, String user) {
        Result result = null;
        try {
            com.xiaomi.youpin.prometheus.agent.result.Result scrapeResult = prometheusScrapeJobService.DeleteScrapeConfig(String.valueOf(jobId));
            log.info("delScrapeJob: {}", scrapeResult);
            result = new Gson().fromJson(new Gson().toJson(scrapeResult), Result.class);
            log.info("open scrape job delete, request : {} ,result:{}", jobId, new Gson().toJson(scrapeResult));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.success(result.getData());
    }

    @Override
    public Result queryScrapeJob(Integer jobId, String identifyId, String user) {
        Result result = null;
        try {
            com.xiaomi.youpin.prometheus.agent.result.Result scrapeResult = prometheusScrapeJobService.GetScrapeConfig(String.valueOf(jobId));
            log.info("queryScrapeJob: {}", scrapeResult);
            result = new Gson().fromJson(new Gson().toJson(scrapeResult), Result.class);
            log.info("open scrape job query, request : {} ,result:{}", jobId, new Gson().toJson(scrapeResult));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.success(result.getData());
    }

    @Override
    public Result queryScrapeJobByName(String name, String identifyId, String user) {
        Result result = null;
        try {
            com.xiaomi.youpin.prometheus.agent.result.Result scrapeResult = prometheusScrapeJobService.GetScrapeConfigByName(name);
            log.info("queryScrapeJobByName: {}", scrapeResult);
            result = new Gson().fromJson(new Gson().toJson(scrapeResult), Result.class);
            log.info("open scrape job query, request : {} ,result:{}", name, new Gson().toJson(scrapeResult));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.success(result.getData());
    }
}
