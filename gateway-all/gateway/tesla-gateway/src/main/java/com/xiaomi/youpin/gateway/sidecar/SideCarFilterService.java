package com.xiaomi.youpin.gateway.sidecar;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.xiaomi.data.push.uds.codes.CodeType;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.gateway.common.*;
import com.xiaomi.youpin.gateway.dispatch.Dispatcher;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.protocol.dubbo.DubboClientUtils;
import com.xiaomi.youpin.gateway.sidecar.bo.SideCarEnum;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 2022/7/9 10:19
 */
@Slf4j
@Service
public class SideCarFilterService {

    private boolean sideCar;

    @NacosValue(value = "${side.car.support:}", autoRefreshed = true)
    private String sidecarSupport;

    /**
     * 用来开关sidecar的
     */
    //private Set<SideCarEnum> sidecarSet = Sets.newHashSet();

    @PostConstruct
    public void init() {
        sideCar = EnvUtils.getEnvOrProperty(SidecarService.SIDE_CAR_ENABLE, "false").equals("true");
        //Splitter.on(",").split(sidecarSupport).forEach(it -> sidecarSet.add(SideCarEnum.valueOf(it)));
    }


    @Resource
    private SidecarService sidecarService;

    @Resource
    private Dispatcher dispatcher;


    public boolean supportSideCar(SideCarEnum sideCarEnum) {
        if (sideCar){
            Set<SideCarEnum> sidecarSet = Sets.newHashSet();
            Splitter.on(",").split(sidecarSupport).forEach(it -> sidecarSet.add(SideCarEnum.valueOf(it)));
            return sidecarSet.contains(sideCarEnum);
        }
        return false;
    }


    public FullHttpResponse callHttpFilterSideCar(FilterContext context, ApiInfo apiInfo, FullHttpRequest request) {
        FilterResponse res = this.callSideCar0(context, apiInfo, request, "httpSideCar");
        if (res.getCode() != FilterResponse.FAILURE) {
            ByteBuf buf = ByteBufUtils.createBuf(context, new String(res.getData()), false);
            FullHttpResponse r = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(res.getCode()), buf);
            res.getHeaders().entrySet().forEach(it -> r.headers().add(it.getKey(), it.getValue()));
            return HttpResponseUtils.create(r);
        } else {
            return HttpResponseUtils.create(Result.fail(GeneralCodes.InternalError, Msg.msgFor500, res.getMessage()));
        }
    }


    public FullHttpResponse callDubboFilterSideCar(FilterContext context, ApiInfo apiInfo, FullHttpRequest request) {
        FilterResponse res = this.callSideCar0(context, apiInfo, request, "dubboSideCar");
        if (res.getCode() == 0) {
            return HttpResponseUtils.create(ByteBufUtils.createBuf(context, new String(res.getData()), false));
        } else {
            String s = "{\"code\":%s,\"message\":\"%s\",\"detailMsg\":\"%s\"}";
            return HttpResponseUtils.create(String.format(s, res.getCode(), res.getMessage(), new String(res.getData())));
        }
    }

    public FullHttpResponse callGRPCFilterSideCar(FilterContext context, ApiInfo apiInfo, FullHttpRequest request) {
        return callSideCar(context, apiInfo, request, "grpcSideCar");
    }

    public FullHttpResponse callSideCar(FilterContext context, ApiInfo apiInfo, FullHttpRequest request, String app) {
        FilterResponse res = this.callSideCar0(context, apiInfo, request, app);
        ByteBuf buf = ByteBufUtils.createBuf(context, new String(res.getData()), false);
        return HttpResponseUtils.create(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf));
    }

    public FilterResponse callSideCar0(FilterContext context, ApiInfo apiInfo, FullHttpRequest request, String app) {
        UdsCommand udsCommand = UdsCommand.createRequest();
        //默认是json格式(sidecarplugin那边有设置)
        udsCommand.setSerializeType(CodeType.HESSIAN);
        udsCommand.setApp(app);
        udsCommand.setCmd("execute");
        udsCommand.setTimeout(dispatcher.getTimeout(apiInfo));
        FilterHttpData httpData = new FilterHttpData();
        httpData.setApiInfo(apiInfo);
        context.setDubboContextAttachments(RpcContext.getContext().getAttachments());
        httpData.setFilterContext(context);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setMethod(request.method().name());
        filterRequest.setBody(DubboClientUtils.getParam(context, request).getBytes());
        filterRequest.setQueryString(HttpRequestUtils.getQueryString(request));
        request.headers().forEach(it -> filterRequest.getHeaders().put(it.getKey(), it.getValue()));
        httpData.setRequest(filterRequest);
        udsCommand.setData(httpData);
        FilterResponse res = sidecarService.call(udsCommand);
        return res;
    }

}
