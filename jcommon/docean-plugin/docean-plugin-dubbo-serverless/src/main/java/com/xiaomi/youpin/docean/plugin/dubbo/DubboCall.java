package com.xiaomi.youpin.docean.plugin.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;

/**
 * @author goodjava@qq.com
 * @date 1/16/21
 */
@Slf4j
public class DubboCall {

    private ApplicationConfig applicationConfig;

    private RegistryConfig registryConfig;

    public DubboCall(ApplicationConfig applicationConfig, RegistryConfig registryConfig) {
        this.applicationConfig = applicationConfig;
        this.registryConfig = registryConfig;
    }

    /**
     * 直接触发泛化调用
     * @param request
     * @return
     */
    public Object call(DubboRequest request) {

        RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, String.valueOf(request.getTimeout()));
        String key = ReferenceConfigCache.getKey(request.getServiceName(), request.getGroup(), request.getVersion());
        GenericService genericService = ReferenceConfigCache.getCache().get(key);
        boolean create = false;
        if (null == genericService) {
            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
            reference.setApplication(applicationConfig);
            reference.setRegistry(registryConfig);
            reference.setInterface(request.getServiceName());
            reference.setGeneric(true);
            reference.setCheck(false);
            reference.setGroup(request.getGroup());
            reference.setVersion(request.getVersion());
            reference.setTimeout(request.getTimeout());
            ReferenceConfigCache cache = ReferenceConfigCache.getCache();
            genericService = cache.get(reference);
            create = true;
        }
        log.info("call key:{} {} {}", key, genericService,create);

        /**
         * 定向调用
         */
        if (StringUtils.isNotEmpty(request.getAddr())) {
            String[] ss = request.getAddr().split(":");
            RpcContext.getContext().setAttachment(Constants.MUST_PROVIDER_IP_PORT, "true");
            RpcContext.getContext().setAttachment(Constants.PROVIDER_IP, ss[0]);
            RpcContext.getContext().setAttachment(Constants.PROVIDER_PORT, ss[1]);
        }

        Object res = null;
        try {
            res = genericService.$invoke(request.getMethodName(), request.getParameterTypes(), request.getArgs());
        } catch (Exception e) {
            log.error("DubboTask call error", e);
            throw new RuntimeException(e);
        } finally {
            RpcContext.getContext().clearAttachments();
        }

        return res;

    }

}
