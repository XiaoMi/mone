package com.xiaomi.youpin.prometheus.agent.api.service;

import com.xiaomi.youpin.prometheus.agent.param.scrapeConfig.ScrapeConfigParam;
import com.xiaomi.youpin.prometheus.agent.result.Result;

/**
 * @author zhangxiaowei6
 */

//提供prometheus 抓取job相关的dubbo api
public interface PrometheusScrapeJobService {
    Result CreateScrapeConfig(ScrapeConfigParam param);
    Result DeleteScrapeConfig(String id);
    Result UpdateScrapeConfig(String id, ScrapeConfigParam entity);
    Result GetScrapeConfig(String id);
    Result GetScrapeConfigList(Integer page_size,Integer page_no);
}
