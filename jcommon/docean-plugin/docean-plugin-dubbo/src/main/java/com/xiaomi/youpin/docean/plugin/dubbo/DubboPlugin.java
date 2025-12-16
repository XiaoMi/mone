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

import com.alibaba.nacos.api.PropertyKeyConst;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.common.Cons;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static org.apache.dubbo.common.constants.CommonConstants.REMOTE_METADATA_STORAGE_TYPE;

/**
 * @author goodjava@qq.com
 * @date 2020/6/25
 */
@Slf4j
@DOceanPlugin
public class DubboPlugin implements IPlugin {


    private ApplicationConfig applicationConfig;
    private RegistryConfig registryConfig;
    private MetadataReportConfig metadataReportConfig;
    private ConfigCenterConfig configCenterConfig;
    private ProtocolConfig protocol;


    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init dubbo plugin");
        Config config = ioc.getBean(Config.class);
        applicationConfig = new ApplicationConfig(config.get(Cons.DUBBO_APP_NAME, ""));
        applicationConfig.setQosEnable(false);
        //注册方式，默认：接口和应用维度
        applicationConfig.setRegisterMode(CommonConstants.DEFAULT_REGISTER_MODE);
        applicationConfig.setEnableFileCache(false);
        applicationConfig.setMetadataType(REMOTE_METADATA_STORAGE_TYPE);
        Map<String, String> appParams = new HashMap<>();
        appParams.put("nacos.subscribe.legacy-name","true");
        applicationConfig.setParameters(appParams);

        //3.0 才有的参数
        if (!config.get("serializeCheckStatus", "").isEmpty()) {
            applicationConfig.setSerializeCheckStatus(config.get("serializeCheckStatus",""));
        }

        registryConfig = registryConfig(config);
        metadataReportConfig = metadataReportConfig(config);
        configCenterConfig = configCenterConfig(config);
        int dubboPort = Integer.valueOf(config.get(Cons.DUBBO_PORT, "-1"));
        int dubboThreads = Integer.valueOf(config.get(Cons.DUBBO_THREADS, "200"));
        protocol = getProtocolConfig(dubboPort, dubboThreads);
        ioc.putBean(applicationConfig);
        ioc.putBean(registryConfig);
        try {
            //3.0 需要初始化DubboBootstrap,但dubbo2 并没有这个类,用次hack的方法fix 掉
            Class<?> clazz = Class.forName("org.apache.dubbo.config.bootstrap.DubboBootstrap");
            Method method = clazz.getMethod("getInstance");
            Object obj = method.invoke(null);
            Method applicationMethod = clazz.getMethod("application", ApplicationConfig.class);
            Method registryMethod = clazz.getMethod("registry", RegistryConfig.class);
            Method metadataReportMethod = clazz.getMethod("metadataReport", MetadataReportConfig.class);
            Method configCenterMethod = clazz.getMethod("configCenter", ConfigCenterConfig.class);
            Method protocolMethod = clazz.getMethod("protocol", ProtocolConfig.class);

            //DubboBootstrap.getInstance().application(this.applicationConfig).registry(this.registryConfig).protocol(this.protocol);
            obj = applicationMethod.invoke(obj, this.applicationConfig);
            obj = registryMethod.invoke(obj, this.registryConfig);
            obj = metadataReportMethod.invoke(obj, this.metadataReportConfig);
            obj = configCenterMethod.invoke(obj, this.configCenterConfig);
            protocolMethod.invoke(obj, this.protocol);
        } catch (Throwable ex) {
            log.error("DubboPlugin init error", ex.getMessage());
        }

