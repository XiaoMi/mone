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
            RpcContext.getContext().setAttachment("_must_provider_ip_port_", "true");
            RpcContext.getContext().setAttachment("_provider_ip_", ss[0]);
            RpcContext.getContext().setAttachment("_provider_port_", ss[1]);
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
