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
package com.xiaomi.mone.log.agent.common.trace;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.xiaomi.hera.tspandata.TAttributeKey;
import com.xiaomi.hera.tspandata.TAttributeType;
import com.xiaomi.hera.tspandata.TAttributes;
import com.xiaomi.hera.tspandata.TEvent;
import com.xiaomi.hera.tspandata.TExtra;
import com.xiaomi.hera.tspandata.TInstrumentationLibraryInfo;
import com.xiaomi.hera.tspandata.TKind;
import com.xiaomi.hera.tspandata.TLink;
import com.xiaomi.hera.tspandata.TResource;
import com.xiaomi.hera.tspandata.TSpanContext;
import com.xiaomi.hera.tspandata.TSpanData;
import com.xiaomi.hera.tspandata.TStatus;
import com.xiaomi.hera.tspandata.TValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransportException;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Slf4j
public class TraceUtil {

    private static final TProtocolFactory PROTOCOL_FACTORY = new TCompactProtocol.Factory();
    private static final String TAG_KEY_SPAN_KIND = "span.kind";
    private static final String TAG_KEY_SERVICE_NAME = "service.name";
    private static final String SPAN_KIND_INTERNAL = "INTERNAL";
    private static final String TAG_KEY_IP = "ip";
    private static final String TAG_KEY_HOST = "host.name";
    private static final Set<String> SPECIAL_TAG_KEYS = Sets.newHashSet(TAG_KEY_SPAN_KIND,
            TAG_KEY_SERVICE_NAME, TAG_KEY_IP, TAG_KEY_HOST);
    private static Pattern EMPTY_PATTERN = Pattern.compile("\\r\\n");

    public static byte[] toBytes(String spanStr) {
        try {
            TSpanData tSpanData = toTSpanData(spanStr);
            return toBytes(tSpanData);
        } catch (Throwable ex) {
            log.error("Failed to convert span to thrift,spanStr={}", spanStr, ex);
        }
        return null;
    }

    public static byte[] toBytes(TSpanData tSpanData) {
        if (tSpanData == null) {
            return null;
        }

        try {
            TSerializer serializer = new TSerializer(PROTOCOL_FACTORY);
            return serializer.serialize(tSpanData);
        } catch (TTransportException e) {
            log.error("Failed to convert span to thrift TTransportException", e);
        } catch (TException e) {
            log.error("Failed to convert span to thrift TException", e);
        }

        return null;
    }

    public static TSpanData toTSpanData(String spanStr) {
        String message;
        spanStr = EMPTY_PATTERN.matcher(spanStr).replaceAll("");
        if (spanStr.contains(" ||| ")) {
            String[] messages = spanStr.split(" \\|\\|\\| ");
            message = messages[1];
        } else {
            message = spanStr.split(" \\| ")[1];
        }
        String[] messageArray = message.split(MessageUtil.SPLIT);
        // Bit check
        if (messageArray.length != MessageUtil.COUNT) {
            log.error("message count illegal : " + spanStr);
            return null;
        }

        return toTSpanData(messageArray);
    }

    private static TSpanData toTSpanData(String[] array) {
        TSpanData span = new TSpanData();
        span.setTraceId(array[MessageUtil.TRACE_ID]);
        span.setSpanId(array[MessageUtil.SPAN_ID]);
        span.setName(array[MessageUtil.SPAN_NAME]);
        span.setStatus(toTStatus(array[MessageUtil.STATUS_CODE]));
        span.setStartEpochNanos(Long.parseLong(array[MessageUtil.START_TIME]));
        span.setEndEpochNanos(span.getStartEpochNanos() + Long.parseLong(array[MessageUtil.DURATION]));
        Map<String, TValue> specialAttrMap = new HashMap<>();
        span.setAttributes(toTAttributes(JSONArray.parseArray(decodeLineBreak(array[MessageUtil.TAGS])),
                specialAttrMap));
        span.setTotalAttributeCount(span.getAttributes().getKeysSize());
        // using tags["span.kind"] as span kind
        String spanKind = specialAttrMap.get(TAG_KEY_SPAN_KIND) == null ? SPAN_KIND_INTERNAL : specialAttrMap.get(TAG_KEY_SPAN_KIND).getStringValue();
        span.setKind(toTKind(spanKind));
        span.setEvents(toTEventList(JSONArray.parseArray(decodeLineBreak(array[MessageUtil.EVENTS]))));
        span.setTotalRecordedEvents(span.getEventsSize());
        span.setResouce(
                toTResource(JSONObject.parseObject(array[MessageUtil.REOUSCES]), specialAttrMap));
        span.setExtra(toTExtra(specialAttrMap));
        // using links["ref_type=CHILD_OF"] as parent span context and using left as links
        AtomicReference<TSpanContext> parentSpanContextRef = new AtomicReference<>();
        span.setLinks(
                toTLinkList(JSONArray.parseArray(array[MessageUtil.REFERERNCES]), parentSpanContextRef));
        span.setParentSpanContext(parentSpanContextRef.get());
        span.setTotalRecordedLinks(span.getLinksSize());
        return span;
    }

