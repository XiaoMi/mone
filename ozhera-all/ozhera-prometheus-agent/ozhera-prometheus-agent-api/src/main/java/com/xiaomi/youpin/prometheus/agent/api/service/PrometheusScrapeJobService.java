package com.xiaomi.youpin.prometheus.agent.api.service;

import com.xiaomi.youpin.prometheus.agent.param.scrapeConfig.ScrapeConfigParam;
import com.xiaomi.youpin.prometheus.agent.result.Result;

/**
 * @author zhangxiaowei6
 */

// Provide Prometheus with the Dubbo API for job scraping.
public interface PrometheusScrapeJobService {
    Result CreateScrapeConfig(ScrapeConfigParam param);

    Result DeleteScrapeConfig(String id);

    Result UpdateScrapeConfig(String id, ScrapeConfigParam entity);

    Result GetScrapeConfig(String id);

    Result GetScrapeConfigByName(String name);

    Result GetScrapeConfigList(Integer page_size, Integer page_no);
}
