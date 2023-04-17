package com.xiaomi.mone.monitor.service.es;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.xiaomi.mone.es.EsClient;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.helper.ProjectHelper;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.middleware.DbInstanceQuery;
import com.xiaomi.mone.monitor.service.model.middleware.MiddleType;
import com.xiaomi.mone.monitor.service.model.middleware.MiddlewareInstanceInfo;
import com.xiaomi.mone.monitor.service.model.prometheus.EsIndexDataType;
import com.xiaomi.mone.monitor.service.model.prometheus.Metric;
import com.xiaomi.mone.monitor.service.model.prometheus.MetricDetail;
import com.xiaomi.mone.monitor.service.model.prometheus.MetricDetailQuery;
import com.xiaomi.mone.monitor.service.prometheus.PrometheusService;
import com.xiaomi.mone.monitor.bo.PlatFormType;
import com.xiaomi.mone.monitor.bo.PlatForm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gaoxihui
 * @date 2021/8/31 5:10 下午
 */
@Slf4j
@Service
public class EsService {

    @NacosValue(value = "${es.address}",autoRefreshed = true)
    private String esAddress;

    @NacosValue(value = "${es.username}",autoRefreshed = true)
    private String esUserName;

    @NacosValue("${es.password}")
    private String esPassWord;

    @Value("${es.cloud.platform.address:novalue}")
    private String cloudPlatformAddress;

    @Value("${es.cloud.platform.username:novalue}")
    private String cloudPlatformUser;

    @NacosValue("${es.cloud.platform.password:novalue}")
    private String cloudPlatformPwd;

    private EsClient cloudPlatformClient;

    @Value("${es.middleware.info.index:novalue}")
    String middlewareIndex;

    @Value("${es.query.timeout:1000}")
    private Long esQueryTimeOut;

    private EsClient esClient;

    @Autowired
    private ProjectHelper projectHelper;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

    @Autowired
    PrometheusService prometheusService;

    @PostConstruct
    public void init() {

        //todo esClient 扩展为不同实现

        esClient = new EsClient(esAddress,esUserName,esPassWord);

        cloudPlatformClient = new EsClient(esAddress,esUserName,esPassWord);
    }

    public EsClient getEsClient() {
        return esClient;
    }

    public void setEsClient(EsClient esClient) {
        this.esClient = esClient;
    }


