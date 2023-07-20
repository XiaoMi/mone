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
package com.xiaomi.mone.log.manager.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xiaomi.mone.es.EsClient;
import com.xiaomi.mone.log.api.model.dto.TraceLogDTO;
import com.xiaomi.mone.log.api.model.vo.TraceLogQuery;
import com.xiaomi.mone.log.api.service.LogDataService;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.common.utils.ExportUtils;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.dao.MilogSpaceDao;
import com.xiaomi.mone.log.manager.domain.EsCluster;
import com.xiaomi.mone.log.manager.domain.SearchLog;
import com.xiaomi.mone.log.manager.domain.TraceLog;
import com.xiaomi.mone.log.manager.model.dto.EsStatisticResult;
import com.xiaomi.mone.log.manager.model.dto.LogDTO;
import com.xiaomi.mone.log.manager.model.dto.LogDataDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;
import com.xiaomi.mone.log.manager.model.vo.LogContextQuery;
import com.xiaomi.mone.log.manager.model.vo.LogQuery;
import com.xiaomi.mone.log.manager.model.vo.RegionTraceLogQuery;
import com.xiaomi.mone.log.manager.model.vo.TraceAppLogUrlQuery;
import com.xiaomi.mone.log.manager.service.EsDataBaseService;
import com.xiaomi.mone.log.manager.service.EsDataService;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionService;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionServiceFactory;
import com.xiaomi.mone.log.parse.LogParser;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import run.mone.excel.ExportExcel;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.GSON;
import static com.xiaomi.mone.log.manager.common.utils.ManagerUtil.getKeyColonPrefix;
import static com.xiaomi.mone.log.manager.common.utils.ManagerUtil.getKeyList;
import static org.elasticsearch.search.sort.SortOrder.ASC;
import static org.elasticsearch.search.sort.SortOrder.DESC;

@Slf4j
@Service
@com.xiaomi.youpin.docean.plugin.dubbo.anno.Service(interfaceClass = LogDataService.class)
public class EsDataServiceImpl implements EsDataService, LogDataService, EsDataBaseService {

    @Resource
    private MilogLogstoreDao logstoreDao;

    @Resource
    private MilogLogTailDao tailDao;

    @Resource
    private MilogSpaceDao spaceDao;

    @Resource
    private EsCluster esCluster;

    @Resource
    private TraceLog traceLog;

    @Resource
    private SearchLog searchLog;

    @Value(value = "$hera.url")
    private String heraUrl;

    @Reference(interfaceClass = LogDataService.class, group = "$dubbo.youpin.group", check = false, timeout = 5000)
    private LogDataService logDataService;

    private CommonExtensionService commonExtensionService;

    public void init() {
        commonExtensionService = CommonExtensionServiceFactory.getCommonExtensionService();
    }

    private Set<String> noHighLightSet = new HashSet<>();

    private Set<String> hidenFiledSet = new HashSet<>();

