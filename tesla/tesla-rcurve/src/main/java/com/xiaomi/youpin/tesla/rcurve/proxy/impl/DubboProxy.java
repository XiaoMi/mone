package com.xiaomi.youpin.tesla.rcurve.proxy.impl;

import com.google.gson.Gson;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
import com.xiaomi.youpin.tesla.rcurve.proxy.Proxy;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyContext;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyType;
import com.xiaomi.youpin.tesla.proxy.MeshRequest;
import com.xiaomi.youpin.tesla.proxy.MeshResponse;
import com.xiaomi.youpin.tesla.proxy.MeshService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.proxy.MeshDubboRequest;
import org.apache.dubbo.rpc.proxy.MeshDubboResponse;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 * 支持dubbo 代理协议
 */
@Slf4j
@Service(interfaceClass = MeshService.class)
public class DubboProxy implements Proxy<MeshRequest, MeshResponse>, MeshService {

    @Resource
    private UdsServer udsServer;


    private Gson gson = new Gson();

    /**
     * 调用上游服务接口
     */
    public MeshResponse udsCall(MeshRequest r) {
        UdsCommand request = UdsCommand.createRequest();
        request.setCmd("call");
        request.setApp(r.getApp());
        request.setRemoteApp(r.getRemoteApp());
        request.setServiceName(r.getServiceName());
        request.setMethodName(r.getMethodName());
        request.setParamTypes(r.getParamTypes());
        request.setParams(r.getParams());
        request.setTimeout(1000);
        UdsCommand res = udsServer.call(request);
        MeshResponse response = new MeshResponse();
        response.setData(res.getData());
        return response;
    }


    /**
     * dubbo 调用的口
     *
     * @param request
     * @return
     */
    @Override
    public MeshResponse invoke(MeshRequest request) {
        ProxyContext context = new ProxyContext();
        context.setType(ProxyType.dubbo);
        return execute(context, request);
    }

    @Override
    public MeshDubboResponse invokeDubbo(MeshDubboRequest request) {
        ProxyContext context = new ProxyContext();
        MeshRequest r = new MeshRequest();
        r.setMethodName(request.getMethodName());
        r.setServiceName(request.getServiceName());
        r.setParamTypes(Arrays.stream(request.getParameterTypes()).map(it->it.getName()).toArray(String[]::new));
        r.setParams(Arrays.stream(request.getArguments()).map(it->gson.toJson(it)).toArray(String[]::new));
        r.setRemoteApp(request.getApp());
        r.setApp(request.getApp());
        MeshResponse rs = execute(context, r);
        MeshDubboResponse mdr = new MeshDubboResponse();
        mdr.setData(rs.getData());
        return mdr;
    }

    @Override
    public MeshResponse execute(ProxyContext context, MeshRequest request) {
        if (request.getMethodName().equals("$version$")) {
            return new MeshResponse(this.type() + ":" + this.version());
        }
        return udsCall(request);
    }

    @Override
    public String type() {
        return ProxyType.dubbo.name();
    }
}
