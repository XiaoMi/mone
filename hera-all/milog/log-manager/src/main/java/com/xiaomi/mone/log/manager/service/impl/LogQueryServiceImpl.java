package com.xiaomi.mone.log.manager.service.impl;

import cn.hutool.core.date.StopWatch;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.es.EsClient;
import com.xiaomi.mone.log.api.model.dto.TraceLogDTO;
import com.xiaomi.mone.log.api.model.vo.TraceLogQuery;
import com.xiaomi.mone.log.api.service.LogDataService;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.dao.LogstoreDao;
import com.xiaomi.mone.log.manager.domain.EsCluster;
import com.xiaomi.mone.log.manager.domain.SearchLog;
import com.xiaomi.mone.log.manager.domain.TraceLog;
import com.xiaomi.mone.log.manager.model.dto.EsStatisticResult;
import com.xiaomi.mone.log.manager.model.dto.LogDTO;
import com.xiaomi.mone.log.manager.model.dto.LogDataDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.vo.LogContextQuery;
import com.xiaomi.mone.log.manager.model.vo.LogQuery;
import com.xiaomi.mone.log.manager.model.vo.RegionTraceLogQuery;
import com.xiaomi.mone.log.manager.service.EsDataBaseService;
import com.xiaomi.mone.log.manager.service.LogQueryService;
import com.xiaomi.mone.log.parse.LogParser;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import run.mone.excel.ExportExcel;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.manager.common.utils.ManagerUtil.getKeyColonPrefix;
import static com.xiaomi.mone.log.manager.common.utils.ManagerUtil.getKeyList;
import static org.elasticsearch.search.sort.SortOrder.ASC;
import static org.elasticsearch.search.sort.SortOrder.DESC;

@Slf4j
@Service
@com.xiaomi.youpin.docean.plugin.dubbo.anno.Service(interfaceClass = LogDataService.class)
public class LogQueryServiceImpl implements LogQueryService, LogDataService, EsDataBaseService {

    @Resource
    private LogstoreDao logstoreDao;

    @Resource
    private EsCluster esCluster;

    @Resource
    private TraceLog traceLog;

    @Resource
    private SearchLog searchLog;

    private Set<String> noHighLightSet = new HashSet<>();

    private Set<String> hidenFiledSet = new HashSet<>();

    {
        noHighLightSet.add("logstore");
        noHighLightSet.add("logsource");
        noHighLightSet.add("tail");
        noHighLightSet.add("timestamp");
        noHighLightSet.add("linenumber");

        hidenFiledSet.add("mqtag");
        hidenFiledSet.add("mqtopic");
        hidenFiledSet.add("logstore");
        hidenFiledSet.add("linenumber");
        hidenFiledSet.add("filename");
    }

