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

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomi.youpin.dubbo.filter.TraceIdUtils;
import com.xiaomi.youpin.gateway.RouteType;
import com.xiaomi.youpin.gateway.TeslaConstants;
import com.xiaomi.youpin.gateway.common.*;
import com.xiaomi.youpin.gateway.dubbo.Dubbo;
import com.xiaomi.youpin.gateway.dubbo.MethodInfo;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.SwitchFlag;
import com.xiaomi.youpin.gateway.protocol.IRpcClient;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.xiaomi.youpin.gateway.TeslaConstants.Tag;
import static org.apache.dubbo.common.Constants.PREFIX_GENERIC_RETRIES_KEY;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class DubboClient extends IRpcClient implements Dubbo {

    private final ApplicationConfig applicationConfig;

    private final RegistryConfig registryConfig;

    private final String teslaVersion = new GateWayVersion().toString();

    @Autowired
    private ConfigService configService;

    @Value("${server.type}")
    private String serverType;

    static final String nullResultTag = "SUPPORT-NULL-RESULT";

    private static final String TAG_KEY = "dubbo.tag";

    public DubboClient(final ApplicationConfig applicationConfig,
                       RegistryConfig registryConfig) {
        super();
        this.applicationConfig = applicationConfig;
        this.registryConfig = registryConfig;
    }


    @Override
    public Object call(MethodInfo methodInfo) {
        try {
            String key = ReferenceConfigCache.getKey(methodInfo.getServiceName(), methodInfo.getGroup(), methodInfo.getVersion());
            GenericService genericService = ReferenceConfigCache.getCache().get(key);
            if (null == genericService) {
                ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
                reference.setApplication(applicationConfig);
                reference.setRegistry(registryConfig);
                reference.setInterface(methodInfo.getServiceName());
                reference.setGeneric(true);
                reference.setCheck(false);
                reference.setGroup(methodInfo.getGroup());
                reference.setVersion(methodInfo.getVersion());
                reference.setTimeout(methodInfo.getTimeout());
                //支持直接使用json协议(更好的支持scirpt filter)
                if ("json".equals(methodInfo.getProto())) {
                    reference.setGeneric(Constants.GENERIC_SERIALIZATION_YOUPIN_JSON);
                }
                ReferenceConfigCache cache = ReferenceConfigCache.getCache();
                genericService = cache.get(reference);
            }
            RpcContext.getContext().setAttachment(PREFIX_GENERIC_RETRIES_KEY + "." + methodInfo.getMethodName(), String.valueOf(methodInfo.getTries()));
            RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, String.valueOf(methodInfo.getTimeout()));
            Object res = genericService.$invoke(methodInfo.getMethodName(), methodInfo.getParameterTypes(), methodInfo.getArgs());
            return res;
        } finally {
            RpcContext.getContext().clearAttachments();
        }
    }

    @Override
    public FullHttpResponse call(FilterContext ctx, final ApiInfo apiInfo, final FullHttpRequest request) {
        String protocolVersion = "";
        String traceId = ctx.getTraceId();
        final String param = DubboClientUtils.getParam(ctx, request);
        String data = "";
        Stopwatch sw = Stopwatch.createStarted();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            //protocolVersion
            protocolVersion = request.headers().get(TeslaConstants.ProtocolVersion, "v1");
            //获取到serviceName
            final String serviceName = DubboClientUtils.getServiceName(ctx, apiInfo);
            //获取到methodName
            final String methodName = DubboClientUtils.getMethodName(ctx, apiInfo);
            //获取到分组
            String group = DubboClientUtils.getGroup(ctx, apiInfo, request);
            //是否使用dubbo原生协议
            boolean nativeProto = apiInfo.getRouteType() == RouteType.Native_Dubbo.type();
            //超时时间
            int timeOut = getTimeout(apiInfo);
            log.info("timeOut===={}", timeOut);
            //获取到版本
            String version = DubboClientUtils.getVersion(ctx, apiInfo);
            //获取initGroup
            String initGroup = DubboClientUtils.getInitGroup(ctx, apiInfo);

            String key = ReferenceConfigCache.getKey(serviceName, group, version);
            GenericService genericService = ReferenceConfigCache.getCache().get(key);
            if (null == genericService) {
                genericService = DubboClientUtils.getGenericService(this.applicationConfig, this.registryConfig, request, protocolVersion, serviceName, group, nativeProto, timeOut, version);
            }
            ctx.getAttachments().put("param", param);

            //是否直接使用json直接通信
            boolean useJson = DubboClientUtils.useJson(ctx);

            Pair<String[], Object[]> typeAndValue = DubboClientUtils.transformerData(ctx, param, DubboClientUtils.getParamTemplate(ctx, apiInfo), request, useJson);

            if (useJson) {
                RpcContext.getContext().setAttachment("gson_generic_args", "true");
            }

            //如果需要放入uid
            DubboClientUtils.setHeaderAndUid(ctx, request, apiInfo, typeAndValue);
            if (StringUtils.isNotEmpty(traceId)) {
                TraceIdUtils.ins().setTraceIdAndSpanId(traceId, "0");
            }

            RpcContext.getContext().getAttachments().put("traceparent", "00" + "-" + ctx.getTraceId() + "-" + ctx.getSpanId() + "-" + "01");
            RpcContext.getContext().getAttachments().put("tesla_version", teslaVersion);

            //会动态修改超时时间
            RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, String.valueOf(timeOut));
            RpcContext.getContext().setAttachment("initGroup", initGroup);

            //修改dubbo tag
            if (StringUtils.isNotEmpty(ctx.getAttachment(Tag, ""))) {
                RpcContext.getContext().setAttachment(TAG_KEY, ctx.getAttachment(Tag, ""));
            }
            //动态修改重试次数
            String retries = ctx.getAttachment("retries", "");
            if (!StringUtils.isEmpty(retries)) {
                RpcContext.getContext().setAttachment(PREFIX_GENERIC_RETRIES_KEY + "." + methodName, retries);
            }
            //动态修改反序列化时使用的协议(provider端的GenericFilter)
            RpcContext.getContext().setAttachment(Constants.GENERIC_KEY, nativeProto ? Constants.GENERIC_SERIALIZATION_DEFAULT : Constants.GENERIC_SERIALIZATION_YOUPIN_JSON);
            log.debug("DubboClient.call, before dubbo invoke, url: {}, RpcContext.attachment:{}", apiInfo.getUrl(), RpcContext.getContext().getAttachments());

            Object response = genericService.$invoke(methodName, typeAndValue.getLeft(), typeAndValue.getRight());
            long useTime = sw.elapsed(TimeUnit.MILLISECONDS);
            ctx.setRpcUseTime(useTime);
            HashMap<String, String> events = new HashMap<>();
            events.put("service", serviceName);
            events.put("method", methodName);
            events.put("time", useTime + "ms");
            ctx.addTraceEvent("dubbo_call_event", events);

            if (protocolVersion.equals("v1")) {
                GsonBuilder builder = new GsonBuilder();
                if ("true".equals(ctx.getAttachment(nullResultTag, ""))) {
                    builder.serializeNulls();
                }
                if (ctx.switchIsAllow(SwitchFlag.SWITCH_GSON_DISABLE_HTML_ESCAPING)
                        || Boolean.valueOf(ctx.getAttachment("Disable-Html-Escaping", "false"))) {
                    builder.disableHtmlEscaping();
                }
                Gson gson = builder.create();
                if (ctx.switchIsAllow(SwitchFlag.SWITCH_DIRECT_TO_STRING)) {
                    data = response.toString();
                } else {
                    data = gson.toJson(response);
                }

            } else if (protocolVersion.equals("v2")) {
                data = response.toString();
            }

            if (ctx.getAttachments().containsKey(TeslaCons.RecordRes)) {
                ctx.getAttachments().put(TeslaCons.Res, data);
            }
            return HttpResponseUtils.create(ByteBufUtils.createBuf(ctx, data, configService.isAllowDirectBuf()));
        } catch (GenericException e) {
            log.warn("dubbo invoke param:" + param + "error:" + e.getMessage() + " " + apiInfo.getId() + ":" + apiInfo.getUrl() + ", GenericException:{}", e.getMessage());
            DubboClientUtils.recordErrorTracingEvent(sw, ctx, apiInfo, param, e);
            if (ConstraintViolationException.class.getName().equals(e.getExceptionClass())) {
                return HttpResponseUtils.create(Result.fail(GeneralCodes.ParamError, Msg.msgFor400, e.getMessage()));
            } else {
                return HttpResponseUtils.create(Result.fail(GeneralCodes.InternalError, Msg.msgFor500, e.getMessage()));
            }
        } catch (Throwable e) {
            log.warn("dubbo invoke param:" + param + "error:" + e.getMessage() + " " + apiInfo.getId() + ":" + apiInfo.getUrl() + ", Throwable:{}", e.getMessage());
            String detailMsg = "failed to get dubbo response";
            DubboClientUtils.recordErrorTracingEvent(sw, ctx, apiInfo, param, e);
            if (configService.isReturnDubboLog() || DubboClientUtils.debug(request)) {
                detailMsg = param + ":" + e.getMessage();
            }
            return HttpResponseUtils.create(Result.fail(GeneralCodes.InternalError, Msg.msgFor500, detailMsg));
        } finally {
            RpcContext.getContext().clearAttachments();
        }
    }

    private int getTimeout(ApiInfo apiInfo) {
        if ("file".equals(serverType)) {
            return configService.getTeslaFileTimeout();
        }
        return apiInfo.getTimeout() > 0 ? apiInfo.getTimeout() : Constants.DEFAULT_TIMEOUT;
    }


}