    private static String decodeLineBreak(String value) {
        if (StringUtils.isNotEmpty(value)) {
            return value.replaceAll("\\\\","\\\\\\\\").replaceAll("##r'", "\\\\\"")
                    .replaceAll("##n", "\\\\n").replaceAll("##r", "\\\\r").replaceAll("##t", "\\\\t")
                    .replaceAll("##tat", "\\\\tat").replaceAll("##'", "\\\\\"");
        }
        return value;
    }

    /**
     * convert links.
     */
    private static List<TLink> toTLinkList(JSONArray links,
                                           AtomicReference<TSpanContext> spanContextAtomicReference) {
        if (links == null) {
            return null;
        }
        List<TLink> ret = new ArrayList<>(links.size());
        links.forEach(link -> {
            JSONObject linkJson = (JSONObject) link;
            if ("CHILD_OF".equals(linkJson.getString("refType"))) {
                spanContextAtomicReference.set(toTSpanContext(linkJson));
            } else {
                ret.add(toTLink(linkJson));
            }
        });
        return ret;
    }

    /**
     * convert events.
     */
    private static List<TEvent> toTEventList(JSONArray events) {
        if (events == null) {
            return null;
        }
        List<TEvent> ret = new ArrayList<>(events.size());
        events.forEach(event -> {
            JSONObject eventJson = (JSONObject) event;
            ret.add(toTEvent(eventJson));
        });
        return ret;
    }

    /**
     * convert span context.
     */
    private static TSpanContext toTSpanContext(JSONObject ctx) {
        if (ctx == null) {
            return null;
        }
        TSpanContext spanContext = new TSpanContext();
        spanContext.setTraceId(ctx.getString("traceID"));
        spanContext.setSpanId(ctx.getString("spanID"));
        return spanContext;
    }

    /**
     * convert instrumentation lib info.
     */
    private static TInstrumentationLibraryInfo toTInstrumentationLibraryInfo(String name,
                                                                             String version) {
        if (name == null && version == null) {
            return null;
        }
        TInstrumentationLibraryInfo ret = new TInstrumentationLibraryInfo();
        ret.setName(name);
        ret.setVersion(version);
        return ret;
    }

    /**
     * convert resource.
     */
    private static TResource toTResource(JSONObject resource, Map<String, TValue> specialAttrMap) {
        if (resource == null || !resource.containsKey("tags")) {
            return null;
        }
        TResource ret = new TResource();
        ret.setAttributes(toTAttributes(resource.getJSONArray("tags"), specialAttrMap));
        return ret;
    }

    /**
     * convert attributes.
     */
    private static TAttributes toTAttributes(JSONArray attributes) {
        return toTAttributes(attributes, null);
    }