    /**
     * 读取ES索引的数据
     *
     * @param logQuery
     * @return
     */
    @Override
    public Result<LogDTO> logQuery(LogQuery logQuery) {
        String logInfo = String.format("queryText:%s, user:%s, logQuery:%s", logQuery.getFullTextSearch(), MoneUserContext.getCurrentUser().getUser(), logQuery);
        log.info("query simple param:{}", logInfo);

        SearchRequest searchRequest = null;
        StopWatch stopWatch = new StopWatch("HERA-LOG-QUERY");
        try {
            stopWatch.start("before-query");
            MilogLogStoreDO logStore = logstoreDao.getByName(logQuery.getLogstore());
            if (logStore == null) {
                log.warn("[EsDataService.logQuery] not find logstore:[{}]", logQuery.getLogstore());
                return Result.failParam("找不到[" + logQuery.getLogstore() + "]对应的数据");
            }
            EsService esService = esCluster.getEsService(logStore.getEsClusterId());
            String esIndexName = logStore.getEsIndex();
            if (esService == null || StringUtils.isEmpty(esIndexName)) {
                log.warn("[EsDataService.logQuery] logstroe:[{}]配置异常", logQuery.getLogstore());
                return Result.failParam("logstroe配置异常");
            }
            List<String> keyList = getKeyList(logStore.getKeyList(), logStore.getColumnTypeList());
            // 构建查询参数
            BoolQueryBuilder boolQueryBuilder = searchLog.getQueryBuilder(logQuery, getKeyColonPrefix(logStore.getKeyList()));
            SearchSourceBuilder builder = new SearchSourceBuilder();
            builder.query(boolQueryBuilder);
            LogDTO dto = new LogDTO();
            stopWatch.stop();

            // 查询
            stopWatch.start("bool-query");
            builder.sort(logQuery.getSortKey(), logQuery.getAsc() ? ASC : DESC);
//            if ("cn".equals(milogLogstoreDO.getMachineRoom())) {
//                builder.sort(LogParser.esKeyMap_lineNumber, logQuery.getAsc() ? ASC : DESC);
//            }
            // 分页
            if (logQuery.getBeginSortValue() != null && logQuery.getBeginSortValue().length != 0) {
                builder.searchAfter(logQuery.getBeginSortValue());
            }
            builder.size(logQuery.getPageSize());
            // 高亮
            builder.highlighter(getHighlightBuilder(keyList));
            builder.timeout(TimeValue.timeValueMinutes(1L));
            searchRequest = new SearchRequest(esIndexName);
            searchRequest.source(builder);
            dto.setSourceBuilder(builder);
            SearchResponse searchResponse = esService.search(searchRequest);
            stopWatch.stop();
            if (stopWatch.getLastTaskTimeMillis() > 7 * 1000) {
                log.warn("##LONG-COST-QUERY##{} cost:{} ms, msg:{}", stopWatch.getLastTaskName(), stopWatch.getLastTaskTimeMillis(), logInfo);
            }

            //结果转换
            stopWatch.start("after-query");
            transformSearchResponse(searchResponse, dto, keyList);

            stopWatch.stop();
            if (stopWatch.getTotalTimeMillis() > 15 * 1000) {
                log.warn("##LONG-COST-QUERY##{} cost:{} ms, msg:{}", "gt15s", stopWatch.getLastTaskTimeMillis(), logInfo);
            }

            return Result.success(dto);
        } catch (ElasticsearchStatusException e) {
            log.error("日志查询错误，日志搜索报错, 错误类型[{}], logQuery:[{}], searchRequest:[{}], user:[{}]", e.status(), logQuery, searchRequest, MoneUserContext.getCurrentUser(), e);
            return Result.failParam("ES资源权限配置错误，请检查用户名密码或Token");
        } catch (Throwable e) {
            log.error("日志查询错误，日志搜索报错,logQuery:[{}],searchRequest:[{}],user:[{}]", logQuery, searchRequest, MoneUserContext.getCurrentUser(), e);
            return Result.failParam("搜索词输入错误，请检查");
        }
    }

