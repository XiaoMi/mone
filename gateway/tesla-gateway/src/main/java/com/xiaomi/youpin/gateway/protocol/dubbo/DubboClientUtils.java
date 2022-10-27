/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gateway.protocol.dubbo;

import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.dubbo.request.RequestContext;
import com.xiaomi.youpin.gateway.TeslaConstants;
import com.xiaomi.youpin.gateway.common.CustomObjectTypeAdapter;
import com.xiaomi.youpin.gateway.common.HttpRequestUtils;
import com.xiaomi.youpin.gateway.common.TypeValue;
import com.xiaomi.youpin.gateway.exception.GatewayException;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 9/4/21
 */
@Slf4j
public abstract class DubboClientUtils {

    static final String longTag = "SUPPORT-GSON-LONG";

    public static String getServiceName(FilterContext ctx, final ApiInfo apiInfo) {
        if (ctx.getDubboApiInfo() != null) {
            return StringUtils.isEmpty(ctx.getDubboApiInfo().getServiceName())
                    ? ""
                    : ctx.getDubboApiInfo().getServiceName();
        }
        return apiInfo.getDubboApiInfo().getServiceName();
    }

    public static String getInitGroup(FilterContext ctx, final ApiInfo apiInfo) {
        String group = ctx.getAttachments().get("initGroup");
        return group == null ? "" : group;
    }


    public static String getMethodName(FilterContext ctx, final ApiInfo apiInfo) {
        if (ctx.getDubboApiInfo() != null) {
            return StringUtils.isEmpty(ctx.getDubboApiInfo().getMethodName())
                    ? ""
                    : ctx.getDubboApiInfo().getMethodName();
        }
        return apiInfo.getDubboApiInfo().getMethodName();
    }

    public static String getGroup(FilterContext ctx, final ApiInfo apiInfo, FullHttpRequest request) {
        String group = apiInfo.getDubboApiInfo().getGroup();
        if (ctx.getDubboApiInfo() != null) {
            group = ctx.getDubboApiInfo().getGroup();
        }
        if (apiInfo.isAllow(Flag.ALLOW_PREVIEW) && HttpRequestUtils.intranet(request)) {
            group = TeslaConstants.Preview;
        }
        String path = ctx.getAttachments().get(TeslaConstants.Path);
        if (StringUtils.isNotEmpty(path)) {
            group = path;
        }
        if (StringUtils.isEmpty(group)) {
            group = "";
        }
        return group;
    }

    /**
     * 是否发过去的消息格式使用json
     *
     * @param ctx
     * @return
     */
    public static boolean useJson(FilterContext ctx) {
        return ctx.getAttachment("use_json", "false").equals("true");
    }

    /**
     * 数据转换
     * <p>
     * 第一个参数是类型列表
     * 第二个参数是参数列表
     *
     * @param request
     * @return
     */
    public static Pair<String[], Object[]> transformerData(final FilterContext ctx, final String outPutJson, final String paramTemplate, final FullHttpRequest request, boolean useJson) {
        Gson gson = getGson(ctx);
        List<TypeValue> paramterTypes = gson.fromJson(paramTemplate, new TypeToken<List<TypeValue>>() {
        }.getType());

        final List<Object> dubboParamters = new ArrayList<>(0);
        //需要考虑参数为空的情景
        if (paramterTypes == null || paramterTypes.size() == 0) {
            String[] types = new String[0];
            Object[] values = new Object[0];
            return ImmutablePair.of(types, values);
        } else {
            if (StringUtils.isEmpty(outPutJson)) {
                throw new GatewayException("paramters is null:" + outPutJson);
            }

            if (useJson) {
                JsonArray array = gson.fromJson(outPutJson, JsonArray.class);
                for (JsonElement e : array) {
                    if (e instanceof JsonPrimitive) {
                        dubboParamters.add(e.getAsString());
                    } else {
                        dubboParamters.add(e.toString());
                    }
                }
            } else {
                dubboParamters.addAll(gson.fromJson(outPutJson, new TypeToken<List<Object>>() {
                }.getType()));
            }

            if (!Optional.fromNullable(dubboParamters).isPresent()) {
                throw new GatewayException("paramters is null:" + outPutJson);
            }
            if (paramterTypes.size() != dubboParamters.size()) {
                throw new GatewayException("types size != dubboParamters size");
            }
            String[] types = new String[dubboParamters.size()];
            Object[] values = new Object[dubboParamters.size()];
            IntStream.range(0, dubboParamters.size()).forEach(index -> {
                types[index] = paramterTypes.get(index).getType();
                values[index] = dubboParamters.get(index);
            });
            return ImmutablePair.of(types, values);
        }

    }

