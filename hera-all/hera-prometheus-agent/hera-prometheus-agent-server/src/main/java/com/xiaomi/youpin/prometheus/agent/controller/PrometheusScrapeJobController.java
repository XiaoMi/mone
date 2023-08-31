package com.xiaomi.youpin.prometheus.agent.controller;

import com.xiaomi.youpin.prometheus.agent.Commons;
import com.xiaomi.youpin.prometheus.agent.result.Result;
import com.xiaomi.youpin.prometheus.agent.enums.ErrorCode;
import com.xiaomi.youpin.prometheus.agent.param.scrapeConfig.ScrapeConfigParam;
import com.xiaomi.youpin.prometheus.agent.service.prometheus.ScrapeJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//抓取exporter job相关接口

/**
 * @author zhangxiaowei6
 */
@RestController
@Slf4j
@RequestMapping(value = "/api/v1")
public class PrometheusScrapeJobController {

    @Autowired
    ScrapeJobService scrapeJobService;

    @RequestMapping(value = "/scrape-config",method = RequestMethod.POST)
    public Result CreateScrapeConfig(@RequestBody ScrapeConfigParam param) {
        return scrapeJobService.CreateScrapeConfig(param);
    }

    @RequestMapping(value = "/scrape-config/{id}",method = RequestMethod.DELETE)
    public Result DeleteScrapeConfig(@PathVariable String id) {
        return scrapeJobService.DeleteScrapeConfig(id);
    }

    @RequestMapping(value = "/scrape-config/{id}",method = RequestMethod.PUT)
    public Result UpdateScrapeConfig(@PathVariable String id, @RequestBody ScrapeConfigParam entity) {
        Result result = scrapeJobService.UpdateScrapeConfig(id,entity);
        return result;
    }

    @RequestMapping(value = "/scrape-config/{id}",method = RequestMethod.GET)
    public Result GetScrapeConfig(@PathVariable  String id) {
        return scrapeJobService.GetScrapeConfig(id);
    }

    @RequestMapping(value = "/scrape-config/list",method = RequestMethod.GET)
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
