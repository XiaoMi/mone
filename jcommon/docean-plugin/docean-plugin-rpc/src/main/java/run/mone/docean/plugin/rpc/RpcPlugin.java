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

import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@DOceanPlugin
@Slf4j
public class RpcPlugin implements IPlugin {

    private RpcServer rpcServer;

    private RpcClient rpcClient;

    private int serverPort;

    private boolean openServer;

    private boolean openClient;

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
    }


    @Override
    public boolean start(Ioc ioc) {
        Config config = ioc.getBean(Config.class);
        this.openServer = Boolean.valueOf(config.get("rpcOpenServer","false"));
        this.openClient = Boolean.valueOf(config.get("rpcOpenClient","false"));
        this.serverPort = Integer.valueOf(config.get("rpcServerPort","9999"));
        initServer(ioc);
        initClient(ioc);
        return true;
    }

    public void initServer(Ioc ioc) {
        if (openServer) {
            Safe.runAndLog(()->{
                RpcServer rpcServer = new RpcServer("", "nacos_server", false);
                rpcServer.setListenPort(serverPort);
                Set<NettyRequestProcessor> processorSet = ioc.getBeans(NettyRequestProcessor.class);
                List<Pair<Integer, NettyRequestProcessor>> list = processorSet.stream().map(it -> {
                    Pair<Integer, NettyRequestProcessor> pair = new Pair<>(it.cmdId(), it);
                    return pair;
                }).collect(Collectors.toList());
                //注册处理器
                rpcServer.setProcessorList(list);
                rpcServer.init();
                rpcServer.start(config -> config.setIdle(false));
                log.info("rpc server init finish");
                ioc.putBean(rpcServer);
            });
        }
    }

    public void initClient(Ioc ioc) {
        if (openClient) {
            Safe.runAndLog(()->{
                rpcClient = new RpcClient("");
                rpcClient.setReconnection(false);
                rpcClient.start(config -> config.setIdle(false));
                rpcClient.init();
                log.info("rpc client init finish");
                ioc.putBean(rpcClient);
            });
        }
    }
}
