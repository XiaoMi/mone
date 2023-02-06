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

package run.mone.docean.plugin.rpc;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.task.Task;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;
import run.mone.docean.plugin.rpc.anno.RpcReference;
import run.mone.docean.plugin.rpc.anno.RpcService;
import run.mone.docean.plugin.rpc.proxy.ProxyUtils;
import run.mone.docean.plugin.rpc.proxy.RpcReferenceBo;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * 让用户使用rpc模块更容易
 */
@DOceanPlugin
@Slf4j
public class RpcPlugin implements IPlugin {

    private RpcServer rpcServer;

    private RpcClient rpcClient;

    private int serverPort;

    private boolean openServer;

    private boolean openClient;

    private String nacosAddr;

    private String serviceName;

    private Config config;


    public static final String CLIENT_KEY = "rpcClient";

    public static final String SERVER_KEY = "rpcServer";

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        init0(ioc);
    }

    @Override
    public boolean start(Ioc ioc) {
        if (openClient) {
            rpcClient.setReconnection(false);
            Set<NettyRequestProcessor> processorSet = ioc.getBeans(NettyRequestProcessor.class);
            List<Pair<Integer, NettyRequestProcessor>> list = processorSet.stream().map(it -> {
                Pair<Integer, NettyRequestProcessor> pair = new Pair<>(it.cmdId(), it);
                return pair;
            }).collect(Collectors.toList());
            rpcClient.setProcessorList(list);
            rpcClient.start(config -> config.setIdle(false));
            log.info("rpc client init finish");
            Set<Task> taskSet = ioc.getBeans(Task.class);
            taskSet.stream().forEach(t -> t.setClient(rpcClient));
            rpcClient.setTasks(new ArrayList<>(taskSet));
            rpcClient.init();
        }

        if (openServer) {
            Set<NettyRequestProcessor> processorSet = ioc.getBeans(NettyRequestProcessor.class);
            List<Pair<Integer, NettyRequestProcessor>> list = processorSet.stream().map(it -> {
                Pair<Integer, NettyRequestProcessor> pair = new Pair<>(it.cmdId(), it);
                return pair;
            }).collect(Collectors.toList());
            //注册处理器
            rpcServer.setProcessorList(list);
            rpcServer.init();
            rpcServer.start(config -> config.setIdle(false));
        }
        log.info("start finish");
        return true;
    }

    public boolean init0(Ioc ioc) {
        this.config = ioc.getBean(Config.class);
        this.openServer = Boolean.valueOf(config.get("rpcOpenServer", "false"));
        this.openClient = Boolean.valueOf(config.get("rpcOpenClient", "false"));
        this.serverPort = Integer.valueOf(config.get("rpcServerPort", "9999"));
        this.serviceName = config.get("serviceName", "server:" + System.currentTimeMillis());
        this.nacosAddr = config.get("nacosAddr", "");
        initServer(ioc);
        initClient(ioc);
        return true;
    }

    public void initServer(Ioc ioc) {
        if (openServer) {
            Safe.runAndLog(() -> {
                boolean regNacos = !StringUtils.isEmpty(nacosAddr);
                this.rpcServer = new RpcServer(this.nacosAddr, serviceName, regNacos);
                rpcServer.setListenPort(serverPort);
                log.info("rpc server init finish");
                ioc.putBean(rpcServer);
            });
        }
    }

    public void initClient(Ioc ioc) {
        if (openClient) {
            Safe.runAndLog(() -> {
                if (StringUtils.isNotEmpty(nacosAddr)) {
                    rpcClient = new RpcClient(nacosAddr, this.serviceName);
                } else {
                    rpcClient = new RpcClient("");
                }
                ioc.putBean(rpcClient);
            });
        }
    }

    /**
     * 用来解决Resource的s
     *
     * @param ioc
     * @param type
     * @param annotations
     * @return
     */
    @Override
    public Optional<String> ioc(Ioc ioc, Class type, Annotation[] annotations) {
        //让docean下的业务可以直接使用client 和 server
        Optional<Annotation> optional = getAnno(annotations, Resource.class);
        if (optional.isPresent()) {
            Resource r = (Resource) optional.get();
            if (r.name().equals(CLIENT_KEY)) {
                return Optional.of(rpcClient.getClass().getName());
            }
            if (r.name().equals(SERVER_KEY)) {
                return Optional.of(rpcServer.getClass().getName());
            }
        }
        //像调用本地方法一样调用远程的方法(类似dubbo那样)
        Optional<Annotation> rpcOptional = getAnno(annotations, RpcReference.class);
        if (rpcOptional.isPresent()) {
            RpcReference reference = (RpcReference) rpcOptional.get();
            Object proxyBean = ProxyUtils.proxy(RpcReferenceBo.builder().build(), ioc, this.config, reference.interfaceClass(), "");
            ioc.putBean(reference.interfaceClass().getName(), proxyBean);
            return Optional.of(reference.interfaceClass().getName());
        }
        return Optional.empty();
    }


    /**
     * 加入注解扫描
     *
     * @return
     */
    @Override
    public List<Class<? extends Annotation>> filterAnnotations() {
        return Lists.newArrayList(RpcService.class);
    }

    @Override
    public Bean initBean(Ioc ioc, Bean bean) {
        RpcService s = bean.getClazz().getAnnotation(RpcService.class);
        if (Optional.ofNullable(s).isPresent()) {
            log.info("s:{}",s);
        }
        return bean;
    }


}
