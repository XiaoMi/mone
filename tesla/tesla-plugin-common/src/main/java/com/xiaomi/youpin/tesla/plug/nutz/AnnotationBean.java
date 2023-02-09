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

package com.xiaomi.youpin.tesla.plug.nutz;


import com.google.gson.Gson;
import com.xiaomi.youpin.tesla.plug.config.TeslaConfig;
import com.xiaomi.youpin.tesla.plug.ioc.IocInit;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.ClassHelper;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.apache.dubbo.config.spring.ServiceBean;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.random.R;
import org.nutz.resource.Scans;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;


/**
 * @author goodjava@qq.com
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@IocBean(depose = "depose")
@Slf4j
public class AnnotationBean {

    @Inject
    protected Ioc ioc;

    @Inject
    protected Map<String, IocObject> iobjs;

    @Inject
    private String[] annotationPackages;

    private ApplicationConfig applicationConfig = null;
    private RegistryConfig registryConfig = null;
    private ProtocolConfig protocolConfig = null;

    private List<ServiceBean> providerList = new ArrayList<>();
    private List<ReferenceConfig> consumerList = new ArrayList<>();

    private Function<String, ClassLoader> func;

    public AnnotationBean() {

    }

    public void depose() {
        try {
            long begin = System.currentTimeMillis();
            log.info("AnnotationBean depose begin");

            if (null != func) {
                log.info("remove classLoader func");
                ClassHelper.classLoaderFunSet.remove(func);
            }

            providerList.stream().forEach(it -> {
                try {
                    log.info("depose:{}", it);
                    it.unexport();
                } catch (Throwable ex) {
                    log.error(ex.getMessage(), ex);
                }
            });

            consumerList.stream().forEach(it -> {
                try {
                    it.destroy();
                } catch (Throwable ex) {
                    log.error(ex.getMessage(), ex);
                }
            });

            RpcContext.getContext().clearAttachments();
            RpcContext.getServerContext().clearAttachments();

            log.info("AnnotationBean depose end:{}", System.currentTimeMillis() - begin);
        } finally {
            //解除整体的阻塞
            IocInit.ins().destoryFinish();
        }
    }


    public void load() {
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        log.info("AnnotationBean load:{} {} {}", new Gson().toJson(TeslaConfig.ins().getDubboDatasource()), new Gson().toJson(this.annotationPackages), Thread.currentThread().getContextClassLoader());

        //没有配置dubbo数据源
        if (null == TeslaConfig.ins().getDubboDatasource()) {
            return;
        }

        String apiPackage = TeslaConfig.ins().getDubboDatasource().getApiPackage();
        if (StringUtils.isNotEmpty(apiPackage.trim())) {
            func = (desc) -> {
                //这里需要配置
                String ap = apiPackage.replaceAll("\\.", "/");
                log.info("get classLoader desc{} value:{} {}", desc, ap, desc.contains(ap));
                if (desc.contains(ap)) {
                    return AnnotationBean.class.getClassLoader();
                }
                return null;
            };
        }

        this.applicationConfig = new ApplicationConfig(TeslaConfig.ins().getDubboDatasource().getAppName());
        this.applicationConfig.setQosEnable(false);
        this.registryConfig = new RegistryConfig(TeslaConfig.ins().getDubboDatasource().getRegAddress());

        this.protocolConfig = new ProtocolConfig();
        protocolConfig.setPort(-1);
        protocolConfig.setTransporter("netty4");
        protocolConfig.setThreadpool("fixed");

        int threads = TeslaConfig.ins().getDubboDatasource().getThreads() > 0 ? TeslaConfig.ins().getDubboDatasource().getThreads() : 100;

        protocolConfig.setThreads(threads);


        Set<Field> consumers = new HashSet<>();
        Set<Class> services = new HashSet<>();
        for (String pkg : annotationPackages) {
            for (Class klass : Scans.me().scanPackage(pkg)) {
                if (klass.isInterface()) {
                    continue;
                }
                Service service = (Service) klass.getAnnotation(Service.class);
                if (service != null) {
                    log.info("--->dubbo add service:{}", klass);
                    services.add(klass);
                }
                for (Field field : klass.getDeclaredFields()) {
                    if (field.getType().isInterface() && field.getAnnotation(Reference.class) != null) {
                        consumers.add(field);
                    }
                }
            }
        }

        //consumer
        for (Field field : consumers) {
            Reference ref = field.getAnnotation(Reference.class);
            ReferenceConfig<?> rc = new ReferenceConfig<>(ref);
            rc.setApplication(applicationConfig);
            rc.setRegistry(registryConfig);
            rc.setInterface(field.getType());
            rc.setGroup(ref.group());
            rc.setRetries(ref.retries());
            rc.setTimeout(ref.timeout());
            String name = R.UU32();
            IocObject iobj = Iocs.wrap(rc);
            iobj.setType(ReferenceBean.class);
            DubboAgent.checkIocObject(name, iobj);
            iobjs.put(name, iobj);

            //构造工厂
            iobj = new IocObject();
            iobj.setType(field.getType());
            iobj.setFactory("$" + name + "#get");
            iobjs.put(R.UU32(), iobj);
        }

        //provider
        for (Class klass : services) {
            Service service = (Service) klass.getAnnotation(Service.class);
            ServiceBean sc = new ServiceBean<>(service);
            sc.setApplication(applicationConfig);
            sc.setRegistry(registryConfig);
            sc.setProtocol(protocolConfig);
            sc.setGroup(service.group());
            sc.setVersion(service.version());
            sc.setRetries(service.retries());
            sc.setTag(service.tag());
            //使用jdk代理
//            sc.setProxy("jdk");
            //从ioc容器中获取实现类
            sc.setRef(ioc.getByType(klass));
            //导出
            sc.export();

            providerList.add(sc);

            String name = R.UU32();
            IocObject iobj = Iocs.wrap(sc);
            iobj.setType(ServiceBean.class);
            DubboAgent.checkIocObject(name, iobj);
            iobjs.put(name, iobj);
        }

        if (null != func) {
            ClassHelper.classLoaderFunSet.add(func);
        }

        log.info("classLoaderFunSet size:{}", ClassHelper.classLoaderFunSet.size());

        DubboProtocol protocol = DubboProtocol.getDubboProtocol();
        Map<String, Exporter<?>> exporterMap = protocol.getExporterMap();

        //需要清除掉,不然下载加载有问题(cache中还保留原来classlader加载出来的类)
        ReflectUtils.clear();

        log.info("plugin exporterMap:{} : {}", exporterMap, protocol);
    }

}
