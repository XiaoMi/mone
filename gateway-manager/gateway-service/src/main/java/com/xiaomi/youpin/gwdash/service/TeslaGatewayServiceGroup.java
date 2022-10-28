package com.xiaomi.youpin.gwdash.service;

import com.google.gson.Gson;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.FilterParam;
import com.youpin.xiaomi.tesla.bo.PlugInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2022/10/4 10:36
 * <p>
 * gatewayService分组(比如tesla 中国区外网  中国区内网)
 */
@Service
@Slf4j
public class TeslaGatewayServiceGroup {

    @Resource
    private ApplicationConfig applicationConfig;

    @Resource
    private RegistryConfig registryConfig;

    public static String serviceName = "com.youpin.xiaomi.tesla.service.TeslaGatewayService";

    public static String version = "";

    private ConcurrentHashMap<String, Long> tenantUpdateTime = new ConcurrentHashMap<>();

    private Gson gson = new Gson();


    public Long getLastUpdateTime(String tenant) {
        if (StringUtils.isEmpty(tenant)) {
            tenant = "";
        }
        if (null == tenantUpdateTime.get(tenant)) {
            tenantUpdateTime.put(tenant, System.currentTimeMillis());
        }
        return tenantUpdateTime.getOrDefault(tenant, System.currentTimeMillis());
    }

    /**
     * 更新filter
     *
     * @param tenant
     * @param name
     * @param groups
     * @param type
     */
    public void updateFilter(String tenant, String name, String groups, String type) {
        log.info("update filter:{}:{}", name, tenant);
        FilterParam param = new FilterParam();
        param.setName(name);
        param.setGroups(groups);
        param.setType(type);
        callDubbo(param, serviceName, "updateFilter", tenant, version, new String[]{"com.youpin.xiaomi.tesla.bo.FilterParam"}, "broadcast");
    }

    public Object getGatewayInfo(String tenant) {
        return callDubbo(null, serviceName, "getGatewayInfo", tenant, version, new String[]{}, "");
    }

    public void stopPlugin(String tenant, PlugInfo plugInfo) {
        callDubbo(plugInfo, serviceName, "stopPlugin", tenant, version, new String[]{"com.youpin.xiaomi.tesla.bo.PlugInfo"}, "broadcast");
    }

    public void stopOnePlugin(String tenant, PlugInfo plugInfo) {
        callDubbo(plugInfo, serviceName, "stopPlugin", tenant, version, new String[]{"com.youpin.xiaomi.tesla.bo.PlugInfo"}, "");
    }

    public void startPlugin(String tenant, PlugInfo plugInfo) {
        callDubbo(plugInfo, serviceName, "startPlugin", tenant, version, new String[]{"com.youpin.xiaomi.tesla.bo.PlugInfo"}, "broadcast");
    }

    public void startOnePlugin(String tenant, PlugInfo plugInfo) {
        callDubbo(plugInfo, serviceName, "startPlugin", tenant, version, new String[]{"com.youpin.xiaomi.tesla.bo.PlugInfo"}, "");
    }

    /**
     * 更新路由信息
     *
     * @param apiInfo
     */
    public void updateApiInfo(ApiInfo apiInfo) {
        log.info("updateApiInfo id:{} tenant:{}", apiInfo.getUrl(), apiInfo.getTenant());
        tenantUpdateTime.put(apiInfo.getTenant(), System.currentTimeMillis());
        callDubbo(apiInfo, serviceName, "updateApiInfo", apiInfo.getTenant(), version, new String[]{"com.youpin.xiaomi.tesla.bo.ApiInfo"}, "broadcast");
    }


    /**
     * 会根据不同的tenant调用到不同的网关组
     *
     * @param param
     * @param serviceName
     * @param methodName
     * @param group
     * @param version
     * @param parameterTypes
     * @return
     */
    public Object callDubbo(Object param, String serviceName, String methodName, String group, String version, String[] parameterTypes, String cluster) {
        String key = ReferenceConfigCache.getKey(serviceName, group, version);
        GenericService genericService = ReferenceConfigCache.getCache().get(key);
        if (null == genericService) {
            log.info("create reference:{}", key);
            ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
            reference.setApplication(applicationConfig);
            reference.setRegistry(this.registryConfig);
            reference.setInterface(serviceName);
            reference.setVersion(version);
            reference.setGroup(group);
            if (StringUtils.isNotEmpty(cluster)) {
                reference.setCluster("broadcast");
            }
            reference.setGeneric(true);
            reference.setGeneric("youpin_json");
            ReferenceConfigCache cache = ReferenceConfigCache.getCache();
            genericService = cache.get(reference);
        }
        try {
            RpcContext.getContext().setAttachment("gson_generic_args", "true");
            Object[] params;
            if (null == param) {
                params = new Object[]{};
            } else {
                params = new Object[]{gson.toJson(param)};
            }
            Object res = genericService.$invoke(methodName, parameterTypes, params);
            log.info("call:{} group:{} res:{}", methodName, group, res);
            return res;
        } finally {
            RpcContext.getContext().clearAttachments();
        }
    }

}
