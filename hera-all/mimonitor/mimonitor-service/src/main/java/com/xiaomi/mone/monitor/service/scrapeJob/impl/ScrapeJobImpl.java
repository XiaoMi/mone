package com.xiaomi.mone.monitor.service.scrapeJob.impl;

import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.scrapeJob.ScrapeJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service(value="openSourceScrapeJob")
public class ScrapeJobImpl implements ScrapeJob {

    @Override
    public Result addScrapeJob(JsonObject param, String identifyId, String user) {
        return null;
    }

    @Override
    public Result editScrapeJob(Integer jobId, JsonObject param, String identifyId, String user) {
        return null;
    }

    @Override
    public Result delScrapeJob(Integer jobId, String identifyId, String user) {
        return null;
    }

    @Override
    public Result queryScrapeJob(Integer jobId, String identifyId, String user) {
        return null;
    }
}
