package com.xiaomi.youpin.tesla.rcurve.proxy.ingress;

import com.google.gson.Gson;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.rcurve.proxy.Proxy;
import com.xiaomi.youpin.tesla.rcurve.proxy.ProxyRequest;
import com.xiaomi.youpin.tesla.rcurve.proxy.common.CurveVersion;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyContext;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyType;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.ControlCallable;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.ControlChain;
import com.xiaomi.youpin.tesla.proxy.MeshResponse;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 * Http Ingress
 */
@Component
@Slf4j
public class HttpIngress implements Proxy<ProxyRequest, MeshResponse> {

    @Resource
    private UdsServer udsServer;

    @Resource
    private ControlChain controlChain;


    @Override
    public MeshResponse execute(ProxyContext context, ProxyRequest request) {
        if (request.getMethodName().equals("$version$")) {
            return new MeshResponse(this.type() + ":" + this.version() + ":" + new CurveVersion());
        }
        return (MeshResponse) controlChain.invoke(new ControlCallable(request.getServiceName() + ":" + request.getMethodName()) {
            @Override
            public Object call() {
                return udsCall(request);
            }
        });
    }


    /**
     * 调用上游服务接口
     */
    public MeshResponse udsCall(ProxyRequest r) {
        try {
            UdsCommand request = UdsCommand.createRequest();
            request.setCmd("call");
            request.setApp(r.getApp());
            request.setServiceName(r.getServiceName());
            request.setMethodName(r.getMethodName());
            request.setParamTypes(r.getParamTypes());
            request.setParams(r.getParams());
            request.setByteParams(Arrays.stream(r.getParams()).map(it->it.getBytes()).toArray(byte[][]::new));
            long timeout = getTimeout(r);
            request.setTimeout(timeout);
            request.putAtt("resultJson", "true");
            UdsCommand res = udsServer.call(request);
            MeshResponse response = new MeshResponse();
            if (res.getCode() != 0) {
                response.setCode(res.getCode());
                response.setMessage(res.getMessage());
            } else {
                String returnType = res.getAtt("returnType","");
                Object data = res.getData(Class.forName(returnType));
                response.setData(new Gson().toJson(data));
            }
            return response;
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            MeshResponse response = new MeshResponse();
            response.setCode(500);
            response.setMessage(ex.getMessage());
            return response;
        }
    }

    private long getTimeout(ProxyRequest r) {
        return r.getTimeout() > 0 ? r.getTimeout() : 1000;
    }

    @Override
    public String type() {
        return ProxyType.http.name();
    }

}
