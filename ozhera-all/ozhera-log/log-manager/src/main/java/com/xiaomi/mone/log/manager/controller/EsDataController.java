/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
                          @RequestParam(value = "storeId") Long storeId,
                          @RequestParam(value = "tail") String tail,
                          @RequestParam(value = "startTime") Long startTime,
                          @RequestParam(value = "endTime") Long endTime,
                          @RequestParam(value = "fullTextSearch") String fullTextSearch) throws Exception {
        LogQuery logQuery = new LogQuery(logstore, storeId, tail, startTime, endTime, fullTextSearch, "timestamp");
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
     * tail log collection ranking
     *
     * @return
     */
    @RequestMapping(path = "/log/statistics/collectTop", method = "get")
    public Result<List<LogtailCollectTopDTO>> collectTop() {
        return logCountService.collectTop();
    }

    /**
     * space log collection trends
     *
     * @return
     */
    @RequestMapping(path = "/log/statistics/spaceCollectTop", method = "get")
    public Result<List<SpaceCollectTopDTO>> spaceCollectTop() {
        return logCountService.collectSpaceTop();
    }

    /**
     * tail log collection trends
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
     * Space log collection trends
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
     * Execute the script-tail log collection trend cache flush
     */
    @RequestMapping(path = "/log/statistics/collectTrendRefresh", method = "get")
    public void collectTrendRefresh() {
        logCountService.collectTrendRefresh();
    }

    /**
     * Perform a script-space log collection trend cache flush
     */
    @RequestMapping(path = "/log/statistics/spaceCollectTrendRefresh", method = "get")
    public void spaceCollectTrendRefresh() {
        logCountService.collectSpaceTrend();
    }

    /**
     * Execute script - Counts the number of logs for a specified date
     *
     * @param thisDay
     * @throws IOException
     */
    @RequestMapping(path = "/log/statistics/collectLogCount", method = "get")
    public void collectLogCount(@RequestParam(value = "thisDay") String thisDay) throws IOException {
        try {
            logCountService.collectLogCount(thisDay);
        } catch (Exception e) {
            log.error("Log statistics failed,error:[{}]", e.getMessage());
        }

    }

    /**
     * Perform a script - statistics log ranking cache flush
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/statistics/collectTopCount", method = "get")
    public void collectTopCount() throws IOException {
        logCountService.collectTopCount();
    }

    /**
     * Perform a script-space log volume cache flush
     */
    @RequestMapping(path = "/log/statistics/collectSpaceTopRefresh", method = "get")
    public void collectSpaceTopRefresh() {
        logCountService.collectSpaceTopCount();
    }

    /**
     * Execute script - Deletes log volume statistics for the specified date
     *
     * @param thisDay
     * @throws IOException
     */
    @RequestMapping(path = "/log/statistics/collectLogDelete", method = "get")
    public void collectLogDelete(@RequestParam(value = "thisDay") String thisDay) throws IOException {
        logCountService.collectLogDelete(thisDay);
    }

    /**
     * Execute script - View log statistics cache
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/statistics/showLogCountCache", method = "get")
    public void showLogCountCache() throws IOException {
        logCountService.showLogCountCache();
    }

    /**
     * Save the query - list display
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/list", method = "get")
    public Result<List<SearchSaveDTO>> searchSavelList(@RequestParam(value = "storeId") Long storeId,
                                                       @RequestParam(value = "sort") Integer sort) {
        return searchSaveService.list(storeId, sort);
    }

    /**
     * Save the query - query parameters
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/detail", method = "get")
    public SearchSaveDTO saveDetail(@RequestParam(value = "id") Long id) {
        return searchSaveService.getById(id);
    }

    /**
     * Save Query - Save
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/favourite")
    public Result<Integer> saveSearchSave(SearchSaveInsertCmd cmd) {
        return searchSaveService.save(cmd);
    }

    /**
     * Save Query - Unfavorite
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/defavourite", method = "get")
    public Result<Integer> defavourite(@RequestParam(value = "sort") Integer sort, @RequestParam(value = "id") Long id) {
        return searchSaveService.defavourite(sort, id);
    }

    /**
     * Save Query - Update
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/update")
    public Result<Integer> saveSearchUpdate(SearchSaveUpdateCmd cmd) {
        return searchSaveService.update(cmd);
    }

    /**
     * Save the query-swap order
     */
    @RequestMapping(path = "/log/save/swap", method = "get")
    public Result<Boolean> swap(@RequestParam(value = "idFrom") Long idFrom, @RequestParam(value = "idTo") Long idTo) {
        return searchSaveService.swapOrder(idFrom, idTo);
    }

    /**
     * Save Query - Delete
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
