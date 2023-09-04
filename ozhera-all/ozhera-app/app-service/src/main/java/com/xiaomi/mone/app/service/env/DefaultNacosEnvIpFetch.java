package com.xiaomi.mone.app.service.env;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.Lists;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.mone.app.model.vo.HeraAppEnvVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/29 16:21
 */
@Service
public class DefaultNacosEnvIpFetch implements EnvIpFetch {

    @Resource
    private NacosNaming nacosNaming;

    @Override
    public HeraAppEnvVo fetch(Long appBaseId, Long appId, String appName) throws Exception {
        if(StringUtils.isNotEmpty(appName)){
            appName = appName.replaceAll("-","_");
        }
        List<Instance> instances = nacosNaming.getAllInstances(String.format("%s_%s_%s", SERVER_PREFIX, appId, appName));
        List<HeraAppEnvVo.EnvVo> envVos = getEnvVos(instances);
        return buildHeraAppEnvVo(appBaseId, appId, appName, envVos);
    }


    private List<HeraAppEnvVo.EnvVo> getEnvVos(List<Instance> instances) {
        List<HeraAppEnvVo.EnvVo> envVos = Lists.newArrayList();
        List<String> envIds = Lists.newArrayList();
        List<String> envNames = Lists.newArrayList();
        buildEnvIdAndName(envIds, envNames, instances);

        for (int i = 0; i < envIds.size(); i++) {
            String envId = envIds.get(i);
            HeraAppEnvVo.EnvVo envVo = new HeraAppEnvVo.EnvVo();
            envVo.setEnvId(Long.valueOf(envId));
            envVo.setEnvName(envNames.get(i));
            if (isDefaultEnv(envId)) {
                envVo.setIpList(instances.parallelStream()
                        .filter(instance -> !instance.getMetadata().containsKey(ENV_ID))
                        .map(Instance::getIp).distinct().collect(Collectors.toList()));
            } else {
                envVo.setIpList(instances.parallelStream()
                        .filter(instance -> Objects.equals(envId, instance.getMetadata().get(ENV_ID)))
                        .map(Instance::getIp).distinct().collect(Collectors.toList()));
            }
            envVos.add(envVo);
        }
        return envVos;
    }

    private void buildEnvIdAndName(List<String> envIds, List<String> envNames, List<Instance> instances) {
        if (CollectionUtils.isNotEmpty(instances)) {
            instances.stream().forEach(instance -> {
                Map<String, String> metadata = instance.getMetadata();
                if (metadata.containsKey(ENV_ID)) {
                    String envId = metadata.get(ENV_ID);
                    if (!envIds.contains(envId)) {
                        envIds.add(envId);
                    }
                    String envName = metadata.get(ENV_NAME);
                    if (!envNames.contains(envName)) {
                        envNames.add(envName);
                    }
                } else {
                    if (!envIds.contains(DEFAULT_EVN_ID)) {
                        envIds.add(DEFAULT_EVN_ID);
                    }
                    if (!envNames.contains(DEFAULT_EVN_NAME)) {
                        envNames.add(DEFAULT_EVN_NAME);
                    }
                }
            });
        }
    }

    private boolean isDefaultEnv(String envId) {
        return Objects.equals(envId, DEFAULT_EVN_ID);
    }
}
