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

package com.xiaomi.youpin.docean.plugin.dmesh;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.context.NetListener;
import com.xiaomi.data.push.uds.context.UdsClientContext;
import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.processor.client.CallMethodProcessor;
import com.xiaomi.data.push.uds.processor.client.RocketMqProcessor;
import com.xiaomi.data.push.uds.processor.sever.PingProcessor;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourcePlugin;
import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;
import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshReference;
import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshService;
import com.xiaomi.youpin.docean.plugin.dmesh.common.Cons;
import com.xiaomi.youpin.docean.plugin.dmesh.ds.Datasource;
import com.xiaomi.youpin.docean.plugin.dmesh.interceptor.*;
import com.xiaomi.youpin.docean.plugin.dmesh.ms.MySql;
import com.xiaomi.youpin.docean.plugin.dmesh.processor.client.MessageProcessor;
import com.xiaomi.youpin.docean.plugin.dmesh.service.MeshServiceConfig;
import com.xiaomi.youpin.docean.plugin.dmesh.state.client.ClientFsm;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * <p>
 * mesh 框架插件(主要用于编写sidecar代码)
 */
@Slf4j
@DOceanPlugin
public class DmeshPlugin implements IPlugin {

    /**
     * 这个app的名称
     */
    private String app;

    private Config config;

    private AtomicBoolean init = new AtomicBoolean(false);

    private boolean server = false;

    private String udsPath = "";

    public static final String SERVER_LIST = "serviceConfigList";


    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("DmeshPlugin init");
        config = ioc.getBean(Config.class);
        String udsPath = config.get("uds_path", "");

