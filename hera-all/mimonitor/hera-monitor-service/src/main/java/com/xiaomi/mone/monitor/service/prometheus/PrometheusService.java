package com.xiaomi.mone.monitor.service.prometheus;

import com.google.gson.Gson;
import com.xiaomi.mone.monitor.dao.AppCapacityAutoAdjustDao;
import com.xiaomi.mone.monitor.dao.model.AppCapacityAutoAdjust;
import com.xiaomi.mone.monitor.service.kubernetes.CapacityAdjustMessageService;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.http.MoneSpec;
import com.xiaomi.mone.monitor.service.http.RestTemplateService;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.prometheus.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author gaoxihui
 * @date 2021/7/22 4:21 下午
 */
@Slf4j
@Service
public class PrometheusService {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private static final String URI_QUERY_MOMENT = "/api/v1/query";
    private static final String URI_QUERY_RANGE = "/api/v1/query_range";

    private static final String P_QUERY = "query";
    private static final String P_TIME = "time";
    private static final String P_STEP = "step";
    private static final String P_START = "start";
    private static final String P_END = "end";
    private static final String P_DEDUP = "dedup";
    private static final String P_PARTIAL_RESPONSE = "partial_response";

    private static final String UNDER_LINE = "_";

    private static final String METRIC_HERA_SIGN = "hera";

    private static final int YOUPIN_GROUP_ORACLE = 2;
    private static final int CHINA_GROUP_ORACLE = 0;
    private static final double LOAD_THRESHOLD_ORACLE = 0.7;

    private final Gson gson = new Gson();


    @Autowired
    RestTemplateService restTemplateService;

    @Autowired
    AppCapacityAutoAdjustDao appCapacityAutoAdjustDao;

    @Autowired
    CapacityAdjustMessageService capacityAdjustMessageService;

    @Value("${prometheus.url}")
    private String prometheusUrl;

    @Value("${server.type}")
    private String env;