    public Result query(String index, MetricDetailQuery param, Integer page, Integer pageSize) throws IOException {


        Map<String, String> labels = param.convertEsParam();

        if(StringUtils.isEmpty(index)){
            log.error("EsService.query error! esIndex is empty!");
            return null;
        }

        if(page == null || page.intValue() == 0){
            page = 1;
        }
        if(pageSize == null || pageSize.intValue() == 0){
            pageSize = 100;
        }


        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder sqb = new SearchSourceBuilder();

        if(!CollectionUtils.isEmpty(labels)){

            BoolQueryBuilder qb = QueryBuilders.boolQuery();
            Set<Map.Entry<String, String>> entries = labels.entrySet();
            for(Map.Entry<String, String> entry : entries){
                if(org.apache.commons.lang3.StringUtils.isBlank(entry.getValue())){
                    continue;
                }

                if(entry.getKey().equals("url") && labels.get("type").equals("mysql")){
                    WildcardQueryBuilder sqlqb = QueryBuilders.wildcardQuery(entry.getKey(),entry.getValue() + "*");
                    qb.must(sqlqb);
                    continue;
                }

                MatchPhraseQueryBuilder mpq = QueryBuilders.matchPhraseQuery(entry.getKey(),entry.getValue());
                qb.must(mpq);

            }

            if(param.getStartTime()!=null && param.getEndTime()!=null){

                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("timestamp").from(param.getStartTime()).to(param.getEndTime());

                qb.must(rangeQueryBuilder);

            }


            sqb.query(qb);
        }


        CountRequest countRequest = new CountRequest(index);
        countRequest.source(sqb);
        Long count = PlatFormType.isCodeBlondToPlatForm(param.getAppSource(), PlatForm.cloud) ? cloudPlatformClient.count(countRequest) : esClient.count(countRequest);

        sqb.from((page-1)*pageSize).size(pageSize).timeout(new TimeValue(esQueryTimeOut));
        sqb = sqb.sort("timestamp", SortOrder.DESC);

        request.source(sqb);
        SearchResponse sr = PlatFormType.isCodeBlondToPlatForm(param.getAppSource(), PlatForm.cloud) ? cloudPlatformClient.search(request) :esClient.search(request);

        log.info("Es query index : {},labels : {}, result : {}" , index,labels,sr);

        SearchHit[] results = sr.getHits().getHits();

        PageData pd = new PageData();
        pd.setPage(page);
        pd.setPageSize(pageSize);
        pd.setTotal(count);

        String viewType = param.getType() == null ? "ERROR-TYPE" : param.getType();

        String methodName = (EsIndexDataType.http_client.name().equals(param.getType())
                ||EsIndexDataType.http.name().equals(param.getType())
                ||EsIndexDataType.dubbo_consumer.name().equals(param.getType())
                ||EsIndexDataType.dubbo_provider.name().equals(param.getType())
                ||EsIndexDataType.grpc_client.name().equals(param.getType())
                ||EsIndexDataType.grpc_server.name().equals(param.getType())
                ||EsIndexDataType.thrift_client.name().equals(param.getType())
                ||EsIndexDataType.thrift_server.name().equals(param.getType())
                ||EsIndexDataType.apus_client.name().equals(param.getType())
                ||EsIndexDataType.apus_server.name().equals(param.getType())
                ||EsIndexDataType.redis.name().equals(param.getType())
                ) ?
                param.getMethodName()
                    : (EsIndexDataType.mysql.name().equals(param.getType())||
                       EsIndexDataType.oracle.name().equals(param.getType()) ||
                       EsIndexDataType.elasticsearch.name().equals(param.getType())) ?
                       param.getSqlMethod() : "NO-Data";


        Map map = new HashMap();
        map.put("projectName",param.getProjectName());
        map.put("bisType",viewType);
        map.put("serverIp",param.getServerIp());
        map.put("methodName",methodName);
        map.put("totalCount",count);
        map.put("serviceName",param.getServiceName());
        map.put("area",param.getArea());
        map.put("serverEnv",param.getServerEnv());
        if(EsIndexDataType.mysql.name().equals(param.getType()) || EsIndexDataType.oracle.name().equals(param.getType())){
            map.put("sql",param.getSql());
            map.put("dataSource",param.getDataSource());
        }


        if(results == null || results.length == 0){
            pd.setSummary(map);
            return Result.success(pd);
        }

        List<MetricDetail> result = new ArrayList<>();
        for(SearchHit hit : results){
            String sourceAsString = hit.getSourceAsString();
            if (!StringUtils.isEmpty(sourceAsString)) {
                MetricDetail metricDetail = new Gson().fromJson(sourceAsString, MetricDetail.class);

                if(!StringUtils.isEmpty(metricDetail.getTimestamp())){
                    metricDetail.setCreateTime(Long.valueOf(metricDetail.getTimestamp()));
                }
                result.add(metricDetail);
            }
        }


        /**
         * lastCreateTime
         */
        String lastCreateTime = "";
        if(!CollectionUtils.isEmpty(result)){
            MetricDetail metricDetail = result.get(result.size() - 1);
            lastCreateTime = metricDetail.getTimestamp();
        }

        if(!StringUtils.isEmpty(lastCreateTime)){
            map.put("lastCreateTime",Long.valueOf(lastCreateTime));
        }

        /**
         * 异常类型的可用率计算
         */

        if("timeout".equals(param.getErrorType())){

            String projectName = param.getProjectId() + "_" + param.getProjectName();
            Map proQLLabels = param.convertPrometheusParam();


            double rate = 100d;
            if("http".equals(param.getType())){
                rate = caculateUseRate("aopTotalMethodCount", "httpError", proQLLabels, projectName, param.getStartTime(), param.getEndTime(), lastCreateTime);
            }
            if("http_client".equals(param.getType())){
                rate = caculateUseRate("aopClientTotalMethodCount", "httpClientError", proQLLabels, projectName, param.getStartTime(), param.getEndTime(), lastCreateTime);
            }
            if("dubbo_consumer".equals(param.getType())){
                rate = caculateUseRate("dubboBisTotalCount", "dubboConsumerError", proQLLabels, projectName, param.getStartTime(), param.getEndTime(), lastCreateTime);
            }
            if("dubbo_provider".equals(param.getType())){
                rate = caculateUseRate("dubboInterfaceCalledCount", "dubboProviderError", proQLLabels, projectName, param.getStartTime(), param.getEndTime(), lastCreateTime);
            }
            if("mysql".equals(param.getType())){
                rate = caculateUseRate("sqlTotalCount", "dbError", proQLLabels, projectName, param.getStartTime(), param.getEndTime(), lastCreateTime);
            }

            map.put("availability",rate);

        }

        if ("error".equals(param.getErrorType()) && projectHelper.accessLogSys(param.getProjectName(), param.getProjectId(), param.getAppSource())) {
            map.put("access_log", "1");
        }
        pd.setSummary(map);

        pd.setList(result);
        return Result.success(pd);
    }