    {
        noHighLightSet.add("logstore");
//        noHighLightSet.add("logsource");
        noHighLightSet.add("tail");
//        noHighLightSet.add("timestamp");
//        noHighLightSet.add("linenumber");

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

        StopWatch stopWatch = new StopWatch("HERA-LOG-QUERY");
        SearchRequest searchRequest = null;
        try {
            MilogLogStoreDO milogLogstoreDO = logstoreDao.queryById(logQuery.getStoreId());
            if (milogLogstoreDO == null) {
                log.warn("[EsDataService.logQuery] not find logStore:[{}]", logQuery.getLogstore());
                return Result.failParam("找不到[" + logQuery.getLogstore() + "]对应的数据");
            }
            EsService esService = esCluster.getEsService(milogLogstoreDO.getEsClusterId());
            String esIndexName = commonExtensionService.getSearchIndex(logQuery.getStoreId(), milogLogstoreDO.getEsIndex());
            if (esService == null || StringUtils.isEmpty(esIndexName)) {
                log.warn("[EsDataService.logQuery] logStore:[{}]配置异常", logQuery.getLogstore());
                return Result.failParam("logStore配置异常");
            }
            List<String> keyList = getKeyList(milogLogstoreDO.getKeyList(), milogLogstoreDO.getColumnTypeList());
            // 构建查询参数
            BoolQueryBuilder boolQueryBuilder = searchLog.getQueryBuilder(logQuery, getKeyColonPrefix(milogLogstoreDO.getKeyList()));
            SearchSourceBuilder builder = assembleSearchSourceBuilder(logQuery, keyList, boolQueryBuilder);

            searchRequest = new SearchRequest(new String[]{esIndexName}, builder);
            // 查询
            stopWatch.start("search-query");
            SearchResponse searchResponse = esService.search(searchRequest);
            stopWatch.stop();
            LogDTO dto = new LogDTO();
            dto.setSourceBuilder(builder);
            if (stopWatch.getLastTaskTimeMillis() > 7 * 1000) {
                log.warn("##LONG-COST-QUERY##{} cost:{} ms, msg:{}", stopWatch.getLastTaskName(), stopWatch.getLastTaskTimeMillis());
            }

            //结果转换
            stopWatch.start("data-assemble");
            transformSearchResponse(searchResponse, dto, keyList);
            stopWatch.stop();

            if (stopWatch.getTotalTimeMillis() > 15 * 1000) {
                log.warn("##LONG-COST-QUERY##{} cost:{} ms, msg:{}", "gt15s", stopWatch.getTotalTimeMillis());
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

    private SearchSourceBuilder assembleSearchSourceBuilder(LogQuery logQuery, List<String> keyList, BoolQueryBuilder boolQueryBuilder) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(boolQueryBuilder);

        builder.sort(logQuery.getSortKey(), logQuery.getAsc() ? ASC : DESC);
        if (null != logQuery.getPage()) {
            builder.from((logQuery.getPage() - 1) * logQuery.getPageSize());
        }
        builder.size(logQuery.getPageSize());
        // 高亮
        builder.highlighter(getHighlightBuilder(keyList));
        builder.timeout(TimeValue.timeValueMinutes(2L));
        return builder;
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


    /**
     * 插入数据
     *
     * @param indexName
     * @param data
     * @return
     */
    @Override
    public void insertDoc(String indexName, Map<String, Object> data) throws IOException {
        EsService esService = esCluster.getEsService(null);
        esService.insertDoc(indexName, data);
    }

    @Override
    public Result<EsStatisticResult> EsStatistic(LogQuery logQuery) throws Exception {
        try {
            EsStatisticResult result = new EsStatisticResult();
            result.setName(constractEsStatisticRet(logQuery));
            MilogLogStoreDO logStore = logstoreDao.queryById(logQuery.getStoreId());
            if (logStore == null) {
                return new Result<>(CommonError.UnknownError.getCode(), "not found logstore", null);
            }
            // get interval
            String interval = searchLog.esHistogramInterval(logQuery.getEndTime() - logQuery.getStartTime());
            EsService esService = esCluster.getEsService(logStore.getEsClusterId());
            String esIndex = commonExtensionService.getSearchIndex(logStore.getId(), logStore.getEsIndex());
            if (esService == null || StringUtils.isEmpty(esIndex)) {
                return Result.failParam("logStore或tail配置异常");
            }
            if (!StringUtils.isEmpty(interval)) {
                BoolQueryBuilder queryBuilder = searchLog.getQueryBuilder(logQuery, getKeyColonPrefix(logStore.getKeyList()));
                String histogramField = commonExtensionService.queryDateHistogramField(logQuery.getStoreId());
                EsClient.EsRet esRet = esService.dateHistogram(esIndex, histogramField, interval, logQuery.getStartTime(), logQuery.getEndTime(), queryBuilder);
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

    public String constractEsStatisticRet(LogQuery logquery) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(logquery.getLogstore())) {
            sb.append("logstore:").append(logquery.getLogstore()).append(";");
        }
        if (!StringUtils.isEmpty(logquery.getFullTextSearch())) {
            sb.append("fullTextSearch:").append(logquery.getFullTextSearch()).append(";");
        }
        return sb.toString();
    }


    /**
     * 获取trace日志
     *
     * @param logQuery
     * @return
     */
    @Override
    public TraceLogDTO getTraceLog(TraceLogQuery logQuery) {
        try {
            log.info("getTraceLog,param data:{}", GSON.toJson(logQuery));
            return traceLog.getTraceLog(logQuery.getTraceId(), "", logQuery.getGenerationTime(), logQuery.getLevel());
        } catch (Exception e) {
            log.error("日志查询错误，查询trace日志报错, logQuery:[{}]", e, GSON.toJson(logQuery), e);
            return TraceLogDTO.emptyData();
        }
    }

    public Result<String> getTraceAppLogUrl(TraceAppLogUrlQuery query) {
        List<MilogLogTailDo> tailDoList = tailDao.queryByAppId(query.getAppId());
        if (null != query.getEnvId() && !CollectionUtils.isEmpty(tailDoList)) {
            tailDoList = tailDoList.stream().filter(logTailDo -> Objects.equals(query.getEnvId(), logTailDo.getEnvId())).collect(Collectors.toList());
        }
        if (tailDoList == null || tailDoList.isEmpty()) {
            return Result.failParam("应用未接入日志");
        }
        String tailName = "";
        for (MilogLogTailDo tail : tailDoList) {
            tailName += tail.getTail() + ",";
        }
        tailName = tailName.substring(0, tailName.length() - 1);
        Long storeId = tailDoList.get(0).getStoreId();
        MilogLogStoreDO storeDO = logstoreDao.queryById(storeId);
        MilogSpaceDO spaceDO = spaceDao.queryById(storeDO.getSpaceId());
        Long startTime = (query.getTimestamp() / 1000) - (1000 * 60 * 10);
        Long endTime = (query.getTimestamp() / 1000) + (1000 * 60 * 10);

        String url = String.format("%s/project-milog/user/space-tree?spaceId=%s&inputV=traceId:%s&storeId=%s&tailName=%s&type=search&startTime=%s&endTime=%s", heraUrl, spaceDO.getId(), query.getTraceId(), storeDO.getId(), tailName, startTime, endTime);
        return Result.success(url);
    }

    /**
     * 获取机房内trace日志
     *
     * @param regionTraceLogQuery
     * @return
     */
    @Override
    public Result<TraceLogDTO> queryRegionTraceLog(RegionTraceLogQuery regionTraceLogQuery) throws IOException {
        return Result.success(traceLog.getTraceLog(regionTraceLogQuery.getTraceId(), regionTraceLogQuery.getRegion(), "", ""));
    }

    /**
     * 获取trace日志
     *
     * @param logQuery
     * @return
     */
    public TraceLogDTO getTraceLogFromDubbo(TraceLogQuery logQuery) throws IOException {
        return logDataService.getTraceLog(logQuery);
    }

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
            List<String> keyList = getKeyList(milogLogstoreDO.getKeyList(), milogLogstoreDO.getColumnTypeList());
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
            log.error("日志查询错误，日志上下文报错, logContextQuery:[{}], searchRequest:[{}], user:[{}]", logContextQuery, searchRequest, MoneUserContext.getCurrentUser(), e);
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
        logData.setLogOfString(JSON.toJSONString(logData.getLogOfKV()));
        return logData;
    }


    public void logExport(LogQuery logQuery) throws Exception {
        // 生成excel
        int maxLogNum = 10000;
        logQuery.setPageSize(maxLogNum);
        Result<LogDTO> logDTOResult = this.logQuery(logQuery);
        List<Map<String, Object>> exportData = logDTOResult.getCode() != CommonError.Success.getCode() || logDTOResult.getData().getLogDataDTOList() == null || logDTOResult.getData().getLogDataDTOList().isEmpty() ? null : logDTOResult.getData().getLogDataDTOList().stream().map(logDataDto -> ExportUtils.SplitTooLongContent(logDataDto)).collect(Collectors.toList());
        HSSFWorkbook excel = ExportExcel.HSSFWorkbook4Map(exportData, generateTitle(logQuery));
        // 下载
        String fileName = String.format("%s_log.xls", logQuery.getLogstore());
        searchLog.downLogFile(excel, fileName);
    }

    private String generateTitle(LogQuery logQuery) {
        return String.format("%s日志，搜索词:[%s]，时间范围%d-%d", logQuery.getLogstore(), logQuery.getFullTextSearch() == null ? "" : logQuery.getFullTextSearch(), logQuery.getStartTime(), logQuery.getEndTime());
    }
}
