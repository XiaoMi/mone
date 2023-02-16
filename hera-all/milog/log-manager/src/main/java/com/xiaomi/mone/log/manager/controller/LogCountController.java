package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.LogtailCollectTopDTO;
import com.xiaomi.mone.log.manager.model.dto.LogtailCollectTrendDTO;
import com.xiaomi.mone.log.manager.model.dto.SpaceCollectTopDTO;
import com.xiaomi.mone.log.manager.model.dto.SpaceCollectTrendDTO;
import com.xiaomi.mone.log.manager.service.impl.LogCountServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Controller
public class LogCountController {
    @Resource
    private LogCountServiceImpl logCountService;

    /**
     * space日志收集排行
     *
     * @return
     */
    @RequestMapping(path = "/log/statistics/spaceCollectTop", method = "get")
    public Result<List<SpaceCollectTopDTO>> spaceCollectTop() {
        return logCountService.collectSpaceTop();
    }

    /**
     * tail日志收集排行
     *
     * @return
     */
    @RequestMapping(path = "/log/statistics/collectTop", method = "get")
    public Result<List<LogtailCollectTopDTO>> collectTop() {
        return logCountService.collectTop();
    }

    /**
     * space日志收集趋势
     *
     * @param spaceId
     * @return
     * @throws IOException
     */
    @RequestMapping(path = "/log/statistics/spaceCollectTrend", method = "get")
    public Result<List<SpaceCollectTrendDTO>> spaceCollectTrend(@RequestParam(value = "spaceId") Long spaceId) {
        return logCountService.spaceCollectTrend(spaceId);
    }

    /**
     * tail日志收集趋势
     *
     * @param tailId
     * @return
     * @throws IOException
     */
    @RequestMapping(path = "/log/statistics/collectTrend", method = "get")
    public Result<List<LogtailCollectTrendDTO>> collectTrend(@RequestParam(value = "tailId") Long tailId) throws IOException {
        return logCountService.collectTrend(tailId);
    }

    /**
     * 执行脚本-tail日志收集趋势缓存刷新
     */
    @RequestMapping(path = "/log/statistics/collectTrendRefresh", method = "get")
    public void collectTrendRefresh() {
        logCountService.collectTrendRefresh();
    }

    /**
     * 执行脚本-space日志收集趋势缓存刷新
     */
    @RequestMapping(path = "/log/statistics/spaceCollectTrendRefresh", method = "get")
    public void spaceCollectTrendRefresh() {
        logCountService.collectSpaceTrend();
    }

    /**
     * 执行脚本-统计指定日期的日志量
     *
     * @param thisDay
     * @throws IOException
     */
    @RequestMapping(path = "/log/statistics/collectLogCount", method = "get")
    public void collectLogCount(@RequestParam(value = "thisDay") String thisDay) throws IOException {
        logCountService.collectLogCount(thisDay);
    }

    /**
     * 执行脚本-统计日志排行缓存刷新
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/statistics/collectTopCount", method = "get")
    public void collectTopCount() throws IOException {
        logCountService.collectTopCount();
    }

    /**
     * 执行脚本-space日志量缓存刷新
     */
    @RequestMapping(path = "/log/statistics/collectSpaceTopRefresh", method = "get")
    public void collectSpaceTopRefresh() {
        logCountService.collectSpaceTopCount();
    }

    /**
     * 执行脚本-删除指定日期的日志量统计
     *
     * @param thisDay
     * @throws IOException
     */
    @RequestMapping(path = "/log/statistics/collectLogDelete", method = "get")
    public void collectLogDelete(@RequestParam(value = "thisDay") String thisDay) throws IOException {
        logCountService.collectLogDelete(thisDay);
    }

    /**
     * 执行脚本-查看日志统计缓存
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/statistics/showLogCountCache", method = "get")
    public void showLogCountCache() throws IOException {
        logCountService.showLogCountCache();
    }
}