    private void transformSearchResponse(SearchResponse searchResponse, final LogDTO logDTO, List<String> keyList) {
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits == null || hits.length == 0) {
            return;
        }
        List<LogDataDTO> logDataList = Lists.newArrayList();
        for (SearchHit hit : hits) {
            LogDataDTO logData = hit2DTO(hit, keyList);
            // 封装高亮
            logData.setHighlight(getHightlinghtMap(hit));
            logDataList.add(logData);
        }
        logDTO.setThisSortValue(hits[hits.length - 1].getSortValues());
        logDTO.setLogDataDTOList(logDataList);
    }

    // 高亮
    private HighlightBuilder getHighlightBuilder(List<String> keyList) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String key : keyList) {
            if (noHighLightSet.contains(key)) {
                continue;
            }
            HighlightBuilder.Field highlightField = new HighlightBuilder.Field(key);
            highlightBuilder.field(highlightField);
        }
        return highlightBuilder;
    }

    @Override
    public Result<EsStatisticResult> EsStatistic(LogQuery logQuery) {
        try {
            EsStatisticResult result = new EsStatisticResult();
            result.setName(constractEsStatisticRet(logQuery));
            MilogLogStoreDO logStore = logstoreDao.getByName(logQuery.getLogstore());
            if (logStore == null) {
                return new Result<>(CommonError.UnknownError.getCode(), "not found logstore", null);
            }
            // get interval
            String interval = searchLog.esHistogramInterval(logQuery.getEndTime() - logQuery.getStartTime());
            EsService esService = esCluster.getEsService(logStore.getEsClusterId());
            String esIndex = logStore.getEsIndex();
            if (esService == null || StringUtils.isEmpty(esIndex)) {
                return Result.failParam("logStore或tail配置异常");
            }
            if (!StringUtils.isEmpty(interval)) {
                BoolQueryBuilder queryBuilder = searchLog.getQueryBuilder(logQuery, getKeyColonPrefix(logStore.getKeyList()));
                EsClient.EsRet esRet = esService.dateHistogram(esIndex, interval, logQuery.getStartTime(), logQuery.getEndTime(), queryBuilder);
                result.setCounts(esRet.getCounts());
                result.setTimestamps(esRet.getTimestamps());
                result.setQueryBuilder(queryBuilder);
                result.calTotalCounts();
                return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), result);
            } else {
                return new Result<>(CommonError.UnknownError.getCode(), "The minimum time interval is 10s", null);
            }
        } catch (ElasticsearchStatusException e) {
            log.error("日志查询错误，日志柱状图统计报错:[{}], 错误类型[{}], logQuery:[{}], user:[{}]", e, e.status(), logQuery, MoneUserContext.getCurrentUser(), e);
            return Result.failParam("ES资源权限配置错误，请检查用户名密码或Token");
        } catch (Exception e) {
            log.error("日志查询错误，日志柱状图统计报错[{}],logQuery:[{}],user:[{}]", e, logQuery, MoneUserContext.getCurrentUser(), e);
            return Result.failParam("搜索词输入错误，请检查");
        }
    }

    private String constractEsStatisticRet(LogQuery logquery) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(logquery.getLogstore())) {
            sb.append("logstore:").append(logquery.getLogstore()).append(";");
        }
        if (!StringUtils.isEmpty(logquery.getFullTextSearch())) {
            sb.append("fullTextSearch:").append(logquery.getFullTextSearch()).append(";");
        }
        return sb.toString();
    }

    private String esHistogramInterval(Long duration) {
        duration = duration / 1000;
        if (duration > 24 * 60 * 60) {
            duration = duration / 100;
            return duration + "s";
        } else if (duration > 12 * 60 * 60) {
            duration = duration / 80;
            return duration + "s";
        } else if (duration > 6 * 60 * 60) {
            duration = duration / 60;
            return duration + "s";
        } else if (duration > 60 * 60) {
            duration = duration / 50;
            return duration + "s";
        } else if (duration > 30 * 60) {
            duration = duration / 40;
            return duration + "s";
        } else if (duration > 10 * 60) {
            duration = duration / 30;
            return duration + "s";
        } else if (duration > 5 * 60) {
            duration = duration / 25;
            return duration + "s";
        } else if (duration > 3 * 60) {
            duration = duration / 20;
            return duration + "s";
        } else if (duration > 60) {
            duration = duration / 15;
            return duration + "s";
        } else if (duration > 10) {
            duration = duration / 10;
            return duration + "s";
        } else {
            return "";
        }
    }

    /**
     * 获取trace日志
     *
     * @param logQuery
     * @return
     */
    @Override
    public TraceLogDTO getTraceLog(TraceLogQuery logQuery) throws IOException {
        try {
            return traceLog.getTraceLog(logQuery.getTraceId(), "");
        } catch (Exception e) {
            log.error("日志查询错误，查询trace日志报错:[{}], logQuery:[{}], user:[{}]", e, logQuery, MoneUserContext.getCurrentUser());
            return TraceLogDTO.emptyData();
        }
    }

    /**
     * 获取机房内trace日志
     *
     * @param regionTraceLogQuery
     * @return
     */
    @Override
    public Result<TraceLogDTO> queryRegionTraceLog(RegionTraceLogQuery regionTraceLogQuery) throws IOException {
        return Result.success(traceLog.getTraceLog(regionTraceLogQuery.getTraceId(), regionTraceLogQuery.getRegion()));
    }

    @Override
    public Result<LogDTO> getDocContext(LogContextQuery logContextQuery) {
        SearchRequest searchRequest = null;
        try {
            if (searchLog.isLegalParam(logContextQuery) == false) {
                return Result.failParam("必要参数缺失");
            }
            MilogLogStoreDO logStore = logstoreDao.getByName(logContextQuery.getLogstore());
            if (logStore.getEsClusterId() == null || StringUtils.isEmpty(logStore.getEsIndex())) {
                return Result.failParam("store 配置异常");
            }
            EsService esService = esCluster.getEsService(logStore.getEsClusterId());
            String esIndexName = logStore.getEsIndex();
            List<String> keyList = getKeyList(logStore.getKeyList(), logStore.getColumnTypeList());
            LogDTO dto = new LogDTO();
            List<LogDataDTO> logDataList = new ArrayList<>();
            int times = 1, pageSize = logContextQuery.getPageSize();
            Long lineNumberSearchAfter = logContextQuery.getLineNumber();
            List<Integer> logOrder = new ArrayList<>();
            logOrder.add(logContextQuery.getType());
            if (0 == logContextQuery.getType()) {
                times = 2;
                pageSize = pageSize / 2;
                logOrder.remove(0);
                logOrder.add(2);
                logOrder.add(1);
            }
            for (int t = 0; t < times; t++) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                boolQueryBuilder.filter(QueryBuilders.termQuery(LogParser.esKeyMap_logip, logContextQuery.getIp()));
                boolQueryBuilder.filter(QueryBuilders.termQuery(LogParser.esKyeMap_fileName, logContextQuery.getFileName()));
                SearchSourceBuilder builder = new SearchSourceBuilder();
                builder.query(boolQueryBuilder);
                if (1 == logOrder.get(t)) {
                    // 1-after
                    builder.sort(LogParser.esKeyMap_timestamp, ASC);
                    builder.sort(LogParser.esKeyMap_lineNumber, ASC);
                } else if (2 == logOrder.get(t)) {
                    // 2-before
                    builder.sort(LogParser.esKeyMap_timestamp, DESC);
                    builder.sort(LogParser.esKeyMap_lineNumber, DESC);
                }
                if (0 == logContextQuery.getType() && 2 == logOrder.get(t)) {
                    builder.searchAfter(new Object[]{logContextQuery.getTimestamp(), lineNumberSearchAfter + 1});
                } else {
                    builder.searchAfter(new Object[]{logContextQuery.getTimestamp(), lineNumberSearchAfter});
                }
                builder.size(pageSize);
                searchRequest = new SearchRequest(esIndexName);
                searchRequest.source(builder);
                SearchResponse searchResponse;
                searchResponse = esService.search(searchRequest);
                SearchHit[] hits = searchResponse.getHits().getHits();
                if (hits == null || hits.length == 0) {
                    continue;
                }
                if (1 == logOrder.get(t)) {
                    for (int i = 0; i < hits.length; i++) {
                        logDataList.add(this.hit2DTO(hits[i], keyList));
                    }
                } else if (2 == logOrder.get(t)) {
                    for (int i = hits.length - 1; i >= 0; i--) {
                        logDataList.add(this.hit2DTO(hits[i], keyList));
                    }
                }
            }
            dto.setLogDataDTOList(logDataList);
            return Result.success(dto);
        } catch (Exception e) {
            log.error("日志查询错误，日志上下文报错:[{}], logContextQuery:[{}], searchRequest:[{}], user:[{}]", e, logContextQuery, searchRequest, MoneUserContext.getCurrentUser());
            return Result.failParam("系统错误，请重试");
        }
    }

    private LogDataDTO hit2DTO(SearchHit hit, List<String> keyList) {
        LogDataDTO logData = new LogDataDTO();
        Map<String, Object> ferry = hit.getSourceAsMap();
        logData.setValue(LogParser.esKeyMap_timestamp, ferry.get(LogParser.esKeyMap_timestamp));
        for (String key : keyList) {
            if (!hidenFiledSet.contains(key)) {
                logData.setValue(key, ferry.get(key));
            }
        }
        logData.setIp(ferry.get(LogParser.esKeyMap_logip) == null ? "" : String.valueOf(ferry.get(LogParser.esKeyMap_logip)));
        logData.setFileName(ferry.get(LogParser.esKyeMap_fileName) == null ? "" : String.valueOf(ferry.get(LogParser.esKyeMap_fileName)));
        logData.setLineNumber(ferry.get(LogParser.esKeyMap_lineNumber) == null ? "" : String.valueOf(ferry.get(LogParser.esKeyMap_lineNumber)));
        logData.setTimestamp(ferry.get(LogParser.esKeyMap_timestamp) == null ? "" : String.valueOf(ferry.get(LogParser.esKeyMap_timestamp)));
        logData.setLogOfString(new Gson().toJson(logData.getLogOfKV()));
        return logData;
    }

    public void logExport(LogQuery logQuery) throws Exception {
        // 生成excel
        int maxLogNum = 10000;
        logQuery.setPageSize(maxLogNum);
        Result<LogDTO> logDTOResult = this.logQuery(logQuery);
        List<Map<String, Object>> exportData =
                logDTOResult.getCode() != CommonError.Success.getCode()
                        || logDTOResult.getData().getLogDataDTOList() == null
                        || logDTOResult.getData().getLogDataDTOList().isEmpty() ?
                        null : logDTOResult.getData().getLogDataDTOList().stream().map(LogDataDTO::getLogOfKV).collect(Collectors.toList());
        HSSFWorkbook excel = ExportExcel.HSSFWorkbook4Map(exportData, generateTitle(logQuery));
        // 下载
        String fileName = String.format("%s_log.xls", logQuery.getLogstore());
        searchLog.downLogFile(excel, fileName);
    }

    private String generateTitle(LogQuery logQuery) {
        return String.format("%s日志，搜索词:[%s]，时间范围%d-%d", logQuery.getLogstore(), logQuery.getFullTextSearch() == null ? "" : logQuery.getFullTextSearch(), logQuery.getStartTime(), logQuery.getEndTime());
    }
}