        if (StringUtils.isEmpty(udsPath)) {
            log.info("uds_path is null return");
            return;
        }
        this.udsPath = udsPath;
        String udsServer = config.get("udsServer", "false");
        this.server = udsServer.equals("true");
        if (server) {
            startServer();
        } else {
            startClient(ioc, udsPath);
        }
        List<MeshServiceConfig> list = Lists.newArrayList();
        Ioc.ins().putBean(SERVER_LIST, list);
    }

    private void startClient(Ioc ioc, String udsPath) {
        this.app = config.get("uds_app", "");
        UdsClient udsClient = new UdsClient(this.app);
        String remote = config.get("mesh_remote", "");
        if (!remote.equals("")) {
            log.info("mesh remote:{}", remote);
            udsClient.setRemote(true);
            udsClient.setHost(remote.split(":")[0]);
            udsClient.setPort(Integer.valueOf(remote.split(":")[1]));
        }
        regClientProcessor(ioc, udsClient);
        Ioc.ins().putBean(udsClient);
        new Thread(() -> udsClient.start(udsPath)).start();
    }

    private void regClientProcessor(Ioc ioc, UdsClient udsClient) {
        CallMethodProcessor callProcessor = new CallMethodProcessor(udsCommand -> ioc.getBean(udsCommand.getServiceName()));
        udsClient.getProcessorMap().put(callProcessor.cmd(), callProcessor);
        RocketMqProcessor rocketmqProcessor = new RocketMqProcessor();
        udsClient.getProcessorMap().put(rocketmqProcessor.cmd(), rocketmqProcessor);
        MessageProcessor messageProcessor = new MessageProcessor();
        udsClient.getProcessorMap().put(messageProcessor.cmd(), messageProcessor);
    }

    private void startServer() {
        UdsServer server = new UdsServer();
        String remote = config.get("mesh_remote", "");
        if (!remote.equals("")) {
            log.info("mesh remote:{}", remote);
            server.setRemote(true);
            server.setHost(remote.split(":")[0]);
            server.setPort(Integer.valueOf(remote.split(":")[1]));
        }
        server.getProcessorMap().put("ping", new PingProcessor());
        Ioc.ins().putBean(server);
    }

    @Override
    public Optional<String> ioc(Ioc ioc, Class type, Annotation[] annotations) {
        Config config = ioc.getBean(Config.class);
        Optional<Annotation> optional = getAnno(annotations, MeshReference.class);
        if (optional.isPresent()) {
            MeshReference reference = (MeshReference) optional.get();
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(reference.interfaceClass());
            enhancer.setCallback(new CallMethodInterceptor(ioc, config, reference));
            Object service = enhancer.create();
            ioc.putBean(reference.interfaceClass().getName(), service);
            return Optional.of(reference.interfaceClass().getName());
        }
        return Optional.empty();
    }


    @Override
    public Bean initBean(Ioc ioc, Bean bean) {
        //获取所有服务列表
        MeshService s = bean.getClazz().getAnnotation(MeshService.class);
        if (Optional.ofNullable(s).isPresent()) {
            MeshServiceConfig config = initService(ioc, bean, s);
            List<MeshServiceConfig> list = Ioc.ins().getBean(SERVER_LIST);
            list.add(config);
        }

        MeshMsService ms = bean.getClazz().getAnnotation(MeshMsService.class);

        if (Optional.ofNullable(ms).isPresent()) {
            if (ms.name().equals("mysql")) {
                if (ioc.containsBean(DatasourcePlugin.DB_NAMES)) {
                    List<String> dsList = ioc.getBean(DatasourcePlugin.DB_NAMES);
                    //多数据源
                    if (dsList.size() >= 2) {
                        dsList.stream().forEach(dsName -> {
                            Enhancer enhancer = new Enhancer();
                            enhancer.setSuperclass(ms.interfaceClass());
                            enhancer.setCallback(new CallMysqlInterceptor(ioc, this.config, ms));
                            Object service = enhancer.create();
                            String name = bean.getClazz().getName();
                            ioc.putBean(name, name, service, dsName, true);
                        });
                        return bean;
                    }
                }
            }
        }

        if (Optional.ofNullable(ms).isPresent()) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(ms.interfaceClass());
            if (ms.name().equals("redis")) {
                enhancer.setCallback(new CallRedisInterceptor(ioc, this.config, ms));
            }
            if (ms.name().equals("mysql")) {
                enhancer.setCallback(new CallMysqlInterceptor(ioc, this.config, ms));
            }
            if (ms.name().equals("rocketmq")) {
                enhancer.setCallback(new CallRocketMqInterceptor(ioc, this.config, ms));
            }
            if (ms.name().equals("http")) {
                enhancer.setCallback(new CallHttpInterceptor(ioc, this.config, ms));
            }
            if (ms.name().equals("nacos")) {
                enhancer.setCallback(new CallNacosInterceptor(ioc, this.config, ms));
            }
            Object service = enhancer.create();
            ioc.putBean(bean.getClazz().getName(), service);
        }


        return bean;
    }

    @Override
    public List<Class<? extends Annotation>> filterAnnotations() {
        return Lists.newArrayList(MeshService.class, MeshMsService.class);
    }


    /**
     * 提供provider的必要信息
     *
     * @param ioc
     * @param bean
     * @param s
     * @return
     */
    public MeshServiceConfig initService(Ioc ioc, Bean bean, MeshService s) {
        MeshServiceConfig serviceConfig = new MeshServiceConfig();
        serviceConfig.setServiceName(s.interfaceClass().getName());
        serviceConfig.setGroup(s.group());
        serviceConfig.setVersion(s.version());
        //使用这个应用的app name
        serviceConfig.setApp(this.app);
        ioc.putBean(s.interfaceClass().getName(), bean.getObj());
        return serviceConfig;
    }


    /**
     * 同步服务信息到 mesh proxy服务器
     *
     * @param ioc
     * @return
     */
    @Override
    public boolean after(Ioc ioc) {
        String udsServer = config.get("udsServer", "false");
        String udsClient = config.get("udsClient", "false");
        //如果是服务端,不需要上报
        if (udsServer.equals(Cons.TRUE)) {
            //网络请求的监听器(服务器的监听)
            NetListener listener = ioc.getBean(NetListener.class);
            UdsServerContext.ins().setListener(listener);
            return true;
        }
        if (udsClient.equals(Cons.TRUE)) {
            NetListener listener = ioc.getBean("clientNetListener");
            UdsClientContext.ins().setListener(listener);
            //客户端需要启动状态机
            ClientFsm fsm = Ioc.ins().getBean(ClientFsm.class);
            new Thread(() -> fsm.execute()).start();
        }
        return true;
    }

    @Override
    public void destory(Ioc ioc) {
        log.info("Dmesh plugin destory");
    }

    @Override
    public boolean start(Ioc ioc) {
        if (server) {
            UdsServer ser = ioc.getBean(UdsServer.class);
            new Thread(() -> {
                Safe.runAndLog(() -> clearSockFile(udsPath));
                ser.start(udsPath);
            }).start();
        }
        return true;
    }

    public void clearSockFile(String path) throws IOException {
        if (Files.exists(Paths.get(path))) {
            Files.delete(Paths.get(path));
        }
    }

    @Override
    public String version() {
        return "0.0.1:2021-01-17";
    }


    public static Map<String, MySql> getMySql() {
        Set<MySql> list = Ioc.ins().getBeans(MySql.class);
        if (list.size() == 1) {
            Map<String, MySql> m = Maps.newHashMap();
            m.put("", Ioc.ins().getBean(MySql.class));
            return m;
        }
        return list.stream().filter(it -> null != it).map(it -> {
            Bean bean = Ioc.ins().getBeanInfo(it.toString());
            return bean;
        }).collect(Collectors.toMap(Bean::getLookup, it -> {
            Object o = it.getObj();
            MySql m = (MySql) o;
            return m;
        }));
    }
}
