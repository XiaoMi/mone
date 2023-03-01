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

package run.mone.docean.plugin.sidecar;

import com.xiaomi.data.push.common.RcurveConfig;
import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.codes.GsonCodes;
import com.xiaomi.data.push.uds.context.NetListener;
import com.xiaomi.data.push.uds.context.UdsClientContext;
import com.xiaomi.data.push.uds.po.RpcServerInfo;
import com.xiaomi.data.push.uds.processor.SideType;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.mone.grpc.GrpcClient;
import com.xiaomi.mone.grpc.GrpcClientGroup;
import com.xiaomi.mone.grpc.GrpcServer;
import com.xiaomi.mone.grpc.context.GrpcServerContext;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.Pair;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import run.mone.api.IClient;
import run.mone.api.IServer;
import run.mone.docean.plugin.sidecar.anno.MeshReference;
import run.mone.docean.plugin.sidecar.bo.Ping;
import run.mone.docean.plugin.sidecar.bo.ProviderMap;
import run.mone.docean.plugin.sidecar.bo.SideCarApp;
import run.mone.docean.plugin.sidecar.interceptor.CallMethodInterceptor;
import run.mone.docean.plugin.sidecar.service.SideCarInfoService;
import run.mone.docean.plugin.sidecar.state.client.ClientFsm;
import run.mone.docean.plugin.sidecar.state.client.ConnectState;
import run.mone.docean.plugin.sidecar.state.client.InitState;
import run.mone.docean.plugin.sidecar.state.client.PingState;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/24 20:31
 * <p>
 * 支持3种协议  uds grpc tcp
 * 主要用来做sidecar间的通信
 * 支持 1:1 n:m 通信模式
 */
@DOceanPlugin
@Slf4j
public class SidecarPlugin implements IPlugin {

    private boolean openServer;

    private boolean openClient;

    private boolean openClientGroup;

    /**
     * 开启多tcp client 模式 (网关的m:n模型)
     */
    private boolean openTcpClientGroup;

    private IClient client;

    private ConcurrentHashMap<String, ProviderMap> clientMap = new ConcurrentHashMap<>();


    /**
     * 用来发现服务的function
     */
    private Function DiscoveryFunction;

    /**
     * uds 或者 tcp 服务器
     */
    private IServer server;

    /**
     * 拉起来的grpc服务器
     */
    private IServer grpcServer;


    /**
     * 使用uds通信的path地址
     */
    private String path;

    /**
     * 使用grpc通信(是否开启一个grpc的server)
     */
    private boolean grpc;

    private String appName;

    private String sideCarGrpcServerPort = "8765";

    private String sideCarGrpcAddr = "127.0.0.1:" + sideCarGrpcServerPort;

    private GrpcClientGroup group = new GrpcClientGroup();

    private String groupConfig = "";

    private String tcpGroupConfig = "";

    /**
     * uds 是否开启remote模式(其实就是tcp模式)
     */
    private boolean remote;

    private String host;

    private int port;

    private int serverPort = 7777;


    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("sidecar plugin init");
        Config config = ioc.getBean(Config.class);
        if (config == null) {
            return;
        }
        this.openServer = Boolean.valueOf(config.get("sidecarServer", "false"));
        this.serverPort = Integer.valueOf(config.get("serverPort", "7777"));
        this.openClient = Boolean.valueOf(config.get("sidecarClient", "false"));

        this.openTcpClientGroup = Boolean.valueOf(config.get("openTcpClientGroup", "false"));
        this.tcpGroupConfig = config.get("tcpGroupConfig", "");

        this.openClientGroup = Boolean.valueOf(config.get("openClientGroup", "false"));
        this.groupConfig = config.get("sidecarGroupConfig", "");
        this.sideCarGrpcServerPort = config.get("sideCarGrpcServerPort", "8765");
        this.sideCarGrpcAddr = config.get("sideCarGrpcAddr", sideCarGrpcAddr);
        this.appName = config.get("app", "");

        this.path = config.get("sidecarPath", "/tmp/tmp.sock");
        this.grpc = Boolean.valueOf(config.get("sidecarGrpc", "false"));
        this.remote = Boolean.valueOf(config.get("sidecarRemote", "false"));

