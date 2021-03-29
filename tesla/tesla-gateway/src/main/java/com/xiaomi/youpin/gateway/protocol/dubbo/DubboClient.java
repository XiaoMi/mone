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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.dubbo.filter.TraceIdContext;
import com.xiaomi.youpin.dubbo.filter.TraceIdUtils;
import com.xiaomi.youpin.dubbo.request.RequestContext;
import com.xiaomi.youpin.gateway.RouteType;
import com.xiaomi.youpin.gateway.TeslaConstants;
import com.xiaomi.youpin.gateway.common.*;
import com.xiaomi.youpin.gateway.dubbo.Dubbo;
import com.xiaomi.youpin.gateway.dubbo.MethodInfo;
import com.xiaomi.youpin.gateway.exception.GatewayException;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.SwitchFlag;
import com.xiaomi.youpin.gateway.protocol.IRpcClient;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 */
public class DubboClient extends IRpcClient implements Dubbo {


    private static final Logger logger = LoggerFactory.getLogger(DubboClient.class);


    private final ApplicationConfig applicationConfig;

    private final RegistryConfig registryConfig;

    @Autowired
    private ConfigService configService;


    public DubboClient(final ApplicationConfig applicationConfig,
                       RegistryConfig registryConfig) {
        super();
        this.applicationConfig = applicationConfig;
        this.registryConfig = registryConfig;
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
    private Pair<String[], Object[]> transformerData(final String outPutJson, final String paramTemplate, final FullHttpRequest request) {
        Gson gson = new Gson();
        List<TypeValue> paramterTypes = gson.fromJson(paramTemplate, new TypeToken<List<TypeValue>>() {
        }.getType());

        final List<Object> dubboParamters = new ArrayList<>(0);
        //需要考虑参数为空的情景
        if (paramterTypes == null || paramterTypes.size() == 0) {
            String[] types = new String[0];
            Object[] values = new Object[0];
            return ImmutablePair.of(types, values);
        } else {
            if (outPutJson == null || outPutJson.isEmpty()) {
                throw new GatewayException("paramters is null:" + outPutJson);
            }
            dubboParamters.addAll(gson.fromJson(outPutJson, new TypeToken<List<Object>>() {
            }.getType()));

            if (!Optional.fromNullable(dubboParamters).isPresent()) {
                throw new GatewayException("paramters is null:" + outPutJson);
            }
            if (paramterTypes.size() != dubboParamters.size()) {
                throw new GatewayException("types.size() != dubboParamters.size()");
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
            RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, String.valueOf(methodInfo.getTimeout()));
            Object res = genericService.$invoke(methodInfo.getMethodName(), methodInfo.getParameterTypes(), methodInfo.getArgs());
            return res;
        } finally {
            RpcContext.getContext().clearAttachments();
        }
    }

    private String getServiceName(FilterContext ctx, final ApiInfo apiInfo) {
        if (ctx.getDubboApiInfo() != null) {
            return StringUtils.isEmpty(ctx.getDubboApiInfo().getServiceName())
                        ? ""
                        : ctx.getDubboApiInfo().getServiceName();
        }
        return apiInfo.getDubboApiInfo().getServiceName();
    }

    private String getMethodName(FilterContext ctx, final ApiInfo apiInfo) {
        if (ctx.getDubboApiInfo() != null) {
            return StringUtils.isEmpty(ctx.getDubboApiInfo().getMethodName())
                        ? ""
                        : ctx.getDubboApiInfo().getMethodName();
        }
        return apiInfo.getDubboApiInfo().getMethodName();
    }

    private String getGroup(FilterContext ctx, final ApiInfo apiInfo) {
        if (ctx.getDubboApiInfo() != null) {
            return StringUtils.isEmpty(ctx.getDubboApiInfo().getGroup())
                        ? ""
                        : ctx.getDubboApiInfo().getGroup();
        }
        return apiInfo.getDubboApiInfo().getGroup();
    }

    private String getVersion(FilterContext ctx, final ApiInfo apiInfo) {
        if (ctx.getDubboApiInfo() != null) {
            return StringUtils.isEmpty(ctx.getDubboApiInfo().getVersion())
                        ? ""
                        : ctx.getDubboApiInfo().getVersion();
        }
        return apiInfo.getDubboApiInfo().getVersion();
    }

    private String getParamTemplate(FilterContext ctx, final ApiInfo apiInfo) {
        if (ctx.getDubboApiInfo() != null) {
            return StringUtils.isEmpty(ctx.getDubboApiInfo().getParamTemplate())
                        ? "[]"
                        : ctx.getDubboApiInfo().getParamTemplate();
        }
        return apiInfo.getDubboApiInfo().getParamTemplate();
    }


    @Override
    public FullHttpResponse call(FilterContext ctx, final ApiInfo apiInfo, final FullHttpRequest request) {
        String source = "";
        String protocolVersion = "";
        String traceId = ctx.getTraceId();
        String outPutJson = null;
        String data = "";
        try {
            source = request.headers().get(TeslaConstants.FrontHeaderSourceName);
            if (StringUtils.isEmpty(source)) {
                source = "";
            }
            source = "gateway:" + source;

            protocolVersion = request.headers().get(TeslaConstants.ProtocolVersion, "v1");


            //获取到serviceName
            final String serviceName = getServiceName(ctx, apiInfo);
            //获取到methodName
            final String methodName = getMethodName(ctx, apiInfo);
            //获取到分组
            String group = getGroup(ctx, apiInfo);

            //判断是否是内网
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

            //使用dubbo原生协议
            boolean nativeProto = apiInfo.getRouteType() == RouteType.Native_Dubbo.type();

            //超时时间
            int timeOut = apiInfo.getTimeout() > 0 ? apiInfo.getTimeout() : Constants.DEFAULT_TIMEOUT;

            //获取到版本
            String version = getVersion(ctx, apiInfo);

            String key = ReferenceConfigCache.getKey(serviceName, group, version);
            GenericService genericService = ReferenceConfigCache.getCache().get(key);
            if (null == genericService) {
                //标准的泛化调用
                ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
                //放入app config
                reference.setApplication(applicationConfig);
                //放入注册中心 config
                reference.setRegistry(registryConfig);

                reference.setInterface(serviceName);

                reference.setGroup(group);

                if (nativeProto) {
                    reference.setGeneric(Constants.GENERIC_SERIALIZATION_DEFAULT);
                } else {
                    reference.setGeneric(Constants.GENERIC_SERIALIZATION_YOUPIN_JSON);
                }

                if (protocolVersion.equals("v1")) {
                    //使用有品自己的系列化模式(性能好一些)
                    RpcContext.getContext().getAttachments().put(Constants.YOUPIN_PROTOCOL_VERSION, "v1");
                } else if (protocolVersion.equals("v2")) {
                    RpcContext.getContext().getAttachments().put(Constants.YOUPIN_PROTOCOL_VERSION, "v2");
                }


                if (request.headers().contains(Constants.FILTER_FIELD)) {
                    RpcContext.getContext().setAttachment(Constants.FILTER_FIELD, request.headers().get(Constants.FILTER_FIELD));
                }

                reference.setCheck(false);
                if (StringUtils.isEmpty(version)) {
                    version = "";
                }
                reference.setVersion(version);


                //超时设置
                reference.setTimeout(timeOut);

                ReferenceConfigCache cache = ReferenceConfigCache.getCache();
                genericService = cache.get(reference);
            }

            outPutJson = "";
            if (StringUtils.isNotEmpty(ctx.getAttachments().get(FilterContext.New_Body))) {
                outPutJson = ctx.getAttachments().get(FilterContext.New_Body);
            }else {
                outPutJson = new String(HttpRequestUtils.getRequestBody(request));
            }

            ctx.getAttachments().put("param", outPutJson);

            Pair<String[], Object[]> typeAndValue = this.transformerData(outPutJson, getParamTemplate(ctx, apiInfo), request);

            //如果需要放入uid
            setHeaderAndUid(ctx, request, apiInfo, typeAndValue);

            //实际的调用
            RpcContext.getContext().getAttachments().put("gateway_source_key", source);
            if (StringUtils.isNotEmpty(traceId)) {
                TraceIdUtils.ins().setTraceIdAndSpanId(traceId, "0");
            }

            //会动态修改超时时间
            RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, String.valueOf(timeOut));
            logger.debug("DubboClient.call, before dubbo invoke, url: {}, RpcContext.attachment:{}", apiInfo.getUrl(), RpcContext.getContext().getAttachments());
            Object response = genericService.$invoke(methodName, typeAndValue.getLeft(), typeAndValue.getRight());


            if (protocolVersion.equals("v1")) {
                GsonBuilder builder = new GsonBuilder();
                if (ctx.switchIsAllow(SwitchFlag.SWITCH_GSON_DISABLE_HTML_ESCAPING)) {
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
            if (ConstraintViolationException.class.getName().equals(e.getExceptionClass())) {
                logger.warn("dubbo invoke param:" + outPutJson + "error:" + e.getMessage() + " " + apiInfo.getId() + " " + source + ":" + apiInfo.getUrl());
                return HttpResponseUtils.create(Result.fail(GeneralCodes.ParamError, Msg.msgFor400, e.getMessage()));
            }
            throw e;
        } catch (Throwable e) {
            logger.warn("dubbo invoke param:" + outPutJson + "error:" + e.getMessage() + " " + apiInfo.getId() + " " + source + ":" + apiInfo.getUrl());
            String detailMsg = "failed to get dubbo response";
            if (configService.isReturnDubboLog() || debug(request)) {
                detailMsg = outPutJson + ":" + e.getMessage();
            }
            return HttpResponseUtils.create(Result.fail(GeneralCodes.InternalError, Msg.msgFor500, detailMsg));
        } finally {
            if (StringUtils.isNotEmpty(traceId)) {
                TraceIdContext.ins().remove(TraceIdUtils.getKey(traceId, "0"));
            }
            RpcContext.getContext().clearAttachments();
        }
    }

    private void setHeaderAndUid(FilterContext ctx, FullHttpRequest request, ApiInfo apiInfo, Pair<String[], Object[]> typeAndValue) {
        try {
            String[] types = typeAndValue.getKey();
            if (null != types
                    && types.length >= 1
                        && types[0].equals(com.xiaomi.youpin.dubbo.request.RequestContext.class.getName())) {

                Map<String, String> headers = request.headers().entries()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1));

                setFilterContextHeaders(ctx, headers);

                Map m = (Map) typeAndValue.getValue()[0];
                m.put(RequestContext.HEADERS, headers);
                if (apiInfo.isAllow(Flag.ALLOW_AUTH)) {
                    m.put(RequestContext.UID, ctx.getUid());
                }
            }
        } catch (Throwable e) {
            logger.error("dubbo.setHeaderAndUid error: {}", e);
        }
    }

    private boolean debug(FullHttpRequest request) {
        if (java.util.Optional.ofNullable(request.headers().get("X-Debug")).isPresent()
                && "true".equals(request.headers().get("X-Debug"))) {
            return true;
        }
        return false;
    }
}