    public Result<PageData> queryRange(String metric_, Map labels, String projectName, String metricSuffix, Long startTime, Long endTime, Long step, String op, double value) {

        log.info("PrometheusService.queryRange received param" +
                        " metric_ : {}, labels : {}, projectName : {}, metricSuffix : {}, startTime : {}, endTime: {}, step : {}, op : {},value : {} "
                , metric_, labels, projectName, metricSuffix, startTime, endTime, step, op, value);

        // 指标名称拼接
        String metric = completePromQL(metric_, labels, projectName, metricSuffix, op, value, null, null);
        log.info("PrometheusService.queryRange metric : {} ", metric);

        Map<String, Object> map = new HashMap<>();
        map.put(P_QUERY, metric);  //指标参数
        map.put(P_START, startTime);
        map.put(P_END, endTime);
        map.put(P_STEP, step);
        map.put(P_DEDUP, true);
        map.put(P_PARTIAL_RESPONSE, true);

        String data = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_RANGE), map);

        log.info("PrometheusService.queryRange " +
                        " metric : {}, labels : {}, projectName : {}, metricSuffix : {}, startTime : {}, endTime: {}, step : {}, op : {},value : {},result : {} "
                , metric, labels, projectName, metricSuffix, startTime, endTime, step, op, value, data);

        MetricResponse metricResult = new Gson().fromJson(data, MetricResponse.class);
        if (metricResult == null || !"success".equals(metricResult.getStatus())) {
            return Result.fail(ErrorCode.unknownError);
        }
        MetricData metricData = metricResult.getData();
        List<MetricDataSet> result = metricData.getResult();

        PageData pageData = new PageData();
        pageData.setTotal(CollectionUtils.isEmpty(result) ? 0l : result.size());

        List<Metric> metrics = convertMetric(result);
        pageData.setList(metrics);

        return Result.success(pageData);
    }


    public Result<PageData> queryRangeSumOverTime(String metric_, Map labels, String projectName, String metricSuffix, Long startTime, Long endTime, Long step, String duration) {

        String offset = null;
        Long offsetLong = System.currentTimeMillis() / 1000 - endTime;
        if (offsetLong > 0) {
            offset = new StringBuilder().append(offsetLong).append("s").toString();
        }

        endTime = System.currentTimeMillis() / 1000;

        // 指标名称拼接
        String metric = completePromQL(metric_, labels, projectName, metricSuffix, null, 0, duration, offset);

        String sumOverTimeFunc = sumSumOverTimeFunc(metric);
        log.info("PrometheusService.queryRangeSumOverTime sumOverTimeFunc : {} ", sumOverTimeFunc);

        Map<String, Object> map = new HashMap<>();
        map.put(P_QUERY, sumOverTimeFunc);  //指标参数
        map.put(P_TIME, endTime);
        map.put(P_START, startTime);
        map.put(P_END, endTime);
        map.put(P_STEP, step);
        map.put(P_DEDUP, true);
        map.put(P_PARTIAL_RESPONSE, true);

        String data = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_MOMENT), map);
        log.info("PrometheusService.queryRangeSumOverTime sumOverTimeFunc : {},startTime : {},endTime : {}, step : {}, result : {}"
                , sumOverTimeFunc, startTime, endTime, step, data);

        MetricResponseVector metricResult = new Gson().fromJson(data, MetricResponseVector.class);
        if (metricResult == null || !"success".equals(metricResult.getStatus())) {
            return Result.fail(ErrorCode.unknownError);
        }
        MetricDataVector metricData = metricResult.getData();
        List<MetricDataSetVector> result = metricData.getResult();

        PageData pageData = new PageData();
        List<Metric> metrics = convertValidMetric(result);

        pageData.setList(metrics);
        pageData.setTotal(CollectionUtils.isEmpty(metrics) ? 0l : metrics.size());

        return Result.success(pageData);

    }

    public Result<PageData> queryCountOverTime(String metric_, Map labels, String projectName, String metricSuffix, Long startTime, Long endTime, Long step, String duration) {

        String offset = null;
        Long offsetLong = System.currentTimeMillis() / 1000 - endTime;
        if (offsetLong > 0) {
            offset = new StringBuilder().append(offsetLong).append("s").toString();
        }

        // 指标名称拼接
        String metric = completePromQL(metric_, labels, projectName, metricSuffix, null, 0, duration, offset);

        String countOverTimeFunc = countOverTimeFunc(metric);
        log.info("PrometheusService.queryCountOverTime countOverTimeFunc : {} ", countOverTimeFunc);

        Map<String, Object> map = new HashMap<>();
        map.put(P_QUERY, countOverTimeFunc);  //指标参数
        map.put(P_TIME, endTime);
        map.put(P_START, startTime);
        map.put(P_END, endTime);
        map.put(P_STEP, step);
        map.put(P_DEDUP, true);
        map.put(P_PARTIAL_RESPONSE, true);

        String data = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_MOMENT), map);
        log.info("PrometheusService.queryRangeSumOverTime sumOverTimeFunc : {},startTime : {},endTime : {}, step : {}, result : {}"
                , countOverTimeFunc, startTime, endTime, step, data);

        MetricResponseVector metricResult = new Gson().fromJson(data, MetricResponseVector.class);
        if (metricResult == null || !"success".equals(metricResult.getStatus())) {
            return Result.fail(ErrorCode.unknownError);
        }
        MetricDataVector metricData = metricResult.getData();
        List<MetricDataSetVector> result = metricData.getResult();

        PageData pageData = new PageData();
        List<Metric> metrics = convertValidMetric(result);
        pageData.setList(metrics);
        pageData.setTotal(CollectionUtils.isEmpty(metrics) ? 0l : metrics.size());

        return Result.success(pageData);

    }

    public Result<PageData> queryIncrease(String metric_, Map labels, String projectName, String metricSuffix, Long startTime, Long endTime, Long step, String duration) {

        String offset = null;
        Long offsetLong = System.currentTimeMillis() / 1000 - endTime;
        if (offsetLong > 0) {
            offset = new StringBuilder().append(offsetLong).append("s").toString();
        }

        // 指标名称拼接
        String metric = completePromQL(metric_, labels, projectName, metricSuffix, null, 0, duration, offset);

        String increaseFunc = increaseFunc(metric);
        log.info("PrometheusService.queryIncrease increaseFunc : {} ", increaseFunc);

        Map<String, Object> map = new HashMap<>();
        map.put(P_QUERY, increaseFunc);  //指标参数
        map.put(P_TIME, endTime);
        map.put(P_START, startTime);
        map.put(P_END, endTime);
        map.put(P_STEP, step);
        map.put(P_DEDUP, true);
        map.put(P_PARTIAL_RESPONSE, true);

        String data = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_MOMENT), map);
        log.info("PrometheusService.queryIncrease increaseFunc : {},startTime : {},endTime : {}, step : {}, result : {}"
                , increaseFunc, startTime, endTime, step, data);

        MetricResponseVector metricResult = new Gson().fromJson(data, MetricResponseVector.class);
        if (metricResult == null || !"success".equals(metricResult.getStatus())) {
            return Result.fail(ErrorCode.unknownError);
        }
        MetricDataVector metricData = metricResult.getData();
        List<MetricDataSetVector> result = metricData.getResult();

        PageData pageData = new PageData();
        List<Metric> metrics = convertValidMetric(result);
        pageData.setList(metrics);
        pageData.setTotal(CollectionUtils.isEmpty(metrics) ? 0l : metrics.size());

        return Result.success(pageData);

    }

    public Result<PageData> queryByMetric(String metric) {

        Long time = System.currentTimeMillis() / 1000;
        Map<String, Object> map = new HashMap<>();
        map.put(P_QUERY, metric);
        map.put(P_TIME, time);
        map.put(P_DEDUP, true);
        map.put(P_PARTIAL_RESPONSE, true);

        String data = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_MOMENT), map);
        log.info("PrometheusService.queryByMetric metric : {}, result : {}"
                , metric, data);

        MetricResponseVector metricResult = new Gson().fromJson(data, MetricResponseVector.class);
        if (metricResult == null || !"success".equals(metricResult.getStatus())) {
            return Result.fail(ErrorCode.unknownError);
        }
        MetricDataVector metricData = metricResult.getData();
        List<MetricDataSetVector> result = metricData.getResult();

        PageData pageData = new PageData();
        List<Metric> metrics = convertValidMetric(result);
        pageData.setList(metrics);
        pageData.setTotal(CollectionUtils.isEmpty(metrics) ? 0L : metrics.size());

        return Result.success(pageData);

    }

    private List<Metric> convertMetric(List<MetricDataSet> result) {
        List<Metric> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(result)) {
            for (MetricDataSet metricDataSet : result) {
                Metric metric = metricDataSet.getMetric();

                try {
                    List<List<Long>> values = metricDataSet.getValues();
                    List<Long> longs = values.get(values.size() - 1);
                    Long time = longs.get(0);
                    String lastCreateTime = formatDate(time * 1000);
                    Long cost = longs.get(1);
                    metric.setLastCreateTime(lastCreateTime);
                    metric.setValue(cost);
                } catch (Exception e) {
                    log.error("convertMetric error:{}", e.getMessage());
                }

                list.add(metric);

            }
        }

        return list;
    }

    private List<TeslaMetric> convertTeslaMetric(List<TeslaMetricDataSet> result) {
        List<TeslaMetric> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(result)) {
            for (TeslaMetricDataSet metricDataSet : result) {
                TeslaMetric metric = metricDataSet.getMetric();

                try {
                    List<List<Long>> values = metricDataSet.getValues();
                    List<Long> longs = values.get(values.size() - 1);
                    Long time = longs.get(0);
                    String lastCreateTime = formatDate(time * 1000);
                    Long cost = longs.get(1);
                    //   metric.setLastCreateTime(lastCreateTime);
                    metric.setValue(cost);
                } catch (Exception e) {
                    log.error("convertMetric error:{}", e.getMessage());
                }

                list.add(metric);

            }
        }

        return list;
    }

    /**
     * 过滤有效的counter指标（value>0），并转换数据
     *
     * @param result
     * @return
     */
    private List<Metric> convertValidMetric(List<MetricDataSetVector> result) {
        List<Metric> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(result)) {
            for (MetricDataSetVector metricDataVector : result) {
                Metric metric = metricDataVector.getMetric();
                if (Double.valueOf(metricDataVector.getValue().get(1)).intValue() == 0) {
                    continue;
                }

                try {
                    List<String> values = metricDataVector.getValue();
                    Long time = Long.valueOf(values.get(0));
                    String lastCreateTime = formatDate(time * 1000);
                    double value = Double.valueOf(metricDataVector.getValue().get(1));
                    metric.setLastCreateTime(lastCreateTime);
                    metric.setValue(value);
                } catch (Exception e) {
                    log.error("convertMetric error:{}", e.getMessage());
                }

                list.add(metric);

            }
        }

        return list;
    }

    private String formatDate(Long date) {
        return simpleDateFormat.format(date);
    }

    public String queryRangeSum(String metric_, Map labels, String projectName, String metricSuffix, Long startTime, Long endTime, Long step, List groups) {

        // 指标名称拼接
        String metric = completePromQL(metric_, labels, projectName, metricSuffix, null, 0, null, null);

        String sumFunc = sumFunc(metric, groups);
        log.info("PrometheusService.queryRangeSum sumFunc : {} ", sumFunc);

        Map<String, Object> map = new HashMap<>();
        map.put(P_QUERY, sumFunc);  //指标参数
        map.put(P_START, startTime);
        map.put(P_END, endTime);
        map.put(P_STEP, step);
        map.put(P_DEDUP, true);
        map.put(P_PARTIAL_RESPONSE, true);

        String data = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_RANGE), map);
        log.info("PrometheusService.queryRangeSum sumFunc : {},startTime : {},endTime : {}, step : {}, result : {}"
                , sumFunc, startTime, endTime, step, data);
        return data;

    }


    /**
     * 按照指定的分组生成求和函数
     *
     * @param source
     * @param groups
     * @return
     */
    private String sumFunc(String source, List<String> groups) {

        StringBuilder sb = new StringBuilder();
        sb.append(" sum(");
        sb.append(source);
        sb.append(") ");

        if (!CollectionUtils.isEmpty(groups)) {
            String s = org.springframework.util.StringUtils.collectionToCommaDelimitedString(groups);
            sb.append(" by (").append(s).append(")");
        }
        return sb.toString();
    }

    /**
     * 按时间区间求和
     *
     * @param source
     * @return
     */
    private String sumOverTimeFunc(String source) {

        StringBuilder sb = new StringBuilder();
        sb.append(" sum_over_time(");
        sb.append(source);
        sb.append(") ");

        return sb.toString();
    }

    /**
     * 按时间区间求和后按serverIp聚合
     *
     * @param source
     * @return
     */
    private String sumSumOverTimeFunc(String source) {

        StringBuilder sb = new StringBuilder();
        sb.append("sum(sum_over_time(");
        sb.append(source);
        sb.append(")) by (serverIp,job,application,methodName,serviceName,dataSource,sqlMethod,sql,serverEnv,method) ");

        return sb.toString();
    }

    /**
     * 按时间区间count
     *
     * @param source
     * @return
     */
    private String countOverTimeFunc(String source) {

        StringBuilder sb = new StringBuilder();
        sb.append(" count_over_time(");
        sb.append(source);
        sb.append(") ");

        return sb.toString();
    }

    /**
     * 按时间区间查询计数
     *
     * @param source
     * @return
     */
    private String increaseFunc(String source) {

        StringBuilder sb = new StringBuilder();
        sb.append(" increase(");
        sb.append(source);
        sb.append(") ");

        return sb.toString();
    }

    /**
     * @param source       原始指标
     * @param labels       指标标签集-map方式传递
     * @param projectName  工程名称-拼接过id的项目名称：projectId_projectName
     * @param metricSuffix 指标后缀：_total/_count
     * @param op           操作运算符：eg: >,<,=
     * @param value        指标的value
     * @param duration     时间区间
     * @param offset       时间偏移
     * @return
     */
    public String completePromQL(String source, Map labels, String projectName, String metricSuffix, String op, double value, String duration, String offset) {

        if (org.apache.commons.lang3.StringUtils.isBlank(env) || "dev".equals(env)) {
            env = "staging";// dev使用staging的数据
        }
        //项目名称替换掉"-"为"_"
        projectName = projectName.replaceAll("-", "_");

        StringBuilder promQL = new StringBuilder(env)
//                .append(UNDER_LINE).append(projectName)
                //todo 兼容两个版本的变量 ：METRIC_JEAGER_SIGN
                .append(UNDER_LINE).append(METRIC_HERA_SIGN)
                .append(UNDER_LINE).append(source)
                .append(metricSuffix == null ? "" : metricSuffix);

        promQL.append("{");
        promQL.append("application='").append(projectName).append("',");

        //标签拼接
        if (!CollectionUtils.isEmpty(labels)) {

            Set<Map.Entry<String, String>> set = labels.entrySet();
            for (Map.Entry<String, String> entry : set) {
                if (org.apache.commons.lang3.StringUtils.isBlank(entry.getValue())) {
                    continue;
                }
                promQL.append(entry.getKey());
                promQL.append("=");
                promQL.append("'");
                promQL.append(entry.getValue());
                promQL.append("'");
                promQL.append(",");
            }

        }

        promQL.append("}");

        //比较运算
        if (StringUtils.isNotBlank(op)) {
            promQL.append(op).append(value);
        }

        //时间区间
        if (StringUtils.isNotBlank(duration)) {
            promQL.append("[").append(duration).append("]");
        }

        //时间偏移
        if (StringUtils.isNotBlank(offset)) {
            promQL.append(" offset ").append(offset);
        }
        return promQL.toString();
    }

    public String completeMetric(String source, Map includeLabels, Map exceptLabels, Integer projectId, String projectName, String metricSuffix, String duration, String offset) {

        if (org.apache.commons.lang3.StringUtils.isBlank(env) || "dev".equals(env)) {
            env = "staging";// dev使用staging的数据
        }
        //项目名称替换掉"-"为"_"
        projectName = projectName.replaceAll("-", "_");

        String applicationSign = new StringBuilder().append(projectId).append(UNDER_LINE).append(projectName).toString();

        StringBuilder promQL = new StringBuilder(env)
//                .append(UNDER_LINE).append(projectId)
//                .append(UNDER_LINE).append(projectName)
                //todo 兼容两个版本的变量 ：METRIC_JEAGER_SIGN
                .append(UNDER_LINE).append(METRIC_HERA_SIGN)
                .append(UNDER_LINE).append(source)
                .append(metricSuffix == null ? "" : metricSuffix);

        promQL.append("{");

        promQL.append("application='").append(applicationSign).append("',");

        StringBuilder labels = new StringBuilder();
        //包含标签拼接
        if (!CollectionUtils.isEmpty(includeLabels)) {

            Set<Map.Entry<String, String>> set = includeLabels.entrySet();
            for (Map.Entry<String, String> entry : set) {
                if (org.apache.commons.lang3.StringUtils.isBlank(entry.getValue())) {
                    continue;
                }
                labels.append(entry.getKey());
                labels.append("=~");
                labels.append("'");
                labels.append(".*").append(entry.getValue()).append(".*");
                labels.append("'");
                labels.append(",");
            }
        }

        if (!CollectionUtils.isEmpty(exceptLabels)) {

            Set<Map.Entry<String, String>> set = exceptLabels.entrySet();
            for (Map.Entry<String, String> entry : set) {
                if (org.apache.commons.lang3.StringUtils.isBlank(entry.getValue())) {
                    continue;
                }
                labels.append(entry.getKey());
                labels.append("!=~");
                labels.append("'");
                labels.append(".*").append(entry.getValue()).append(".*");
                labels.append("'");
                labels.append(",");
            }

        }

        String labelsV = labels.toString();
        if (labelsV.endsWith(",")) {
            labelsV = labelsV.substring(0, labelsV.length() - 1);
        }

        promQL.append(labelsV);

        promQL.append("}");


        //时间区间
        if (!StringUtils.isEmpty(duration)) {
            promQL.append("[").append(duration).append("]");
        }

        //时间偏移
        if (!StringUtils.isEmpty(offset)) {
            promQL.append(" offset ").append(offset);
        }
        return promQL.toString();
    }

    public String completeMetricForAlarm(String source, Map includeLabels, Map exceptLabels, Integer projectId, String projectName, String metricSuffix, String duration, String offset) {

        if (org.apache.commons.lang3.StringUtils.isBlank(env) || "dev".equals(env)) {
            env = "staging";// dev使用staging的数据
        }
        //项目名称替换掉"-"为"_"
        projectName = projectName.replaceAll("-", "_");

        String applicationSign = new StringBuilder().append(projectId).append(UNDER_LINE).append(projectName).toString();

        StringBuilder promQL = new StringBuilder(env)
//                .append(UNDER_LINE).append(projectId)
//                .append(UNDER_LINE).append(projectName)
                //todo 兼容两个版本的变量 ：METRIC_JEAGER_SIGN
                .append(UNDER_LINE).append(METRIC_HERA_SIGN)
                .append(UNDER_LINE).append(source)
                .append(metricSuffix == null ? "" : metricSuffix);

        promQL.append("{");
        promQL.append("application='").append(applicationSign).append("',");
        StringBuilder labels = new StringBuilder();
        //包含标签拼接
        if (!CollectionUtils.isEmpty(includeLabels)) {

            Set<Map.Entry<String, String>> set = includeLabels.entrySet();
            for (Map.Entry<String, String> entry : set) {
                if (org.apache.commons.lang3.StringUtils.isBlank(entry.getValue())) {
                    continue;
                }
                labels.append(entry.getKey());
                labels.append("=~");
                labels.append("'");
                labels.append(entry.getValue());
                labels.append("'");
                labels.append(",");
            }
        }

        if (!CollectionUtils.isEmpty(exceptLabels)) {

            Set<Map.Entry<String, String>> set = exceptLabels.entrySet();
            for (Map.Entry<String, String> entry : set) {
                if (org.apache.commons.lang3.StringUtils.isBlank(entry.getValue())) {
                    continue;
                }
                labels.append(entry.getKey());
                labels.append("!~");
                labels.append("'");
                labels.append(entry.getValue());
                labels.append("'");
                labels.append(",");
            }
        }

        String labelsV = labels.toString();
        if (labelsV.endsWith(",")) {
            labelsV = labelsV.substring(0, labelsV.length() - 1);
        }

        promQL.append(labelsV);

        promQL.append("}");

        //时间区间
        if (!StringUtils.isEmpty(duration)) {
            promQL.append("[").append(duration).append("]");
        }

        //时间偏移
        if (!StringUtils.isEmpty(offset)) {
            promQL.append(" offset ").append(offset);
        }
        return promQL.toString();
    }

    private String completeQueryUrl(String domain, String uri) {
        return new StringBuffer(domain)
                .append(uri).toString();
    }

    public Result<PageData> getTeslaError(String department, String area) {
        HashMap<String, String> labels = new HashMap<>();
        labels.put("job", getTeslaJob(department, area));
        String metrics = "errCode";
        String projectName = "tesla";
        String metricSuffix = "total";
        Long startTime = (System.currentTimeMillis() - 6 * 60 * 60 * 1000) / 1000L;
        Long endTime = System.currentTimeMillis() / 1000L;
        Long step = (endTime - startTime) / 2;
        String op = ">";
        double value = 0;
        return queryTeslaRange(metrics, labels, projectName, metricSuffix, startTime, endTime, step, op, value, department, area);
    }

    //根据不同部门、环境、内外网返回job标签的值
    private String getTeslaJob(String department, String area) {
        switch (department) {
            case "china":
                if ("dev".equals(env) || "staging".equals(env)) {
                    return "tesla-china-intranet";
                } else if ("online".equals(env)) {
                    if ("in".equals(area)) {
                        return "tesla-china-intranet";
                    } else if ("out".equals(area)) {
                        return "tesla-china";
                    }
                    return "";
                } else {
                    return "";
                }
            case "youpin":
                return "tesla-youpin";
            case "innovation":
                if ("dev".equals(env) || "staging".equals(env)) {
                    return "tesla-innovation-intranet";
                } else if ("online".equals(env)) {
                    if ("in".equals(area)) {
                        return "tesla-innovation-intranet";
                    } else if ("out".equals(area)) {
                        return "tesla-innovation";
                    }
                    return "";
                } else {
                    return "";
                }
            default:
                return "";
        }
    }

    //根据不同部门、环境、内外网返回tesla的env标识
    private String getTeslaEnv(String department, String area) {
        switch (department) {
            case "china":
                if ("dev".equals(env) || "staging".equals(env)) {
                    return "mistaging";
                } else if ("online".equals(env)) {
                    if ("in".equals(area)) {
                        return "miintranet";
                    } else if ("out".equals(area)) {
                        return "online";
                    }
                    return "";
                } else {
                    return "";
                }
            case "youpin":
                return "tesla-youpin";
            case "innovation":
                if ("dev".equals(env) || "staging".equals(env)) {
                    return "mistaging";
                } else if ("online".equals(env)) {
                    if ("in".equals(area)) {
                        return "miintranet";
                    } else if ("out".equals(area)) {
                        return "online";
                    }
                    return "";
                } else {
                    return "";
                }
            default:
                return "";
        }
    }

    public Result<PageData> queryTeslaRange(String metric_, Map labels, String projectName, String metricSuffix, Long startTime, Long endTime, Long step, String op, double value, String department, String area) {
        //拼接tesla的promQL
        String env = getTeslaEnv(department, area);
        String job = getTeslaJob(department, area);
        log.info("PrometheusService.queryTeslaRange env : {}, job : {}, department: {} ,area: {}"
                , env, job, department, area);
        if (StringUtils.isEmpty(env) || StringUtils.isEmpty(job)) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        String metric = "ceil(sum(increase(" + env + "_" + projectName + "_" + metric_ + "_" + metricSuffix + "{job=\"" + job + "\"}" + "[6h])) by (group,url,code))" + op + value;
        System.out.println("metrics is : " + metric);
        Map<String, Object> map = new HashMap<>();
        map.put(P_QUERY, metric);  //指标参数
        map.put(P_START, startTime);
        map.put(P_TIME, endTime);
        map.put(P_END, endTime);
        map.put(P_STEP, step);
        map.put(P_DEDUP, true);
        map.put(P_PARTIAL_RESPONSE, true);

        String data = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_RANGE), map);
        TeslaMetricResponse metricResult = new Gson().fromJson(data, TeslaMetricResponse.class);
        if (metricResult == null || !"success".equals(metricResult.getStatus())) {
            return Result.fail(ErrorCode.unknownError);
        }
        TeslaMetricData metricData = metricResult.getData();
        List<TeslaMetricDataSet> result = metricData.getResult();

        PageData pageData = new PageData();
        pageData.setTotal(CollectionUtils.isEmpty(result) ? 0l : result.size());

        List<TeslaMetric> metrics = convertTeslaMetric(result);
        pageData.setList(metrics);

        return Result.success(pageData);
    }

    public Result<PageData> queryTeslaMetric(String metric_, Map labels, Long startTime, Long endTime, String op, double value, Long step, String duration) {

        String offset = null;
        Long offsetLong = System.currentTimeMillis() / 1000 - endTime;
        if (offsetLong > 0) {
            offset = new StringBuilder().append(offsetLong).append("s").toString();
        }

        // 指标名称拼接
        String metric = completePromQLForTesla(metric_, labels, op, value, duration, offset);

        log.info("PrometheusService.queryTeslaMetric metric : {} ", metric);

        Map<String, Object> map = new HashMap<>();
        map.put(P_QUERY, metric);  //指标参数
        map.put(P_TIME, endTime);
        map.put(P_START, startTime);
        map.put(P_END, endTime);
        map.put(P_STEP, step);
        map.put(P_DEDUP, true);
        map.put(P_PARTIAL_RESPONSE, true);

        String data = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_MOMENT), map);
        log.info("PrometheusService.queryIncrease metric : {},startTime : {},endTime : {}, step : {}, result : {}"
                , metric, startTime, endTime, step, data);

        MetricResponseVector metricResult = new Gson().fromJson(data, MetricResponseVector.class);
        if (metricResult == null || !"success".equals(metricResult.getStatus())) {
            return Result.fail(ErrorCode.unknownError);
        }
        MetricDataVector metricData = metricResult.getData();
        List<MetricDataSetVector> result = metricData.getResult();

        PageData pageData = new PageData();
        List<Metric> metrics = convertValidMetric(result);
        pageData.setList(metrics);
        pageData.setTotal(CollectionUtils.isEmpty(metrics) ? 0l : metrics.size());

        return Result.success(pageData);

    }

    private String getLabels(Map<String, String> labels) {

        StringBuilder builder = new StringBuilder();

        if (!CollectionUtils.isEmpty(labels)) {
            Set<Map.Entry<String, String>> set = labels.entrySet();
            for (Map.Entry<String, String> entry : set) {
                if (org.apache.commons.lang3.StringUtils.isBlank(entry.getValue())) {
                    continue;
                }
                builder.append(entry.getKey());
                builder.append("=~");
                builder.append("'");
                builder.append(".*").append(entry.getValue()).append(".*");
                builder.append("'");
                builder.append(",");
            }
        }

        String result = builder.toString();

        return result.toString();
    }

    public String completePromQLForTesla(String source, Map labels, String op, double value, String duration, String offset) {

        if (org.apache.commons.lang3.StringUtils.isBlank(env) || "dev".equals(env)) {
            env = "staging";// dev使用staging的数据
        }

        StringBuilder builder = new StringBuilder();

        switch (source) {
            case "china_intranet_tesla_p99_time_cost":

                String metric = env.equals("staging") ? "mistaging_tesla_use_time_bucket" : "miintranet_tesla_use_time_bucket";

                builder.append("histogram_quantile(0.99,sum(rate(").append(metric).append("{");
                builder.append(getLabels(labels));
                builder.append("}");
                if (StringUtils.isNotBlank(duration)) {
                    builder.append("[").append(duration).append("]");
                }
                if (StringUtils.isNotBlank(offset)) {
                    builder.append("offset ").append(offset);
                }
                builder.append(")) by (le,system,ip,instance, group,url))").append(op).append(value);

            case "tesla_intranet_availability":

                String metric_err = env.equals("staging") ? "mistaging_tesla_errCode_total" : "mistaging_tesla_TotalCounter_total";
                String metric_total = env.equals("staging") ? "mistaging_tesla_errCode_total" : "miintranet_tesla_TotalCounter_total";

                builder.append("(1- (sum(increase(").append(metric_err).append("{");
                builder.append(getLabels(labels));
                builder.append("}");

                if (StringUtils.isNotBlank(duration)) {
                    builder.append("[").append(duration).append("]");
                }
                if (StringUtils.isNotBlank(offset)) {
                    builder.append("offset ").append(offset);
                }
                builder.append(")>0) ")
                        .append(" by (system,url,group,instance,ip) / sum(increase(").append(metric_total).append("{");

                builder.append(getLabels(labels));
                builder.append("}");

                if (StringUtils.isNotBlank(duration)) {
                    builder.append("[").append(duration).append("]");
                }
                if (StringUtils.isNotBlank(offset)) {
                    builder.append("offset ").append(offset);
                }
                builder.append(")>0) by (system,url,group,instance,ip))) * 100 ");

        }

        return builder.toString();
        /**
         * eg:time_cost_99
         * histogram_quantile(0.99,sum(rate(miintranet_tesla_use_time_bucket{}[1h])) by (le,system,ip,instance, group,url))>0
         */

    }

    public Result queryServiceQps(String serviceName, String type) {
        //指标名称替换
        String prometheusEnv = "staging";
        if ("online".equals(env)) {
            prometheusEnv = "online";
        }
        String avgMetric = "";
        String totalMetric = "";
        switch (type) {
            case "http":
                avgMetric = "clamp_min(sum(sum_over_time(" + prometheusEnv + "_jaeger_aopTotalMethodCount_total{application=\"" + serviceName + "\"}[30s])/30)  / count(count(sum_over_time(" + prometheusEnv + "_jaeger_aopTotalMethodCount_total{application=\"" + serviceName + "\"}[30s])/30) by(serverIp)),0)";
                totalMetric = "clamp_min(sum(sum(sum_over_time(" + prometheusEnv + "_jaeger_aopTotalMethodCount_total{application=\"" + serviceName + "\"}[30s])/30) by(serverIp)) by (application),0)";
                break;
            case "dubbo":
                avgMetric = "clamp_min(sum(sum_over_time(" + prometheusEnv + "_jaeger_dubboMethodCalledCount_total{application=\"" + serviceName + "\"}[30s])/30)  /  count(count(sum_over_time(" + prometheusEnv + "_jaeger_dubboMethodCalledCount_total{application=\"" + serviceName + "\"}[30s])/30) by(serverIp)),0)";
                totalMetric = "clamp_min(sum(sum(sum_over_time(" + prometheusEnv + "_jaeger_dubboMethodCalledCount_total{application=\"" + serviceName + "\"}[30s])/30) by(serverIp)) by (application),0)";
                break;
            default:
                return Result.fail(ErrorCode.UNKNOWN_TYPE);
        }
        log.info("PrometheusService.queryServiceQps avgmetric : {},totalmetric : {} ", avgMetric, totalMetric);
        String avgQps = queryQpsByPrometheus(avgMetric);
        String totalQps = queryQpsByPrometheus(totalMetric);
        ServiceQps serviceQps = new ServiceQps();
        serviceQps.setType(type);
        serviceQps.setAvgQps(avgQps);
        serviceQps.setTotalQps(totalQps);
        return Result.success(serviceQps);
    }

    private String queryQpsByPrometheus(String metric) {
        Map<String, Object> map = new HashMap<>();
        map.put(P_QUERY, metric);  //指标参数
        map.put(P_TIME, System.currentTimeMillis() / 1000L);
        try {
            String data = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_MOMENT), map);
            System.out.println(data);
            MetricResponseVector metricResult = new Gson().fromJson(data, MetricResponseVector.class);
            if (metricResult == null || !"success".equals(metricResult.getStatus())) {
                return "0";
            }
            return metricResult.getData().getResult().get(0).getValue().get(1);
        } catch (Exception e) {
            log.error("PrometheusService.queryQpsByPrometheus err :{}", e.toString());
            return "0";
        }
    }

    public Result<PageData> getServiceQps(String serviceName, String type) {
        return queryServiceQps(serviceName, type);
    }

    //根据传入的服务名，获取对应的该服务的service列表
    public Result queryDubboServiceList(String serviceName, String type,String startTime, String endTime) {
        //sum(sum_over_time(staging_jaeger_dubboProviderCount_count{application="221_maitian"}[30m]))by (serviceName)
        log.info("queryDubboServiceList serviceName:{},type :{},startTime:{},endTime:{}",serviceName,type,startTime,endTime);
        //指标名称替换
        String prometheusEnv = "staging";
        if ("online".equals(env)) {
            prometheusEnv = "online";
        }
        String query = "";
        switch (type) {
            case "http":
                query = "sum(sum_over_time(" + prometheusEnv + "_jaeger_aopTotalMethodCount_total{application=\"" + serviceName + "\"}[30s])) by (methodName)";
                break;
            case "dubboConsumer":
                query = "sum(sum_over_time("+ prometheusEnv +"_jaeger_dubboBisTotalCount_total{application=\"" + serviceName + "\"}[30s])) by (serviceName)";
                break;
            case "dubbo":
                query = "sum(sum_over_time(" + prometheusEnv + "_jaeger_dubboProviderCount_count{application=\"" + serviceName + "\"}[30s]))by (serviceName)";
                break;
            case "grpcServer":
                query = "sum(sum_over_time(" + prometheusEnv + "_jaeger_grpcServer_total{application=\"" + serviceName + "\"}[30s]))by (serviceName)";
                break;
            case "grpcClient":
                query = "sum(sum_over_time(" + prometheusEnv + "_jaeger_grpcClient_total{application=\"" + serviceName + "\"}[30s]))by (serviceName)";
                break;
            case "thriftServer":
                query = "sum(sum_over_time(" + prometheusEnv + "_jaeger_thriftServer_total{application=\"" + serviceName + "\"}[30s]))by (serviceName)";
                break;
            case "thriftClient":
                query = "sum(sum_over_time(" + prometheusEnv + "_jaeger_thriftClient_total{application=\"" + serviceName + "\"}[30s]))by (serviceName)";
                break;
            case "apusClient":
                query = "sum(sum_over_time(" + prometheusEnv + "_jaeger_apusClient_total{application=\"" + serviceName + "\"}[30s]))by (serviceName)";
                break;
            case "apusServer":
                query = "sum(sum_over_time(" + prometheusEnv + "_jaeger_apusServer_total{application=\"" + serviceName + "\"}[30s]))by (serviceName)";
                break;
            default:
                query = "sum(sum_over_time(" + prometheusEnv + "_jaeger_dubboProviderCount_count{application=\"" + serviceName + "\"}[30s]))by (serviceName)";
        }
        log.info("PrometheusService.queryDubboServiceList query : {}", query);
        return queryDubboServiceListByPrometheus(query, type,startTime,endTime);
        // return Result.success(0);
    }


    private Result queryDubboServiceListByPrometheus(String metric, String type,String startTime, String endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put(P_QUERY, metric);  //指标参数
     //   map.put(P_TIME, System.currentTimeMillis() / 1000L);
        //step 1h = 15 2h = 2* 15
        Long multi = (Long.parseLong(endTime) - Long.parseLong(startTime)) / 3600;
        if (multi < 1 ) {
            multi = 1L;
        }
        map.put(P_STEP,multi * 15);
        map.put(P_START,startTime);
        map.put(P_END,endTime);
        log.info("queryDubboServiceListByPrometheus map :{},url :{},promql :{}",gson.toJson(map),prometheusUrl + URI_QUERY_RANGE,metric);
        try {
            String data = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_RANGE), map);
            MetricResponseVector metricResult = new Gson().fromJson(data, MetricResponseVector.class);
            //System.out.println(metricResult);
            if (metricResult == null || !"success".equals(metricResult.getStatus())) {
                return Result.fail(ErrorCode.success);
            }
            List<MetricDataSetVector> resultData = metricResult.getData().getResult();
            if (!resultData.isEmpty()) {
                if ("http".equals(type)) {
                    return Result.success(resultData.stream().map(it -> it.getMetric().getMethodName()));
                }
                return Result.success(resultData.stream().map(it -> it.getMetric().getServiceName()));
            }
            return Result.fail(ErrorCode.success);
        } catch (Exception e) {
            log.error("PrometheusService.queryQpsByPrometheus err :{}", e.toString());
            return Result.fail(ErrorCode.success);
        }
    }

    public Result oracle(String mode, String type) {
        log.info("PrometheusService.oracle mode : {},type : {}", mode, type);
        switch (type) {
            case "load":
                return loadTypeOracle(mode);
            case "qps":
                return qpsTypeOracle(mode);
            default:
                return Result.fail(ErrorCode.UNKNOWN_TYPE);
        }
    }

    private Result loadTypeOracle(String mode) {
        //负载动态扩缩绒模式
        log.info("PrometheusService.loadTypeOracle mode : {}", mode);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 50, 5, TimeUnit.MINUTES, new LinkedBlockingQueue(100),
                (Runnable r) -> new Thread(r, "compute-execute-thread-v2"), new ThreadPoolExecutor.CallerRunsPolicy());
        AppCapacityAutoAdjust autoQuery = new AppCapacityAutoAdjust();
        autoQuery.setStatus(0);         //0表示开启状态
        autoQuery.setAutoCapacity(1);  //开启自动扩容的
        try {
            List<AppCapacityAutoAdjust> result = appCapacityAutoAdjustDao.query(autoQuery, null, null);
            //获取container字段，并且开始遍历查询prometheus
            result.stream().forEach(res -> {
                String container = res.getContainer();
                int pipelineId = res.getPipelineId();
                String query = "sum(container_spec_cpu_quota{system=\"mione\",container=\"" + container + "\",image != \"\"}) by (container)" +
                        " / sum(container_spec_cpu_period{system=\"mione\",container=\"" + container + "\",image != \"\"}) by (container)";
                executor.execute(() -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put(P_QUERY, query);  //指标参数
                    map.put(P_TIME, System.currentTimeMillis() / 1000L);
                    String data = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_MOMENT), map);
                    // System.out.println(data);
                    MetricResponseVector metricResult = new Gson().fromJson(data, MetricResponseVector.class);
                    if (metricResult == null || !"success".equals(metricResult.getStatus())) {
                        return;
                    }
                    List<MetricDataSetVector> metricSet = metricResult.getData().getResult();
                    //metricSet只有一条元素，因为container只有一个
                    int cpuCoreSize = Integer.parseInt(metricSet.get(0).getValue().get(1));
                    String loadQuery = "";
                    switch (mode) {
                        case "normal":
                            loadQuery = "avg(container_cpu_load_average_10s{system=\"mione\",image != \"\",container=\"" + container + "\"}) by (container,namespace) /1000";
                            break;
                        case "predict":
                            loadQuery = "";
                            break;
                        default:
                            loadQuery = "";
                    }
                    map.put(P_QUERY, loadQuery);  //指标参数
                    String loadData = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_MOMENT), map);
                    MetricResponseVector loadMetricResult = new Gson().fromJson(loadData, MetricResponseVector.class);
                    if (loadMetricResult == null || !"success".equals(loadMetricResult.getStatus())) {
                        return;
                    }
                    List<MetricDataSetVector> loadMetricSet = loadMetricResult.getData().getResult();
                    Double podRealLoad = Double.parseDouble(loadMetricSet.get(0).getValue().get(1));
                    //和负载阈值比较
                    if (cpuCoreSize * LOAD_THRESHOLD_ORACLE < podRealLoad) {
                        //eg .10cpu * 0.7 = 7 < 10 则要扩容
                        log.info("container : {} ,pipeline: {},需要扩容了 podRealLoad : {} ", container, pipelineId, podRealLoad);
                        //查询实例数量
                        String instanceQuery = "count(container_cpu_load_average_10s{system=\"mione\",image !=\"\",container=\"" + container + "\"}) by (pod) ";
                        map.put(P_QUERY, instanceQuery);
                        String instanceData = restTemplateService.getHttpM(completeQueryUrl(prometheusUrl, URI_QUERY_MOMENT), map);
                        MetricResponseVector instanceMetricResult = new Gson().fromJson(instanceData, MetricResponseVector.class);
                        if (instanceMetricResult == null || !"success".equals(instanceMetricResult.getStatus())) {
                            return;
                        }
                        List<MetricDataSetVector> instanceMetricSet = instanceMetricResult.getData().getResult();
                        int instanceNum = Integer.parseInt(instanceMetricSet.get(0).getValue().get(1));
                        MoneSpec moneSpec = new MoneSpec();
                        moneSpec.init();
                        moneSpec.setEnvID(pipelineId);
                        moneSpec.setNamespace(loadMetricSet.get(0).getMetric().getNamespace());
                        moneSpec.setContainer(loadMetricSet.get(0).getMetric().getContainer());
                        moneSpec.setReplicas(instanceNum);
                        moneSpec.setSetReplicas(CountExpectedInstance(instanceNum, "normal",res.getMaxInstance()));
                        //发送消息
                        capacityAdjustMessageService.product(moneSpec);
//                        capacityService.capacityAdjustWithRecord(moneSpec);
//                        AppCapacityAutoAdjust byId = appCapacityAutoAdjustDao.getById(res.getId());
//                        log.info("container : {} ,pipeline: {} 扩容mq为: {},queue size : {},AppCapacityAutoAdjust:{}",container, pipelineId, moneSpec.toString(),capacityAdjustMessageService.queueSize(),byId.toString());
                    } else {
                        log.info("container : {} ,pipeline: {},不需要扩容了 podRealLoad : {} ", container, pipelineId, podRealLoad);
                    }
                });
            });
        } catch (Exception e) {
            log.error("loadTypeOracle error : {}", e.toString());
        }
        return Result.success(0);
    }

    private Result qpsTypeOracle(String mode) {
        //QPS动态扩缩绒模式
        return Result.success("暂不支持!");
    }

    //计算期望的实例数量
    private int CountExpectedInstance(int curInstance,String type,int maxInstance) {

        switch (type) {
            case "normal":
                //比curInstance多三分之一
                if (curInstance <= 3) {
                    return Math.min(curInstance+1,maxInstance);
                } else {
                    return Math.min( curInstance + (curInstance / 3),maxInstance);
                }
            default:
                return Math.min(curInstance+1,maxInstance);
        }
    }
}
