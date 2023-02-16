package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.api.model.dto.TraceLogDTO;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.EsStatisticResult;
import com.xiaomi.mone.log.manager.model.dto.LogDTO;
import com.xiaomi.mone.log.manager.model.dto.SearchSaveDTO;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.vo.*;
import com.xiaomi.mone.log.manager.service.impl.LogQueryServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogSearchSaveServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
public class LogQueryController {

    @Resource
    private LogQueryServiceImpl logQueryService;

    @Resource
    private LogSearchSaveServiceImpl searchSaveService;

    @RequestMapping(path = "/log/query")
    public Result<LogDTO> logQuery(LogQuery logQuery) throws Exception {
        return logQueryService.logQuery(logQuery);
    }

    @RequestMapping(path = "/milog/statistic/es")
    public Result<EsStatisticResult> statisticEs(@RequestParam("param") LogQuery param) throws Exception {
        return logQueryService.EsStatistic(param);
    }

    @RequestMapping(path = "/log/context")
    public Result<LogDTO> logContext(LogContextQuery logContextQuery) throws Exception {
        return logQueryService.getDocContext(logContextQuery);
    }

    @RequestMapping(path = "/log/export", method = "get")
    public void logExport(@RequestParam(value = "logstore") String logstore,
                          @RequestParam(value = "tail") String tail,
                          @RequestParam(value = "startTime") Long startTime,
                          @RequestParam(value = "endTime") Long endTime,
                          @RequestParam(value = "fullTextSearch") String fullTextSearch) throws Exception {
        LogQuery logQuery = new LogQuery(logstore, tail, startTime, endTime, fullTextSearch, "timestamp");
        logQueryService.logExport(logQuery);
    }

    @RequestMapping(path = "/log/queryRegionTraceLog")
    public Result<TraceLogDTO> queryRegionTraceLog(RegionTraceLogQuery regionTraceLogQuery) throws IOException {
        return logQueryService.queryRegionTraceLog(regionTraceLogQuery);
    }

    /**
     * 保存查询-列表展示
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/list", method = "get")
    public Result<PageInfo<SearchSaveDTO>> searchSaveList(@RequestParam(value = "storeId") Long storeId, @RequestParam(value = "pageNum") Integer pageNum, @RequestParam(value = "pageSize") Integer pageSize) throws IOException {
        return searchSaveService.list(storeId, pageNum, pageSize);
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
    @RequestMapping(path = "/log/save/save")
    public Result<Integer> saveSearchSave(SearchSaveInsertCmd cmd) {
        return searchSaveService.save(cmd);
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
     * 保存查询-删除
     *
     * @throws IOException
     */
    @RequestMapping(path = "/log/save/delete", method = "get")
    public Result<Integer> saveDelete(@RequestParam(value = "id") Long id) {
        return searchSaveService.removeById(id);
    }

}
