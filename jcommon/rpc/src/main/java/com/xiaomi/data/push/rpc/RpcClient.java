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

package com.xiaomi.data.push.rpc;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.data.push.bo.ClientInfo;
import com.xiaomi.data.push.common.Service;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.data.push.rpc.common.InvokeCallback;
import com.xiaomi.data.push.rpc.common.NetUtils;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.common.RpcClientVersion;
import com.xiaomi.data.push.rpc.netty.NettyClientConfig;
import com.xiaomi.data.push.rpc.netty.NettyRemotingClient;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangzhiyong
 * @date 30/05/2018
 */
public class RpcClient implements Service {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private NettyRemotingClient client;

    private final String nacosAddrs;

    private final String serverName;

    private String serverIp;

    private int serverPort;

    private NacosNaming nacosNaming = new NacosNaming();

    @Setter
    private List<Pair<Integer, NettyRequestProcessor>> processorList = Lists.newLinkedList();


    private AtomicReference<String> serverAddrs = new AtomicReference<>("");

    private ExecutorService defaultPool = new ThreadPoolExecutor(1000, 1000,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    private ScheduledExecutorService pool;


    @Getter
    private String clientIp;

    @Getter
    private int clientPort;

    @Setter
    private List<Task> tasks = Lists.newArrayList();

    @Setter
    private boolean reconnection = true;

    @Setter
    private ClientInfo clientInfo;


    public RpcClient(String nacosAddrs, String serverName) {
        logger.info("rpc client version:{}", new RpcVersion());
        this.nacosAddrs = nacosAddrs;
        this.serverName = serverName;
    }


    public String getServerAddrs() {
        String address = this.serverAddrs.get();
        return address;
    }


    @Override
    public void start() {
        logger.info("client version:{}", new RpcClientVersion());
        NettyClientConfig config = new NettyClientConfig();
        config.setReconnection(this.reconnection);
        client = new NettyRemotingClient(config);
        client.setGetAddrsFunc((str) -> getServerAddrs());
        processorList.stream().forEach(it -> client.registerProcessor(it.getObject1(), it.getObject2(), defaultPool));
        //并没有connect 只是初始化了下(init)
        client.start();

        logger.info("nacos addr:{}", this.nacosAddrs);

        nacosNaming.setServerAddr(this.nacosAddrs);
        nacosNaming.init();

        logger.info("refresh server addrs");
        refreshServerAddrs();

        logger.info("reg");
        Executors.newSingleThreadExecutor().submit(() -> reg());
    }


    private void reg() {
        Optional.ofNullable(this.clientInfo).ifPresent(it -> {
            try {
                Instance instance = new Instance();
                instance.setIp(it.getIp());
                instance.setPort(it.getPort());
                Map<String, String> meta = Maps.newHashMap();
                meta.put("ctime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                meta.put("version", new RpcVersion().toString() + ":" + it.getVersion());
                instance.setMetadata(meta);
                nacosNaming.registerInstance(it.getName(), instance);
            } catch (NacosException e) {
                logger.error(e.getMessage());
            }
        });
    }


    private void initAddr() {
        try {
            List<Instance> list = nacosNaming.getAllInstances(serverName)
                    .stream().filter(it->it.isHealthy() && it.isEnabled()).collect(Collectors.toList());
            if (list.size() > 0) {
                String serverIp = list.get(0).getIp();
                int serverPort = list.get(0).getPort();
                logger.info("serverIp:{} serverPort:{}", serverIp, serverPort);
                this.serverAddrs.set(serverIp + ":" + serverPort);
            } else {
                logger.warn("server size = 0");
                this.serverAddrs.set("");
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage());
        }
    }

    //异步刷新
    private void refreshServerAddrs() {
        new Thread(() -> {
            while (true) {
                initAddr();
                sleep(5);
            }
        }).start();
    }


    private static void sleep(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }
    }


    /**
     * 默认客户端直接拉起
     */
    @Override
    @PostConstruct
    public void init() {
        int size = this.tasks.size();
        if (size > 0) {
            pool = Executors.newScheduledThreadPool(size);
            this.tasks.forEach(task -> {
                pool.scheduleAtFixedRate(() -> {
                    try {
                        task.getRunnable().run();
                    } catch (Throwable ex) {
                        logger.warn(ex.getMessage());
                    }
                }, 5, task.getDelay(), TimeUnit.SECONDS);
            });
        }
    }


    @Override
    public void shutdown() {
        if (null != this.client) {
            try {
                pool.shutdown();
                client.shutdown();
            } catch (Exception ex) {
                logger.info("rpc client shutdwon error:{}", ex.getMessage());
            }
        }
    }

    /**
     * 异步发送消息
     *
     * @param addr
     * @param code
     * @param message
     */
    public void sendMessage(String addr, int code, String message) {
        if (StringUtils.isEmpty(addr)) {
            logger.warn("addr is empty");
            return;
        }
        try {
            RemotingCommand req = RemotingCommand.createRequestCommand(code);
            req.setBody(message.getBytes());
            this.client.invokeAsync(addr, req, TimeUnit.SECONDS.toMillis(3), responseFuture -> {
            });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * one way
     * 不需要返回结果
     *
     * @param req
     */
    public void sendMessage(RemotingCommand req) {
        String addr = this.serverAddrs.get();
        if (StringUtils.isEmpty(addr)) {
            logger.warn("send message addr is null");
            return;
        }
        try {
            this.client.invokeOneway(addr, req, req.getTimeout());
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e);
        }
    }


    public void oneway(String addr, RemotingCommand req) {
        try {
            this.client.createChannel(addr);
            this.client.invokeOneway(addr, req, 1000L);
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e);
        }
    }


    public RemotingCommand sendMessage(String addr, int code, String message, long timeout) {
        try {
            RemotingCommand req = RemotingCommand.createRequestCommand(code);
            req.setBody(message.getBytes());
            return this.client.invokeSync(addr, req, timeout);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public void sendMessage(String addr, RemotingCommand req, InvokeCallback callback) {
        try {
            this.client.invokeAsync(addr, req, TimeUnit.SECONDS.toMillis(3), callback);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * 向服务器发送ping信息(有连接则直接发,没有就先新建连接)
     *
     * @param addr
     */
    public void ping(String addr) {
        this.sendMessage(addr, RpcCmd.pingReq, "ping");
    }


    /**
     * 关闭所有建立的连接
     */
    public void logout() {
        logger.info("client logout");
        this.client.closeAllChannel();
    }

    public NettyRemotingClient getClient() {
        return client;
    }


    public NettyRequestProcessor getProcessor(Integer cmd) {
        return this.processorList.stream().filter(it -> it.getObject1().equals(cmd)).map(it -> it.getObject2()).findAny()
                .orElse(new NettyRequestProcessor() {
                    @Override
                    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
                        return null;
                    }

                    @Override
                    public boolean rejectRequest() {
                        return false;
                    }
                });
    }

}
