package com.xiaomi.mone.log.manager.service.impl;

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
import com.xiaomi.mone.log.manager.service.LogQueryService;
import com.xiaomi.mone.log.parse.LogParser;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import run.mone.excel.ExportExcel;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.search.sort.SortOrder.ASC;
import static org.elasticsearch.search.sort.SortOrder.DESC;

@Slf4j
@Service
@com.xiaomi.youpin.docean.plugin.dubbo.anno.Service(interfaceClass = LogDataService.class)
public class LogQueryServiceImpl implements LogQueryService, LogDataService {

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
    public Result<LogDTO> logQuery(LogQuery logQuery) throws Exception {
        SearchRequest searchRequest = null;
        try {
            MilogLogStoreDO milogLogstoreDO = logstoreDao.getByName(logQuery.getLogstore());
            if (milogLogstoreDO == null) {
                log.warn("[EsDataService.logQuery] not find logstore:[{}]", logQuery.getLogstore());
                return Result.failParam("找不到[" + logQuery.getLogstore() + "]对应的数据");
            }
            EsService esService = esCluster.getEsService(milogLogstoreDO.getEsClusterId());
            String esIndexName = milogLogstoreDO.getEsIndex();
            if (esService == null || StringUtils.isEmpty(esIndexName)) {
                log.warn("[EsDataService.logQuery] logstroe:[{}]配置异常", logQuery.getLogstore());
                return Result.failParam("logstroe配置异常");
            }
            List<String> keyList = getKeyList(milogLogstoreDO);
            // 构建查询参数
            BoolQueryBuilder boolQueryBuilder = searchLog.getQueryBuilder(logQuery, keyList);
            SearchSourceBuilder builder = new SearchSourceBuilder();
            builder.query(boolQueryBuilder);
            LogDTO dto = new LogDTO();
            // 统计
            CountRequest countRequest = new CountRequest();
            countRequest.indices(esIndexName);
            countRequest.source(builder);
            Long total = esService.count(countRequest);
            dto.setTotal(total);
            // 查询
            builder.sort(logQuery.getSortKey(), logQuery.getAsc() ? ASC : DESC);
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
            SearchResponse searchResponse = esService.search(searchRequest);
            SearchHit[] hits = searchResponse.getHits().getHits();
            if (hits == null || hits.length == 0) {
                return Result.success(dto);
            }
            List<LogDataDTO> logDataList = new ArrayList<>();
            LogDataDTO logData;
            for (SearchHit hit : hits) {
                logData = hit2DTO(hit, keyList);
                // 封装高亮
                Map<String, Object> highlinghtMap = new HashMap<>();
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields != null && !highlightFields.isEmpty()) {
                    Collection<HighlightField> HighlightFieldCollection = highlightFields.values();
                    for (HighlightField highlightField : HighlightFieldCollection) {
                        highlinghtMap.put(highlightField.getName(), highlightField.getFragments()[0].string());
                    }
                }
                logData.setHighlight(highlinghtMap);
                logDataList.add(logData);
            }
            dto.setThisSortValue(hits[hits.length - 1].getSortValues());
            dto.setLogDataDTOList(logDataList);
            return Result.success(dto);
        } catch (Exception e) {
            log.error("日志查询错误，日志搜索报错:[{}],logQuery:[{}],searchRequest:[{}],user:[{}]", e, logQuery, searchRequest, MoneUserContext.getCurrentUser());
            return Result.failParam("系统错误，请重试");
        }
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
            EsStatisticResult ret = new EsStatisticResult();
            ret.setName(constractEsStatisticRet(logQuery));
            MilogLogStoreDO logstore = logstoreDao.getByName(logQuery.getLogstore());
            if (logstore == null) {
                return new Result<>(CommonError.UnknownError.getCode(), "not found logstore", null);
            }
            List<String> keyList = this.getKeyList(logstore);
            // get interval
            String interval = esHistogramInterval(logQuery.getEndTime() - logQuery.getStartTime());
            EsService esService = esCluster.getEsService(logstore.getEsClusterId());
            String esIndex = logstore.getEsIndex();
            if (esService == null || StringUtils.isEmpty(esIndex)) {
                return Result.failParam("logstore或tail配置异常");
            }
            if (!StringUtils.isEmpty(interval)) {
                BoolQueryBuilder builder = searchLog.getQueryBuilder(logQuery, keyList);
                EsClient.EsRet esRet = esService.dateHistogram(esIndex, interval, logQuery.getStartTime(), logQuery.getEndTime(), builder);
                ret.setCounts(esRet.getCounts());
                ret.setTimestamps(esRet.getTimestamps());
                return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
            } else {
                return new Result<>(CommonError.UnknownError.getCode(), "The minimum time interval is 10s", null);
            }
        } catch (Exception e) {
            log.error("日志查询错误，日志柱状图统计报错[{}],logQuery:[{}],user:[{}]", e, logQuery, MoneUserContext.getCurrentUser());
            return Result.failParam("系统错误，请重试");
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
            MilogLogStoreDO milogLogstoreDO = logstoreDao.getByName(logContextQuery.getLogstore());
            if (milogLogstoreDO.getEsClusterId() == null || StringUtils.isEmpty(milogLogstoreDO.getEsIndex())) {
                return Result.failParam("store 配置异常");
            }
            EsService esService = esCluster.getEsService(milogLogstoreDO.getEsClusterId());
            String esIndexName = milogLogstoreDO.getEsIndex();
            List<String> keyList = this.getKeyList(milogLogstoreDO);
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

    private List<String> getKeyList(MilogLogStoreDO milogLogstoreDO) {
        String[] keyDescripArray = milogLogstoreDO.getKeyList().split(",");
        String[] keyTypeArray = milogLogstoreDO.getColumnTypeList().split(",");
        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < keyDescripArray.length; i++) {
            if (!"keyword".equals(keyTypeArray[i]) && !"text".equals(keyTypeArray[i])) {
                continue;
            }
            keyList.add(keyDescripArray[i].split(":")[0]);
        }
        return keyList;
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
