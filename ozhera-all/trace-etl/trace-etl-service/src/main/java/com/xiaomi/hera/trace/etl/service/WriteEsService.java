package com.xiaomi.hera.trace.etl.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xiaomi.hera.trace.etl.domain.DriverDomain;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerAttrType;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerAttribute;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerESDomain;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerLogs;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerProcess;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerRefType;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerReferences;
import com.xiaomi.hera.trace.etl.util.ExecutorUtil;
import com.xiaomi.hera.trace.etl.util.MessageUtil;
import com.xiaomi.hera.trace.etl.util.es.EsTraceUtil;
import com.xiaomi.hera.tspandata.TAttributeKey;
import com.xiaomi.hera.tspandata.TAttributes;
import com.xiaomi.hera.tspandata.TEvent;
import com.xiaomi.hera.tspandata.TLink;
import com.xiaomi.hera.tspandata.TResource;
import com.xiaomi.hera.tspandata.TSpanContext;
import com.xiaomi.hera.tspandata.TSpanData;
import com.xiaomi.hera.tspandata.TValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WriteEsService {

    @Value("${es.trace.index.prefix}")
    private String indexPrefix;
    @Value("${es.trace.index.service.prefix}")
    private String servicePrefix;
    @Value("${es.trace.index.driver.prefix}")
    private String driverIndexPrefix;
    @Value("${es.error.index}")
    private String errorIndexPrefix;

    private EsTraceUtil esTraceUtil;

    public WriteEsService(EsTraceUtil esTraceUtil){
        this.esTraceUtil = esTraceUtil;
    }

    private Cache<String, String> localCache =
            CacheBuilder.newBuilder().
                    maximumSize(50000).
                    expireAfterWrite(MessageUtil.TRACE_SERVICE_REDIS_KEY_EXPIRE, TimeUnit.SECONDS).
                    build();

    public void insertJaegerService(String date, String serviceName, String oprationName) {
        // Determine whether there is
        String key = serviceName + ":" + oprationName;
        if (localCache.asMap().containsKey(key)) {
            return;
        } else {
            // writer into ES
            Map<String, String> map1 = new HashMap<>();
            map1.put("serviceName", serviceName);
            map1.put("operationName", oprationName);
            esTraceUtil.insertBulk(servicePrefix + date, map1);
            localCache.put(key, "1");
        }
    }

    public void submitErrorEsTrace(String domain, String url, String serviceName, String traceId, String type, String ip, String time, String dataSource, String duration, String errorType, String errorCode,String env) {
        Map<String, Object> map = new HashMap<>();
        map.put("domain", domain);
        map.put("type", type);
        map.put("host", ip);
        map.put("url", url);
        map.put("dataSource", dataSource);
        map.put("serviceName", serviceName);
        map.put("traceId", traceId);
        map.put("timestamp", time);
        map.put("duration", duration);
        // error timeout
        map.put("errorType", errorType);
        map.put("errorCode", errorCode);
        map.put("serverEnv", env);
        ExecutorUtil.submit(() -> {
            String format1 = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(LocalDate.now());
            esTraceUtil.insertErrorBulk(errorIndexPrefix + format1, map);
        });
    }

    public String buildJaegerES(TSpanData tSpanData) {
        JaegerESDomain jaegerESDomain = new JaegerESDomain();
        jaegerESDomain.setTraceID(tSpanData.getTraceId());
        jaegerESDomain.setSpanID(tSpanData.getSpanId());
        jaegerESDomain.setOperationName(tSpanData.getName());
        long startTime = tSpanData.getStartEpochNanos();
        jaegerESDomain.setStartTime(startTime / 1000);
        jaegerESDomain.setStartTimeMillis(startTime / (1000 * 1000));
        long duration = tSpanData.getEndEpochNanos() - tSpanData.getStartEpochNanos();
        jaegerESDomain.setDuration(duration / (1000));
        // build references
        jaegerESDomain.setReferences(buildReferences(tSpanData.getParentSpanContext(), tSpanData.getLinks()));
        // build tags
        jaegerESDomain.setTags(buildAttributes(tSpanData.getAttributes()));
        // build logs
        jaegerESDomain.setLogs(buildLogs(tSpanData.getEvents()));
        // build process
        jaegerESDomain.setProcess(buildProcess(tSpanData.getExtra().getServiceName(), tSpanData.getResouce()));
        return JSONObject.toJSONString(jaegerESDomain, SerializerFeature.WriteMapNullValue);
    }

    private List<JaegerReferences> buildReferences(TSpanContext parentSpanContext, List<TLink> links) {
        List<JaegerReferences> list = new ArrayList<>();
        if (parentSpanContext != null) {
            JaegerReferences jaegerReferences = new JaegerReferences();
            jaegerReferences.setTraceID(parentSpanContext.getTraceId());
            jaegerReferences.setSpanID(parentSpanContext.getSpanId());
            jaegerReferences.setRefType(JaegerRefType.CHILD_OF);
            list.add(jaegerReferences);
        }
        // link is not used
        return list;
    }

    private List<JaegerLogs> buildLogs(List<TEvent> events) {
        List<JaegerLogs> list = new ArrayList<>();
        if (events != null && events.size() > 0) {
            for (TEvent tEvent : events) {
                JaegerLogs log = new JaegerLogs();
                log.setTimestamp(tEvent.getEpochNanos());
                log.setFields(buildAttributes(tEvent.getAttributes()));
                list.add(log);
            }
        }
        return list;
    }

    private List<JaegerAttribute> buildAttributes(TAttributes attributes) {
        List<JaegerAttribute> list = new ArrayList<>();
        if (attributes != null && attributes.getKeys() != null && attributes.getKeys().size() > 0) {
            List<TAttributeKey> keys = attributes.getKeys();
            List<TValue> values = attributes.getValues();
            for (int i = 0; i < keys.size(); i++) {
                JaegerAttribute attr = new JaegerAttribute();
                TAttributeKey tAttributeKey = keys.get(i);
                attr.setKey(tAttributeKey.getValue());
                switch (tAttributeKey.getType()) {
                    case STRING:
                        attr.setType(JaegerAttrType.STRING);
                        attr.setValue(decodeLineBreak(values.get(i).getStringValue()));
                        break;
                    case LONG:
                        attr.setType(JaegerAttrType.LONG);
                        attr.setValue(String.valueOf(values.get(i).getLongValue()));
                        break;
                    case BOOLEAN:
                        attr.setType(JaegerAttrType.BOOLEAN);
                        attr.setValue(String.valueOf(values.get(i).isBoolValue()));
                        break;
                    case DOUBLE:
                        attr.setType(JaegerAttrType.DOUBLE);
                        attr.setValue(String.valueOf(values.get(i).getDoubleValue()));
                        break;
                }
                list.add(attr);
            }
        }
        return list;
    }

    private JaegerProcess buildProcess(String serviceName, TResource resource){
        JaegerProcess jaegerProcess = new JaegerProcess();
        jaegerProcess.setServiceName(serviceName);
        if(resource != null) {
            jaegerProcess.setTags(buildAttributes(resource.getAttributes()));
        }
        return jaegerProcess;
    }

    private String decodeLineBreak(String value) {
        if (StringUtils.isNotEmpty(value)) {
            return value.replaceAll("##r'", "\\\\\"").replaceAll("##n", "\\\\n").replaceAll("##r", "\\\\r").replaceAll("##t", "\\\\t").replaceAll("##tat", "\\\\tat").replaceAll("##'", "\\\\\"");
        }
        return value;
    }

    public void insertDriver(DriverDomain driverDomain) {
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String replace = format.replace("-", ".");
        String index = driverIndexPrefix + replace;
        try {
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(driverDomain);
            esTraceUtil.insertBulk(index, jsonObject);
        } catch (Exception e) {
            log.error("db/redis es data exception:", e);
        }
    }

    public void insertJaegerSpan(TSpanData tSpanData, String serviceName, String spanName) {
        SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd");
        String format = date.format(new Date());
        insertJaegerService(format, serviceName, spanName);
        String jaegerESJson = buildJaegerES(tSpanData);
        JSONObject jsonObject = JSONObject.parseObject(jaegerESJson);
        esTraceUtil.insertBulk(indexPrefix + format, jsonObject);
    }

}
