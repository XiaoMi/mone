package com.xiaomi.mone.monitor.service.scrapeJob;

import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ScrapeJobAdapt {
    private ApplicationContext applicationContext;

    @Value("${scrape.job:staging}")
    private String ScrapeJobEnv;

    @Autowired
    private ScrapeJob scrapeJob;

    public Result addScrapeJob(JsonObject param, String identifyId, String user) {
        return scrapeJob.addScrapeJob(param,identifyId, user);
    }

    public Result editScrapeJob(Integer jobId,JsonObject param,String identifyId, String user) {
        return scrapeJob.editScrapeJob(jobId, param, identifyId, user);
    }

    public Result delScrapeJob(Integer jobId,String identifyId, String user) {
        return scrapeJob.delScrapeJob(jobId,identifyId, user);
    }

    public Result queryScrapeJob(Integer jobId, String identifyId, String user) {
        return scrapeJob.queryScrapeJob(jobId,identifyId, user);
    }

    public Result queryScrapeJobByName(String name, String identifyId, String user) {
        return scrapeJob.queryScrapeJobByName(name,identifyId, user);
    }

}