    private static Gson getGson(FilterContext ctx) {
        if ("true".equals(ctx.getAttachment(longTag, "false"))) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(new TypeToken<List<Object>>() {
            }.getType(), new CustomObjectTypeAdapter());
            return builder.create();
        }
        return new Gson();
    }

    public static void setHeaderAndUid(FilterContext ctx, FullHttpRequest request, ApiInfo apiInfo, Pair<String[], Object[]> typeAndValue) {
        try {
            String[] types = typeAndValue.getKey();
            if (null != types
                    && types.length >= 1
                    && types[0].equals(com.xiaomi.youpin.dubbo.request.RequestContext.class.getName())) {

                Map<String, String> headers = request.headers().entries()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1));

                DubboClient.setFilterContextHeaders(ctx, headers);

                Map m = (Map) typeAndValue.getValue()[0];
                m.put(RequestContext.HEADERS, headers);
                m.put(RequestContext.ATTACHMENT, ctx.getAttachments());
                if (apiInfo.isAllow(Flag.ALLOW_AUTH)) {
                    m.put(RequestContext.UID, ctx.getUid());
                }
            }
        } catch (Throwable e) {
            log.error("dubbo.setHeaderAndUid error: {}", e);
        }
    }

    public static String getVersion(FilterContext ctx, final ApiInfo apiInfo) {
        if (ctx.getDubboApiInfo() != null) {
            return StringUtils.isEmpty(ctx.getDubboApiInfo().getVersion())
                    ? ""
                    : ctx.getDubboApiInfo().getVersion();
        }
        return apiInfo.getDubboApiInfo().getVersion();
    }

    public static boolean debug(FullHttpRequest request) {
        if (java.util.Optional.ofNullable(request.headers().get("X-Debug")).isPresent()
                && "true".equals(request.headers().get("X-Debug"))) {
            return true;
        }
        return false;
    }

    public static String getParam(FilterContext ctx, FullHttpRequest request) {
        if (StringUtils.isNotEmpty(ctx.getAttachments().get(FilterContext.New_Body))) {
            return ctx.getAttachments().get(FilterContext.New_Body);
        } else {
            return new String(HttpRequestUtils.getRequestBody(request));
        }
    }

    public static void recordErrorTracingEvent(Stopwatch sw, FilterContext ctx, ApiInfo apiInfo, String param, Throwable e) {
        long useTime = sw.elapsed(TimeUnit.MILLISECONDS);
        HashMap<String, String> events = new HashMap<>();
        events.put("param", FilterContext.reduceTraceMessage(param));
        events.put("time", useTime + "ms");
        events.put("timeout", apiInfo.getTimeout() + "ms");
        events.put("error", FilterContext.reduceTraceMessage(e.getMessage()));
        ctx.addTraceEvent("dubbo_call_error", events);
    }

    public static GenericService getGenericService(ApplicationConfig applicationConfig, RegistryConfig registryConfig, FullHttpRequest request, String protocolVersion, String serviceName, String group, boolean nativeProto, int timeOut, String version) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
        reference.setRegistry(registryConfig);
        reference.setInterface(serviceName);
        reference.setGroup(group);
        if (nativeProto) {
            reference.setGeneric(Constants.GENERIC_SERIALIZATION_DEFAULT);
        } else {
            reference.setGeneric(Constants.GENERIC_SERIALIZATION_YOUPIN_JSON);
        }
        RpcContext.getContext().getAttachments().put(Constants.YOUPIN_PROTOCOL_VERSION, protocolVersion);
        if (request.headers().contains(Constants.FILTER_FIELD)) {
            RpcContext.getContext().setAttachment(Constants.FILTER_FIELD, request.headers().get(Constants.FILTER_FIELD));
        }
        reference.setCheck(false);
        reference.setVersion(version);
        //超时设置
        reference.setTimeout(timeOut);
        //设置重试次数
        reference.setRetries(0);
        GenericService genericService = ReferenceConfigCache.getCache().get(reference);
        return genericService;
    }

    public static String getParamTemplate(FilterContext ctx, final ApiInfo apiInfo) {
        if (ctx.getDubboApiInfo() != null) {
            return StringUtils.isEmpty(ctx.getDubboApiInfo().getParamTemplate())
                    ? "[]"
                    : ctx.getDubboApiInfo().getParamTemplate();
        }
        return apiInfo.getDubboApiInfo().getParamTemplate();
    }


}
