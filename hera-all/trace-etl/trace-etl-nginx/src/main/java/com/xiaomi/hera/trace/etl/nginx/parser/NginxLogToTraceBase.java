package com.xiaomi.hera.trace.etl.nginx.parser;

import com.alibaba.fastjson.JSONObject;
import com.xiaomi.hera.trace.etl.domain.NginxJaegerDomain;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerAttrType;
import com.xiaomi.hera.trace.etl.util.ThriftUtil;
import com.xiaomi.hera.tspandata.TAttributeKey;
import com.xiaomi.hera.tspandata.TAttributeType;
import com.xiaomi.hera.tspandata.TAttributes;
import com.xiaomi.hera.tspandata.TExtra;
import com.xiaomi.hera.tspandata.TKind;
import com.xiaomi.hera.tspandata.TResource;
import com.xiaomi.hera.tspandata.TSpanData;
import com.xiaomi.hera.tspandata.TStatus;
import com.xiaomi.hera.tspandata.TValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/2/15 2:56 下午
 */
@Slf4j
public abstract class NginxLogToTraceBase {

    private String[] spanIdChars = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private Random r = new Random();

    public String toJaegerTrace(String message) {
        NginxJaegerDomain parse = parse(message);
        if (parse != null) {
            JSONObject json = new JSONObject();
            json.put("traceID", parse.getTraceId());
            json.put("spanID", parse.getSpanId());
            json.put("operationName", parse.getUri());
            json.put("references", new ArrayList<>());
            // ms
            long startTime = Long.parseLong(parse.getStartTime());
            json.put("startTime", startTime * 1000);
            json.put("startTimeMillis", startTime);
            json.put("duration", Long.parseLong(parse.getRequestTime()) * (1000));
            json.put("tags", completeTags(parse));
            json.put("logs", new ArrayList<>());
            json.put("process", complateProcess(parse));
            return json.toJSONString();
        }
        return null;
    }

    public byte[] toTSpanDateBytes(String message) {
        // 将nginx日志转换为NginxJaegerDomain, 这由具体实现类实现，可以每种nginx日志对应不同实现
        NginxJaegerDomain parse = parse(message);
        if (parse != null) {
            try {
            TSpanData spanData = new TSpanData();
            spanData.setTraceId(parse.getTraceId());
            spanData.setSpanId(parse.getSpanId());
            spanData.setName(parse.getUri());
            spanData.setStatus(400 <= parse.getStatus() && parse.getStatus() < 600 ? TStatus.ERROR : TStatus.UNSET);
            long startTime = Long.parseLong(parse.getStartTime()) * 1000 * 1000;
            spanData.setStartEpochNanos(startTime);
            long duration = Long.parseLong(parse.getRequestTime()) * 1000 * 1000;
            spanData.setEndEpochNanos(startTime + duration);
            spanData.setAttributes(completeAttributes(parse));
            spanData.setTotalAttributeCount(spanData.getAttributes().getKeysSize());
            spanData.setKind(TKind.SERVER);
            spanData.setResouce(completeResource(parse));
            spanData.setExtra(completeExtra(parse));
            TSerializer serializer = new TSerializer(ThriftUtil.PROTOCOL_FACTORY);
                return serializer.serialize(spanData);
            } catch (Throwable e) {
                log.error("transform TSpanData error : ",e);
            }
        }
        return null;
    }

    private List<Map<String, Object>> completeTags(NginxJaegerDomain parse) {
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(genarateTags("http.remote.address", parse.getRemoteAddr(), JaegerAttrType.STRING));
        result.add(genarateTags("http.request", parse.getRequest(), JaegerAttrType.STRING));
        result.add(genarateTags("http.status_code", parse.getStatus() + "", JaegerAttrType.LONG));
        result.add(genarateTags("error", 400 <= parse.getStatus() && parse.getStatus() < 600 ? "true" : "false", JaegerAttrType.BOOLEAN));
        result.add(genarateTags("http.referer", parse.getRefer(), JaegerAttrType.STRING));
        result.add(genarateTags("http.user_agent", parse.getUa(), JaegerAttrType.STRING));
        result.add(genarateTags("http.x-forwarded-for", parse.getxForwardedFor(), JaegerAttrType.STRING));
        result.add(genarateTags("http.upstream.address", parse.getUpstreamAddr(), JaegerAttrType.STRING));
        result.add(genarateTags("http.upstream.status", parse.getUpstreamStatus(), JaegerAttrType.STRING));
        return result;
    }