    /**
     * convert attributes.
     */
    private static TAttributes toTAttributes(JSONArray attributes,
                                             Map<String, TValue> specialAttrMap) {
        if (attributes == null) {
            return null;
        }
        TAttributes ret = new TAttributes();
        ret.setKeys(new ArrayList<>());
        ret.setValues(new ArrayList<>());
        // attribute key put type in
        attributes.forEach(attr -> {
            JSONObject attrJson = (JSONObject) attr;
            TAttributeKey attributeKey = toTAttributeKey(attrJson);
            Object value = attrJson.get("value");
//            try {
                TValue attributeValue = new TValue();
                switch (attributeKey.getType()) {
                    case LONG:
                        if(value instanceof String){
                            attributeValue.setLongValue(Long.valueOf((String) value));
                        } else {
                            attributeValue.setLongValue((Long) value);
                        }
                        break;
                    case DOUBLE:
                        if(value instanceof String){
                            attributeValue.setDoubleValue(Double.valueOf((String) value));
                        } else {
                            attributeValue.setDoubleValue((Double) value);
                        }
                        break;
                    case STRING:
                            attributeValue.setStringValue(String.valueOf(value));
                        break;
                    case BOOLEAN:
                        if(value instanceof String){
                            attributeValue.setBoolValue(Boolean.valueOf((String) value));
                        } else {
                            attributeValue.setBoolValue((Boolean) value);
                        }
                        break;
                }
                if (specialAttrMap != null && SPECIAL_TAG_KEYS.contains(attributeKey.getValue())) {
                    specialAttrMap.put(attributeKey.getValue(), attributeValue);
                }
                ret.getKeys().add(attributeKey);
                ret.getValues().add(attributeValue);
//            } catch (Exception e) {
//                log.error("Failed to add key '{}' value '{}' to attributes", attributeKey, value, e);
//            }
        });
        return ret;
    }


    /**
     * convert attribute key.
     */
    private static TAttributeKey toTAttributeKey(JSONObject attrJson) {
        if (attrJson == null) {
            return null;
        }
        TAttributeKey ret = new TAttributeKey();
        ret.setType(toTAttributeType(attrJson.getString("type")));
        ret.setValue(attrJson.getString("key"));
        return ret;
    }

    /**
     * convert attribute type.
     */
    private static TAttributeType toTAttributeType(String type) {
        if (type == null) {
            return null;
        }
        type = type.toUpperCase();
        switch (type) {
            case "INT64":
                return TAttributeType.LONG;
            case "FLOAT64":
                return TAttributeType.DOUBLE;
            case "STRING":
                return TAttributeType.STRING;
            case "BOOL":
                return TAttributeType.BOOLEAN;
        }
        return null;
    }

    /**
     * convert span status.
     */
    private static TStatus toTStatus(String status) {
        if (status == null) {
            return null;
        }
        status = status.toUpperCase();
        switch (status) {
            case "OK":
                return TStatus.OK;
            case "ERROR":
                return TStatus.ERROR;
            case "UNSET":
                return TStatus.UNSET;
        }
        return null;
    }

    /**
     * convert span event.
     */
    private static TEvent toTEvent(JSONObject event) {
        if (event == null) {
            return null;
        }
        TEvent ret = new TEvent();
        ret.setName(event.getString("name"));
        if (event.containsKey("fields")) {
            TAttributes attributes = toTAttributes(event.getJSONArray("fields"));
            ret.setAttributes(attributes);
            ret.setTotalAttributeCount(attributes.getKeysSize());
        }
        ret.setEpochNanos(event.getLong("timestamp"));
        return ret;
    }

    /**
     * convert span kind.
     */
    private static TKind toTKind(String kind) {
        if (kind == null) {
            return null;
        }
        kind = kind.toUpperCase();
        switch (kind) {
            case "CLIENT":
                return TKind.CLIENT;
            case "SERVER":
                return TKind.SERVER;
            case "CONSUMER":
                return TKind.CONSUMER;
            case "PRODUCER":
                return TKind.PRODUCER;
            default:
                return TKind.INTERNAL;
        }
    }

    /**
     * convert span link.
     */
    private static TLink toTLink(JSONObject link) {
        if (link == null) {
            return null;
        }
        TLink ret = new TLink();
        ret.setSpanContext(toTSpanContext(link));
        return ret;
    }

    private static TExtra toTExtra(Map<String, TValue> specialAttrMap) {
        TExtra tExtra = new TExtra();
        if (specialAttrMap.containsKey(TAG_KEY_SERVICE_NAME)) {
            tExtra.setServiceName(specialAttrMap.get(TAG_KEY_SERVICE_NAME).getStringValue());
        }
        if (specialAttrMap.containsKey(TAG_KEY_IP)) {
            tExtra.setIp(specialAttrMap.get(TAG_KEY_IP).getStringValue());
        }
        if (specialAttrMap.containsKey(TAG_KEY_HOST)) {
            tExtra.setHostname(specialAttrMap.get(TAG_KEY_HOST).getStringValue());
        }
        return tExtra;
    }
}
