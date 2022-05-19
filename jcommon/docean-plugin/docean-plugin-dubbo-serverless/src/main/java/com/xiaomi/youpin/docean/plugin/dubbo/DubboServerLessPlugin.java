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
import com.xiaomi.youpin.docean.plugin.dubbo.common.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.annotation.DubboReference;
import org.apache.dubbo.annotation.DubboService;
import org.apache.dubbo.config.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author goodjava@qq.com
 * @date 2020/6/25
 */
@Slf4j
@DOceanPlugin
public class DubboServerLessPlugin implements IPlugin {

    private ApplicationConfig applicationConfig;

    private RegistryConfig registryConfig;

    private ProtocolConfig protocol;

    private Config config;

    private boolean serverLess = false;


    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init dubbo plugin");
        this.config = ioc.getBean(Config.class);
        serverLess = Boolean.valueOf(this.config.get("serverless", "false"));

        Bean info = ioc.getBeanInfo(DubboConfig.class.getName());
        if (Optional.ofNullable(info).isPresent()) {
            DubboConfig dubboConfig = ioc.getBean(DubboConfig.class);
            applicationConfig = dubboConfig.getApplicationConfig();
            registryConfig = dubboConfig.getRegistryConfig();
        } else {
            return;
        }
        //启动的时候是否check 注册中心
        registryConfig.setCheck(Boolean.valueOf(config.get(Cons.DUBBO_REG_CHECK, Boolean.FALSE.toString())));
        Map<String, String> m = Maps.newHashMap();
        m.put(PropertyKeyConst.NAMING_LOAD_CACHE_AT_START, config.get(Cons.DUBBO_LOAD_CACHE_AT_START, Boolean.TRUE.toString()));
        registryConfig.setParameters(m);
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
            Method protocolMethod = clazz.getMethod("protocol", ProtocolConfig.class);
            obj = applicationMethod.invoke(obj, this.applicationConfig);
            obj = registryMethod.invoke(obj, this.registryConfig);
            protocolMethod.invoke(obj, this.protocol);
        } catch (Throwable ex) {
            log.info(ex.getMessage());
        }

        DubboCall dubboCall = new DubboCall(this.applicationConfig, this.registryConfig);
        ioc.putBean(dubboCall);
    }


    public void initService(Ioc ioc, Bean bean, ServiceInfo si) {
        ServiceConfig<Object> serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setInterface(si.getInterfaceClass());
        serviceConfig.setRef(bean.getObj());
        serviceConfig.setGroup(getGroup(ioc, si.getGroup()));
        serviceConfig.setVersion(si.getVersion());
        serviceConfig.setProtocol(protocol);
        serviceConfig.setTimeout(si.getTimeout());
        serviceConfig.setAsync(si.isAsync());
        if (serverLess) {
            Map<String, String> map = serviceConfig.getParameters();
            if (null == map) {
                map = new HashMap<>(1);
                map.put("server_less", "true");
                serviceConfig.setParameters(map);
            }
        }
        serviceConfig.export(ioc.getClassLoader());
    }


    private ProtocolConfig getProtocolConfig(int dubboPort, int dubboThreads) {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("dubbo");
        protocol.setPort(dubboPort);
        protocol.setThreads(dubboThreads);
        return protocol;
    }

    /**
     * 支持自定义注解和标准注解
     * @return
     */
    @Override
    public List<Class<? extends Annotation>> filterAnnotations() {
        return Lists.newArrayList(Service.class, DubboService.class);
    }

    @Override
    public Bean initBean(Ioc ioc, Bean bean) {
        Service s = bean.getClazz().getAnnotation(Service.class);
        if (Optional.ofNullable(s).isPresent()) {
            ServiceInfo si = DubboInfoUtils.getService(s);
            initService(ioc, bean, si);
        } else {
            DubboService s2 = bean.getClazz().getAnnotation(DubboService.class);
            if (Optional.ofNullable(s2).isPresent()) {
                ServiceInfo si = DubboInfoUtils.getService(s2);
                initService(ioc, bean, si);
            }
        }
        return bean;
    }

    /**
     * 注入reference的dubbo服务
     * @param ioc
     * @param annotations
     * @return
     */
    @Override
    public Optional<String> ioc(Ioc ioc, Annotation[] annotations) {
        ReferenceInfo info = null;
        Optional<Annotation> optional = getAnno(annotations, Reference.class);
        if (optional.isPresent()) {
            info = DubboInfoUtils.getReference((Reference) optional.get());
        } else {
            Optional<Annotation> optional2 = getAnno(annotations, DubboReference.class);
            if (optional2.isPresent()) {
                info = DubboInfoUtils.getReference((DubboReference) optional2.get());
            }
        }
        if (null != info) {
            ReferenceConfig<Object> referenceConfig = new ReferenceConfig<>();
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setRegistry(registryConfig);
            referenceConfig.setInterface(info.getInterfaceClass());
            referenceConfig.setGroup(getGroup(ioc, info.getGroup()));
            referenceConfig.setCheck(info.isCheck());
            referenceConfig.setVersion(getVersion(ioc, info.getVersion()));
            referenceConfig.setTimeout(info.getTimeout());
            referenceConfig.setCluster(info.getCluster());
            if (serverLess) {
                Map<String, String> map = referenceConfig.getParameters();
                if (null == map) {
                    map = new HashMap<>(1);
                    map.put("server_less", "true");
                    referenceConfig.setParameters(map);
                }
            }
            Object service = referenceConfig.get(ioc.getClassLoader());
            ioc.putBean(info.getInterfaceClass().getName(), service);
            return Optional.of(info.getInterfaceClass().getName());
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
    public String version() {
        return "0.0.1:2021-03-25";
    }
}