    private TAttributes completeAttributes(NginxJaegerDomain parse){
        TAttributes ret = new TAttributes();
        List<TAttributeKey> keys = new ArrayList<>();
        List<TValue> values = new ArrayList<>();

        keys.add(getKey("http.remote.address", TAttributeType.STRING));
        values.add(new TValue().setStringValue(parse.getRemoteAddr()));

        keys.add(getKey("http.request", TAttributeType.STRING));
        values.add(new TValue().setStringValue(parse.getRequest()));

        keys.add(getKey("http.status_code", TAttributeType.LONG));
        values.add(new TValue().setLongValue(parse.getStatus()));

        keys.add(getKey("error", TAttributeType.BOOLEAN));
        boolean error = 400 <= parse.getStatus() && parse.getStatus() < 600;
        values.add(new TValue().setBoolValue(error));

        keys.add(getKey("http.referer", TAttributeType.STRING));
        values.add(new TValue().setStringValue(parse.getRefer()));

        keys.add(getKey("http.user_agent", TAttributeType.STRING));
        values.add(new TValue().setStringValue(parse.getUa()));

        keys.add(getKey("http.x-forwarded-for", TAttributeType.STRING));
        values.add(new TValue().setStringValue(parse.getxForwardedFor()));

        keys.add(getKey("http.upstream.address", TAttributeType.STRING));
        values.add(new TValue().setStringValue(parse.getUpstreamAddr()));

        keys.add(getKey("http.upstream.status", TAttributeType.STRING));
        values.add(new TValue().setStringValue(parse.getUpstreamStatus()));

        ret.setKeys(keys);
        ret.setValues(values);
        return ret;
    }

    private TResource completeResource(NginxJaegerDomain parse){
        TResource ret = new TResource();
        TAttributes tAttributes = new TAttributes();
        List<TAttributeKey> keys = new ArrayList<>();
        List<TValue> values = new ArrayList<>();

        keys.add(getKey("host", TAttributeType.STRING));
        values.add(new TValue().setStringValue(parse.getNginxHostName()));

        keys.add(getKey("ip", TAttributeType.STRING));
        values.add(new TValue().setStringValue(parse.getNginxIp()));

        tAttributes.setKeys(keys);
        tAttributes.setValues(values);
        ret.setAttributes(tAttributes);
        return ret;
    }

    private TExtra completeExtra(NginxJaegerDomain parse){
        TExtra tExtra = new TExtra();
        tExtra.setServiceName("nginx-" + parse.getHost());
        tExtra.setHostname(parse.getNginxHostName());
        tExtra.setIp(parse.getNginxIp());
        return tExtra;
    }

    private TAttributeKey getKey(String key, TAttributeType type){
        TAttributeKey ret = new TAttributeKey();
        ret.setValue(key);
        ret.setType(type);
        return ret;
    }

    private Map<String, Object> complateProcess(NginxJaegerDomain parse) {
        Map<String, Object> result = new HashMap<>();
        result.put("serviceName", "nginx-" + parse.getHost());
        List<Map<String, Object>> processTags = new ArrayList<>();
        if(StringUtils.isNotEmpty(parse.getNginxHostName())) {
            processTags.add(genarateTags("host", parse.getNginxHostName(), JaegerAttrType.STRING));
        }
        if(StringUtils.isNotEmpty(parse.getNginxIp())) {
            processTags.add(genarateTags("ip", parse.getNginxIp(), JaegerAttrType.STRING));
        }
        result.put("tags", processTags);
        return result;
    }

    private Map<String, Object> genarateTags(String key, Object value, String type) {
        if(value != null) {
            Map<String, Object> remoteAddr = new HashMap<>();
            remoteAddr.put("key", key);
            remoteAddr.put("value", value);
            remoteAddr.put("type", type);
            return remoteAddr;
        }
        return null;
    }

    protected String generateSpanId() {
        StringBuffer spanId = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            spanId.append(r.nextInt(16) - 1);
        }
        return spanId.toString();
    }

    /**
     * 去掉请求参数
     * @param requestUri
     * @return
     */
    protected String parseUri(String requestUri){
        if(StringUtils.isEmpty(requestUri)){
            return "null";
        }
        if(requestUri.contains("?")){
            return requestUri.substring(0,requestUri.indexOf("?"));
        }
        return requestUri;
    }

    public abstract NginxJaegerDomain parse(String message);
}
