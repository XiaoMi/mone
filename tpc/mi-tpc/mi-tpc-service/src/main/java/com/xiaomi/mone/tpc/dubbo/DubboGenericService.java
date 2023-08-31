package com.xiaomi.mone.tpc.dubbo;

import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/4/6 16:55
 */
@Slf4j
@Service
public class DubboGenericService {

    private final Map<String, GenericService> genericMap = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationConfig applicationConfig;
    @Autowired
    private RegistryConfig registryConfig;

    /**
     * 获取泛化调用接口
     * @param service
     * @param version
     * @param group
     * @return
     */
    private GenericService get(String service,String version, String group, String regAddress) {
        StringBuilder strKey = new StringBuilder();
        strKey.append(service);
        if (StringUtils.isNotEmpty(group)) {
            strKey.append(".").append(group);
        }
        if (StringUtils.isNotEmpty(version)) {
            strKey.append(".").append(version);
        }
        if (StringUtils.isNotEmpty(regAddress)) {
            strKey.append(".").append(regAddress);
        }
        String key = strKey.toString();
        GenericService genericService = genericMap.get(key);
        if (genericService != null) {
            return genericService;
        }
        synchronized (genericMap) {
            genericService = genericMap.get(key);
            if (genericService != null) {
                return genericService;
            }
            ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
            reference.setApplication(applicationConfig);
            if (StringUtils.isNotBlank(regAddress)) {
                RegistryConfig selfRegistryConfig = new RegistryConfig();
                selfRegistryConfig.setAddress(regAddress);
                reference.setRegistry(selfRegistryConfig);
            } else {
                reference.setRegistry(registryConfig);
            }
            reference.setInterface(service);
            reference.setGroup(group);
            reference.setVersion(version);
            reference.setCheck(false);
            reference.setGeneric(true);
            reference.setTimeout(8000);
            reference.setRetries(0);
            genericService = reference.get();
            genericMap.put(key, genericService);
        }
        return genericService;
    }

    public ResultVo dubboCall(String service, String method, String group, String version, String regAddress, Map<String, Object> arg) {
        try {
            GenericService genericService = get(service, version, group, regAddress);
            log.info("dubbo泛化调用请求{}", arg);
            Map<String, Object> map = (Map<String, Object>)genericService.$invoke(method, new String[] {"java.util.Map"}, new Object[] {arg});
            log.info("dubbo泛化调用请求响应{}", map);
            if (map != null && map.containsKey("code") && "0".equals(map.get("code").toString())) {
                return ResponseCode.SUCCESS.build();
            }
            return ResponseCode.OUTER_CALL_FAILED.build();
        } catch (Throwable e) {
            log.error("dubbo泛化调用异常{}", arg, e);
            return ResponseCode.OUTER_CALL_FAILED.build();
        }
    }
}