        //服务有可能两种都开启
        if (openServer) {
            createServer(ioc);
        }
        //client是互斥的,永远只会开启一种
        if (openClient) {
            client = createClient(config);
            ioc.putBean("sideCarClient", client);
        }
        //网关或者faas那种一次调用多个异构语言的filter
        if (openClientGroup) {
            ioc.putBean(group);
        }
    }


    private void createServer(Ioc ioc) {
        //是否开启grpc server
        if (grpc) {
            this.grpcServer = new GrpcServer(new GrpcServerContext());
            ioc.putBean("grpcSideCarServer", this.grpcServer);
        }
        UdsServer udsServer = new UdsServer();
        if (this.remote) {
            udsServer.setRemote(true);
            udsServer.setHost("0.0.0.0");
            udsServer.setPort(this.serverPort);
        }
        this.server = udsServer;
        ioc.putBean("sideCarServer", this.server);
    }

    private IClient createClient(Config config) {
        if (grpc) {
            return new GrpcClient();
        }
        UdsClient udsClient = new UdsClient(String.valueOf(System.currentTimeMillis()));
        if (this.remote) {
            udsClient.setRemote(true);
            String host = config.get("remote.side.car.server.addr", "127.0.0.1");
            if (config.get("remote.side.car.use.host.ip", "false").equals("true")) {
                //如果设置env变量,则使用env中的host.ip 连接到sidecar
                String hostIp = System.getenv("host.ip");
                if (StringUtils.isNotEmpty(hostIp)) {
                    log.info("use host.ip env:{}", hostIp);
                    host = hostIp;
                }
            }
            udsClient.setHost(host);
            udsClient.setPort(7777);
        }
        return udsClient;
    }

    /**
     * 需要每个client上挂一个状态机
     *
     * @param address
     * @return
     */
    public IClient createTcpClient(Pair<String, Integer> address) {
        UdsClient udsClient = new UdsClient(String.valueOf(System.currentTimeMillis()));
        udsClient.setRemote(true);
        udsClient.setHost(address.getKey());
        udsClient.setPort(address.getValue());

        new Thread(() -> udsClient.start("")).start();

        String app = "app_" + UUID.randomUUID();

        ClientFsm fsm = new ClientFsm();
        fsm.setClient(udsClient);

        PingState pingState = new PingState();
        InitState initState = new InitState();
        ConnectState connectState = new ConnectState();

        pingState.setFsm(fsm);
        pingState.setClient(udsClient);
        pingState.setApp(app);
        pingState.setConnectState(connectState);
        pingState.setDisableLog("true");

        initState.setFsm(fsm);
        initState.setClient(udsClient);
        initState.setApp(app);
        initState.setPingState(pingState);
        initState.setSideCarInfoService(new SideCarInfoService() {
            @Override
            public SideCarApp getSideCarApp() {
                SideCarApp app = new SideCarApp();
                app.setUtime(System.currentTimeMillis());
                return app;
            }

            @Override
            public Ping getPingData() {
                return new Ping();
            }
        });

        connectState.setFsm(fsm);
        connectState.setClient(udsClient);
        connectState.setApp(app);
        connectState.setInitState(initState);

        fsm.setState(connectState);
        fsm.execute();

        return udsClient;
    }


    @Override
    public boolean start(Ioc ioc) {
        log.info("sidecar plugin start");
        RcurveConfig.ins().init(it -> it.setCodeType(GsonCodes.type));
        if (openServer) {
            Set<UdsProcessor> processorSet = ioc.getBeans(UdsProcessor.class);
            processorSet.stream().filter(it -> it.side().equals(SideType.server)).forEach(it -> server.putProcessor(it));
            if (grpc) {
                new Thread(() -> this.grpcServer.start(this.sideCarGrpcServerPort)).start();
            }
            Consumer<RpcServerInfo> consumer = ioc.getBean("regConsumer");
            if (Optional.ofNullable(consumer).isPresent()) {
                server.setRegConsumer(consumer);
            }
            new Thread(() -> server.start(path)).start();
        }
        if (openClient) {
            Set<UdsProcessor> processorSet = ioc.getBeans(UdsProcessor.class);
            processorSet.stream().filter(it -> it.side().equals(SideType.client)).forEach(it -> client.putProcessor(it));
            String param = path;
            if (grpc) {
                param = sideCarGrpcAddr + ":" + this.appName;
            }
            client.start(param);
            log.info("side car client start finish");
        }
        if (openClientGroup) {

            ioc.putBean("", new SideCarInfoService() {

                @Override
                public SideCarApp getSideCarApp() {
                    SideCarApp app = new SideCarApp();
                    return null;
                }

                @Override
                public Ping getPingData() {
                    return new Ping();
                }
            });

            Arrays.stream(groupConfig.split(";")).forEach(it -> {
                GrpcClient c = new GrpcClient();
                c.start(it);
                group.getClients().put(c.getApp(), c);
            });
        }

        if (openTcpClientGroup) {
            Function f = ioc.getBean("sidecarDiscoveryFunction");
            if (null != f) {
                this.DiscoveryFunction = f;
                Arrays.stream(tcpGroupConfig.split(";")).forEach(it -> {
                    getClient(it);
                });
            }
        }

        /**
         * 开启client状态机
         */
        if (openClient) {
            NetListener listener = ioc.getBean("sideCarClientNetListener");
            UdsClientContext.ins().setListener(listener);
            //客户端需要启动状态机
            ClientFsm fsm = Ioc.ins().getBean(ClientFsm.class);
            new Thread(() -> fsm.execute()).start();
        }
        return true;
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
    public String version() {
        return "0.0.1:sidecar_plugin:2022-11-18";
    }

    public synchronized IClient getClient(String name) {
        if (null == this.DiscoveryFunction) {
            return null;
        }
        ProviderMap providerList = this.clientMap.get(name);
        List<IClient> list = null;
        if (null == providerList) {
            providerList = new ProviderMap();
            providerList.refresh(this.DiscoveryFunction, name, this);
            this.clientMap.put(name, providerList);
        }
        list = new ArrayList<>(providerList.getClientMap().values());
        int n = list.size();
        if (n == 1) {
            return list.get(0);
        }
        if (n == 0) {
            return null;
        }

        return list.get(ThreadLocalRandom.current().nextInt(n));
    }
}