    private double caculateUseRate(String totalMetric,String errorMetric,Map labels,String projectName,Long startTime,Long endTime,String lastTime){

        if(endTime == null){
            /**
             * 前端没有传时间参数兼容，默认当前时间向前3h
             */
            log.info("caculateUseRate Param Error! endTime is null!projectName:{},errorMetric:{},",projectName,errorMetric);
            endTime = System.currentTimeMillis();
            startTime = endTime - 3 * 60 * 60 * 1000;
        }

        Long durarionSecond = (endTime - startTime)/1000;
        String durarion = (endTime - startTime)/1000 + "s";
        Result<PageData> total = prometheusService.queryIncrease(totalMetric, labels, projectName, "_total", startTime, endTime, durarionSecond, durarion);

        //没有总数，返回100%
        if(total.getData() == null){
            return 100d;
        }

        //没有总数，返回100%
        List<Metric> result = (List<Metric>) total.getData().getList();
        if(CollectionUtils.isEmpty(result)){
            return 100d;
        }

        Metric metric = result.get(0);
        double valueTotal = metric.getValue();

        Result<PageData> errors = prometheusService.queryIncrease(errorMetric, labels, projectName, "_total", startTime, endTime, null, null);

        //没有错误，返回100%
        if(errors.getData() == null){
            return 100d;
        }

        //没有错误数，返回100%
        List<Metric> errorMetrics = (List<Metric>) errors.getData().getList();
        if(CollectionUtils.isEmpty(errorMetrics)){
            return 100d;
        }

        Metric error = errorMetrics.get(0);
        double valueError = error.getValue();

        return (valueTotal - valueError)/valueTotal;

    }

    public Result queryMiddlewareInstance(DbInstanceQuery param,Integer page, Integer pageSize) throws IOException {


        if(StringUtils.isEmpty(middlewareIndex)){
            log.error("EsService.query error! esIndex is empty!");
            return null;
        }

        if(page == null || page.intValue() == 0){
            page = 1;
        }
        if(pageSize == null || pageSize.intValue() == 0){
            pageSize = 100;
        }

        SearchRequest request = new SearchRequest(middlewareIndex);

        SearchSourceBuilder sqb = new SearchSourceBuilder();

        if(MiddleType.db.name().equals(param.getType())){
            sqb.collapse(new CollapseBuilder("appName"));
            sqb.collapse(new CollapseBuilder("userName"));
            sqb.collapse(new CollapseBuilder("password"));
            sqb.collapse(new CollapseBuilder("domainPort"));
            sqb.collapse(new CollapseBuilder("dataBaseName"));
        }

        if(MiddleType.redis.name().equals(param.getType())){
            sqb.collapse(new CollapseBuilder("appName"));
            sqb.collapse(new CollapseBuilder("password"));
            sqb.collapse(new CollapseBuilder("domainPort"));
        }


        Map<String,String> esParam =  param.convertEsParam();
        if(!CollectionUtils.isEmpty(esParam)){

            BoolQueryBuilder qb = QueryBuilders.boolQuery();
            Set<Map.Entry<String, String>> entries = esParam.entrySet();
            for(Map.Entry<String, String> entry : entries){
                if(StringUtils.isEmpty(entry.getValue())){
                    continue;
                }
                MatchPhraseQueryBuilder mpq = QueryBuilders.matchPhraseQuery(entry.getKey(),entry.getValue());
                qb.must(mpq);
            }

            sqb.query(qb);
        }


        CountRequest countRequest = new CountRequest(middlewareIndex);
        countRequest.source(sqb);
        Long count = esClient.count(countRequest);

        sqb.from((page-1)*pageSize).size(pageSize).timeout(new TimeValue(esQueryTimeOut));
        sqb = sqb.sort("timestamp", SortOrder.DESC);

        request.source(sqb);
        SearchResponse sr = esClient.search(request);

        log.info("Es queryMiddlewareInstance index : {},labels : {}, result : {}" , middlewareIndex,param,sr);

        SearchHit[] results = sr.getHits().getHits();

        PageData pd = new PageData();
        pd.setPage(page);
        pd.setPageSize(pageSize);
        pd.setTotal(count);

        if(results == null || results.length == 0){
            return Result.success(pd);
        }

        List<MiddlewareInstanceInfo> result = new ArrayList<>();
        for(SearchHit hit : results){
            String sourceAsString = hit.getSourceAsString();
            if (!StringUtils.isEmpty(sourceAsString)) {
                MiddlewareInstanceInfo instanceInfo = new Gson().fromJson(sourceAsString, MiddlewareInstanceInfo.class);
                result.add(instanceInfo);
            }
        }
        pd.setList(result);


        return  Result.success(pd);

    }
}
