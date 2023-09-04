package com.xiaomi.mone.monitor.service.scrapeJob;

import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.result.Result;

public interface ScrapeJob {
    public Result addScrapeJob(JsonObject param, String identifyId, String user);

    public Result editScrapeJob(Integer jobId,JsonObject param,String identifyId, String user);

    public Result delScrapeJob(Integer jobId,String identifyId, String user);

    public Result  queryScrapeJob(Integer jobId, String identifyId, String user);
    public Result  queryScrapeJobByName(String name, String identifyId, String user);
}
