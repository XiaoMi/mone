package com.xiaomi.youpin.tesla.rcurve.proxy.egress;

import com.google.gson.Gson;
import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.dubbo.DubboCall;
import com.xiaomi.youpin.docean.plugin.dubbo.DubboRequest;
import com.xiaomi.youpin.tesla.proxy.MeshRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * 往外调用dubbo服务(egress)
 */
@Slf4j
@Component
public class DubboEgress implements UdsProcessor {


    @Resource
    private ApplicationConfig applicationConfig;

    @Resource
    private RegistryConfig registryConfig;

    @Resource
    private DubboCall dubboCall;


    public void init() {
        UdsServer server = Ioc.ins().getBean(UdsServer.class);
        server.getProcessorMap().put("dubboCall", this);
    }


    @Override
    public void processRequest(UdsCommand udsCommand) {
        UdsCommand udsRes = UdsCommand.createResponse(udsCommand);
        String serviceName = udsCommand.getServiceName();
        String methodName = udsCommand.getMethodName();

        boolean mesh = udsCommand.getAtt("mesh", "true").equals("true");


        if (!mesh) {
            //非mesh 标准调用(标准的泛化调用)
            standardCall(udsCommand, udsRes);
            return;
        }

        if (mesh) {
            methodName = "invoke";
        }
        String group = "";
        String version = "";


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

//
            reference.setGeneric(true);

            reference.setCheck(false);
            if (StringUtils.isEmpty(version)) {
                version = "";
            }
            reference.setVersion(version);

//            reference.setGeneric(Constants.GENERIC_SERIALIZATION_YOUPIN_JSON);

            //超时设置
            reference.setTimeout((int) udsCommand.getTimeout());

            ReferenceConfigCache cache = ReferenceConfigCache.getCache();
            genericService = cache.get(reference);
        }

        RpcContext.getContext().getAttachments().put(Constants.YOUPIN_PROTOCOL_VERSION, "v1");
        RpcContext.getContext().getAttachments().put("mesh", "true");

        Object res = null;

        try {

            String[] types = udsCommand.getParamTypes();
            Object[] params = udsCommand.getParams();
            if (mesh) {
                types = new String[]{MeshRequest.class.getName()};
                MeshRequest r = new MeshRequest();
                r.setServiceName(udsCommand.getServiceName());
                r.setMethodName(udsCommand.getMethodName());

                //要是对方的app
                r.setApp(udsCommand.getRemoteApp());

                //自己的ip变为远程app
                r.setRemoteApp(udsCommand.getApp());

                r.setParamTypes(udsCommand.getParamTypes());
                r.setParams(udsCommand.getParams());

                params = new Object[]{
                        r
                };
            }
            res = genericService.$invoke(methodName, types, params);
            udsRes.setCode(0);
            udsRes.setData(new Gson().toJson(res));
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            udsRes.setCode(500);
            udsRes.setMessage(ex.getMessage());
        }
        Send.send(UdsServerContext.ins().channel(udsCommand.getApp()), udsRes);
        return;
    }

    /**
     * 标准的调用(泛化调用)
     *
     * @param udsCommand
     * @param udsRes
     */
    private void standardCall(UdsCommand udsCommand, UdsCommand udsRes) {
        try {
            DubboRequest dubboRequest = new DubboRequest();
            dubboRequest.setGroup(udsCommand.getAtt("group", ""));
            dubboRequest.setVersion(udsCommand.getAtt("version", ""));
            dubboRequest.setTimeout(Integer.valueOf(udsCommand.getAtt("timeout", "1000")));
            dubboRequest.setMethodName(udsCommand.getMethodName());
            dubboRequest.setServiceName(udsCommand.getServiceName());
            dubboRequest.setParameterTypes(udsCommand.getParamTypes());
            dubboRequest.setArgs(udsCommand.getParams());
            Object res = dubboCall.call(dubboRequest);
            udsRes.setCode(0);
            udsRes.setData(res);
            Send.send(UdsServerContext.ins().channel(udsCommand.getApp()), udsRes);
        } catch (Throwable ex) {
            String message = "egress dubbo error:" + udsCommand.getApp() + ":" + ex.getMessage();
            log.error(message, ex);
            UdsCommand res = UdsCommand.createErrorResponse(udsCommand.getId(), message);
            Send.sendResponse(udsCommand.getChannel(), res);
        }
    }
}