        DubboCall dubboCall = new DubboCall(this.applicationConfig, this.registryConfig, metadataReportConfig, configCenterConfig);
        ioc.putBean(dubboCall);
    }

    private RegistryConfig registryConfig(Config config) {
        RegistryConfig registryConfig = new RegistryConfig(config.get(Cons.DUBBO_REG_ADDRESS, ""));
        //启动的时候是否check 注册中心
        registryConfig.setCheck(Boolean.valueOf(config.get(Cons.DUBBO_REG_CHECK, Boolean.FALSE.toString())));
        Map<String, String> m = Maps.newHashMap();
        m.put(PropertyKeyConst.NAMING_LOAD_CACHE_AT_START, config.get(Cons.DUBBO_LOAD_CACHE_AT_START, Boolean.TRUE.toString()));
        registryConfig.setParameters(m);
        return registryConfig;
    }

    private MetadataReportConfig metadataReportConfig(Config config) {
        String metaAddr = config.get(Cons.DUBBO_REG_META_ADDRESS, "");
        if (StringUtils.isEmpty(metaAddr)) {
            metaAddr = config.get(Cons.DUBBO_REG_ADDRESS, "");
        }
        MetadataReportConfig metadataReportConfig = new MetadataReportConfig();
        metadataReportConfig.setId("meta");
        metadataReportConfig.setAddress(metaAddr);
        return metadataReportConfig;
    }

    private ConfigCenterConfig configCenterConfig(Config config) {
        String cfgAddr = config.get(Cons.DUBBO_REG_CFG_ADDRESS, "");
        if (StringUtils.isEmpty(cfgAddr)) {
            cfgAddr = config.get(Cons.DUBBO_REG_ADDRESS, "");
        }
        ConfigCenterConfig configCenterConfig = new ConfigCenterConfig();
        configCenterConfig.setId("cfg");
        configCenterConfig.setAddress(cfgAddr);
        return configCenterConfig;
    }


    public void initService(Ioc ioc, Bean bean) {
        ServiceConfig<Object> serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setMetadataReportConfig(metadataReportConfig);
        serviceConfig.setConfigCenter(configCenterConfig);
        Service s = bean.getClazz().getAnnotation(Service.class);
        serviceConfig.setInterface(s.interfaceClass());
        serviceConfig.setRef(bean.getObj());
        serviceConfig.setGroup(getGroup(ioc, s.group()));
        serviceConfig.setVersion(getVersion(ioc, s.version()));
        serviceConfig.setProtocol(protocol);
        serviceConfig.setTimeout(s.timeout());
        serviceConfig.setAsync(s.async());
        serviceConfig.export();
    }


    private ProtocolConfig getProtocolConfig(int dubboPort, int dubboThreads) {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("dubbo");
        protocol.setPort(dubboPort);
        protocol.setThreads(dubboThreads);
        return protocol;
    }

    @Override
    public List<Class<? extends Annotation>> filterAnnotations() {
        return Lists.newArrayList(Service.class);
    }

    @Override
    public Bean initBean(Ioc ioc, Bean bean) {
        Service s = bean.getClazz().getAnnotation(Service.class);
        if (Optional.ofNullable(s).isPresent()) {
            initService(ioc, bean);
        }
        return bean;
    }

    @Override
    public Optional<String> ioc(Ioc ioc, Class type, Annotation[] annotations) {
        Optional<Annotation> optional = getAnno(annotations, Reference.class);
        if (optional.isPresent()) {
            Reference reference = (Reference) optional.get();
            ReferenceConfig<Object> referenceConfig = new ReferenceConfig<>();
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setRegistry(registryConfig);
            referenceConfig.setMetadataReportConfig(metadataReportConfig);
            referenceConfig.setConfigCenter(configCenterConfig);
            referenceConfig.setInterface(reference.interfaceClass());
            referenceConfig.setGroup(getGroup(ioc, reference.group()));
            referenceConfig.setCheck(reference.check());
            referenceConfig.setVersion(getVersion(ioc, reference.version()));
            referenceConfig.setTimeout(reference.timeout());
            referenceConfig.setCluster(reference.cluster());
            Object service = referenceConfig.get();
            ioc.putBean(reference.interfaceClass().getName(), service);
            return Optional.of(reference.interfaceClass().getName());
        }
        return Optional.empty();
    }


    private String getGroup(Ioc ioc, String group) {
        if (group.startsWith("$")) {
            return ioc.getBean(group);
        }
        return group;
    }

    private String getVersion(Ioc ioc, String version) {
        if (version.startsWith("$")) {
            return ioc.getBean(version);
        }
        return version;
    }

    @Override
    public boolean disable(Ioc ioc) {
        Config config = ioc.getBean(Config.class);
        return Boolean.valueOf(config.get("disable_dubbo_plugin", "false"));
    }

    @Override
    public String version() {
        return "0.0.1:2021-01-16";
    }
}

