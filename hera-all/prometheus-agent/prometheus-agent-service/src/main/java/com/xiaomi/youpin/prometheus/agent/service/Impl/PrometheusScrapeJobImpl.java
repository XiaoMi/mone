package com.xiaomi.youpin.prometheus.agent.service.Impl;

import com.xiaomi.youpin.prometheus.agent.Commons;
import com.xiaomi.youpin.prometheus.agent.api.service.PrometheusScrapeJobService;
import com.xiaomi.youpin.prometheus.agent.enums.ErrorCode;
import com.xiaomi.youpin.prometheus.agent.param.scrapeConfig.ScrapeConfigParam;
import com.xiaomi.youpin.prometheus.agent.result.Result;
import com.xiaomi.youpin.prometheus.agent.service.prometheus.ScrapeJobService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
@Service(timeout = 5000, group = "${dubbo.group}")
public class PrometheusScrapeJobImpl implements PrometheusScrapeJobService {
    @Autowired
    ScrapeJobService scrapeJobService;

    @Override
    public Result CreateScrapeConfig(ScrapeConfigParam param) {
        return scrapeJobService.CreateScrapeConfig(param);
    }

    @Override
    public Result DeleteScrapeConfig(String id) {
        return scrapeJobService.DeleteScrapeConfig(id);
    }

    @Override
    public Result UpdateScrapeConfig(String id, ScrapeConfigParam entity) {
        Result result = scrapeJobService.UpdateScrapeConfig(id,entity);
        return result;
    }

    @Override
    public Result GetScrapeConfig(String id) {
        return scrapeJobService.GetScrapeConfig(id);
    }

    @Override
    public Result GetScrapeConfigList(Integer page_size,Integer page_no) {
        if (page_size == null && page_no == null) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        if (page_size == null) {
            page_size = Commons.COMMON_PAGE_SIZE;
        }
        if (page_no == null) {
            page_no = Commons.COMMON_PAGE_NO;
        }

        Result result = scrapeJobService.GetScrapeConfigList(page_size, page_no);
        return result;
    }
}
