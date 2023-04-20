package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.api.model.dto.TraceLogDTO;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.*;
import com.xiaomi.mone.log.manager.model.vo.*;
import com.xiaomi.mone.log.manager.service.impl.EsDataServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.EsIndexTemplateServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogCountServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.MilogLogSearchSaveServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class EsDataController {
    @Resource
    private EsDataServiceImpl esDataService;

    @Resource
    private EsIndexTemplateServiceImpl esIndexTemplateService;

    @Resource
    private LogCountServiceImpl logCountService;

    @Resource
    private MilogLogSearchSaveServiceImpl searchSaveService;

    @RequestMapping(path = "/es/updateIndexTemplate", method = "POST")
    public Boolean updateIndexTemplate(@RequestParam(value = "updateIndexTemplateCommand") UpdateIndexTemplateCommand updateIndexTemplateCommand) throws IOException {
        return esIndexTemplateService.updateIndexTemplate(updateIndexTemplateCommand);
    }

    @RequestMapping(path = "/es/createIndex", method = "get")
    public Boolean createIndex(@RequestParam(value = "templateName") String templateName) throws IOException {
        return esIndexTemplateService.createIndex(templateName);
    }

    @RequestMapping(path = "/log/export", method = "get")
    public void logExport(@RequestParam(value = "logstore") String logstore,
                          @RequestParam(value = "tail") String tail,
                          @RequestParam(value = "startTime") Long startTime,
                          @RequestParam(value = "endTime") Long endTime,
                          @RequestParam(value = "fullTextSearch") String fullTextSearch) throws Exception {
        LogQuery logQuery = new LogQuery(logstore, tail, startTime, endTime, fullTextSearch, "timestamp");
        esDataService.logExport(logQuery);
    }

    @RequestMapping(path = "/log/query")
    public Result<LogDTO> logQuery(LogQuery logQuery) {
        return esDataService.logQuery(logQuery);
    }

    @RequestMapping(path = "/log/context")
    public Result<LogDTO> logContext(LogContextQuery logContextQuery) throws Exception {
        return esDataService.getDocContext(logContextQuery);
    }

    @RequestMapping(path = "/log/queryRegionTraceLog")
    public Result<TraceLogDTO> queryRegionTraceLog(RegionTraceLogQuery regionTraceLogQuery) throws IOException {
        return esDataService.queryRegionTraceLog(regionTraceLogQuery);
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
     * space日志收集排行
     *
     * @return
     */
    @RequestMapping(path = "/log/statistics/spaceCollectTop", method = "get")
    public Result<List<SpaceCollectTopDTO>> spaceCollectTop() {
        return logCountService.collectSpaceTop();
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
        try {
            logCountService.collectLogCount(thisDay);
        } catch (Exception e) {
            log.error("日志统计失败,error:[{}]", e.getMessage());
        }

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

    /**
     * 保存查询-列表展示
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/list", method = "get")
    public Result<List<SearchSaveDTO>> searchSavelList(@RequestParam(value = "storeId") Long storeId,
                                                       @RequestParam(value = "sort") Integer sort) {
        return searchSaveService.list(storeId,sort);
    }

    /**
     * 保存查询-查询参数
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/detail", method = "get")
    public SearchSaveDTO saveDetail(@RequestParam(value = "id") Long id) {
        return searchSaveService.getById(id);
    }

    /**
     * 保存查询-保存
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/favourite")
    public Result<Integer> saveSearchSave(SearchSaveInsertCmd cmd) {
        return searchSaveService.save(cmd);
    }

    /**
     * 保存查询-取消收藏
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/defavourite", method = "get")
    public Result<Integer> defavourite(@RequestParam(value = "sort") Integer sort, @RequestParam(value = "id") Long id) {
        return searchSaveService.defavourite(sort, id);
    }

    /**
     * 保存查询-更新
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/update")
    public Result<Integer> saveSearchUpdate(SearchSaveUpdateCmd cmd) {
        return searchSaveService.update(cmd);
    }

    /**
     * 保存查询-交换顺序
     */
    @RequestMapping(path = "/log/save/swap", method = "get")
    public Result<Boolean> swap(@RequestParam(value = "idFrom") Long idFrom, @RequestParam(value = "idTo") Long idTo) {
        return searchSaveService.swapOrder(idFrom, idTo);
    }

    /**
     * 保存查询-删除
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/delete", method = "get")
    public Result<Integer> saveDelete(@RequestParam(value = "id") Long id) {
        return searchSaveService.removeById(id);
    }

    @RequestMapping(path = "/log/trace/url")
    public Result<String> getTraceAppLogUrl(TraceAppLogUrlQuery query) {
        return esDataService.getTraceAppLogUrl(query);
    }

    @RequestMapping(path = "/log/save/storeTree", method = "get")
    public Result<List<SpaceTreeFavouriteDTO>> storeTree() {
        return searchSaveService.storeTree();
    }

    @RequestMapping(path = "/log/save/initOrder", method = "get")
    public Result<Integer> initOrder(@RequestParam(value = "key") String key) {
        return searchSaveService.initOrder(key);
    }

}
