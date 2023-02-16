package com.xiaomi.mone.monitor.service.scrapeJob;

import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.result.Result;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class ScrapeJobAdapt implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;

    @Value("${scrape.job:staging}")
    private String ScrapeJobEnv;

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

    @Override
    public void afterPropertiesSet() throws Exception {


        switch (ScrapeJobEnv){
            case "openSource" :
                scrapeJob = (ScrapeJob)this.applicationContext.getBean("openSourceScrapeJob");

            case "micloud" :
                scrapeJob = (ScrapeJob)this.applicationContext.getBean("miCloudScrapeJob");

            default:
                scrapeJob = (ScrapeJob)this.applicationContext.getBean("openSourceScrapeJob");
        }

    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
    }
}
